use bevy::prelude::*;
use bevy_inspector_egui::quick::WorldInspectorPlugin;
use bevy::input::common_conditions::input_toggle_active;

mod screen_backgrounds;
use screen_backgrounds::ScreenBackgroundsPlugin;

mod cursor_character;
use cursor_character::{CursorCharacterPlugin, Character};

fn main() {
    App::new()
        .add_plugins(
            DefaultPlugins
                .set(ImagePlugin::default_nearest())
                .set(WindowPlugin {
                    primary_window: Some(Window {
                        title: "Cursor Hero".into(),
                        resolution: (640.0, 480.0).into(),
                        resizable: true,
                        ..default()
                    }),
                    ..default()
                })
                .build(),
        )
        .add_plugins(WorldInspectorPlugin::default().run_if(input_toggle_active(false, KeyCode::Grave)))
        .add_plugins((ScreenBackgroundsPlugin, CursorCharacterPlugin))
        .add_systems(Startup, setup)
        .add_systems(Update, camera_follow_tick)
        .run();
}

fn setup(
    mut commands: Commands,
) {
    commands.spawn(Camera2dBundle::default());
}


fn camera_follow_tick(
    mut set: ParamSet<(
        Query<&mut Transform, With<Camera>>,
        Query<(&mut Transform, With<Character>)>,
    )>,
) {
    let char_pos = set.p1().single().0.translation;
    let mut camera = set.p0();
    let mut camera = camera.single_mut();
    camera.translation = char_pos;
}