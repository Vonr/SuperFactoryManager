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
        .par_bridge()
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
#[derive(Debug, Clone)]
struct HistoryEntry {
    recipe_id: String,
    recipe_category: String,
    ingredients: Vec<String>,
    resulting_emc: f32,
}
fn find_emc_cycles(start_item_id: &str, recipes: &[Recipe], items: &[Item]) {
    // Create a HashMap for quick lookup of item EMC values
    let item_map: HashMap<String, f32> = items.iter()
        .filter_map(|item| item.emc.map(|emc| (item.id.clone(), emc)))
        .collect();

    // Filter recipes that contain the starting item as an input and have no more than 9 ingredients
    let relevant_recipes: Vec<&Recipe> = recipes.iter()
        .filter(|recipe| {
            recipe.ingredients.iter().any(|ingredient| ingredient.ingredient_id == start_item_id && ingredient.role == "INPUT") &&
            recipe.ingredients.len() <= 9
        })
        .collect();

    // Depth limit
    let max_depth = 1;

    // Iterate over each relevant recipe as a starting point
    for recipe in relevant_recipes {
        // Calculate the starting EMC as the sum of the EMC of all ingredients in the recipe
        let start_emc: f32 = recipe.ingredients.iter()
            .filter_map(|ingredient| {
                item_map.get(&ingredient.ingredient_id)
                    .map(|&emc| emc * ingredient.ingredient_amount as f32)
            })
            .sum();

        let initial_buffer = recipe.ingredients.iter().map(|ing| ing.ingredient_id.clone()).collect::<Vec<String>>();
        let visited_recipes = Vec::new();
        let history = Vec::new();

        println!("Starting with ingredients:");
        for ingredient in &recipe.ingredients {
            let quantity = ingredient.ingredient_amount;
            let emc = item_map.get(&ingredient.ingredient_id).copied().unwrap_or(0.0);
            let total_emc = emc * quantity as f32;
            println!("  {}x {} ({} EMC, {} total EMC)", quantity, ingredient.ingredient_id, emc, total_emc);
        }
        println!("Initial EMC: {}\n", start_emc);

        // Start the cycle detection from this recipe with depth limit
        traverse_recipes(
            start_emc,
            initial_buffer,
            visited_recipes,
            history,
            recipes,
            items,
            &item_map, // Pass the item_map to the recursive function
            0,         // Start with depth 0
            max_depth, // Set maximum depth to 1
        );
    }
}
fn traverse_recipes(
    _current_emc: f32,
    buffer: Vec<String>,               // Pass by value
    visited_recipes: Vec<String>,      // Pass by value
    _history: Vec<HistoryEntry>,       // History tracking removed
    recipes: &[Recipe],
    _items: &[Item],
    item_map: &HashMap<String, f32>, // Use a HashMap for faster item lookup
    depth: usize,                     // Add depth parameter
    max_depth: usize,                  // Add max_depth parameter
) -> Vec<HistoryEntry> {
    if depth >= max_depth {
        return Vec::new(); // Return empty if the max depth is reached
    }

    recipes.iter().flat_map(|recipe| { // Use flat_map instead of map
        let mut local_buffer = buffer.clone();
        let mut local_visited_recipes = visited_recipes.clone();

        // Skip recipes we've already visited
        if local_visited_recipes.contains(&recipe.recipe_object) {
            return Vec::new(); // Return empty vector if the recipe was already visited
        }

        // Check if this recipe uses any item in the buffer as an input
        let input_items: Vec<&Ingredient> = recipe
            .ingredients
            .iter()
            .filter(|ingredient| ingredient.role == "INPUT" && local_buffer.contains(&ingredient.ingredient_id))
            .collect();

        if !input_items.is_empty() {
            local_visited_recipes.push(recipe.recipe_object.clone());

            // Ensure we have enough items in the buffer to satisfy the recipe
            let mut buffer_copy = local_buffer.clone();
            let enough_items = input_items.iter().all(|ingredient| {
                let count = buffer_copy.iter().filter(|id| *id == &ingredient.ingredient_id).count();
                if count >= ingredient.ingredient_amount as usize {
                    for _ in 0..ingredient.ingredient_amount {
                        if let Some(index) = buffer_copy.iter().position(|id| id == &ingredient.ingredient_id) {
                            buffer_copy.remove(index);
                        }
                    }
                    true
                } else {
                    false
                }
            });

            if !enough_items {
                return Vec::new(); // Return empty vector if not enough items
            }

            // Calculate the total EMC of inputs
            let total_input_emc: f32 = input_items.iter().map(|ingredient| {
                item_map.get(&ingredient.ingredient_id)
                    .map(|&emc| emc * ingredient.ingredient_amount as f32)
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
                if let Some(&emc) = item_map.get(&output.ingredient_id) {
                    total_output_emc += emc * output.ingredient_amount as f32;
                    local_buffer.push(output.ingredient_id.clone());
                }
            }

            // Trim recipe name and get the last part after `.`
            let trimmed_recipe_name = recipe.recipe_object.rsplit('.').next().unwrap_or(&recipe.recipe_object);

            // Output the current step in a formatted manner
            println!("Step {}: Applying [{}] recipe: {}", depth + 1, recipe.category_title, trimmed_recipe_name);
            println!("  Inputs:");
            for ingredient in &input_items {
                let quantity = ingredient.ingredient_amount;
                let emc = item_map.get(&ingredient.ingredient_id).copied().unwrap_or(0.0);
                let total_emc = emc * quantity as f32;
                println!("    {}x {} ({} EMC, {} total EMC)", quantity, ingredient.ingredient_id, emc, total_emc);
            }
            println!("  Outputs:");
            for output in &output_items {
                let quantity = output.ingredient_amount;
                let emc = item_map.get(&output.ingredient_id).copied().unwrap_or(0.0);
                let total_emc = emc * quantity as f32;
                println!("    {}x {} ({} EMC, {} total EMC)", quantity, output.ingredient_id, emc, total_emc);
            }
            println!("  Buffer now contains: {:?}", local_buffer);
            println!("  Total EMC: {}", total_output_emc);
            println!();

            // Stop recursion if depth is reached
            if depth + 1 < max_depth {
                return traverse_recipes(total_output_emc, local_buffer, local_visited_recipes, Vec::new(), recipes, &[], item_map, depth + 1, max_depth);
            }
        }

        Vec::<HistoryEntry>::new() // Return empty vector if no valid operation
    }).collect()
}


fn main() {
    let items_json = include_str!("../../items.json");
    let mut items: Vec<Item> = serde_json::from_str(items_json).unwrap();
    calculate_emc(&mut items);

    println!("Loading recipes...");
    eprintln!("Loading recipes...");
    let recipes = load_recipes();
    println!("Loaded {} recipes", recipes.len());
    eprintln!("Loaded {} recipes", recipes.len());
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
