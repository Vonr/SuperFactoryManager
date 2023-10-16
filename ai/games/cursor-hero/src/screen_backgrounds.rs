use bevy::prelude::*;
use captrs::Capturer;
use dashmap::DashMap;
use image::{DynamicImage, ImageBuffer, Rgba};
use rayon::prelude::*;
use screenshots::Screen as ScreenLib;
use std::{
    collections::{HashMap, VecDeque},
    sync::{atomic::{AtomicPtr, Ordering}, Arc},
};

pub struct ScreenBackgroundsPlugin;
use crate::windows_screen_capturing::{
    get_all_monitors, get_full_monitor_capturers, MonitorRegionCapturer,
};

impl Plugin for ScreenBackgroundsPlugin {
    fn build(&self, app: &mut App) {
        let latest_frames: Arc<DashMap<String, AtomicPtr<ImageBuffer<Rgba<u8>, Vec<u8>>>>> =
            Arc::new(DashMap::new());

        // Spawn a new background thread for capturing
        let latest_frames_clone = latest_frames.clone();
        std::thread::spawn(move || {
            loop {
                
            let mut capturers = match get_full_monitor_capturers() {
                Ok(capturers) => capturers,
                Err(e) => panic!("Failed to get capturers: {}", e),
            };
                for capturer in capturers.iter_mut() {
                    let frame = capturer.capture().unwrap();
                    let frame_ptr = Box::into_raw(Box::new(frame));

                    // Update the latest frame
                    latest_frames_clone.insert(
                        capturer.monitor.info.name.clone(),
                        AtomicPtr::new(frame_ptr),
                    );
                }
            }
        });

        app.add_systems(Startup, spawn_screens)
            .add_systems(Update, (update_screens, cycle_capture_method))
            .insert_non_send_resource(CapturerResource {
                captrs_capturers: HashMap::new(),
                inhouse_capturers: get_full_monitor_capturers().unwrap(),
                latest_from_other_thread: latest_frames,
            });
    }
}

pub struct CapturerResource {
    pub captrs_capturers: HashMap<u32, Capturer>,
    pub inhouse_capturers: Vec<MonitorRegionCapturer>,
    pub latest_from_other_thread: Arc<DashMap<String, AtomicPtr<ImageBuffer<Rgba<u8>, Vec<u8>>>>>,
}

#[derive(Debug, Clone, Copy, Default, Reflect)]
pub enum CaptureMethod {
    Screen,
    Captrs,
    #[default]
    Inhouse,
    Multithreaded,
}

#[derive(Component, Default, Reflect)]
#[reflect(Component)]
pub struct Screen {
    id: u32,
    name: String,
    refresh_rate: Timer,
    capture_method: CaptureMethod,
}

#[derive(Component)]
pub struct ScreenParent;

fn spawn_screens(
    mut commands: Commands,
    mut textures: ResMut<Assets<Image>>,
    // mut capturer_resource: NonSendMut<CapturerResource>,
) {
    commands.spawn((
        SpatialBundle::default(),
        ScreenParent,
        Name::new("Screen Parent"),
    ));

    // create a Screen component for each screen
    let mut screen_names = get_all_monitors()
        .unwrap()
        .iter()
        .map(|monitor| monitor.info.name.clone())
        .collect::<VecDeque<String>>();
    for screen in ScreenLib::all().unwrap().iter() {
        let image_buf = screen.capture().unwrap();
        let dynamic_image = DynamicImage::ImageRgba8(image_buf);
        let image = Image::from_dynamic(dynamic_image, true);
        let texture = textures.add(image);

        // // Assuming the index aligns with the capturer's expected screen index
        // let capturer = Capturer::new_with_timeout(index, Duration::from_millis(2000))
        // println!("Creating capturer for screen {}", index);
        // let capturer = Capturer::new(index)
        //     .expect(format!("Failed to create capturer for screen {}", index).as_str());
        let name = screen_names.pop_front().unwrap();

        // capturer_resource
        //     .capturers
        //     .insert(screen.display_info.id, capturer);

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
                name,
                id: screen.display_info.id,
                refresh_rate: Timer::from_seconds(1.0, TimerMode::Repeating),
                capture_method: default(),
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
                            let start = std::time::Instant::now();
                            let image_buf = libscreen.capture().unwrap();
                            println!(" | total screen update took {:?}", start.elapsed());

                            let dynamic_image = DynamicImage::ImageRgba8(image_buf);
                            let image = Image::from_dynamic(dynamic_image, true);
                            textures.get_mut(&texture).unwrap().data = image.data;
                        }
                        CaptureMethod::Captrs => {
                            let start = std::time::Instant::now();
                            let capturer = capturer_resource
                                .captrs_capturers
                                .get_mut(&screen.id)
                                .expect(
                                    format!("captrs capturer not found for screen {}", screen.id)
                                        .as_str(),
                                );
                            let (width, height) = capturer.geometry();
                            let image_buf = capturer.capture_frame().unwrap();
                            let image_buf = ImageBuffer::from_fn(width, height, |x, y| {
                                let pixel = image_buf[(y * width + x) as usize];
                                image::Rgba([pixel.b, pixel.g, pixel.r, pixel.a])
                            });
                            println!(" | total screen update took {:?}", start.elapsed());
                            let dynamic_image = DynamicImage::ImageRgba8(image_buf);

                            let image = Image::from_dynamic(dynamic_image, true);

                            textures.get_mut(&texture).unwrap().data = image.data;
                        }
                        CaptureMethod::Inhouse => {
                            let start = std::time::Instant::now();
                            let capturer = capturer_resource
                                .inhouse_capturers
                                .iter_mut()
                                .find(|capturer| capturer.monitor.info.name == screen.name)
                                .expect(
                                    format!("inhouse capturer not found for screen {}", screen.id)
                                        .as_str(),
                                );
                            let frame = capturer.capture().unwrap();
                            println!(" | total screen update took {:?}", start.elapsed());

                            // let dynamic_image = DynamicImage::ImageRgba8(frame);
                            // let image = Image::from_dynamic(dynamic_image, true);
                            // textures.get_mut(&texture).unwrap().data = image.data;
                            textures.get_mut(&texture).unwrap().data = frame.to_vec();
                        }
                        CaptureMethod::Multithreaded => {
                            // let start = std::time::Instant::now();
                            // let frame = capturer_resource
                            //     .latest_from_other_thread
                            //     .get(&screen.name)
                            //     .unwrap()
                            //     .value();
                            // // let frame: Box<ImageBuffer<Rgba<u8>, Vec<u8>>
                            // println!(" | total screen update took {:?}", start.elapsed());
                            // // textures.get_mut(&texture).unwrap().data = frame.to_vec();

                            let start = std::time::Instant::now();
                            if let Some(value_ref) =
                                capturer_resource.latest_from_other_thread.get(&screen.name)
                            {
                                let atomic_ptr = value_ref.value();

                                // Safely load the pointer
                                let frame_ptr = atomic_ptr.load(Ordering::Relaxed); // Use proper ordering
                                let frame_box = unsafe { Box::from_raw(frame_ptr) };
                                let frame: &ImageBuffer<Rgba<u8>, Vec<u8>> = &(*frame_box); // Deref to get to ImageBuffer

                                // Do something with frame, for example:
                                textures.get_mut(&texture).unwrap().data = frame.to_vec();

                                // Don't forget to forget the box to avoid deallocation
                                std::mem::forget(frame_box);
                            }
                            println!(" | total screen update took {:?}", start.elapsed());
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
                    // println!("Switched to Captrs method");
                    // CaptureMethod::Captrs

                    println!("Switched to Inhouse method");
                    CaptureMethod::Inhouse
                }
                CaptureMethod::Captrs => {
                    println!("Switched to Inhouse method");
                    CaptureMethod::Inhouse
                }
                CaptureMethod::Inhouse => {
                    println!("Switched to Multithreaded method");
                    CaptureMethod::Multithreaded
                }
                CaptureMethod::Multithreaded => {
                    println!("Switched to Screen method");
                    CaptureMethod::Screen
                }
            };
        }
    }
}
