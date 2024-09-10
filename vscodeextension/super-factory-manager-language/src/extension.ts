import * as vscode from 'vscode';
import { everySnippet } from './snippets/snippetController';
import { activityBar, deleteTempFiles } from './activitybar/ActivityBar';

/**
 * Main method to call everything we need
 * @param context Vscode extension
 */
export function activate(context: vscode.ExtensionContext) {
    everySnippet(context);
    activityBar(context);
}

export function deactivate() {
    deleteTempFiles();
}
