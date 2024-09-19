import * as vscode from 'vscode';
import axios from 'axios';
import * as path from 'path';
import * as fs from 'fs';
import * as os from 'os';

/**
 * Class representing only GitHub files and folder, its much simpler than MixedTreeDataProvider
 */
export class SFMLTreeDataProvider implements vscode.TreeDataProvider<vscode.TreeItem> {
    private _onDidChangeTreeData: vscode.EventEmitter<vscode.TreeItem | undefined> = new vscode.EventEmitter<vscode.TreeItem | undefined>();
    readonly onDidChangeTreeData: vscode.Event<vscode.TreeItem | undefined> = this._onDidChangeTreeData.event;

    private context: vscode.ExtensionContext;
    private repositoryUrl: string;
    private repoFiles: any[] = [];
    private showFilesFirst: boolean;
    private enableActivityBar: boolean;

    constructor(context: vscode.ExtensionContext, url: string) 
    {
        this.repositoryUrl = url;
        this.context = context;

        this.showFilesFirst = vscode.workspace.getConfiguration('sfml').get('filesOrder', false);
        vscode.workspace.onDidChangeConfiguration(event => {
            if(event.affectsConfiguration('sfml.filesOrder')) 
            {
                this.showFilesFirst = vscode.workspace.getConfiguration('sfml').get('filesOrder', false);
                this._onDidChangeTreeData.fire(undefined);
            }

            if(event.affectsConfiguration('sfml.enableActivityBar')) 
            {
                this.enableActivityBar = vscode.workspace.getConfiguration('sfml').get('enableActivityBar', true);
                vscode.commands.executeCommand("setContext", "sfml.isActivated", this.enableActivityBar);
                if (this.enableActivityBar) 
                {
                    this.loadRepoContents();
                } 
                else 
                {
                    this._onDidChangeTreeData.fire(undefined);
                }
            }

            if(event.affectsConfiguration('sfml.changeFolderIconsOnActivityBar') || event.affectsConfiguration('sfml.changeFileIconsOnActivityBar')) 
            {
                this._onDidChangeTreeData.fire(undefined);
            }
        });

        this.showFilesFirst = vscode.workspace.getConfiguration('sfml').get('filesOrder', false);
        this.enableActivityBar = vscode.workspace.getConfiguration('sfml').get('enableActivityBar', true);
        vscode.commands.executeCommand("setContext", "sfml.isActivated", this.enableActivityBar);

        if(this.enableActivityBar) 
        {
            this.loadRepoContents();
        }
    }

    //Download the structure from github
    //Maybe extracting this part to be used on Mixed and this?
    async loadRepoContents() 
    {
        try 
        {
            const response = await axios.get(this.repositoryUrl);
            this.repoFiles = response.data;
            this._onDidChangeTreeData.fire(undefined); //update view
        } 
        catch(error) 
        {
            vscode.window.showErrorMessage(`Error fetching examples from github: ${error}`);
        }
    }

    getTreeItem(element: vscode.TreeItem): vscode.TreeItem {
        return element;
    }

    /**
     * This is similar to the local one on MixedTreeData provider
     * Gets the structure from GitHub or the file system
     * Then it create the new tree items for the view
     * For the Mixed one, its similar but needs a few checks not to mix local files and downloaded ones
     * @param element 
     * @returns 
     */
    async getChildren(element?: vscode.TreeItem): Promise<vscode.TreeItem[]> 
    {
        if(!element) 
        {
            const folders = this.repoFiles.filter(file => file.type === 'dir');
            const files = this.repoFiles.filter(file => file.type !== 'dir' && (file.name.endsWith('.sfm') || file.name.endsWith('.sfml')));

            const folderItems = folders.map(folder => this.createTreeItem(folder));
            const fileItems = files.map(file => this.createTreeItem(file));

            return this.showFilesFirst ? [...fileItems, ...folderItems] : [...folderItems, ...fileItems];
        } 
        else 
        {
            const fileData = this.repoFiles.find(file => file.name === element.label);
            if (fileData && fileData.type === 'dir') 
            {
                const folderContents = await loadGithubFolderContents(fileData.url);

                const folders = folderContents.filter((file: { type: string; }) => file.type === 'dir');
                const files = folderContents.filter((file: { type: string; name: string; }) => file.type !== 'dir' && (file.name.endsWith('.sfm') || file.name.endsWith('.sfml')));

                const folderItems = folders.map((folder: any) => this.createTreeItem(folder));
                const fileItems = files.map((file: any) => this.createTreeItem(file));

                return this.showFilesFirst ? [...fileItems, ...folderItems] : [...folderItems, ...fileItems];
            }
        }
        return [];
    }

    /**
     * Generate a treeItem with the corresponding icon
     * @param file 
     * @returns A treeItem for the tree
     */
    private createTreeItem(file: any): vscode.TreeItem 
    {
        const iconPath = loadIconPaths(this.context)
        const treeItem = new vscode.TreeItem(file.name);
        if (file.type === 'dir') 
        {
            treeItem.collapsibleState = vscode.TreeItemCollapsibleState.Collapsed;
            treeItem.iconPath = iconPath.folder;
        } 
        else 
        {
            treeItem.collapsibleState = vscode.TreeItemCollapsibleState.None;
            treeItem.iconPath = iconPath.file;
        }
        treeItem.command = {
            command: 'sfml.openFile',
            title: 'Open File',
            arguments: [file]
        };
        return treeItem;
    }
}

/**
 * Given an github url, get the structure of the directory
 * @param url GitHub API url
 * @returns Data from the response. If you get timed out, it will do an error
 */
export async function loadGithubFolderContents(url: string) 
{
    try 
    {
        const response = await axios.get(url);
        return response.data;
    } 
    catch(error) 
    {
        vscode.window.showErrorMessage(`Error fetching folder contents: ${error}`);
        return [];
    }
}

/**
 * Get the icon selected from the setting and according to that, it will change the icons
 * @param context Vscode extension
 * @returns A array with 2 thing, one for icon path and another for folder icon
 */
export function loadIconPaths(context: vscode.ExtensionContext) 
{
    const iconConfig = vscode.workspace.getConfiguration('sfml');
    const fileIcon = iconConfig.get('changeFileIconsOnActivityBar', 'exp');
    const folderIcon = iconConfig.get('changeFolderIconsOnActivityBar', 'tool');
    
    const iconMap: { [key: string]: string } = {
        'xp glob': 'exp.png',
        'disk': 'icon_sfm.png',
        'controller': 'icon.png',
        'xp shard': 'exp.png',
        'label': 'label.png',
        'tool': 'tool.png'
    };
    
    return{
        file: {
            light: vscode.Uri.file(path.join(context.extensionPath, 'media', iconMap[fileIcon] || 'exp.png')),
            dark: vscode.Uri.file(path.join(context.extensionPath, 'media', iconMap[fileIcon] || 'exp.png'))
        },
        folder: {
            light: vscode.Uri.file(path.join(context.extensionPath, 'media', iconMap[folderIcon] || 'tool.png')),
            dark: vscode.Uri.file(path.join(context.extensionPath, 'media', iconMap[folderIcon] || 'tool.png'))
        }
    };
}

/**
 * Handle everything related to downloading files and folders on github and local
 * Local files and folder will load the structure as github one, to try reduce the usage
 * GitHub files and folder will load only the structure to reduce internet usage. Should be small
 * if you do not open a file (all Teamy files have cost 1MB)
 * Download_url on files doesnt makes sense but it was to reuse the code
 * @param tempFiles Map in which the downloaded files are located, usually on tmp dir
 * @returns Open the file on a new windows or shows an error
 */
export function getOpenCommand(tempFiles: Map<string, string>): vscode.Disposable {
    return vscode.commands.registerCommand('sfml.openFile', async (file) => {
        if (file.type === 'dir') {
            //Not worth it, due to the 2 different stuff to get files
            return;
        }

        // Check if the file is a local file or online and there is a download_url
        if (file.download_url && !file.download_url.includes('https://')) {
            // Handle local file
            try {
                const document = await vscode.workspace.openTextDocument(file.download_url);
                vscode.window.showTextDocument(document);
            }
            catch (error) {
                vscode.window.showErrorMessage(`Error while opening local file: ${error}`);
            }
        }
        else {
            // Handle online file
            const tempFilePath = path.join(os.tmpdir(), path.basename(file.name));
            if (tempFiles.has(tempFilePath)) {
                // If file already exists in temp, open it
                const fileUri = vscode.Uri.file(tempFilePath);
                const document = await vscode.workspace.openTextDocument(fileUri);
                vscode.window.showTextDocument(document);    
            }
            else //Get the file from github and its content
            {
                try {
                    const response = await axios.get(file.download_url, { responseType: 'arraybuffer' });
                    fs.writeFileSync(tempFilePath, response.data);
                    tempFiles.set(tempFilePath, tempFilePath);

                    const fileUri = vscode.Uri.file(tempFilePath);
                    const document = await vscode.workspace.openTextDocument(fileUri);
                    vscode.window.showTextDocument(document);
                }
                catch (error) {
                    vscode.window.showErrorMessage(`Error while opening file: ${error}`);
                }
            }
        }
    });
}

/**
 * @param url API GitHub url, it will get the user name
 * For example, takes this url: https://api.github.com/repos/TeamDman/SuperFactoryManager/contents/examples
 * In this case it will return TeamDman, which is the regex pattern
 * @returns username from the url
 */
export function extractGithubUsername(url: string): string 
{
    const match = url.match(/api\.github\.com\/repos\/([^\/]+)\//);
    return match ? match[1] : 'unknown'; // Devuelve 'unknown' si no se encuentra el nombre de usuario
}

/**
 * Transform the url from https://github.com to a api one, including the branch
 * @param githubUrl GitHub url, not the api one
 * @returns Api url from github
 */
export function getApiUrlFromGithubUrl(githubUrl: string): string 
{
    // Expresión regular para extraer la información de la URL de GitHub para archivos o directorios
    const treeRegex = /https:\/\/github\.com\/([^\/]+)\/([^\/]+)\/tree\/([^\/]+)\/(.+)/;
    const repoRegex = /https:\/\/github\.com\/([^\/]+)\/([^\/]+)/;

    let match = githubUrl.match(treeRegex);
    if(match) 
    {
        const [, owner, repo, branch, path] = match;
        const apiUrl = `https://api.github.com/repos/${owner}/${repo}/contents/${path}?ref=${branch}`;
        return apiUrl;
    }

    match = githubUrl.match(repoRegex);
    if(match) 
    {
        const [, owner, repo] = match;
        const apiUrl = `https://api.github.com/repos/${owner}/${repo}/contents/`;
        return apiUrl;
    }
    console.log("Url no valid" + githubUrl)
    return "";
}