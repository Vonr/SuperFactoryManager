import * as vscode from 'vscode';
import { everySnippet } from './snippets/snippetController';

/**
 * Main method to call everything
 * @param context Vscode extension
 */
export function activate(context: vscode.ExtensionContext) {
    everySnippet(context);
    
}

export function deactivate() {
}
