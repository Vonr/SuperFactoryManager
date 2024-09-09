import * as vscode from "vscode";

/**
 * Class for doing all activity on the Activity Bar related
 * label.png should stay as such, because vscode will transform every colors into 1 single color
*/
export class SFMLActivityBar {
    private isHidden: boolean
    private data: any
    
    /**
     *
     */
    constructor() {
        this.isHidden = false;
        this.data = null;
    }

}

export async function checkSFMLFiles(): Promise<boolean> {
    const workspaceFolders = vscode.workspace.workspaceFolders;
    if(workspaceFolders) 
    {
        const files = await vscode.workspace.findFiles("**/*.{sfml,sfm}", "**/node_modules/*", 1);
        return files.length > 0;
    }
    return false;
}