import * as vscode from 'vscode';
import axios from 'axios';
import * as path from 'path';
import * as fs from 'fs';
import * as os from 'os';

//This was harder than i thought
//We have to filter from url and local stuff and if we dont do it, well, mess
//Each url has 'url/local' and separated by a ,
export class SFMLTreeDataProvider implements vscode.TreeDataProvider<vscode.TreeItem> 
{
    private _onDidChangeTreeData: vscode.EventEmitter<vscode.TreeItem | undefined> = new vscode.EventEmitter<vscode.TreeItem | undefined>();
    readonly onDidChangeTreeData: vscode.Event<vscode.TreeItem | undefined> = this._onDidChangeTreeData.event;

    private context: vscode.ExtensionContext;
    private githubUrls: string[] = [];
    private localPaths: string[] = [];
    private repoFiles: any[] = [];
    private filesData: Map<string, any[]> = new Map();
    private showFilesFirst: boolean;
    private enableActivityBar: boolean;
    private mode: number
    private repositoryUrl: string = "";

    //Mode equals 0, github and local
    //Mode equals 1, github only
    constructor(context: vscode.ExtensionContext, apiUrl: string, mode: number) 
    {
        this.context = context;
        this.mode = mode;
        if(mode === 0){
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
    
                if(event.affectsConfiguration('sfml.changeFolderIconsOnActivityBar') || event.affectsConfiguration('sfml.changeFileIconsOnActivityBar'))
                {
                    this._onDidChangeTreeData.fire(undefined);
                }
    
                if(event.affectsConfiguration('sfml.externalURL'))
                {
                    this.loadSettings();
                    this.loadSources()
                }
            });
        }
        else if(mode === 1)
        {
            this.repositoryUrl = apiUrl;
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


        this.showFilesFirst = vscode.workspace.getConfiguration('sfml').get('filesOrder', false);
        this.enableActivityBar = vscode.workspace.getConfiguration('sfml').get('enableActivityBar', true);
        vscode.commands.executeCommand("setContext", "sfml.isActivated", this.enableActivityBar);
        
        if (this.enableActivityBar) 
        {
            this.loadSources();
        }
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
                }
                catch(e)
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

    //Basically the same as SFMLTreeDataProvider but for files and separete different repos and local folders
    async getChildren(element?: vscode.TreeItem): Promise<vscode.TreeItem[]> 
    {
        if(this.mode === 0)
        {
            if(!element) 
            {
                const rootItems = this.githubUrls.map(url => {
                    // Gets the username from the url, at this point we already have all url to api.github.com
                    return this.createTreeItem({
                        name: extractGithubUsername(url),
                        type: 'dir',
                        download_url: url,
                    });
                })
                .concat(this.localPaths.map(path => this.createTreeItem({
                    name: path.split('/').pop() || '', //If the last folder is named Pepe, it will display Pepe
                    type: 'dir',
                    download_url: path,
                })));
                return rootItems; // Always this way, to separate different url and folders from each other
            } 
            else 
            {
                // If its a folder, time to get more stuff to show
                //Else time to download more stuff
                const source = element.id as string;
                
                if(source.startsWith('https://api.github.com')) 
                {
                    const contents = await loadGithubFolderContents(source);
                    return this.processGithubContents(contents);
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
                        download_url: file.download_url,
                    }));
                }
            }
        }
        else if(this.mode === 1)
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
                if(fileData && fileData.type === 'dir') 
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
        return [];
        
    }

    /**
     * Get the structure of a folder
     * @param path Local path to a folder
     */
    private async loadLocalFiles(path: string) 
    {
        try 
        {
            const files = await vscode.workspace.fs.readDirectory(vscode.Uri.file(path));
            const localFiles = files.map(([name, type]) => ({
                name,
                type: type === vscode.FileType.Directory ? 'dir' : 'file',
                download_url: path + '/' + name,
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
    
    //Maybe worth extracting?
    private processGithubContents(contents: any[]): vscode.TreeItem[] 
    {
        const files = contents
            .filter(item => item.type === 'file' && (item.name.includes(".sfm") || item.name.includes(".sfml")))
            .map(item => this.createTreeItem({
                name: item.name,
                type: 'file',
                download_url: item.download_url, // Download url for structure
            }));
            
        const folders = contents
            .filter(item => item.type === 'dir')
            .map(item => this.createTreeItem({
                name: item.name,
                type: 'dir',
                download_url: item.url, // Where the folder is located
            }));
    
        // Return the contents according the user settings
        return this.showFilesFirst ? [...files, ...folders] : [...folders, ...files];
    }

    //The same as the sfml tree data provider but this time adding where the files are located
    private createTreeItem(file: {name: string, type: string, download_url: string}): vscode.TreeItem 
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
        treeItem.id = file.download_url; //Needed for the local files
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
        'Disk': 'disk.png',
        'Controller': 'controller.png',
        'Label Gun': 'label gun.png',
        'Experience Goop': 'experience goop.png',
        'Experience Shard': 'experience shard.png',
        'Tool Network': 'tool.png',
        'Printing Form': 'printing press.png'
    };
    
    return{
        file: {
            light: vscode.Uri.file(path.join(context.extensionPath, 'media', iconMap[fileIcon] || 'experience goop.png')),
            dark: vscode.Uri.file(path.join(context.extensionPath, 'media', iconMap[fileIcon] || 'experience goop.png'))
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
