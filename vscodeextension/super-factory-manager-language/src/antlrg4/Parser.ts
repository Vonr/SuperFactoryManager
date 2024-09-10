/*
 * Class for parser and error checking
 */

import { CharStreams, CommonTokenStream } from 'antlr4ts';
import { SFMLLexer } from '../generated/SFMLLexer';
import { SFMLParser } from '../generated/SFMLParser';

function parseInput(input: string) {
    
    const inputStream = CharStreams.fromString(input);
    const lexer = new SFMLLexer(inputStream);
    const tokenStream = new CommonTokenStream(lexer);
    const parser = new SFMLParser(tokenStream);
    const tree = parser.program();
    if(parser.numberOfSyntaxErrors > 0) 
    {
        return false;
    }
    return true;

    /*vscode.workspace.onDidSaveTextDocument((document) => {
        const inputText = document.getText();
        const isValid = parseInput(inputText);
        
        if (!isValid) {
            //vscode.window.showErrorMessage('No valid');
        } else {
            //vscode.window.showInformationMessage('Valid');
        }
    });
    return false;*/
}