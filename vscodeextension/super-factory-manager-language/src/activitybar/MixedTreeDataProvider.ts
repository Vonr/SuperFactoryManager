import * as vscode from 'vscode';
import axios from 'axios';

import { extractGithubUsername, getApiUrlFromGithubUrl, loadGithubFolderContents, loadIconPaths } from './SFMLTreeDataProvider';


//This was harder than i thought
//We have to filter from url and local stuff and if we dont do it, well, mess
//Each url has 'url/local' and separated by a ,
export class MixedTreeDataProvider implements vscode.TreeDataProvider<vscode.TreeItem> 
{
    private _onDidChangeTreeData: vscode.EventEmitter<vscode.TreeItem | undefined> = new vscode.EventEmitter<vscode.TreeItem | undefined>();
    readonly onDidChangeTreeData: vscode.Event<vscode.TreeItem | undefined> = this._onDidChangeTreeData.event;

    private context: vscode.ExtensionContext;
    private githubUrls: string[] = [];
    private localPaths: string[] = [];
    private filesData: Map<string, any[]> = new Map();
    private showFilesFirst: boolean;
    private enableActivityBar: boolean;

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

    getTreeItem(element: vscode.TreeItem): vscode.TreeItem {
        return element;
    }

    //Basically the same as SFMLTreeDataProvider but for files and separete different repos and local folders
    async getChildren(element?: vscode.TreeItem): Promise<vscode.TreeItem[]> 
    {
        if (!element) 
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
        
            if (source.startsWith('https://api.github.com')) 
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
