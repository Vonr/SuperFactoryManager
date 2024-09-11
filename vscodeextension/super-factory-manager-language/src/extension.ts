import * as vscode from 'vscode';
import { everySnippet } from './snippets/snippetController';
import { activityBar, deleteTempFiles } from './activitybar/ActivityBar';
import { handleDocument} from './antlrg4/Parser';

/**
 * Main method to call everything we need
 * @param context Vscode extension
 */
export function activate(context: vscode.ExtensionContext) {
    console.log("bruh activating");
    everySnippet(context);
    activityBar(context);
    
    const disposable = vscode.workspace.onDidSaveTextDocument((document) => {
        handleDocument(document);
    });
    context.subscriptions.push(disposable);
}

export function deactivate() {
    deleteTempFiles();
}
