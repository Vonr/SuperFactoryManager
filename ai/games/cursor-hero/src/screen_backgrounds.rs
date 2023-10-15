use bevy::prelude::*;
use bevy::utils::synccell::SyncCell;
use captrs::Capturer;
use image::{DynamicImage, ImageBuffer};
use rayon::prelude::*;
use screenshots::Screen as ScreenLib;
use std::{collections::HashMap, sync::Arc};

pub struct ScreenBackgroundsPlugin;

impl Plugin for ScreenBackgroundsPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(Startup, spawn_screens)
            .add_systems(Update, (update_screens, cycle_capture_method))
            .insert_non_send_resource(CapturerResource {
                capturers: HashMap::new(),
            });
    }
}

pub struct CapturerResource {
    pub capturers: HashMap<u32, Capturer>,
}

#[derive(Debug, Clone, Copy, Default, Reflect)]
pub enum CaptureMethod {
    #[default]
    Screen,
    Captrs,
}

#[derive(Component, Default, Reflect)]
#[reflect(Component)]
pub struct Screen {
    id: u32,
    refresh_rate: Timer,
    capture_method: CaptureMethod,
}

#[derive(Component)]
pub struct ScreenParent;

fn spawn_screens(
    mut commands: Commands,
    mut textures: ResMut<Assets<Image>>,
    mut capturer_resource: NonSendMut<CapturerResource>,
) {
    commands.spawn((
        SpatialBundle::default(),
        ScreenParent,
        Name::new("Screen Parent"),
    ));

    // create a Screen component for each screen
    let mut index = 0;
    for screen in ScreenLib::all().unwrap().iter() {
        let image_buf = screen.capture().unwrap();
        let dynamic_image = DynamicImage::ImageRgba8(image_buf);
        let image = Image::from_dynamic(dynamic_image, true);
        let texture = textures.add(image);

        // // Assuming the index aligns with the capturer's expected screen index
        let capturer = Capturer::new(index)
            .expect(format!("Failed to create capturer for screen {}", index).as_str());
        capturer_resource
            .capturers
            .insert(screen.display_info.id, capturer);
        index += 1;

        commands.spawn((
            SpriteBundle {
                texture,
                transform: Transform::from_xyz(
                    screen.display_info.x as f32,
                    screen.display_info.y as f32,
                    -1.0,
                ), // Position behind the character
                ..Default::default()
            },
            Screen {
                id: screen.display_info.id,
                refresh_rate: Timer::from_seconds(1.0, TimerMode::Repeating),
                capture_method: CaptureMethod::Screen,
            },
            Name::new(format!("Screen {}", screen.display_info.id)),
        ));
    }
}

fn update_screens(
    mut query: Query<(&mut Screen, &Handle<Image>)>,
    mut textures: ResMut<Assets<Image>>,
    time: Res<Time>,
    mut capturer_resource: NonSendMut<CapturerResource>,
) {
    // Cache the screens
    let all_screens = ScreenLib::all().unwrap();

    // Filter and collect the screens you're interested in, you can parallelize this part
    let relevant_screens: Vec<_> = all_screens
        .par_iter()
        .filter(|&libscreen| {
            query
                .iter()
                .any(|(screen, _)| libscreen.display_info.id == screen.id)
        })
        .collect();

    for (mut screen, texture) in &mut query {
        screen.refresh_rate.tick(time.delta());
        if screen.refresh_rate.finished() {
            // Only consider the screens that were filtered before
            for libscreen in relevant_screens.iter() {
                if libscreen.display_info.id == screen.id {
                    match screen.capture_method {
                        CaptureMethod::Screen => {
                            let image_buf = libscreen.capture().unwrap();
                            let dynamic_image = DynamicImage::ImageRgba8(image_buf);
                            let image = Image::from_dynamic(dynamic_image, true);

                            textures.get_mut(&texture).unwrap().data = image.data;
                        }
                        CaptureMethod::Captrs => {
                            let start = std::time::Instant::now();
                            let mut capturer =
                                capturer_resource.capturers.get_mut(&screen.id).expect(
                                    format!("captrs capturer not found for screen {}", screen.id)
                                        .as_str(),
                                );
                            let (width, height) = capturer.geometry();
                            let image_buf = capturer.capture_frame().unwrap();
                            let image_buf = ImageBuffer::from_fn(width, height, |x, y| {
                                let pixel = image_buf[(y * width + x) as usize];
                                image::Rgba([pixel.b, pixel.g, pixel.r, pixel.a])
                            });
                            println!("capture took {:?}", start.elapsed());
                            let dynamic_image = DynamicImage::ImageRgba8(image_buf);

                            let image = Image::from_dynamic(dynamic_image, true);

                            textures.get_mut(&texture).unwrap().data = image.data;
                        }
                    }
                }
            }
        }
    }
}

fn cycle_capture_method(mut query: Query<&mut Screen>, keyboard_input: Res<Input<KeyCode>>) {
    if keyboard_input.just_pressed(KeyCode::M) {
        for mut screen in query.iter_mut() {
            screen.capture_method = match screen.capture_method {
                CaptureMethod::Screen => {
                    println!("Switched to Captrs method");
                    CaptureMethod::Captrs
                }
                CaptureMethod::Captrs => {
                    println!("Switched to Screen method");
                    CaptureMethod::Screen
                }
            };
        }
    }
}
