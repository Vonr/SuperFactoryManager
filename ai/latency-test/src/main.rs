use std::fs;
use std::io::{self, Write, Seek};
use std::path::PathBuf;
use std::thread;
use std::time::{Duration, Instant};

use structopt::StructOpt;

#[derive(Debug, StructOpt)]
#[structopt(name = "sine-wave")]
struct Opt {
    #[structopt(parse(from_os_str))]
    path: PathBuf,

    #[structopt(short, long, default_value = "5")]
    fps: u64,

    #[structopt(short = "r", long, default_value = "20")]
    rows: usize,

    #[structopt(short = "l", long, default_value = "80")]
    columns: usize,

    #[structopt(short = "s", long, default_value = "🚀")]
    character: String,

    #[structopt(short = "p", long, default_value = "1")]
    repeats: usize,
}

fn main() -> io::Result<()> {
    let opt = Opt::from_args();
    let mut file = fs::File::create(&opt.path)?;

    let dt = 1.0 / opt.fps as f64;
    let mut offset = 0.0;

    loop {
        let start_time = Instant::now();

        // Clear the file for the new frame
        file.seek(std::io::SeekFrom::Start(0))?;
        file.set_len(0)?;

        for y in (0..opt.rows).rev() {
            let mut row = vec!['.'; opt.columns];
            for x in 0..opt.columns {
                let angle = 2.0 * std::f64::consts::PI * (x as f64 + offset) / opt.columns as f64 * opt.repeats as f64;
                let sine_val = angle.sin();
                let graph_y = ((sine_val + 1.0) / 2.0 * (opt.rows as f64 - 1.0)).round() as usize;

                if graph_y == y {
                    row[x] = opt.character.chars().next().unwrap_or('#');
                }
            }
            let row_str: String = row.into_iter().collect();
            writeln!(file, "{}", row_str)?;
        }

        let elapsed_time = start_time.elapsed();
        if elapsed_time < Duration::from_secs_f64(dt) {
            thread::sleep(Duration::from_secs_f64(dt) - elapsed_time);
        }

        offset += 1.0;
    }
}
