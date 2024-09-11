/*
 * File for parser and error checking
 */
import * as vscode from 'vscode';
import { CharStreams, CommonTokenStream } from 'antlr4ts';
import { SFMLLexer } from '../generated/SFMLLexer';
import { SFMLParser } from '../generated/SFMLParser';
import { ANTLRErrorListener, RecognitionException, Recognizer } from 'antlr4ts';

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
        
        const lineEnd = offendingToken ? offendingToken.line : lineStart; // Asegurar que 'lineEnd' no sea undefined
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

export function handleDocument(document: vscode.TextDocument) {
    const text = document.getText();
    const diagnostics: vscode.Diagnostic[] = [];

    const parseResult = parseInput(text);
    const { success, errors } = parseResult;

    if (!success) {
        errors.forEach((error: any) => {
            const { lineStart, columnStart, lineEnd, columnEnd, message } = error;

            //From start to finish, dont work quite yet
            const range = new vscode.Range(
                new vscode.Position(lineStart - 1, 0),  // Start of the line
                new vscode.Position(lineEnd - 1, columnEnd)  // End of the line
            );

            // Crear un diagnóstico y asociarlo al rango
            const diagnostic = new vscode.Diagnostic(range, message, vscode.DiagnosticSeverity.Error);
            diagnostics.push(diagnostic);
        });
    }

    const diagnosticCollection = vscode.languages.createDiagnosticCollection('syntaxErrors');
    diagnosticCollection.set(document.uri, diagnostics);
}