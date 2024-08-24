#![feature(try_blocks)]
use std::{
    collections::{HashMap, VecDeque},
    path::PathBuf,
};

use rayon::iter::{ParallelBridge, ParallelIterator};
use serde::Deserialize;

#[derive(Debug, Deserialize)]
#[allow(dead_code)]
pub struct Item {
    id: String,
    data: Option<String>,
    tags: Vec<String>,
    tooltip: String,
    emc: Option<f32>,
}

#[derive(Debug, Deserialize)]
#[allow(dead_code)]
pub struct Ingredient {
    role: String,
    #[serde(rename = "ingredientType")]
    ingredient_type: String,
    #[serde(rename = "ingredientAmount")]
    ingredient_amount: i32,
    #[serde(rename = "ingredientId")]
    ingredient_id: String,
    tags: Vec<String>,
    ingredient: String,
}

#[derive(Debug, Deserialize)]
#[allow(dead_code)]
pub struct Recipe {
    category: String,
    #[serde(rename = "categoryTitle")]
    category_title: String,
    #[serde(rename = "recipeTypeId")]
    recipe_type_id: String,
    #[serde(rename = "recipeClass")]
    recipe_class: String,
    #[serde(rename = "recipeObject")]
    recipe_object: String,
    ingredients: Vec<Ingredient>,
}

fn get_emc_from_tooltip(number_words: &HashMap<&str, i64>, tooltip: &str) -> Option<f32> {
    for line in tooltip.lines() {
        if let Some(emc) = try {
            let x = line.strip_prefix("EMC: ")?;
            let x = x.trim_end_matches(" (✗)");
            let x = x.replace(",", "");
            let mut words = x.split_whitespace().collect::<VecDeque<&str>>();
            let number = words.pop_front()?;
            let mut number = number.parse::<f32>().ok()?;
            if let Some(number_word) = words.pop_front() {
                if let Some(&multiplier) = number_words.get(number_word) {
                    number *= multiplier as f32
                }
            }
            number
        } {
            return Some(emc);
        }
    }
    return None;
}

fn get_number_words() -> HashMap<&'static str, i64> {
    [
        ("Million", 1_000_000i64),
        ("Billion", 1_000_000_000i64),
        ("Trillion", 1_000_000_000_000i64),
        ("Quadrillion", 1_000_000_000_000_000i64),
        ("Quintillion", 1_000_000_000_000_000_000i64),
    ]
    .into_iter()
    .collect()
}

fn calculate_emc(items: &mut Vec<Item>) {
    let number_words = get_number_words();
    for item in items.iter_mut() {
        if item.emc.is_none() {
            item.emc = get_emc_from_tooltip(&number_words, &item.tooltip);
        }
    }
}

fn load_recipes() -> Vec<Recipe> {
    let jei_folder = PathBuf::from("../jei");

    let recipes: Vec<Recipe> = jei_folder
        .read_dir()
        .unwrap()
        .into_iter()
        .par_bridge() // Convert to parallel iterator
        .filter_map(|entry| {
            let path = entry.ok()?.path();
            std::fs::read_to_string(&path)
                .ok()
                .and_then(|content| serde_json::from_str::<Vec<Recipe>>(&content).ok())
        })
        .flatten() // Flatten the Vec<Vec<Recipe>> into Vec<Recipe>
        .collect();

    recipes
}
#[derive(Debug)]
struct HistoryEntry {
    recipe_id: String,
    recipe_category: String,
    ingredients: Vec<String>,
    resulting_emc: f32,
}
fn find_emc_cycles(start_item_id: &str, recipes: &[Recipe], items: &[Item]) {
    // Filter recipes that contain the starting item as an input
    let relevant_recipes: Vec<&Recipe> = recipes.iter()
        .filter(|recipe| recipe.ingredients.iter().any(|ingredient| ingredient.ingredient_id == start_item_id && ingredient.role == "INPUT"))
        .collect();

    // Iterate over each relevant recipe as a starting point
    for recipe in relevant_recipes {
        // Calculate the starting EMC as the sum of the EMC of all ingredients in the recipe
        let start_emc: f32 = recipe.ingredients.iter()
            .filter_map(|ingredient| {
                items.iter().find(|item| item.id == ingredient.ingredient_id)
                    .and_then(|item| item.emc)
                    .map(|emc| emc * ingredient.ingredient_amount as f32)
            })
            .sum();

        let mut visited_recipes = Vec::new();
        let mut buffer = recipe.ingredients.iter().map(|ing| ing.ingredient_id.clone()).collect::<Vec<String>>();
        let mut history = Vec::new();

        // Start the cycle detection from this recipe
        traverse_recipes(
            start_emc,
            &mut buffer,
            &mut visited_recipes,
            &mut history,
            recipes,
            items,
        );
    }
}

fn traverse_recipes(
    current_emc: f32,
    buffer: &mut Vec<String>,
    visited_recipes: &mut Vec<String>,
    history: &mut Vec<HistoryEntry>,
    recipes: &[Recipe],
    items: &[Item],
) {
    for recipe in recipes { // TODO: parallelize
        // Skip recipes we've already visited
        if visited_recipes.contains(&recipe.recipe_object) {
            continue;
        }

        // Check if this recipe uses any item in the buffer as an input
        let input_items: Vec<&Ingredient> = recipe
            .ingredients
            .iter()
            .filter(|ingredient| ingredient.role == "INPUT" && buffer.contains(&ingredient.ingredient_id))
            .collect();
        // TODO: ensure that we have enough items in the buffer to satisfy the recipe

        if !input_items.is_empty() {
            visited_recipes.push(recipe.recipe_object.clone());

            // Calculate the total EMC of inputs
            let total_input_emc: f32 = input_items.iter().map(|ingredient| {
                items.iter().find(|item| item.id == ingredient.ingredient_id) // TODO: create a hashmap for this
                    .and_then(|item| item.emc)
                    .map(|emc| emc * ingredient.ingredient_amount as f32)
                    .unwrap_or(0.0)
            }).sum();

            // Add output items to the buffer
            let output_items: Vec<&Ingredient> = recipe
                .ingredients
                .iter()
                .filter(|ingredient| ingredient.role == "OUTPUT")
                .collect();

            let mut total_output_emc = 0.0;
            for output in &output_items {
                if let Some(item) = items.iter().find(|item| item.id == output.ingredient_id) {
                    if let Some(emc) = item.emc {
                        total_output_emc += emc * output.ingredient_amount as f32;
                        buffer.push(output.ingredient_id.clone());
                    }
                }
            }

            // Track the current step in the history
            let history_entry = HistoryEntry {
                recipe_id: recipe.recipe_object.clone(),
                recipe_category: recipe.category_title.clone(),
                ingredients: input_items.iter().map(|ing| ing.ingredient_id.clone()).collect(),
                resulting_emc: total_output_emc,
            };
            history.push(history_entry);

            // Check if the total output EMC is greater than the total input EMC
            if total_output_emc > total_input_emc {
                println!(
                    "Found profitable cycle: Initial EMC = {}, Input EMC = {}, Output EMC = {}",
                    current_emc, total_input_emc, total_output_emc
                );
                println!("Final Buffer: {:?}", buffer);
                println!("History of steps taken:");

                for entry in history.iter() {
                    println!(
                        "Recipe: {}, Category: {}, Ingredients: {:?}, Resulting EMC: {}",
                        entry.recipe_id, entry.recipe_category, entry.ingredients, entry.resulting_emc
                    );
                }
                println!();
                println!();
            }

            // Recur to explore further chains
            traverse_recipes(total_output_emc, buffer, visited_recipes, history, recipes, items);

            // Backtrack by removing the current recipe from visited
            visited_recipes.pop();

            // Safely clear or truncate the buffer
            if buffer.len() >= output_items.len() {
                buffer.truncate(buffer.len() - output_items.len());
            } else {
                buffer.clear(); // Ensure buffer is safely managed
            }

            // Remove the last history entry after backtracking
            history.pop();
        }
    }
}

fn main() {
    let items_json = include_str!("../../items.json");
    let mut items: Vec<Item> = serde_json::from_str(items_json).unwrap();
    calculate_emc(&mut items);

    println!("Loading recipes...");
    let recipes = load_recipes();
    println!("Loaded {} recipes", recipes.len());
    assert!(recipes.len() > 10_000);

    // Find EMC cycles starting from blaze rods
    find_emc_cycles("minecraft:blaze_rod", &recipes, &items);
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn first_25() {
        let items_json = include_str!("../../items.json");
        let mut items: Vec<Item> = serde_json::from_str(items_json).unwrap();
        calculate_emc(&mut items);

        for item in items.iter().filter(|x| x.emc.is_some()).take(25) {
            println!("{}: {}", item.id, item.emc.unwrap());
        }
    }

    #[test]
    fn test_load_recipes() {
        let recipes = load_recipes();
        assert!(recipes.len() > 10_000);
    }

    #[test]
    fn test_get_emc_from_tooltip() {
        let number_words = get_number_words();

        let tooltip = "EMC: 2";
        let emc = get_emc_from_tooltip(&number_words, tooltip).unwrap();
        assert_eq!(emc, 2f32);

        let tooltip = "a\nb\nEMC: 2,048 (✗)\nc\nd";
        let emc = get_emc_from_tooltip(&number_words, tooltip).unwrap();
        assert_eq!(emc, 2048f32);

        let tooltip = "a\nb\nEMC: 1.61 Billion (✗)\nc\nd";
        let emc = get_emc_from_tooltip(&number_words, tooltip).unwrap();
        assert_eq!(emc, 1_610_000_000f32);
    }
}
