const tag = `[Konvord]`
console.log(`${tag} content.js loaded ayo`);
const defaultServerUrl = "http://127.0.0.1:8000";
let serverUrl = defaultServerUrl;
let enabled = false;
chrome.storage.local.get("enabled", function (data) {
    enabled = data.enabled || false;
    console.log(`${tag} enabled is ${enabled}`);
});

chrome.storage.local.get("serverUrl", function (data) {
    serverUrl = data.serverUrl || defaultServerUrl;
    console.log(`${tag} serverUrl is ${serverUrl}`);
});

let purpose = "";
function getPurpose() {
    if (purpose) {
        return purpose;
    }
    return "generate"
}
chrome.storage.local.get("purpose", function (data) {
    purpose = data.purpose || "generate";
    console.log(`${tag} purpose is ${purpose}`);
});


// listen for changes
chrome.storage.onChanged.addListener(function (changes, namespace) {
    for (let key in changes) {
        if (key === "serverUrl") {
            serverUrl = changes[key].newValue.replace(/\/$/, "");
            console.log(`${tag} serverUrl is ${serverUrl}`);
        } else if (key === "enabled") {
            enabled = changes[key].newValue;
            console.log(`${tag} enabled is ${enabled}`);
        } else if (key === "purpose") {
            purpose = changes[key].newValue;
            console.log(`${tag} purpose is ${purpose}`);
        } else if (key === "resubmit") {
            processingDebounce = false;
            console.log(`${tag} resubmit request received, resetting debounce`);
        }
    }
});

let loaded = false;
let typingLogDebounce = false;
let processingDebounce = false;

async function sleep(ms) { return new Promise(resolve => setTimeout(resolve, ms)); }
setInterval(async ()=>{
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
    if (!enabled) {
        console.log(`${tag} skipping since disabled`);
        return;
    }
    console.log(`${tag} Preflight checks cleared`);

    const latestButton = copyButtons[copyButtons.length-1].parentElement.parentElement;
    console.log(`${tag} Setting clipboard content`, latestButton);
    latestButton.click();
    await sleep(500);
    const endpoint = {
        "generate": `${serverUrl}/generate_from_clipboard`,
        "test": `${serverUrl}/test_from_clipboard`,
        "manim": `${serverUrl}/manim_from_clipboard`,
    }[getPurpose()]
    console.log(`${tag} Calling generate endpoint ${endpoint}`);
    const resp = await fetch(endpoint, {method: "POST"});
    const text = await resp.text();
    console.log(`${tag} Received response`, text);

    const content = `Local response\n===\n${text}\n===\n`;
    console.log(`${tag} Populating chatbox`, content);
    const textarea = document.querySelector("textarea");
    textarea.style.height = "400px";
    textarea.value += content;
}, 1000);