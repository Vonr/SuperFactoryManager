#![feature(try_blocks)]

mod preprocess;

use preprocess::get_data;
use preprocess::DerivedEmc;
use preprocess::Ingredient;
use preprocess::Item;
use preprocess::ProcessedData;
use preprocess::ProcessedRecipe;
use rayon::iter::ParallelBridge;
use rayon::iter::ParallelIterator;
use std::fs::File;
use std::fs::{self};
use std::io::Write;
use std::path::Path;
use std::path::PathBuf;
use std::sync::Arc;
use std::sync::Mutex;
use uuid::Uuid;

#[derive(Debug, Clone)]
struct HistoryEntry {
    recipe_id: String,
    recipe_category: String,
    ingredients: Vec<String>,
    resulting_emc: f32,
}

fn explore_recipe(recipe: &ProcessedRecipe, recipes: &[ProcessedRecipe], output_dir: &Path) {
    let initial_buffer = recipe
        .inputs
        .iter()
        .map(|ing| ing.ingredient_id.clone())
        .collect::<Vec<String>>();

    let mut visited_recipes = Vec::new();
    let mut history = Vec::new();

    let result = traverse_recipes(
        initial_buffer,
        &mut visited_recipes,
        &mut history,
        recipes,
        0,
        1, // Set max depth to 1
    );

    if let Some(history) = result {
        let output_file = output_dir.join(format!("{}.txt", Uuid::new_v4()));
        let mut file = File::create(output_file).expect("Unable to create file");
        for entry in history {
            writeln!(
                file,
                "[{}] Recipe: {}, Ingredients: {:?}, Resulting EMC: {}",
                entry.recipe_category, entry.recipe_id, entry.ingredients, entry.resulting_emc
            )
            .expect("Unable to write to file");
        }
    }
}

fn traverse_recipes(
    buffer: Vec<String>,
    visited_recipes: &mut Vec<String>,
    history: &mut Vec<HistoryEntry>,
    recipes: &[ProcessedRecipe],
    depth: usize,
    max_depth: usize,
) -> Option<Vec<HistoryEntry>> {
    if depth >= max_depth {
        return None;
    }

    for recipe in recipes.iter() {
        if visited_recipes.contains(&recipe.recipe_id) {
            continue;
        }

        let input_items: Vec<&Ingredient> = recipe
            .inputs
            .iter()
            .filter(|ingredient| buffer.contains(&ingredient.ingredient_id))
            .collect();

        if !input_items.is_empty() {
            visited_recipes.push(recipe.recipe_id.clone());

            let mut buffer_copy = buffer.clone();
            let enough_items = input_items.iter().all(|ingredient| {
                let count = buffer_copy
                    .iter()
                    .filter(|id| *id == &ingredient.ingredient_id)
                    .count();
                if count >= ingredient.ingredient_amount as usize {
                    for _ in 0..ingredient.ingredient_amount {
                        if let Some(index) = buffer_copy
                            .iter()
                            .position(|id| id == &ingredient.ingredient_id)
                        {
                            buffer_copy.remove(index);
                        }
                    }
                    true
                } else {
                    false
                }
            });

            if !enough_items {
                continue;
            }

            let total_input_emc = recipe.base_input_emc;
            let total_output_emc = recipe.base_output_emc;

            for output in &recipe.outputs {
                buffer_copy.push(output.ingredient_id.clone());
            }

            history.push(HistoryEntry {
                recipe_id: recipe.recipe_id.clone(),
                recipe_category: recipe.category_title.clone(),
                ingredients: input_items
                    .iter()
                    .map(|ing| ing.ingredient_id.clone())
                    .collect(),
                resulting_emc: total_output_emc,
            });

            if total_output_emc > total_input_emc {
                return Some(history.clone());
            }

            if depth + 1 < max_depth {
                if let Some(result) = traverse_recipes(
                    buffer_copy,
                    visited_recipes,
                    history,
                    recipes,
                    depth + 1,
                    max_depth,
                ) {
                    return Some(result);
                }
            }

            visited_recipes.pop();
            history.pop();
        }
    }

    None
}

fn update_derived<F,E> (data: ProcessedData, item_filter: F, epoch_filter: E) -> ProcessedData
where
    F: Fn(&Item) -> bool + Sync,
    E: Fn(i32) -> bool + Sync,
{
    let start = std::time::Instant::now();
    let mut items = data.items;
    let mut recipes = data.recipes;
    let changed = Arc::new(Mutex::new(true));
    let mut epoch = 0;
    while *changed.lock().unwrap() {
        if !epoch_filter(epoch) {
            println!("Stopping after {} epochs", epoch);
            break;
        }
        println!("Starting epoch {}", epoch);
        epoch += 1;

        *changed.lock().unwrap() = false;
        items
            .iter_mut()
            .par_bridge()
            .filter(|item| item_filter(item))
            .for_each_with(changed.clone(), |changed, item| {
                if item.emc.is_none() && item.derived_emc.is_none() {
                    for recipe in recipes.iter() {
                        let mut inputs_our_item = false;
                        let mut inputs_lacking_emc = false;
                        let mut input_amount = 0;
                        for ingredient in recipe.inputs.iter() {
                            if ingredient.ingredient_id == item.id {
                                input_amount += ingredient.ingredient_amount;
                                inputs_our_item = true;
                            } else if ingredient.emc.is_none() {
                                inputs_lacking_emc = true;
                            }
                        }
                        if inputs_our_item && !inputs_lacking_emc {
                            let output_emc = recipe.base_output_emc;
                            let input_emc = recipe.base_input_emc;
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
                                    *changed.lock().unwrap() = true;
                                }
                            }
                        }
                    }
                }
            });
    }

    // we want to update the derived emc on all the recipes now
    let item_lookup = items
        .iter()
        .map(|item| (item.id.clone(), item))
        .collect::<std::collections::HashMap<_, _>>();
    recipes.iter_mut().par_bridge().for_each(|recipe| {
        for ingredient in recipe.inputs.iter_mut().chain(recipe.outputs.iter_mut()) {
            if let Some(item) = item_lookup.get(&ingredient.ingredient_id) {
                if let Some(derived_emc) = item.derived_emc.as_ref() {
                    ingredient.derived_emc = Some(derived_emc.clone());
                }
            }
        }
    });
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

    let data = update_derived(data, |item| item.id.starts_with("minecraft:"), |epoch| epoch < 2);
    for item in data.items {
        if item.derived_emc.is_some() {
            println!("{item:#?}");
        }
    }
}

#[cfg(test)]
mod tests {
    use rayon::iter::IntoParallelRefIterator;
    use rayon::iter::ParallelIterator;

    use crate::preprocess::get_data;
    use crate::preprocess::DerivedEmc;

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
    fn find_ender_pearl_path() {
        // 1 blaze rod - mechanical squeezer -> 5 blaze powder (no emc) - crafting -> ender pearl (has emc)
        let data = get_data();
        let mut items = data.items;
        let recipes = data.recipes;
        for item in items.iter_mut() {
            if item.derived_emc.is_none() && item.id == "minecraft:blaze_powder" {
                for recipe in recipes.iter() {
                    let mut inputs_our_recipe = false;
                    let mut inputs_other_non_emc_ingredients = false;
                    for ingredient in recipe.inputs.iter() {
                        if ingredient.ingredient_id == item.id {
                            inputs_our_recipe = true;
                        } else if ingredient.emc.is_none() {
                            inputs_other_non_emc_ingredients = true;
                        }
                    }
                    if inputs_our_recipe && !inputs_other_non_emc_ingredients {
                        let output_emc = recipe.base_output_emc;
                        let input_emc = recipe.base_input_emc;
                        let diff = output_emc - input_emc;
                        if diff > 0.0 {
                            if item.derived_emc.as_ref().map(|x| x.emc).unwrap_or(0.) < diff {
                                item.derived_emc = Some(DerivedEmc {
                                    emc: diff,
                                    path: vec![recipe.recipe_id.to_owned()],
                                });
                                println!(
                                    "Found derived emc for {:#?} using recipe {:#?}",
                                    item, recipe
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    #[test]
    fn update_derived() {
        // 1 blaze rod - mechanical squeezer -> 5 blaze powder (no emc) - crafting -> ender pearl (has emc)
        let data = get_data();
        let mut items = data.items;
        let recipes = data.recipes;
        let mut changed = true;
        let start = std::time::Instant::now();
        let should_stop = || start.elapsed().as_secs() > 1;
        while changed && !should_stop() {
            changed = false;
            for item in items.iter_mut() {
                if should_stop() {
                    break;
                }
                if item.emc.is_none() && item.derived_emc.is_none() {
                    for recipe in recipes.iter() {
                        if should_stop() {
                            break;
                        }
                        let mut inputs_our_item = false;
                        let mut inputs_lacking_emc = false;
                        let mut input_amount = 0;
                        for ingredient in recipe.inputs.iter() {
                            if ingredient.ingredient_id == item.id {
                                input_amount += ingredient.ingredient_amount;
                                inputs_our_item = true;
                            } else if ingredient.emc.is_none() {
                                inputs_lacking_emc = true;
                            }
                        }
                        if inputs_our_item && !inputs_lacking_emc {
                            let output_emc = recipe.base_output_emc;
                            let input_emc = recipe.base_input_emc;
                            let diff = output_emc - input_emc;
                            if diff > 0.0 {
                                let emc_for_item = diff / input_amount as f32;
                                if item.derived_emc.as_ref().map(|x| x.emc).unwrap_or(0.) < diff {
                                    item.derived_emc = Some(DerivedEmc {
                                        emc: emc_for_item,
                                        path: vec![recipe.recipe_id.to_owned()],
                                    });
                                    println!(
                                        "Found derived emc for {:#?} using recipe {:#?}",
                                        item, recipe
                                    );
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
