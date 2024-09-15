import { CharStreams, CommonTokenStream } from 'antlr4ts';
import { SFMLLexer } from '../generated/SFMLLexer';
import { SFMLParser, BlockContext } from '../generated/SFMLParser';
import { SFMLListener } from '../generated/SFMLListener';
import { ParseTreeWalker } from 'antlr4ts/tree/ParseTreeWalker';
import { InputStatementContext, OutputStatementContext } from '../generated/SFMLParser';
import { TextDocument, Diagnostic, DiagnosticSeverity, Range } from 'vscode';
import * as vscode from 'vscode';

export const diagnosticCollectionWarning = vscode.languages.createDiagnosticCollection('sfml');

class InputOutputChecker implements SFMLListener 
{
    private inputs: Set<string> = new Set();
    private outputs: Set<string> = new Set();
    private processBlock = true;
    private document: TextDocument;
    private enabled: boolean;

    constructor(document: TextDocument) 
    {
        this.document = document;
        this.enabled = vscode.workspace.getConfiguration('sfml').get('enableWarningChecking', false);

        vscode.workspace.onDidChangeConfiguration(event => {
            if (event.affectsConfiguration('sfml.enableWarningChecking'))
            {
                this.enabled = vscode.workspace.getConfiguration('sfml').get('enableWarningChecking', false);
                if(!this.enabled) diagnosticCollectionWarning.clear();
            }
        });
    }

    private createDiagnosticRange(start: number, end: number): Range 
    {
        const startPosition = this.document.positionAt(start);
        const endPosition = this.document.positionAt(end);
        return new Range(startPosition, endPosition);
    }

    private createDiagnostic(start: number, end: number, message: string): Diagnostic 
    {
        const diagnosticRange = this.createDiagnosticRange(start, end);
        return {
            severity: DiagnosticSeverity.Warning,
            range: diagnosticRange,
            message,
            source: 'InputOutputChecker'
        };
    }

    private addDiagnostic(start: number, end: number, message: string) 
    {
        if (!this.enabled) return; // Skip adding diagnostics if warnings are disabled

        const diagnostic = this.createDiagnostic(start, end, message);
        const currentDiagnostics = diagnosticCollectionWarning.get(this.document.uri) || [];
        const diagnostics = [...currentDiagnostics, diagnostic];
        diagnosticCollectionWarning.set(this.document.uri, diagnostics);
    }

    private verifyInputsAndOutputs(ctx: BlockContext) 
    {
        this.outputs.forEach(outputType => {
            if (!this.inputs.has(outputType)) 
            {
                const start = ctx.start?.startIndex ?? 0;
                const end = ctx.stop?.stopIndex ?? 0;
                this.addDiagnostic(start, end, `Warning: Output ${outputType}:: without corresponding input.`);
            }
        });
    }

    enterInputStatement(ctx: InputStatementContext) 
    {
        if (!this.processBlock) return;

        let inputType = ctx.text.match(/(fe|fluid|gas)::/i)?.[1]?.toLowerCase();
        if (!inputType) 
        {
            inputType = 'item';
        }
        this.inputs.add(inputType);
    }

    enterOutputStatement(ctx: OutputStatementContext) 
    {

        let outputType = ctx.text.match(/(fe|fluid|gas)::/i)?.[1]?.toLowerCase();
        if (!outputType || ctx.text.includes('*')) 
        {
            outputType = 'item';
        }
        this.outputs.add(outputType);
    }

    exitBlock(ctx: BlockContext) 
    {
        if (!this.processBlock) 
        {
            this.processBlock = true;
            return;
        }

        this.verifyInputsAndOutputs(ctx);

        this.inputs.clear();
        this.outputs.clear();
    }

    enterForgetStatement(ctx: any) 
    {
        this.processBlock = false;
    }

    enterIfStatement(ctx: any)
    {
        this.processBlock = true;
        this.inputs.clear();
        this.outputs.clear();
    }

    enterElseStatement(ctx: any) 
    {
        this.processBlock = true;
        this.inputs.clear();
        this.outputs.clear();
    }

    exitIfStatement(ctx: any) 
    {
        this.processBlock = true;
        this.verifyInputsAndOutputs(ctx);
    }

    exitElseStatement(ctx: any) 
    {
        this.processBlock = true;
        this.verifyInputsAndOutputs(ctx);
    }

    enterEveryRule?(ctx: any): void {}
    exitEveryRule?(ctx: any): void {}
    visitTerminal?(node: any): void {}
    visitErrorNode?(node: any): void {}
}


// Función para analizar el código
export function checkInputOutput(document: TextDocument) 
{
    const enableWarningChecking = vscode.workspace.getConfiguration('sfml').get('enableWarningChecking', false);
    if (!enableWarningChecking) return; // Skip processing if warnings are disabled

    diagnosticCollectionWarning.clear(); // Limpia la colección de diagnósticos globalmente
    const inputStream = CharStreams.fromString(document.getText());
    const lexer = new SFMLLexer(inputStream);
    const tokenStream = new CommonTokenStream(lexer);
    const parser = new SFMLParser(tokenStream);

    const tree = parser.program();

    const checker = new InputOutputChecker(document);
    const walker = new ParseTreeWalker();
    walker.walk(checker, tree);
}
