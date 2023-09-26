const tag = `[Konvord]`
console.log(`${tag} content.js loaded ayo`);
const defaultServerUrl = "http://127.0.0.1:8000";
let serverUrl = defaultServerUrl;
chrome.storage.local.get("serverUrl", function (data) {
    serverUrl = data.serverUrl || defaultServerUrl;
    console.log(`${tag} serverUrl is ${serverUrl}`);
});
// listen for changes
chrome.storage.onChanged.addListener(function (changes, namespace) {
    for (let key in changes) {
        if (key === "serverUrl") {
            serverUrl = changes[key].newValue.replace(/\/$/, "");
            console.log(`${tag} serverUrl is ${serverUrl}`);
        }
    }
});

let loaded = false;
let typingLogDebounce = false;
let processingDebounce = false;
// ensure server is running before starting
fetch(`${serverUrl}/echo`, {method: "POST"}).then(()=>{
    hook = setInterval(()=>{
        const chatgpt_is_typing = document.querySelector("form button[data-testid='send-button'] div") != null;
        if (chatgpt_is_typing && !typingLogDebounce) {
            console.log(`${tag} ChatGPT is typing`);
            typingLogDebounce = true;
            processingDebounce = false;
        }
        if (chatgpt_is_typing || processingDebounce) { return; }
        processingDebounce = true;
        typingLogDebounce = false;
        const copyButtons = Array.from(document.querySelectorAll("path")).filter(x => x.getAttribute("d")=="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2");
        console.log(`${tag} ChatGPT stopped typing`);
        if (!loaded) {
            loaded = true;
            console.log(`${tag} skipping first load`);
            return;
        }
        const latestButton = copyButtons[copyButtons.length-1].parentElement.parentElement;
        console.log(`${tag} clicking latest`, latestButton);
        latestButton.click();
        setTimeout(async ()=>{
            console.log(`${tag} phoning home`);
            const resp = await fetch(`${serverUrl}/message_copied`, {method: "POST"});
            const text = await resp.text();
            console.log(`${tag} received responsez ${text}`);
            console.log("bruh1");
            const textarea = document.querySelector("textarea");
            console.log("bruh2");
            textarea.style.height = "400px";
            console.log("bruh3");
            const content = `Local LLM Response\n===\n${text}\n===\n`;
            console.log("bruh4");
            console.log(content);
            console.log("bruh5");
            textarea.value += content;
            console.log("bruh6");
        }, 500);
    }, 1000);
});