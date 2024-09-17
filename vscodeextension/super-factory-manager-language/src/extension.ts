import * as vscode from 'vscode';
import { everySnippet } from './snippets/snippetController';
import { activityBar, deleteTempFiles } from './activitybar/ActivityBar';
import { handleDocument} from './antlrg4/Parser';
import { checkInputOutput } from './antlrg4/Warning';

/**
 * Main method to call everything we need
 * @param context Vscode extension
 */
export function activate(context: vscode.ExtensionContext) {
    console.log("bruh activating");
    everySnippet(context);
    activityBar(context);
    
    const checking = vscode.workspace.onDidSaveTextDocument((document) => {
        handleDocument(document);
        checkInputOutput(document);
    });
    context.subscriptions.push(checking);
}

export function deactivate() {
    deleteTempFiles();
}
