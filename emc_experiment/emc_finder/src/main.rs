#![feature(try_blocks)]

mod preprocess;

use preprocess::get_data;
use preprocess::DerivedEmc;
use preprocess::HasEmc;
use preprocess::Item;
use preprocess::ProcessedData;
use rayon::iter::ParallelBridge;
use rayon::iter::ParallelIterator;
use std::fs::{self};
use std::path::PathBuf;

fn update_derived_emc<F, E>(data: ProcessedData, item_filter: F, epoch_filter: E) -> ProcessedData
where
    F: Fn(&Item) -> bool + Sync,
    E: Fn(i32) -> bool + Sync,
{
    let start = std::time::Instant::now();
    let mut items = data.items;
    let mut recipes = data.recipes;
    let mut epoch = 0;
    let mut changed = true;
    loop {
        if epoch > 0 {
            // we want to update the derived emc on all the recipes
            // we want to do this update before each epoch after the first
            // we want to do this update before returning
            recipes.iter_mut().par_bridge().for_each(|recipe| {
                for ingredient in recipe.inputs.iter_mut().chain(recipe.outputs.iter_mut()) {
                    if let Some(item) = items.get(&ingredient.ingredient_id) {
                        if let Some(derived_emc) = item.derived_emc.as_ref() {
                            ingredient.derived_emc = Some(derived_emc.clone());
                        }
                    }
                }
            });
        }

        if !epoch_filter(epoch) {
            println!("Stopping after {} epochs", epoch);
            break;
        } else {
            if !changed {
                println!("No changes in epoch {}", epoch - 1);
                break;
            }
            println!("Starting epoch {}", epoch);
            epoch += 1;
        }

        changed = items
            .values_mut()
            .par_bridge()
            .filter(|item| item_filter(item))
            .map(|item| {
                let mut changed = false;
                if item.emc.is_none() && item.derived_emc.is_none() {
                    for recipe in recipes.iter() {
                        let mut inputs_our_item = false;
                        let mut inputs_lacking_emc = false;
                        let mut input_amount = 0;
                        for ingredient in recipe.inputs.iter() {
                            if ingredient.ingredient_id == item.id {
                                input_amount += ingredient.ingredient_amount;
                                inputs_our_item = true;
                            } else if ingredient.get_emc().is_none() {
                                inputs_lacking_emc = true;
                            }
                        }
                        if inputs_our_item && !inputs_lacking_emc {
                            let output_emc = recipe.get_output_emc();
                            let input_emc = recipe.get_input_emc();
                            let diff = output_emc - input_emc;
                            if diff > 0.0 {
                                let emc_for_item = diff / input_amount as f32;
                                if item.derived_emc.as_ref().map(|x| x.emc).unwrap_or(0.) < diff {
                                    item.derived_emc = Some(DerivedEmc {
                                        emc: emc_for_item,
                                        path: vec![recipe.recipe_id.to_owned()],
                                    });
                                    // println!(
                                    //     "Found derived emc for {:#?} using recipe {:#?}",
                                    //     item, recipe
                                    // );
                                    changed = true;
                                }
                            }
                        }
                    }
                }
                changed
            })
            .reduce(|| false, |acc, x| acc || x);
    }

    println!(
        "Updated derived emc for all recipes in {} seconds",
        start.elapsed().as_secs()
    );
    ProcessedData { items, recipes }
}

fn main() {
    let output_dir = PathBuf::from("outputs");

    // remove output dir if exists
    if output_dir.exists() {
        fs::remove_dir_all(&output_dir).expect("Unable to remove output directory");
    }
    fs::create_dir_all(&output_dir).expect("Unable to create output directory");

    let data = get_data();
    println!(
        "Loaded {} recipes and {} items",
        data.recipes.len(),
        data.items.len()
    );

    let data = update_derived_emc(
        data,
        |item| item.id.starts_with("minecraft:"),
        |epoch| epoch < 2,
    );
    for item in data.items.values() {
        if item.derived_emc.is_some() {
            println!("{item:#?}");
        }
    }
}

#[cfg(test)]
mod tests {
    use crate::preprocess::get_data;
    use crate::update_derived_emc;
    use rayon::iter::IntoParallelRefIterator;
    use rayon::iter::ParallelIterator;

    #[test]
    fn find_blaze_powder_recipes() {
        let data = get_data();

        let found = data
            .recipes
            .par_iter()
            .filter(|recipe| {
                recipe
                    .outputs
                    .iter()
                    .all(|ing| ing.ingredient_id == "minecraft:blaze_powder")
                    && recipe
                        .inputs
                        .iter()
                        .all(|ing| ing.ingredient_id == "minecraft:blaze_rod")
            })
            .collect::<Vec<_>>();
        for recipe in found {
            println!("{:#?}", recipe);
            println!();
        }
    }

    #[test]
    fn find_blaze_powder_emc() {
        // blaze powder has no emc
        // blaze powder plus ender pearl gives eye of ender which has emc
        let data = get_data();

        let blaze_powder = data.items.get("minecraft:blaze_powder").unwrap();
        assert_eq!(blaze_powder.emc, None);

        let data = update_derived_emc(
            data,
            |item| item.id == "minecraft:blaze_powder",
            |epoch| epoch < 2,
        );

        let blaze_powder = data.items.get("minecraft:blaze_powder").unwrap();
        dbg!(blaze_powder);
        assert_eq!(blaze_powder.derived_emc.as_ref().unwrap().emc, 768.0);
    }

    #[test]
    fn find_blaze_powder_loop() {
        // 1 blaze rod - mechanical squeezer -> 5 blaze powder (no emc) - crafting -> ender pearl (has emc)
        let data = get_data();
        let data = update_derived_emc(
            data,
            |item| item.id == "minecraft:blaze_powder",
            |epoch| epoch < 2,
        );

        for recipe in data.recipes {
            if recipe.has_non_emc_input() || recipe.has_non_emc_output() {
                continue;
            }
            if recipe
                .inputs
                .iter()
                .any(|ing| ing.ingredient_id == "minecraft:blaze_powder")
            {
                if recipe.get_output_emc() > recipe.get_input_emc() {
                    println!("{:#?}", recipe);
                }
            }
        }
    }
}
