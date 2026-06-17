use color_eyre::owo_colors::Rgb;

pub fn stable_hue(input: &str) -> f32 {
    let mut hash = 0x811c9dc5u32; // FNV-1a
    for byte in input.bytes() {
        hash ^= u32::from(byte);
        hash = hash.wrapping_mul(0x01000193);
    }
    (hash % 360) as f32
}

pub fn stable_color(input: &str) -> Rgb {
    hsv_to_rgb(stable_hue(input), 0.72, 0.95)
}

pub fn hsv_to_rgb(hue: f32, saturation: f32, value: f32) -> Rgb {
    let hue = hue.rem_euclid(360.0);
    let c = value * saturation;
    let x = c * (1.0 - ((hue / 60.0) % 2.0 - 1.0).abs());
    let m = value - c;

    let (r, g, b) = match hue {
        h if h < 60.0 => (c, x, 0.0),
        h if h < 120.0 => (x, c, 0.0),
        h if h < 180.0 => (0.0, c, x),
        h if h < 240.0 => (0.0, x, c),
        h if h < 300.0 => (x, 0.0, c),
        _ => (c, 0.0, x),
    };

    Rgb(
        ((r + m) * 255.0).round() as u8,
        ((g + m) * 255.0).round() as u8,
        ((b + m) * 255.0).round() as u8,
    )
}
