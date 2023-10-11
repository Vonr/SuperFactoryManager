use bevy::prelude::*;
use image::DynamicImage;
use screenshots::Screen as ScreenLib;

pub struct ScreenBackgroundsPlugin;

impl Plugin for ScreenBackgroundsPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(Startup, spawn_screens)
            .add_systems(Update, update_screens);
    }
}

#[derive(Component, Default, Reflect)]
#[reflect(Component)]
pub struct Screen {
    id: u32,
    refresh_rate: Timer,
}

#[derive(Component)]
pub struct ScreenParent;

fn spawn_screens(
    mut commands: Commands,
    mut textures: ResMut<Assets<Image>>,
) {
    commands.spawn((
        SpatialBundle::default(),
        ScreenParent,
        Name::new("Screen Parent"),
    ));

    // create a Screen component for each screen
    for screen in ScreenLib::all().unwrap().iter() {
        let image_buf = screen.capture().unwrap();
        let dynamic_image = DynamicImage::ImageRgba8(image_buf);
        let image = Image::from_dynamic(dynamic_image, true);
        let texture = textures.add(image);

        commands.spawn((
            SpriteBundle {
                texture,
                transform: Transform::from_xyz(screen.display_info.x as f32, screen.display_info.y as f32, -1.0), // Position behind the character
                ..Default::default()
            },
            Screen {
                id: screen.display_info.id,
                refresh_rate: Timer::from_seconds(1.0, TimerMode::Repeating)
            },
            Name::new(format!("Screen {}", screen.display_info.id)),
        ));
    }
}

fn update_screens(
    mut query: Query<(&mut Screen, &Handle<Image>)>,
    mut textures: ResMut<Assets<Image>>,
    time: Res<Time>,
) {
    for (mut screen, texture) in &mut query {
        screen.refresh_rate.tick(time.delta());
        if screen.refresh_rate.finished() {
            for libscreen in ScreenLib::all().unwrap().iter() {
                if libscreen.display_info.id == screen.id {
                    let image_buf = libscreen.capture().unwrap();
                    let dynamic_image = DynamicImage::ImageRgba8(image_buf);
                    let image = Image::from_dynamic(dynamic_image, true);

                    textures.get_mut(&texture).unwrap().data = image.data;
                }
            }
        }
    }
}
