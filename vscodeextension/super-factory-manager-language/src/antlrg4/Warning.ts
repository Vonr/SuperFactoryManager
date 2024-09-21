import { CharStreams, CommonTokenStream } from 'antlr4ts';
import { SFMLLexer } from '../generated/SFMLLexer';
import { SFMLParser, BlockContext, ForgetStatementContext, IfStatementContext, InputStatementContext, OutputStatementContext } from '../generated/SFMLParser';
import { SFMLListener } from '../generated/SFMLListener';
import { ParseTreeWalker } from 'antlr4ts/tree/ParseTreeWalker';
import { TextDocument, Diagnostic, DiagnosticSeverity, Range } from 'vscode';
import * as vscode from 'vscode';

export const diagnosticCollectionWarning = vscode.languages.createDiagnosticCollection('sfml');

class InputOutputChecker implements SFMLListener {
    private inputs: Set<any> = new Set();
    private outputs: Set<any> = new Set();
    private enabled: boolean;
    private onIfElseStatment: boolean = false;
    private diagnostics: vscode.Diagnostic[] = [];

    constructor() 
    {
        this.enabled = vscode.workspace.getConfiguration('sfml').get('enableWarningChecking', true);

        vscode.workspace.onDidChangeConfiguration(event => {
            if (event.affectsConfiguration('sfml.enableWarningChecking')) 
            {
                this.enabled = vscode.workspace.getConfiguration('sfml').get('enableWarningChecking', true);
                if (!this.enabled) diagnosticCollectionWarning.clear();
            }
        });
    }

    /**
     * Checks if a corresponding input or output has its corresponding counterpart
     */
    private verifyInputsAndOutputs() {
        const activeEditor = vscode.window.activeTextEditor;

        if(!activeEditor) {
            return;
        }

        const document = activeEditor.document;

        // console.log("Inputs: ", this.inputs);
        // console.log("Outputs: ", this.outputs);

        this.inputs.forEach(input => {
            if (!Array.from(this.outputs).some(output => output.type === input.type)) {
                const range = this.calculateRange(input, document);
                const message = `Warning: Input ${input.type}:: without corresponding output.`;
                const diagnostic = new vscode.Diagnostic(range, message, vscode.DiagnosticSeverity.Warning);
                this.diagnostics.push(diagnostic);
            }
        });

        this.outputs.forEach(output => {
            if (!Array.from(this.inputs).some(input => input.type === output.type)) {
                const range = this.calculateRange(output, document);
                const message = `Warning: Output ${output.type}:: without corresponding input.`;
                const diagnostic = new vscode.Diagnostic(range, message, vscode.DiagnosticSeverity.Warning);
                this.diagnostics.push(diagnostic);
            }
        });

        const uri = document.uri;
        diagnosticCollectionWarning.set(uri, this.diagnostics);
    }

    //Get the range, from the first no space to the last letter
    private calculateRange(lineData: { start: any, stop: any }, document: vscode.TextDocument): vscode.Range {
        const startLine = lineData.start.line - 1;
        const endLine = lineData.stop.line - 1;
        const lineText = document.lineAt(startLine).text;
        const firstNonWhitespaceIndex = lineText.search(/\S/);
        const lastNonWhitespaceIndex = lineText.trimEnd().length;

        return new vscode.Range(
            new vscode.Position(startLine, firstNonWhitespaceIndex),
            new vscode.Position(endLine, lastNonWhitespaceIndex)
        );
    }

    public clearDiagnostics() {
        this.diagnostics = [];
        diagnosticCollectionWarning.clear();
    }

    //Inputs statments
    enterInputStatement(ctx: InputStatementContext) 
    {
        //Blame ctx.text because it deletes all spaces
        let inputType = ctx.text.match(/(fe|fluid|gas|item)(?:::[^:]*|:[^:*]*:\*|:[^:*]*)/i)?.[1]?.toLowerCase();
        
        // If we dont find anything above, we consider it item::
        if(!inputType || !ctx.text.includes(":")) inputType = 'item';

        if(inputType.startsWith("fluid:")) inputType = "fluid"
        if(inputType.startsWith("fe:")) inputType = "fe"
        if(inputType.startsWith("gas:")) inputType = "gas"
        if(inputType.startsWith("item:")) inputType = "item"
        const line = {
            type: inputType,
            start: ctx.start,
            stop: ctx.stop
        }
        this.inputs.add(line);
    }

    //Output statments
    enterOutputStatement(ctx: OutputStatementContext) 
    {
        //Blame ctx.text because it deletes all spaces
        let outputType = ctx.text.match(/(fe|fluid|gas|item)(?:::[^:]*|:[^:*]*:\*|:[^:*]*)/i)?.[1]?.toLowerCase();
    
        // If we dont find anything above, we consider it item::
        if(!outputType || !ctx.text.includes(":")) outputType = 'item';
        
        if(outputType.startsWith("fluid:")) outputType = "fluid"
        if(outputType.startsWith("fe:")) outputType = "fe"
        if(outputType.startsWith("gas:")) outputType = "gas"
        if(outputType.startsWith("item:")) outputType = "item"
        const line = {
            type: outputType,
            start: ctx.start,
            stop: ctx.stop
        }
        this.outputs.add(line);
    }

    //Forget everything before, and start the handling of warnings
    //After that, we dont care about what comes before and we clear everything
    enterForgetStatement(ctx: ForgetStatementContext) 
    {
        this.verifyInputsAndOutputs();
        console.log("Forget statment");
        this.inputs.clear();
        this.outputs.clear();
    }

    //If on an ifStatment, it will do nothing, because we know we dont exit
    //If we are not inside one, we ended that block
    exitBlock(ctx: BlockContext) 
    {
        if(this.onIfElseStatment)
        {
            this.onIfElseStatment = false;
            return;
        }
        this.verifyInputsAndOutputs();
        this.inputs.clear();
        this.outputs.clear();
    }

    enterIfStatement(ctx: IfStatementContext){
        this.onIfElseStatment = true;
    }

    // Do not remove, walker.walk errors if we delete those
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

    diagnosticCollectionWarning.clear(); //Clear everything else
    const inputStream = CharStreams.fromString(document.getText());
    const lexer = new SFMLLexer(inputStream);
    const tokenStream = new CommonTokenStream(lexer);
    const parser = new SFMLParser(tokenStream);

    const tree = parser.program();

    const checker = new InputOutputChecker();
    const walker = new ParseTreeWalker();
    walker.walk(checker, tree);
}
