```pwsh
sfm-propagate-changes.exe run game-test-server --branch 1.19.2 bisect wither_aggro_does_not_break_tough_cable_facaded_as_bedrock_wall
```

gave

```pwsh
sfm-propagate-changes.exe run game-test-server --branch 1.19.2 --filter "sfm:wither_aggro_does_not_break_tough_cable_facaded_as_bedrock_wall,sfm:mek_bin_full,sfm:mek_bin_some,sfm:mek_chemtank_infusion_empty,sfm:mek_chemtank_infusion_full,sfm:mek_chemtank_infusion_some,sfm:mek_cube,sfm:mek_energy_empty,sfm:mek_energy_full,sfm:mek_energy_some,sfm:mek_energy_ten,sfm:mek_induction,sfm:mek_many_lava_cauldrons,sfm:mek_multi_fluid,sfm:capability_discovery_mapper,sfm:fluid_tank_retain_regression,sfm:meat_fluid_direct,sfm:meat_fluid,sfm:resource_loss_regression,sfm:thermal_furnace_array,sfm:thermal_phyto_array,sfm:enchantment_collection_equality_and_hash_code,sfm:enchantment_collection_write_to_book,sfm:enchantment_collection_write_to_tool,sfm:falling_anvil_disenchant,sfm:falling_anvil_enchantment_form,sfm:falling_anvil_program_form,sfm:falling_anvil_xp_shard,sfm:falling_anvil_xp_shard_many,sfm:circle_redstone,sfm:manager_state_update,sfm:manager_swap_program,sfm:move_1_stack_direct,sfm:move_1_stack,sfm:output_default_stacks_when_no_empty_modifier,sfm:output_empty_slots_only_avoid_stacking,sfm:output_empty_slots_only_no_empty_space,sfm:output_empty_slots_only_reversed_syntax,sfm:recipes,sfm:side_resolve_direction,sfm:cable_network_formation,sfm:cable_network_rebuilding,sfm:cable_spiral,sfm:casing_rules,sfm:cauldron_lava_movement,sfm:comparison_eq,sfm:comparison_ge,sfm:comparison_gt,sfm:comparison_le,sfm:comparison_lt,sfm:conditional_output_inspection,sfm:count_execution_paths_1,sfm:count_execution_paths_2,sfm:count_execution_paths_3,sfm:count_execution_paths_conditional_1_b,sfm:count_execution_paths_conditional_1,sfm:count_execution_paths_conditional_2,sfm:disk_item_clientside_regression,sfm:disk_name,sfm:each_dest_quantity_each_retain,sfm:each_dest_quantity,sfm:each_dest_quantity_retain,sfm:each_dest_retain,sfm:each_src_quantity_each_retain,sfm:each_src_quantity,sfm:each_src_quantity_retain,sfm:each_src_retain,sfm:forget_1,sfm:forget_2,sfm:forget_input_count_state,sfm:forget_slot,sfm:gather_supplies,sfm:has_or,sfm:inv_wrapper_investigation,sfm:many_outputs,sfm:mekanism_null_io_direction,sfm:move_cauldron_lava,sfm:move_cauldron_water,sfm:move_full_chest,sfm:move_if_powered,sfm:move_many_full,sfm:move_many_inventories"
```

but the test still succeeds with that, so our bisect command is probably broken since the bisect command should emit the minimum set of tests that cause it to fail, not succeed.

Running all tests 

```pwsh
sfm-propagate-changes.exe run game-test-server --branch 1.19.2
```

gives

```log
[1.19.2 mc] [23:40:48] [Server thread/ERROR] [minecraft/LogTestReporter]: wither_aggro_does_not_break_tough_cable_facaded_as_bedrock_wall failed! Scenario 'wither_aggro_does_not_break_tough_cable_facaded_as_bedrock_wall' expected sheep to remain alive
[1.19.2 mc] [23:40:52] [Server thread/INFO] [minecraft/GameTestServer]: ========= 219 GAME TESTS COMPLETE ======================
[1.19.2 mc] [23:40:52] [Server thread/INFO] [minecraft/GameTestServer]: 1 required tests failed :(
[1.19.2 mc] [23:40:52] [Server thread/INFO] [minecraft/GameTestServer]:    - wither_aggro_does_not_break_tough_cable_facaded_as_bedrock_wall

[1.19.2 rust ERROR] target_failed
  error=runGameTestServer exited with exit code: 1. See D:/Repos/Minecraft/SFM/repos2/1.19.2\platform\minecraft\build\sfm-toolchain\run\runGameTestServer\console.log
[1.19.2 rust] runGameTestServer target summary: 0/1 succeeded, 1 failed.
[1.19.2 rust] runGameTestServer target report:
[1.19.2 rust] branch  status       time  warnings  errors  message
[1.19.2 rust] 1.19.2  failed      4m48s         2       1  runGameTestServer exited with exit code: 1. See D:/Repos/Minecraft/SFM/repos2/1.19.2\platform\minecraft\build\sfm-toolchain\run\runGameTestServer\console.log
[1.19.2 rust] ------------------------------------------------------------------------------------------------
[1.19.2 rust] Failed target 1.19.2 (D:/Repos/Minecraft/SFM/repos2/1.19.2): runGameTestServer exited with exit code: 1. See D:/Repos/Minecraft/SFM/repos2/1.19.2\platform\minecraft\build\sfm-toolchain\run\runGameTestServer\console.log
Error: 
   0: runGameTestServer failed for 1 of 1 target(s).

Location:
   src\jar_build\engine.rs:624

  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ SPANTRACE ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

   0: sfm_propagate_changes::jar_build::engine::invoke_run with branch=1.19.2 kind="runGameTestServer" refresh=false explain_rebuild=false dry_run=false allow_local_artifact_cache=false require_portable_artifacts=false error_action=bail parallelism=sequential
      at src\jar_build\engine.rs:138

Backtrace omitted. Run with RUST_BACKTRACE=1 environment variable to display it.
Run with RUST_BACKTRACE=full to include source snippets.
```

So, there is `expected sheep to remain alive`.

For some reason, when running all tests, the sheep dies when it should not be dead.

Running all the `*wither*` game tests does not reproduce the failure.

Running all the tests with `sfm-propagate-changes.exe run client-puppet --branch 1.19.2` does not reproduce the issue, it's only the headless game-test-server that has issues.

Running the `runGameTestServer` run configuration in IntelliJ does reproduce the issue.

Troubleshooting next steps include:
- updating the bisect command so it properly emits the failing test subset
   - note the previous bisect run took 7h10m so if we can resume from the above or something that would help
- add additional diagnostic hooks to inspect what is causing the death of our sheep