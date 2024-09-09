import * as vscode from 'vscode';

/**
 * Each time you put every on the editor, VSCode will suggest different words that match the word
 * even if it has differences
 * If the word its "every ", it will suggest the snippet and will trigger a suggestion again,
 * making the only 
 * @param context Vscode extension
 */
export function everySnippet(context: vscode.ExtensionContext){
    var changed: number = 0;
    const provider = vscode.languages.registerCompletionItemProvider({ scheme:'file', language:'sfml' },
		{
			provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) 
            {
				const linePrefix = document.lineAt(position).text.slice(0, position.character);
				if (!linePrefix.endsWith('every ')) 
                {
					return undefined;
				}
                changed++;
                const snippetCompletion = new vscode.CompletionItem('every', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('20 ticks do \n   ${1: }\nend');
                snippetCompletion.detail = "Provides a every 20 ticks do end";
                return [
                    snippetCompletion
				];
			}
		},
		' ' //Triggers when the space is pressed
	);
    //If not done with an if, it will trigger suggestion when you open a sfm file
    if(changed > 0)
    {
        vscode.commands.executeCommand('editor.action.triggerSuggest');
        changed--;
    }
    context.subscriptions.push(provider);
}