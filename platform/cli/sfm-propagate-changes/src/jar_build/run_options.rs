#[derive(Clone, Debug, Default)]
pub struct RunOptions {
    pub game_test_filter: Option<String>,
    pub game_test_bisect: Option<GameTestBisectOptions>,
}

#[derive(Clone, Debug)]
pub struct GameTestBisectOptions {
    pub target: String,
    pub max_runs: Option<usize>,
}
