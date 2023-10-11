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
pub struct Screen {}

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
                transform: Transform::from_xyz(0.0, 0.0, -1.0), // Position behind the character
                ..Default::default()
            },
            Screen {},
            Name::new(format!("Screen {}", screen.display_info.id)),
        ));
    }
    
    // // Capture the screen
    // let screen = ScreenLib::all().unwrap()[0];
    // let image_buf = screen.capture().unwrap();
    // let dynamic_image = DynamicImage::ImageRgba8(image_buf);
    // // Create an image using Bevy's Image type
    // let image = Image::from_dynamic(dynamic_image, true);
    // let texture = textures.add(image);

    // // Create a sprite with the background texture
    // commands.spawn((
    //     SpriteBundle {
    //         texture,
    //         transform: Transform::from_xyz(0.0, 0.0, -1.0), // Position behind the character
    //         ..Default::default()
    //     },
    //     Screen {

    //     },
    // ));
}

fn update_screens(
    mut commands: Commands,
    mut query: Query<(Entity, &mut Screen, &mut Transform), Changed<Screen>>,
    // mut parent_query: Query<&mut Transform, With<ScreenParent>>,
) {
    // for (entity, mut screen, mut transform) in query.iter_mut() {
    //     let mut parent_transform = parent_query.single_mut().unwrap();
    //     let screen_transform = Transform::from_translation(Vec3::new(screen.position.x, screen.position.y, 0.0));
    //     let screen_transform = parent_transform.compute_matrix() * screen_transform.compute_matrix();
    //     transform.translation = screen_transform.translation;
    //     transform.scale = screen_transform.scale;
    //     transform.rotation = screen_transform.rotation;
    //     commands.entity(entity).remove::<Changed<Screen>>();
    // }
}
