#![feature(try_blocks)]

mod preprocess;

use preprocess::get_processed_recipes;
use preprocess::Ingredient;
use preprocess::ProcessedRecipe;
use rayon::iter::IntoParallelRefIterator;
use rayon::iter::ParallelIterator;
use std::fs::File;
use std::fs::{self};
use std::io::Write;
use std::path::Path;
use std::path::PathBuf;
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

            let total_input_emc = recipe.total_input_emc;
            let total_output_emc = recipe.total_output_emc;

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

fn main() {
    let output_dir = PathBuf::from("outputs");

    // remove output dir if exists
    if output_dir.exists() {
        fs::remove_dir_all(&output_dir).expect("Unable to remove output directory");
    }
    fs::create_dir_all(&output_dir).expect("Unable to create output directory");

    let recipes = get_processed_recipes();
    println!("Loaded {} recipes", recipes.len());

    // recipes
    //     .par_iter()
    //     .for_each(|recipe| explore_recipe(recipe, &recipes, &output_dir));

    let found = recipes
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

#[cfg(test)]
mod tests {}
