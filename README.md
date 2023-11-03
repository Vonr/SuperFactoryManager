# Super Factory Manager 4.0.0

[![](https://cf.way2muchnoise.eu/full_306935_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/super-factory-manager) [![Discord](https://img.shields.io/discord/967118679370264627.svg?colorB=7289DA&logo=data:image/png)](https://discord.gg/5mbUY3mu6m)

![image](media/banner.png)

Check out [the examples folder](./examples) for sample scripts.

More info to come here soon™

There's also a basic [VSCode extension](https://marketplace.visualstudio.com/items?itemName=TeamDman.super-factory-manager-language) for syntax highlighting

![](media/vscode%20syntax.png)

## Optimization

There's some neat tricks used to improve the performance of the mod, here's an overview :D

### Caching

### Testing

I created a custom barrel block used only for testing. Running all the game tests for the mod creates 2,866 barrel
blocks.
Many of those barrels are so full of items, that when I clear or restart the tests it causes 27,310 stacks to be dropped
on the ground.

By creating a custom barrel that doesn't drop the inventory contents, we can reduce the friction of doing more tests!

![tests](media/tests.png)

### User Empowerment

Moving a very large number of items is a core target of this mod. If a user has created a setup with the mod that lags,
hopefully 