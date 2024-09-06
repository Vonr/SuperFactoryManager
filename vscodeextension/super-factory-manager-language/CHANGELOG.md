# Changelog

## [0.0.1 - 0.04]

- Initial release

## [0.0.5]

### Added

- **Correct numbers coloring:**. Now, labels with numbers will not have a colored number.
‎
- **Test Folder:** Added 2 files: .sfm and .sfml
Those files have 2 examples, one using a Phytogenic insolator setup and another with random words to check if everything was done correctly
‎
- **File Icons:** `.sfm` and `.sfml` file extensions have now icons, using the disk from the mod.
‎
- **Extended Color Palette:** Expanded color options beyond the 3-color limit (in some themes).
Colors depends on how the theme using.
‎
- **New Snippets:** Added snippets including `basic`, `energy`, `if`, `ifelse`, `ifelseif`, `input`, and `output`.
‎
- **Extension Icon:** Updated the extension icon from the default to a custom design.

## [0.0.6]

### Changes on code

- **Extension:** Now, every `.vsix` will be on its own folder, to avoid future mistakes 😉
‎
- **Typescript:** Added the scr folder with a blank example and the module needed for ts
‎
- **Icons changes:** Previous icons has some blank spaces, which made the icon on file smaller. Now, file icons will be larger in general
‎
- **.gitignore :** Added it for the carpet `/out`
‎
- **Tasks and lauch options:** There are 2 launch options:
  - `Debug Visually Extension`: For just colors
  - `Compile and Debug Extension`: For code debugging
    ‎
    Also added a task that automatically launch when using `Compile and Debug Extension` to compile the project
  
### Added

- **Folding:** Now, folding or collapsing from if and every should be down better

- **Keywords:**
  - `everY` or `eVERy` will be hightlighted. Also done for others keywords.
  - Added some missing boolean operants (`<`, `>`, `=`, `>=`, `<=`)
