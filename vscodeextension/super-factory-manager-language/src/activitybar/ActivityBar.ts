import * as vscode from 'vscode';
import axios from 'axios';
import * as path from 'path';
import * as fs from 'fs';
import * as os from 'os';

//Note: it would be faster if we have already on the folder media or similar, but things can change
// and wanted to make it so its always up-to-date - Titop54

/**
 * For each file we download from github (the content, not which file is), we want to have a record, 
 * so we can delete them later when extension calls deactivate() on extension.ts
 */
const tempFiles: Map<string, string> = new Map();

/**
 * When wanting to check an example, it will download a .json from github containing all folders
 * and files on said directory, which is the example one. 
 * This only download the "how many files and folder", but not their contents
 * If we click on any of those, it then will download the actual content on a temp file
 * on the tmp folder of your os.
 * When deactivate() is called, it will delete those file, because we dont want it after we
 * close vscode  
 * @param context VSCode extension
 */
export async function activityBar(context: vscode.ExtensionContext) 
{
    const hasSFMLFiles = await checkSFMLFiles();
    const treeDataProvider = new SFMLTreeDataProvider(context, 'https://api.github.com/repos/TeamDman/SuperFactoryManager/contents/src/main/resources/assets/sfm/template_programs');
    const treeDataProvider2 = new SFMLTreeDataProvider(context, 'https://api.github.com/repos/TeamDman/SuperFactoryManager/contents/examples');

    //If we dont have some .sfm or .sfml, we dont want to see the activity bar
    //Only when the extension activates, like some other extensions do (java extension or antlr one)
    if(hasSFMLFiles) 
    {
        const view = vscode.window.createTreeView('examplesGames', {
            treeDataProvider: treeDataProvider
        });
        const view2 = vscode.window.createTreeView('examplegithub', {
            treeDataProvider: treeDataProvider2
        });
        context.subscriptions.push(view);
        context.subscriptions.push(view2);
    }
    else
    {
        vscode.commands.executeCommand("setContext", "sfml.isActivated", false);
    }

    const openFileCommand = vscode.commands.registerCommand('extension.openFile', async (file) => {
        if(file.type === 'dir')
        {
            //TODO solve expansion of folder
            return; 
        }
        
        const tempFilePath = path.join(os.tmpdir(), file.name);
        if(tempFiles.has(tempFilePath)) //If we have it already, why download again?
        { 
            const fileUri = vscode.Uri.file(tempFilePath);
            const document = await vscode.workspace.openTextDocument(fileUri);
            vscode.window.showTextDocument(document);
        }
        else
        {
            try 
            {
                const response = await axios.get(file.download_url, {responseType: 'arraybuffer'});
                fs.writeFileSync(tempFilePath, response.data);
                tempFiles.set(tempFilePath, tempFilePath);

                const fileUri = vscode.Uri.file(tempFilePath);
                const document = await vscode.workspace.openTextDocument(fileUri);
                vscode.window.showTextDocument(document);
            } 
            catch (error) 
            {
                vscode.window.showErrorMessage(`Error while opening file: ${error}`);
            }
        }
    });
    context.subscriptions.push(openFileCommand);
}

class SFMLTreeDataProvider implements vscode.TreeDataProvider<vscode.TreeItem> 
{
    private _onDidChangeTreeData: vscode.EventEmitter<vscode.TreeItem | undefined> = new vscode.EventEmitter<vscode.TreeItem | undefined>();
    readonly onDidChangeTreeData: vscode.Event<vscode.TreeItem | undefined> = this._onDidChangeTreeData.event;

    private context: vscode.ExtensionContext; //Needed for icons
    private repositoryUrl: string // url of the repo to download the structure and then the files if needed
    private repoFiles: any[] = [];
    private showFilesFirst: boolean; //Configuration, default is false, so folder goes first
    private enableActivityBar: boolean; //Configuration, default is false, activity bar on

    constructor(context: vscode.ExtensionContext, url: string) 
    {
        this.repositoryUrl = url;
        this.context = context;

        this.showFilesFirst = vscode.workspace.getConfiguration('sfml').get('filesOrder', false);
        vscode.workspace.onDidChangeConfiguration(event => {
            if (event.affectsConfiguration('sfml.filesOrder')) 
            {
                this.showFilesFirst = vscode.workspace.getConfiguration('sfml').get('filesOrder', false);
                this._onDidChangeTreeData.fire(undefined);
            }
        });

        this.enableActivityBar = vscode.workspace.getConfiguration('sfml').get('enableActivityBar', true);
        vscode.commands.executeCommand("setContext", "sfml.isActivated", this.enableActivityBar);

        if (this.enableActivityBar) 
        {
            this.loadRepoContents();
        }

        vscode.workspace.onDidChangeConfiguration(event => {
            if (event.affectsConfiguration('sfml.enableActivityBar')) 
            {
                this.enableActivityBar = vscode.workspace.getConfiguration('sfml').get('enableActivityBar', true);
    
                vscode.commands.executeCommand("setContext", "sfml.isActivated", this.enableActivityBar);
    
                if(!this.enableActivityBar) 
                {
                    this._onDidChangeTreeData.fire(undefined);
                } 
                else 
                {
                    this.loadRepoContents();
                }
            }
        });
    }

    /**
     * Get the structure of the folder and its files
     */
    async loadRepoContents() 
    {
        try 
        {
            const response = await axios.get(this.repositoryUrl);
            this.repoFiles = response.data;
            this._onDidChangeTreeData.fire(undefined); //update view
        } 
        catch (error) 
        {
            vscode.window.showErrorMessage('Error fetching examples from github');
        }
    }

    getTreeItem(element: vscode.TreeItem): vscode.TreeItem 
    {
        return element;
    }

    async getChildren(element?: vscode.TreeItem): Promise<vscode.TreeItem[]> 
    {
        if (!element) 
        {
            // Separeta files from dir
            const folders = this.repoFiles.filter(file => file.type === 'dir');
            const files = this.repoFiles.filter(file => file.type !== 'dir' && (file.name.endsWith('.sfm') || file.name.endsWith('.sfml')));
    
            //Folder structure for view
            const folderItems = folders.map(folder => this.createTreeItem(folder));
            const fileItems = files.map(file => this.createTreeItem(file));
    
            return this.showFilesFirst ? [...fileItems, ...folderItems] : [...folderItems, ...fileItems];
        } 
        else 
        {
            // If we find a folder, we want also the files from inside
            const fileData = this.repoFiles.find(file => file.name === element.label);
            if (fileData && fileData.type === 'dir') 
            {
                const folderContents = await this.loadFolderContents(fileData.url);
    
                //Folder structure for view
                const folders = folderContents.filter((file: { type: string; }) => file.type === 'dir');
                const files = folderContents.filter((file: { type: string; name: string; }) => file.type !== 'dir' && (file.name.endsWith('.sfm') || file.name.endsWith('.sfml')));
    
                const folderItems = folders.map((folder: any) => this.createTreeItem(folder));
                const fileItems = files.map((file: any) => this.createTreeItem(file));
    
                return this.showFilesFirst ? [...fileItems, ...folderItems] : [...folderItems, ...fileItems];
    
            }
        }
        return []; //in case we dont have any items but url is good
    }

    private async loadFolderContents(url: string) 
    {
        try 
        {
            const response = await axios.get(url);
            return response.data;
        } 
        catch (error) 
        {
            vscode.window.showErrorMessage('Error fetching folder contents');
            return [];
        }
    }

    private createTreeItem(file: any): vscode.TreeItem 
    {
        const treeItem = new vscode.TreeItem(file.name);
        if (file.type === 'dir') 
        {
            treeItem.collapsibleState = vscode.TreeItemCollapsibleState.Collapsed;
            treeItem.iconPath = {
                light: vscode.Uri.file(path.join(this.context.extensionPath, 'media', 'tool.png')),
                dark: vscode.Uri.file(path.join(this.context.extensionPath, 'media', 'tool.png'))
            };
        } 
        else 
        {
            treeItem.collapsibleState = vscode.TreeItemCollapsibleState.None;
            treeItem.iconPath = {
                light: vscode.Uri.file(path.join(this.context.extensionPath, 'media', 'exp.png')),
                dark: vscode.Uri.file(path.join(this.context.extensionPath, 'media', 'exp.png'))
            };
        }

        //vscode.open or vscode.diff should be used on command, but we dont have the files downloaded
        treeItem.command = {
            command: 'extension.openFile', 
            title: 'Open File',
            arguments: [file]
        };

        return treeItem;
    }
}

/**
 * Checks on the working directory if we have any .sfm or .sfml file on any folder
 * WIP, add more folders to the exception
 * @returns A Promise<boolean>
 */
export async function checkSFMLFiles(): Promise<boolean> 
{
    const workspaceFolders = vscode.workspace.workspaceFolders;
    if(workspaceFolders) //Sometimes we dont have anything on a workspace, so undefined and nothing
    {
        const files = await vscode.workspace.findFiles("**/*.{sfml,sfm}", "**/node_modules/*", 1);
        return files.length > 0;
    }
    return false;
}

//Call the os to delete the files we downloaded earlier, we dont want them
export function deleteTempFiles()
{
    tempFiles.forEach((filePath) => {
        try 
        {
            if (fs.existsSync(filePath)) 
            {
                fs.unlinkSync(filePath);
            }
        }
        catch (error) 
        {
            vscode.window.showErrorMessage(`Error deleting temporal files from the temporal directory: ${error}`);
        }
    });
    tempFiles.clear();
}
