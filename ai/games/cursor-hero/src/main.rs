use bevy::prelude::*;
use image::DynamicImage;
use screenshots::Screen;

#[derive(Component)]
pub struct Background {

}

fn main() {
    App::new()
        .add_plugins(
            DefaultPlugins
                .set(ImagePlugin::default_nearest())
                .set(WindowPlugin {
                    primary_window: Some(Window {
                        title: "Cursor Hero".into(),
                        resolution: (640.0, 480.0).into(),
                        resizable: false,
                        ..default()
                    }),
                    ..default()
                })
                .build(),
        )
        .add_systems(Startup, setup)
        .add_systems(Update, character_movement)
        .run();
}

fn setup(
    mut commands: Commands,
    asset_server: Res<AssetServer>,
    mut textures: ResMut<Assets<Image>>,
) {
    commands.spawn(Camera2dBundle::default());

    let texture = asset_server.load("character.png");

    commands.spawn(SpriteBundle {
        sprite: Sprite {
            custom_size: Some(Vec2::new(100.0, 100.0)),
            ..default()
        },
        texture,
        ..default()
    });

    // Capture the screen
    let screen = Screen::all().unwrap()[0];
    let image_buf = screen.capture().unwrap();
    let dynamic_image = DynamicImage::ImageRgba8(image_buf);
    // Create an image using Bevy's Image type
    let image = Image::from_dynamic(dynamic_image, true);
    let texture = textures.add(image);

    // Create a sprite with the background texture
    commands.spawn((
        SpriteBundle {
            texture,
            transform: Transform::from_xyz(0.0, 0.0, -1.0), // Position behind the character
            ..Default::default()
        },
        Background {},
    ));
}

fn background_refresh(
    
)

fn character_movement(
    mut characters: Query<(&mut Transform, &Sprite)>,
    input: Res<Input<KeyCode>>,
    time: Res<Time>,
) {
    for (mut transform, _) in &mut characters {
        if input.pressed(KeyCode::W) {
            transform.translation.y += 100.0 * time.delta_seconds();
        }
        if input.pressed(KeyCode::S) {
            transform.translation.y -= 100.0 * time.delta_seconds();
        }
        if input.pressed(KeyCode::D) {
            transform.translation.x += 100.0 * time.delta_seconds();
        }
        if input.pressed(KeyCode::A) {
            transform.translation.x -= 100.0 * time.delta_seconds();
        }
    }
}
