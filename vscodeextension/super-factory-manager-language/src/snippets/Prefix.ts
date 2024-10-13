import * as vscode from 'vscode';

export function everySnippet2(snippetPrefix:string): vscode.Disposable {

    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem(`${snippetPrefix}every `, vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('every 20 ticks do \n   ${1: }\nend');
                snippetCompletion.detail = "Provides a every 20 ticks do end";
                snippetCompletion.range = range
                return [snippetCompletion];

            }
        },
        `${snippetPrefix}`, `${snippetPrefix}e`, `${snippetPrefix}ev`, `${snippetPrefix}eve`, `${snippetPrefix}ever`, `${snippetPrefix}every`, `${snippetPrefix}every `
    );
    return provider
}

export function ifSnippet2(snippetPrefix:string): vscode.Disposable {
    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem('if ', vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString('if ${1:boolean_expression} then\n   ${2:code}\nend');
                snippetCompletion.detail = 'If statement';
                snippetCompletion.range = range
                return [snippetCompletion];
            }
        },
        `${snippetPrefix}`, `${snippetPrefix}i`, `${snippetPrefix}if`, `${snippetPrefix}if `
    );
    return provider
}

export function ifElseSnippet2(snippetPrefix:string): vscode.Disposable {
    const snippet = {
        label: `${snippetPrefix}ifelse`,
        insertText: 'if ${1:boolean_expression} then\n   ${2:code}\nelse\n   ${3:code}\nend',
        detail: 'If-Else statement'
    };

    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem(snippet.label, vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(snippet.insertText);
                snippetCompletion.range = range;
                snippetCompletion.detail = snippet.detail;

                return [snippetCompletion];
            }
        },
        `${snippetPrefix}`, `${snippetPrefix}i`, `${snippetPrefix}if`, `${snippetPrefix}if `
    );
    return provider
}

export function ifElseIfSnippet2(snippetPrefix:string): vscode.Disposable {
    const snippet = {
        label: `${snippetPrefix}ifelseif`,
        insertText: 'if ${1:boolean_expression} then\n   ${2:code}\nelse if ${3:boolean_expression} then\n   ${4:code}\nend',
        detail: 'If-Else-If statement'
    };

    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem(snippet.label, vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(snippet.insertText);
                snippetCompletion.range = range;
                snippetCompletion.detail = snippet.detail;

                return [snippetCompletion];
            }
        },
        `${snippetPrefix}`, `${snippetPrefix}i`, `${snippetPrefix}if`, `${snippetPrefix}if `
    );
    return provider
}

export function inputSnippet2(snippetPrefix:string): vscode.Disposable {

    const snippet = {
        label: `${snippetPrefix}input`,
        insertText: 'input $1 from ${2:label}',
        detail: 'Input statement'
    };

    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem(snippet.label, vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(snippet.insertText);
                snippetCompletion.range = range;
                snippetCompletion.detail = snippet.detail;

                return [snippetCompletion];
            }
        },
        `${snippetPrefix}`, `${snippetPrefix}i`, `${snippetPrefix}in`, `${snippetPrefix}inp`, `${snippetPrefix}inpu`, `${snippetPrefix}input`, `${snippetPrefix}input `
    );
    return provider
}

export function outputSnippet2(snippetPrefix:string): vscode.Disposable {
    const snippet = {
        label: `${snippetPrefix}output`,
        insertText: 'output $1 to ${2:label}',
        detail: 'Output statement'
    };

    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem(snippet.label, vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(snippet.insertText);
                snippetCompletion.range = range;
                snippetCompletion.detail = snippet.detail;

                return [snippetCompletion];
            }
        },
        `${snippetPrefix}`, `${snippetPrefix}o`, `${snippetPrefix}ou`, `${snippetPrefix}out`, `${snippetPrefix}outp`, `${snippetPrefix}outpu`, `${snippetPrefix}output`, `${snippetPrefix}output `
    );
    return provider
}

export function basicSnippet2(snippetPrefix:string): vscode.Disposable {
    const snippet = {
        label: `${snippetPrefix}basic`,
        insertText: 
            'name "A program"\n\n' +
            'every 1 ticks do\n   input fe:: from ${1:power_source}\n   output fe:: to ${2:machine}\nend\n\n' +
            'every 20 ticks do\n   input from ${3:chest_input}\n   output to ${4:furnace}\nend',
        detail: 'Basic structure for most uses'
    };

    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem(snippet.label, vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(snippet.insertText);
                snippetCompletion.range = range;
                snippetCompletion.detail = snippet.detail;

                return [snippetCompletion];
            }
        },
        `${snippetPrefix}`, `${snippetPrefix}b`, `${snippetPrefix}ba`, `${snippetPrefix}bas`, `${snippetPrefix}basi`, `${snippetPrefix}basic`, `${snippetPrefix}basic `
    );
    return provider
}

export function energySnippet2(snippetPrefix:string): vscode.Disposable {
    const snippet = {
        label: `${snippetPrefix}energy`,
        insertText: 
            'every 1 ticks do\n' +
            '   input fe:: from ${1:power_source}\n' +
            '   output fe:: to ${2:machine}\nend',
        detail: 'Create code for energy movement'
    };

    const provider = vscode.languages.registerCompletionItemProvider(
        { scheme: 'file', language: 'sfml' }, 
        {
            provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
                const range = new vscode.Range(
                    position.with(undefined, position.character - snippetPrefix.length),
                    position
                );

                const snippetCompletion = new vscode.CompletionItem(snippet.label, vscode.CompletionItemKind.Snippet);
                snippetCompletion.insertText = new vscode.SnippetString(snippet.insertText);
                snippetCompletion.range = range;
                snippetCompletion.detail = snippet.detail;

                return [snippetCompletion];
            }
        },
        `${snippetPrefix}`, `${snippetPrefix}e`, `${snippetPrefix}en`, `${snippetPrefix}ene`, `${snippetPrefix}ener`, `${snippetPrefix}energ`, `${snippetPrefix}energy`, `${snippetPrefix}energy ` // Trigger on space
    );
    return provider
}
