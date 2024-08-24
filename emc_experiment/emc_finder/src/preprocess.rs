use rayon::prelude::*;
use serde::Deserialize;
use serde::Serialize;
use std::collections::HashMap;
use std::collections::HashSet;
use std::collections::VecDeque;
use std::fs::File;
use std::io::Read;
use std::io::Write;
use std::path::Path;
use std::path::PathBuf;
use std::str::FromStr;

#[derive(Debug, Deserialize, Serialize, Clone, PartialEq)]
pub struct DerivedEmc {
    pub emc: f32,
    pub path: Vec<String>,
}

#[derive(Debug, Deserialize, Serialize, Clone)]
#[allow(dead_code)]
pub struct Item {
    pub id: String,
    pub data: Option<String>,
    pub tags: Vec<String>,
    pub tooltip: String,
    pub emc: Option<f32>,
    pub derived_emc: Option<DerivedEmc>,
}

#[derive(Debug, Deserialize, Serialize, Clone)]
#[allow(dead_code)]
pub struct Ingredient {
    pub role: String,
    #[serde(rename = "ingredientType")]
    pub ingredient_type: String,
    #[serde(rename = "ingredientAmount")]
    pub ingredient_amount: i32,
    #[serde(rename = "ingredientId")]
    pub ingredient_id: String,
    pub tags: Vec<String>,
    pub ingredient: String,
    pub emc: Option<f32>,
    pub derived_emc: Option<DerivedEmc>,
}

#[derive(Debug, Deserialize)]
#[allow(dead_code)]
pub struct Recipe {
    pub category: String,
    #[serde(rename = "categoryTitle")]
    pub category_title: String,
    #[serde(rename = "recipeTypeId")]
    pub recipe_type_id: String,
    #[serde(rename = "recipeClass")]
    pub recipe_class: String,
    #[serde(rename = "recipeObject")]
    pub recipe_object: String,
    pub ingredients: Vec<Ingredient>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ProcessedRecipe {
    pub recipe_id: String,
    pub category_title: String,
    pub inputs: Vec<Ingredient>,
    pub outputs: Vec<Ingredient>,
    pub base_input_emc: f32,
    pub base_output_emc: f32,
    pub has_non_emc_ingredient: bool,
    pub has_non_emc_output: bool,
}
impl ProcessedRecipe {
    pub fn get_input_emc(&self) -> f32 {
        self.inputs
            .iter()
            .map(|ing| {
                ing.emc
                    .or(ing.derived_emc.as_ref().map(|de| de.emc))
                    .unwrap_or(0.0)
            })
            .sum()
    }
    pub fn get_output_emc(&self) -> f32 {
        self.outputs
            .iter()
            .map(|ing| {
                ing.emc
                    .or(ing.derived_emc.as_ref().map(|de| de.emc))
                    .unwrap_or(0.0)
            })
            .sum()
    }
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ProcessedData {
    pub items: Vec<Item>,
    pub recipes: Vec<ProcessedRecipe>,
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

fn load_recipes(jei_folder: &str) -> Vec<Recipe> {
    let jei_folder = PathBuf::from(jei_folder);

    jei_folder
        .read_dir()
        .unwrap()
        .par_bridge()
        .filter_map(|entry| {
            let path = entry.ok()?.path();
            println!("Loading recipes from {:?}", path);
            std::fs::read_to_string(&path)
                .ok()
                .and_then(|content| serde_json::from_str::<Vec<Recipe>>(&content).ok())
        })
        .flatten()
        .collect()
}

fn process_recipes(recipes: &[Recipe], item_map: &HashMap<String, f32>) -> Vec<ProcessedRecipe> {
    let disallowed_recipe_types = ["jei:information", "ftbquests:quest", "minecraft:anvil"]
        .into_iter()
        .map(|s| s.to_owned())
        .collect::<HashSet<String>>();
    recipes
        .par_iter()
        .filter_map(|recipe| {
            if disallowed_recipe_types.contains(&recipe.recipe_type_id) {
                return None;
            }

            let mut has_non_emc_ingredient = false;

            let inputs: Vec<Ingredient> = recipe
                .ingredients
                .iter()
                .filter(|ingredient| ingredient.role == "INPUT")
                .map(|ingredient| {
                    let emc = item_map.get(&ingredient.ingredient_id).copied();
                    if emc.is_none() {
                        has_non_emc_ingredient = true;
                    }
                    Ingredient {
                        emc,
                        ..ingredient.clone()
                    }
                })
                .collect();

            if inputs.len() > 9 {
                return None;
            }

            let mut has_non_emc_output = false;

            let outputs: Vec<Ingredient> = recipe
                .ingredients
                .iter()
                .filter(|ingredient| ingredient.role == "OUTPUT")
                .map(|ingredient| {
                    let emc = item_map.get(&ingredient.ingredient_id).copied();
                    if emc.is_none() {
                        has_non_emc_output = true;
                    }
                    Ingredient {
                        emc,
                        ..ingredient.clone()
                    }
                })
                .collect();

            let total_input_emc: f32 = inputs
                .iter()
                .filter_map(|ing| ing.emc.map(|emc| emc * ing.ingredient_amount as f32))
                .sum();
            let total_output_emc: f32 = outputs
                .iter()
                .filter_map(|ing| ing.emc.map(|emc| emc * ing.ingredient_amount as f32))
                .sum();

            if total_output_emc <= total_input_emc && !has_non_emc_ingredient && !has_non_emc_output
            {
                return None;
            }

            Some(ProcessedRecipe {
                recipe_id: recipe.recipe_object.clone(),
                category_title: recipe.category_title.clone(),
                inputs,
                outputs,
                base_input_emc: total_input_emc,
                base_output_emc: total_output_emc,
                has_non_emc_ingredient,
                has_non_emc_output,
            })
        })
        .collect()
}

fn save_processed_data(data: &ProcessedData, path: &Path) {
    let encoded: Vec<u8> = bincode::serialize(data).unwrap();
    let mut file = File::create(path).unwrap();
    file.write_all(&encoded).unwrap();
}

fn load_processed_data(path: &Path) -> ProcessedData {
    let mut file = File::open(path).unwrap();
    let mut buffer = Vec::new();
    file.read_to_end(&mut buffer).unwrap();
    bincode::deserialize(&buffer).unwrap()
}

pub fn get_data() -> ProcessedData {
    let start = std::time::Instant::now();
    let processed_file = PathBuf::from("processed.bin");
    let rtn = if processed_file.exists() {
        println!("Loading processed data from {:?}", processed_file);
        load_processed_data(&processed_file)
    } else {
        println!("Performing first time data processing");
        let items_json = include_str!("../../items.json");

        let mut items: Vec<Item> = serde_json::from_str(items_json).unwrap();
        calculate_emc(&mut items);

        let item_map: HashMap<String, f32> = items
            .iter()
            .filter_map(|item| item.emc.map(|emc| (item.id.clone(), emc)))
            .collect();

        let raw_recipes = load_recipes("../jei");
        let processed_recipes = process_recipes(&raw_recipes, &item_map);
        println!(
            "Reduced recipes from {} to {}",
            raw_recipes.len(),
            processed_recipes.len()
        );
        let rtn = ProcessedData {
            items,
            recipes: processed_recipes,
        };
        save_processed_data(&rtn, &processed_file);
        rtn
    };
    assert!(rtn.items.len() > 1_000);
    assert!(rtn.recipes.len() > 10_000);
    println!(
        "Prepared data in {:?}, got {} items and {} recipes",
        start.elapsed(),
        rtn.items.len(),
        rtn.recipes.len()
    );
    rtn
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_get_emc_from_tooltip() {
        let number_words = get_number_words();
        let tooltip = "EMC: 1,000,000 (✗)";
        let emc = get_emc_from_tooltip(&number_words, tooltip);
        assert_eq!(emc, Some(1_000_000.0));
    }

    #[test]
    fn test_get_emc_from_tooltip_with_words() {
        let number_words = get_number_words();
        let tooltip = "EMC: 1 Million (✗)";
        let emc = get_emc_from_tooltip(&number_words, tooltip);
        assert_eq!(emc, Some(1_000_000.0));
    }

    #[test]
    fn test_get_emc_from_tooltip_with_words_and_commas() {
        let number_words = get_number_words();
        let tooltip = "EMC: 1 Million (✗)";
        let emc = get_emc_from_tooltip(&number_words, tooltip);
        assert_eq!(emc, Some(1_000_000.0));
    }

    #[test]
    fn test_get_emc_from_tooltip_with_words_and_commas_and_spaces() {
        let number_words = get_number_words();
        let tooltip = "EMC: 1 Million (✗)";
        let emc = get_emc_from_tooltip(&number_words, tooltip);
        assert_eq!(emc, Some(1_000_000.0));
    }

    #[test]
    fn test_get_emc_from_tooltip_with_words_and_commas_and_spaces_and_newlines() {
        let number_words = get_number_words();
        let tooltip = "EMC: 1 Million (✗)\n";
        let emc = get_emc_from_tooltip(&number_words, tooltip);
        assert_eq!(emc, Some(1_000_000.0));
    }

    #[test]
    fn test_get_emc_from_tooltip_with_words_and_commas_and_spaces_and_newlines_and_extra() {
        let number_words = get_number_words();
        let tooltip = "EMC: 1 Million (✗)\nExtra";
        let emc = get_emc_from_tooltip(&number_words, tooltip);
        assert_eq!(emc, Some(1_000_000.0));
    }

    #[test]
    fn process() {
        let data = get_data();
        assert!(data.recipes.len() > 10_000);
        assert!(data.items.len() > 1_000);
    }
}
