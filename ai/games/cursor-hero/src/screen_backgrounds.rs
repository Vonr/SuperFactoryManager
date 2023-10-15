use bevy::prelude::*;
use captrs::Capturer;
use std::collections::HashMap;
use image::DynamicImage;
use screenshots::Screen as ScreenLib;
use rayon::prelude::*;

pub struct ScreenBackgroundsPlugin;

impl Plugin for ScreenBackgroundsPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(Startup, spawn_screens)
            .add_systems(Update, update_screens)
            .insert_resource(CapturerResource {
                capturers: HashMap::new(),
            });
    }
}

#[derive(Resource)]
pub struct CapturerResource {
    pub capturers: bevy::utils::synccell::SyncCell<HashMap<u32, Capturer>>,
}


#[derive(Debug, Clone, Copy)]
pub enum CaptureMethod {
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
    mut capturer_resource: ResMut<CapturerResource>,
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

        // Assuming the index aligns with the capturer's expected screen index
        if let Ok(capturer) = Capturer::new(index+=1) {
            capturer_resource.capturers.insert(screen.display_info.id, capturer);
        }

        commands.spawn((
            SpriteBundle {
                texture,
                transform: Transform::from_xyz(screen.display_info.x as f32, screen.display_info.y as f32, -1.0), // Position behind the character
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
    capturer_resource: Res<CapturerResource>,
) {
    // Cache the screens
    let all_screens = ScreenLib::all().unwrap();

    // Filter and collect the screens you're interested in, you can parallelize this part
    let relevant_screens: Vec<_> = all_screens
        .par_iter()
        .filter(|&libscreen| {
            query.iter().any(|(screen, _)| libscreen.display_info.id == screen.id)
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
                        },
                        CaptureMethod::Captrs => {
                            let start = std::time::Instant::now();
                            let image_buf = screen.capturer.as_mut().unwrap().capture().unwrap();
                            println!("capture took {:?}", start.elapsed());
                            let dynamic_image = DynamicImage::ImageRgba8(image_buf);
                            let image = Image::from_dynamic(dynamic_image, true);

                            textures.get_mut(&texture).unwrap().data = image.data;
                        },
                    }
                }
            }
        }
    }
}

fn cycle_capture_method(
    mut query: Query<&mut Screen>,
    keyboard_input: Res<Input<KeyCode>>,
) {
    if keyboard_input.just_pressed(KeyCode::M) {
        for mut screen in query.iter_mut() {
            screen.capture_method = match screen.capture_method {
                CaptureMethod::Screen => {
                    println!("Switched to Captrs method");
                    CaptureMethod::Captrs
                },
                CaptureMethod::Captrs => {
                    println!("Switched to Screen method");
                    CaptureMethod::Screen
                },
            };
        }
    }
}
