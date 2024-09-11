/*
 * File for parser and error checking
 */
import * as vscode from 'vscode';
import { CharStreams, CommonTokenStream } from 'antlr4ts';
import { SFMLLexer } from '../generated/SFMLLexer';
import { SFMLParser } from '../generated/SFMLParser';
import { ANTLRErrorListener, RecognitionException, Recognizer } from 'antlr4ts';

//Will clear all errors each time a file is saved, so we dont have duped errors or non existent
export const diagnosticCollection = vscode.languages.createDiagnosticCollection('syntaxErrors');

/**
 * Each time a file is saved, it will trigger the parser, instead of a real-time one (my implementation was so bad)
 * It will clear any existing errors, so we do not have any duplicate
 * @param document File which is saved or being used
 */
export function handleDocument(document: vscode.TextDocument) {
    const text = document.getText();
    const diagnostics: vscode.Diagnostic[] = [];
    diagnosticCollection.clear();

    const parseResult = parseInput(text);
    const { success, errors } = parseResult;

    if (!success) 
    {
        errors.forEach((error: any) => {
            const { lineStart, columnStart, lineEnd, columnEnd, message } = error;

            //Gets the start of the line without spaces at the beginning, will be the start of the error
            const lineStartText = document.lineAt(lineStart - 1).text;
            const firstNonWhitespaceColumn = lineStartText.search(/\S|$/);  

            //Gets the ending of the line
            const lineEndText = document.lineAt(lineEnd - 1).text;
            const lineEndLength = lineEndText.length;

            //Create range in which the curly error stuff will be
            const range = new vscode.Range(
                new vscode.Position(lineStart - 1, firstNonWhitespaceColumn),
                new vscode.Position(lineEnd - 1, lineEndLength)
            );

            //Create the error in which diagnostics will handle
            const diagnostic = new vscode.Diagnostic(range, message, vscode.DiagnosticSeverity.Error);
            diagnostics.push(diagnostic);
        });
    }
    diagnosticCollection.set(document.uri, diagnostics);
}

/**
 * Calls the ANTLR parser and "scan" the text from the saved file.
 * If it has any error, it will output it into an array for handling 
 * @param input Text to parse
 * @returns Array with 2 positions, success and errors. If there is an error, sucess will always be false 
 * and if there no error, sucess will be true
 */
export function parseInput(input: string): { success: boolean, errors: any[] } {
    const inputStream = CharStreams.fromString(input);
    const lexer = new SFMLLexer(inputStream);
    const tokenStream = new CommonTokenStream(lexer);
    const parser = new SFMLParser(tokenStream);

    const errorListener = new UnderlineErrorListener();
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    const tree = parser.program();

    const errors = errorListener.getErrors();

    return {
        success: parser.numberOfSyntaxErrors === 0,
        errors: errors.map(error => {
            return {
                lineStart: error.lineStart,  // Línea de inicio donde ocurre el error
                columnStart: error.columnStart,  // Columna de inicio del error
                lineEnd: error.lineEnd,  // Línea de fin (puede ser igual a lineStart si es en una sola línea)
                columnEnd: error.columnEnd,  // Columna de fin del error
                message: error.message  // Mensaje de error
            };
        })
    };
}

//Something on the examples ive seen online doing something similar, dont ask why, because idk
export class UnderlineErrorListener implements ANTLRErrorListener<any> {
    private errors: any[] = [];
    //Something ANTLR ask for, idk why
    syntaxError<T>(
        recognizer: Recognizer<T, any>,
        offendingSymbol: T,
        lineStart: number,
        columnStart: number,
        msg: string,
        e: RecognitionException | undefined
    ): void {
        
        const offendingToken = e?.getOffendingToken();
        
        const lineEnd = offendingToken ? offendingToken.line : lineStart; // Check if lineEnd is defined, not undefined
        const columnEnd = offendingToken ? offendingToken.charPositionInLine + (offendingToken.text?.length || 0) : columnStart;

        this.errors.push({
            lineStart: lineStart,
            columnStart: columnStart,
            lineEnd: lineEnd,
            columnEnd: columnEnd,
            message: msg
        });
    }

    getErrors() {
        return this.errors;
    }
}
