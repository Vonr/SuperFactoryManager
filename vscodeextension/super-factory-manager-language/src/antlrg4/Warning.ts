import { CharStreams, CommonTokenStream } from 'antlr4ts';
import { SFMLLexer } from '../generated/SFMLLexer';
import { SFMLParser, BlockContext, ForgetStatementContext, IfStatementContext, InputStatementContext, OutputStatementContext } from '../generated/SFMLParser';
import { SFMLListener } from '../generated/SFMLListener';
import { ParseTreeWalker } from 'antlr4ts/tree/ParseTreeWalker';
import { TextDocument, Diagnostic, DiagnosticSeverity, Range } from 'vscode';
import * as vscode from 'vscode';

export const diagnosticCollectionWarning = vscode.languages.createDiagnosticCollection('sfml');

class InputOutputChecker implements SFMLListener {
    private inputs: Set<string> = new Set();
    private outputs: Set<string> = new Set();
    private enabled: boolean;
    private onIfElseStatment: boolean = false;

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

    // Checks if an input has an output and viceversa 
    private verifyInputsAndOutputs() {
        this.inputs.forEach(inputType => {
            if (!this.outputs.has(inputType)) 
            {
                console.log(`Warning: Input ${inputType}:: without corresponding output.`);
            }
        });
        this.outputs.forEach(outputType => {
            if (!this.inputs.has(outputType)) 
            {
                console.log(`Warning: Output ${outputType}:: without corresponding input.`);
            }
        });
    }

    // Inputs statments
    enterInputStatement(ctx: InputStatementContext) 
    {
        let inputType = ctx.text.match(/(fe|fluid|gas|item)::/i)?.[1]?.toLowerCase();
        
        // If we dont find anything above, we consider it item::
        if (!inputType) 
        {
            inputType = 'item'; 
        }
        this.inputs.add(inputType);
    }

    // Output statments
    enterOutputStatement(ctx: OutputStatementContext) 
    {
        let outputType = ctx.text.match(/(fe|fluid|gas|item)::/i)?.[1]?.toLowerCase();
    
        // If we dont find anything above, we consider it item::
        if (!outputType || ctx.text.includes('*')) 
        {
            outputType = 'item'; 
        }
        this.outputs.add(outputType);
    }

    // Forget everything before, and start the handling of warnings
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
