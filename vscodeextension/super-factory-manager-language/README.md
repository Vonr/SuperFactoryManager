# Super Factory Manager Language

This extension provides syntax highlighting for the Super Factory Manager Language (SFM)

## Features

- **Syntax highlighting:** For SFM code, similar to in-game text editor.
Colors depends on the theme you are using

- **Snippets:** Basic functionalities that improves code generation, like every snippet or energy snippet

- **Folding:** You will not see part of your code that you do not need

- **Activity bar:** To activate it, you need to open a workspace folder and then open a .sfm or .sfml. It includes examples, both in-game and from github. Requires Internet connection.

## Installation

1. Install the extension through the VSCode Marketplace.
2. Open your SFM files(`.sfm`, `.sfml`) or create a new file on a workspace (for full features)
3. Enjoy and keep building!

## Contributing

### 1. Clone the Repository

Clone the repository using the following command:

```bash
git clone https://github.com/TeamDman/SuperFactoryManager.git
```

### 2. Access the correct folder

Navigate to the VSCode extension directory:

```bash
cd vscodeextension/super-factory-manager-language/
```

### 3. Donwload dependecies

Install all necessary dependencies by running once you are on the folder:

```bash
npm install
```

### 4. Start adding

By now, you have almost everything to start, just need some imagination.
We made some changes on the generated files to make it compile. Just 2 small changes because of `@lexer::members {
    public boolean INCLUDE_UNUSED = false; // we want syntax highlighting to not break on unexpected tokens
}` on the .g4

### 5. Pull request

Make a pull request to the original repo. Make sure it doesnt break anything that was before.

## License

This extension is licensed under the Mozilla Public License Version 2.0
