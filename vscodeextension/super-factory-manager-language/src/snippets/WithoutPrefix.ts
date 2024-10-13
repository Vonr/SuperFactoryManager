import * as vscode from 'vscode';

export function everySnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('every ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('every 20 ticks do \n   ${1: }\nend');
                snippetCompletion.detail = "Provides a every 20 ticks do end";
                return [snippetCompletion];
            }
        },
        'eve, ever, every, every '
    );
    return provider;
}

export function ifSnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('if ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('if ${1:boolean_expression} then\n   ${2:code}\nend');
                snippetCompletion.detail = 'If statement';
                return [snippetCompletion];
            }
        },
        'if, if '
    );
    return provider;
}

export function ifElseSnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('ifelse ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('if ${1:boolean_expression} then\n   ${2:code}\nelse\n   ${3:code}\nend');
                snippetCompletion.detail = 'If-Else statement';
                return [snippetCompletion];
            }
        },
        'if, if '
    );
    return provider;
}

export function ifElseIfSnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('ifelseif ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('if ${1:boolean_expression} then\n   ${2:code}\nelse if ${3:boolean_expression} then\n   ${4:code}\nend');
                snippetCompletion.detail = 'If-Else-If statement';
                return [snippetCompletion];
            }
        },
        'if, if '
    );
    return provider;
}

export function inputSnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('input ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('input $1 from ${2:label}');
                snippetCompletion.detail = 'Input statement';
                return [snippetCompletion];
            }
        },
        'inpu, input, input '
    );
    return provider;
}

export function outputSnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('output ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('output $1 to ${2:label}');
                snippetCompletion.detail = 'Output statement';
                return [snippetCompletion];
            }
        },
        'outp, outpu, output, output '
    );
    return provider;
}

export function basicSnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('basic ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(
                    'name "A program"\n\n' +
                    'every 1 ticks do\n   input fe:: from ${1:power_source}\n   output fe:: to ${2:machine}\nend\n\n' +
                    'every 20 ticks do\n   input from ${3:chest_input}\n   output to ${4:furnace}\nend'
                );
                snippetCompletion.detail = 'Basic structure for most uses';
                return [snippetCompletion];
            }
        },
        'basi, basic, basic '
    );
    return provider;
}

export function energySnippet(): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider({ scheme: 'file', language: 'sfml' },
        {
            provideCompletionItems() {
                const snippetCompletion = new vscode.CompletionItem('energy ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(
                    'every 1 ticks do\n   input fe:: from ${1:power_source}\n   output fe:: to ${2:machine}\nend'
                );
                snippetCompletion.detail = 'Create code for energy movement';
                return [snippetCompletion];
            }
        },
        'ener, energ, energy, energy '
    );
    return provider;
}
