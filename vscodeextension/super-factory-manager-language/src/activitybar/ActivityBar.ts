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
    const treeDataProvider3 = new MixedTreeDataProvider(context);
    //If we dont have some .sfm or .sfml, we dont want to see the activity bar
    //Only when the extension activates, like some other extensions do (java extension or antlr one)
    //Dont ask why there 2 openFiles
    if(hasSFMLFiles) 
    {
        const view = vscode.window.createTreeView('examplesGames', {
            treeDataProvider: treeDataProvider
        });
        const view2 = vscode.window.createTreeView('examplegithub', {
            treeDataProvider: treeDataProvider2
        });
        const viewExternal = vscode.window.createTreeView('examplesOthers',{
            treeDataProvider: treeDataProvider3
        });
        context.subscriptions.push(view);
        context.subscriptions.push(view2);
        context.subscriptions.push(viewExternal);
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


    const openFileCommand2 = vscode.commands.registerCommand('extension.openFile2', async (file) => {
        if(file.type === 'dir') 
        {
            // TODO: Implement folder expansion
            return;
        }
    
        // Check if the file is a local file or online
        if(file.uri && !file.uri.includes('https://')) 
        {
            // Handle local file
            const fileUri = file.uri;
            try 
            {
                const document = await vscode.workspace.openTextDocument(fileUri);
                vscode.window.showTextDocument(document);
            } 
            catch(error) 
            {
                vscode.window.showErrorMessage(`Error while opening local file: ${error}`);
            }
        } 
        else
        {
            // Handle online file
            const tempFilePath = path.join(os.tmpdir(), path.basename(file.name));
            if(fs.existsSync(tempFilePath)) 
            {
                // If file already exists in temp, open it
                const fileUri = vscode.Uri.file(tempFilePath);
                try
                {
                    const document = await vscode.workspace.openTextDocument(fileUri);
                    vscode.window.showTextDocument(document);
                } 
                catch(error)
                {
                    vscode.window.showErrorMessage(`Error while opening temporary file: ${error}`);
                }
            } 
            else //Local stuff
            {
                try 
                {
                    const response = await axios.get(file.uri, { responseType: 'arraybuffer' });
                    fs.writeFileSync(tempFilePath, response.data);
                    tempFiles.set(tempFilePath, tempFilePath);
    
                    const fileUri = vscode.Uri.file(tempFilePath);
                    const document = await vscode.workspace.openTextDocument(fileUri);
                    vscode.window.showTextDocument(document);
                } 
                catch(error) 
                {
                    vscode.window.showErrorMessage(`Error while downloading and opening file: ${error}`);
                }
            }
        }
    });
    
    context.subscriptions.push(openFileCommand);
    context.subscriptions.push(openFileCommand2);
}

class SFMLTreeDataProvider implements vscode.TreeDataProvider<vscode.TreeItem> {
    private _onDidChangeTreeData: vscode.EventEmitter<vscode.TreeItem | undefined> = new vscode.EventEmitter<vscode.TreeItem | undefined>();
    readonly onDidChangeTreeData: vscode.Event<vscode.TreeItem | undefined> = this._onDidChangeTreeData.event;

    private context: vscode.ExtensionContext;
    private repositoryUrl: string;
    private repoFiles: any[] = [];
    private showFilesFirst: boolean;
    private enableActivityBar: boolean;
    private iconPaths!: { [key: string]: { light: vscode.Uri; dark: vscode.Uri; }; };

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

            if(event.affectsConfiguration('sfml.changeFileIconsAcBarI') || event.affectsConfiguration('sfml.changeFileIconsAcBarF')) 
            {
                this.loadIconPaths();
                this._onDidChangeTreeData.fire(undefined);
            }
        });

        this.loadIconPaths();
        this.enableActivityBar = vscode.workspace.getConfiguration('sfml').get('enableActivityBar', true);
        vscode.commands.executeCommand("setContext", "sfml.isActivated", this.enableActivityBar);

        if(this.enableActivityBar) 
        {
            this.loadRepoContents();
        }
    }

    //Load the corresponing icon, adding more means adding more to this list and the package.json
    private loadIconPaths() 
    {
        const iconConfig = vscode.workspace.getConfiguration('sfml');
        const fileIcon = iconConfig.get('changeFileIconsAcBarI', 'exp');
        const folderIcon = iconConfig.get('changeFileIconsAcBarF', 'tool');
    
        const iconMap: { [key: string]: string } = {
            'xp glob': 'exp.png',
            'disk': 'icon_sfm.png',
            'controller': 'icon.png',
            'xp shard': 'exp.png',
            'label': 'label.png',
            'tool': 'tool.png'
        };
    
        this.iconPaths = {
            file: {
                light: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[fileIcon] || 'exp.png')),
                dark: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[fileIcon] || 'exp.png'))
            },
            folder: {
                light: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[folderIcon] || 'tool.png')),
                dark: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[folderIcon] || 'tool.png'))
            }
        };
    }

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
            vscode.window.showErrorMessage('Error fetching examples from github');
        }
    }

    getTreeItem(element: vscode.TreeItem): vscode.TreeItem {
        return element;
    }

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
                const folderContents = await this.loadFolderContents(fileData.url);

                const folders = folderContents.filter((file: { type: string; }) => file.type === 'dir');
                const files = folderContents.filter((file: { type: string; name: string; }) => file.type !== 'dir' && (file.name.endsWith('.sfm') || file.name.endsWith('.sfml')));

                const folderItems = folders.map((folder: any) => this.createTreeItem(folder));
                const fileItems = files.map((file: any) => this.createTreeItem(file));

                return this.showFilesFirst ? [...fileItems, ...folderItems] : [...folderItems, ...fileItems];
            }
        }
        return [];
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
            treeItem.iconPath = this.iconPaths.folder;
        } 
        else 
        {
            treeItem.collapsibleState = vscode.TreeItemCollapsibleState.None;
            treeItem.iconPath = this.iconPaths.file;
        }

        treeItem.command = {
            command: 'extension.openFile',
            title: 'Open File',
            arguments: [file]
        };

        return treeItem;
    }
}

//This was harder than i thought
//We have to filter from url and local stuff and if we dont do it, well, mess
//Each url has 'url/local' and separated by a ,
class MixedTreeDataProvider implements vscode.TreeDataProvider<vscode.TreeItem> 
{
    private _onDidChangeTreeData: vscode.EventEmitter<vscode.TreeItem | undefined> = new vscode.EventEmitter<vscode.TreeItem | undefined>();
    readonly onDidChangeTreeData: vscode.Event<vscode.TreeItem | undefined> = this._onDidChangeTreeData.event;

    private context: vscode.ExtensionContext;
    private githubUrls: string[] = [];
    private localPaths: string[] = [];
    private filesData: Map<string, any[]> = new Map();
    private showFilesFirst: boolean;
    private enableActivityBar: boolean;
    private iconPaths!: { [key: string]: { light: vscode.Uri; dark: vscode.Uri; }; };

    constructor(context: vscode.ExtensionContext) 
    {
        this.context = context;
        this.loadSettings();

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
                    this.loadSources();
                } 
                else 
                {
                    this._onDidChangeTreeData.fire(undefined);
                }
            }

            if(event.affectsConfiguration('sfml.changeFileIconsAcBarI') || event.affectsConfiguration('sfml.changeFileIconsAcBarF')) 
            {
                this.loadIconPaths();
                this._onDidChangeTreeData.fire(undefined);
            }

            if(event.affectsConfiguration('sfml.externalURL'))
            {
                this.loadSettings();
                this.loadSources()
            }
        });

        this.loadIconPaths();

        this.showFilesFirst = vscode.workspace.getConfiguration('sfml').get('filesOrder', false);
        this.enableActivityBar = vscode.workspace.getConfiguration('sfml').get('enableActivityBar', true);
        vscode.commands.executeCommand("setContext", "sfml.isActivated", this.enableActivityBar);
        if (this.enableActivityBar) 
        {
            this.loadSources();
        }
    }

    private loadIconPaths() 
    {
        const iconConfig = vscode.workspace.getConfiguration('sfml');
        const fileIcon = iconConfig.get('changeFileIconsAcBarI', 'exp');
        const folderIcon = iconConfig.get('changeFileIconsAcBarF', 'tool');
    
        const iconMap: { [key: string]: string } = {
            'xp glob': 'exp.png',
            'disk': 'icon_sfm.png',
            'controller': 'icon.png',
            'xp shard': 'exp.png',
            'label': 'label.png',
            'tool': 'tool.png'
        };
    
        this.iconPaths = {
            file: {
                light: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[fileIcon] || 'exp.png')),
                dark: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[fileIcon] || 'exp.png'))
            },
            folder: {
                light: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[folderIcon] || 'tool.png')),
                dark: vscode.Uri.file(path.join(this.context.extensionPath, 'media', iconMap[folderIcon] || 'tool.png'))
            }
        };
    }

    private loadSettings() 
    {
        const config = vscode.workspace.getConfiguration('sfml');
        this.githubUrls = [];
        this.localPaths = [];
    
        // Get the externalURL setting value
        const settingValue = config.get<string>('externalURL', '');
    
        // Parse URLs and local paths from the setting value
        const sources = settingValue.split(',').map(source => source.trim().replace(/^'|'$/g, ''));

        const transformedUrls = sources.map(source => {
            if(source.startsWith('https://github.com')) 
            {
                try 
                {
                    return getApiUrlFromGithubUrl(source);
                }catch(e)
                {
                    console.error(`Error converting GitHub URL: ${source}`, e);
                    return null;
                }
            }
            return source;
        }).filter(source => source !== null);
    
        // Filter GitHub URLs and local paths
        this.githubUrls = transformedUrls.filter(source =>
            source.startsWith('https://api.github.com')
        );

        this.localPaths = sources.filter(source => 
            source !== '' && 
            !source.startsWith('https://api.github.com') && 
            !source.startsWith('https://github.com')
        );

        if(this.localPaths.length !== 0 || this.githubUrls.length !== 0)
        {
            vscode.commands.executeCommand("setContext", "sfml.thereAreFiles", true);
        }
        else
        {
            vscode.commands.executeCommand("setContext", "sfml.thereAreFiles", false);
        }
    }

    private async loadSources() 
    {
        for(const url of this.githubUrls) 
        {
            try 
            {
                const response = await axios.get(url);
                this.filesData.set(url, response.data);
            } 
            catch(error) 
            {
                vscode.window.showErrorMessage(`Error fetching from GitHub: ${error}`);
            }
        }
        for(const path of this.localPaths) 
        {
            this.filesData.set(path, []); // Initialize empty array for local paths
        }
        this._onDidChangeTreeData.fire(undefined);
    }

    getTreeItem(element: vscode.TreeItem): vscode.TreeItem {
        return element;
    }

    async getChildren(element?: vscode.TreeItem): Promise<vscode.TreeItem[]> 
    {
        if (!element) 
        {
            const rootItems = this.githubUrls.map(url => {
                // Gets the username from the url, at this point we already have all url to api.github.com
                const username = this.extractGithubUsername(url);
                return this.createTreeItem({
                    name: username,
                    type: 'dir',
                    uri: url,
                    online: true
                });
            })
            .concat(this.localPaths.map(path => this.createTreeItem({
                name: path.split('/').pop() || '', //If the last folder is named Pepe, it will display Pepe
                type: 'dir',
                uri: path,
                online: false
            })));
            return rootItems; // Always this way, to separate different url and folders
        } 
        else 
        {
            // If its a folder, time to get more stuff to show
            const source = element.id as string;
        
            if (source.startsWith('https://api.github.com')) 
            {
                const contents = await this.loadGithubFolderContents(source);
                return this.processGithubContents(contents, source);
            } 
            else 
            {
                await this.loadLocalFiles(source);
                const localFiles = this.filesData.get(source) || [];
                const files = localFiles.filter(file => file.type === 'file');
                const folders = localFiles.filter(file => file.type === 'dir');

                //According to this.showFilesFirst
                const sortedItems = this.showFilesFirst
                    ? [...files, ...folders]
                    : [...folders, ...files];

                return sortedItems.map(file => this.createTreeItem({
                    name: file.name,
                    type: file.type,
                    uri: file.uri,
                    online: false
                }));
            }
        }
    }

    private async loadLocalFiles(path: string) 
    {
        try 
        {
            const files = await vscode.workspace.fs.readDirectory(vscode.Uri.file(path));
            const localFiles = files.map(([name, type]) => ({
                name,
                type: type === vscode.FileType.Directory ? 'dir' : 'file',
                uri: path + '/' + name,
                online: false
            }))
            .filter(file => file.type === 'dir' || /\.(sfm|sfml)$/.test(file.name));
            this.filesData.set(path, localFiles);
        } 
        catch(error) 
        {
            vscode.window.showErrorMessage(`Error reading local files: ${error}`);
        }
    }

    private extractGithubUsername(url: string): string 
    {
        const match = url.match(/api\.github\.com\/repos\/([^\/]+)\//);
        return match ? match[1] : 'unknown'; // Devuelve 'unknown' si no se encuentra el nombre de usuario
    }

    //The same as SFMLTreeProvider
    private async loadGithubFolderContents(url: string) 
    {
        try 
        {
            const response = await axios.get(url);
            return response.data;
        } 
        catch(error) 
        {
            vscode.window.showErrorMessage(`Error fetching folder contents from GitHub: ${error}`);
            return [];
        }
    }
    
    private processGithubContents(contents: any[], parentUrl: string): vscode.TreeItem[] 
    {
        const files = contents
            .filter(item => item.type === 'file' && (item.name.includes(".sfm") || item.name.includes(".sfml")))
            .map(item => this.createTreeItem({
                name: item.name,
                type: 'file',
                uri: item.download_url, // Enlace para descargar el archivo
                online: true
            }));
            
        const folders = contents
            .filter(item => item.type === 'dir')
            .map(item => this.createTreeItem({
                name: item.name,
                type: 'dir',
                uri: item.url, // Enlace a la carpeta
                online: true
            }));
    
        // Ordenar los elementos según showFilesFirst
        return this.showFilesFirst ? [...files, ...folders] : [...folders, ...files];
    }

        
    private createTreeItem(file: { name: string, type: string, uri: string , online: boolean}): vscode.TreeItem 
    {
        const treeItem = new vscode.TreeItem(file.name);
        if (file.type === 'dir') {
            treeItem.collapsibleState = vscode.TreeItemCollapsibleState.Collapsed;
            treeItem.iconPath = this.iconPaths.folder;
        } else {
            treeItem.collapsibleState = vscode.TreeItemCollapsibleState.None;
            treeItem.iconPath = this.iconPaths.file;
        }
        treeItem.id = file.uri;
        treeItem.command = {
            command: 'extension.openFile2',
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

function getApiUrlFromGithubUrl(githubUrl: string): string 
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
