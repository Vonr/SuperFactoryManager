import * as vscode from 'vscode';
import { basicSnippet, energySnippet, everySnippet, ifElseIfSnippet, ifElseSnippet, ifSnippet, inputSnippet, outputSnippet } from './WithoutPrefix';
import { basicSnippet2, energySnippet2, everySnippet2, ifElseIfSnippet2, ifElseSnippet2, ifSnippet2, inputSnippet2, outputSnippet2 } from './Prefix';

/**
 * Handle everything related to snippets completion.
 * @param context 
 */
export function registerSnippets(context: vscode.ExtensionContext) {
    let registeredProviders: vscode.Disposable[] = [];
    
    //Register all snippets, depends on if you have a prefix or not
    const registerSnippetProviders = (snippetPrefix: string) => {
        //Ive tested something more compact, but vscode didnt like it :/
        //Had to use the ugly way
        clearSnippetProviders()
        if(snippetPrefix.trim() === ''){
            registeredProviders.push(everySnippet());
            registeredProviders.push(ifSnippet());
            registeredProviders.push(ifElseSnippet());
            registeredProviders.push(ifElseIfSnippet());
            registeredProviders.push(inputSnippet());
            registeredProviders.push(outputSnippet());
            registeredProviders.push(basicSnippet());
            registeredProviders.push(energySnippet());
        }
        else
        {
            registeredProviders.push(everySnippet2(snippetPrefix));
            registeredProviders.push(ifSnippet2(snippetPrefix));
            registeredProviders.push(ifElseSnippet2(snippetPrefix));
            registeredProviders.push(ifElseIfSnippet2(snippetPrefix));
            registeredProviders.push(inputSnippet2(snippetPrefix));
            registeredProviders.push(outputSnippet2(snippetPrefix));
            registeredProviders.push(basicSnippet2(snippetPrefix));
            registeredProviders.push(energySnippet2(snippetPrefix));
        }
        registeredProviders.forEach(provider => context.subscriptions.push(provider));
    };

    const clearSnippetProviders = () => {
        //Delete the previous subscription from context.subs
        //Why? you dont want to have 1000 undefined ones when you change stuff
        registeredProviders.forEach(provider => {
            const index = context.subscriptions.indexOf(provider);
            if (index > -1) {
                context.subscriptions.splice(index, 1);
            }
        });
    
        //Clear the list
        registeredProviders.forEach(provider => provider.dispose());
        registeredProviders = [];
    };

    const configuration = vscode.workspace.getConfiguration('sfml');
    const snippetPrefix = configuration.get<string>('SnippetActivation') || ' '; // Si no hay prefijo, usa espacio por defecto
    const snippetsEnabled = configuration.get<boolean>('enableSnippets'); // Check if snippets are enabled

    if(snippetsEnabled) 
    {
        registerSnippetProviders(snippetPrefix);
    }
    else 
    {
        clearSnippetProviders();
    }

    //Check on any changes affecting those 2 settings
    vscode.workspace.onDidChangeConfiguration((event) => {
        if (event.affectsConfiguration('sfml.enableSnippets') || event.affectsConfiguration('sfml.SnippetActivation')) {
            const newSnippetsEnabled = vscode.workspace.getConfiguration('sfml').get<boolean>('enableSnippets');
            const newPrefix = vscode.workspace.getConfiguration('sfml').get<string>('SnippetActivation') || ' ';

            if(newSnippetsEnabled) 
            {
                registerSnippetProviders(newPrefix);
            } 
            else 
            {
                clearSnippetProviders();
            }
        }
    });
}
