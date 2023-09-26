console.log("[Konvord.popup] popup.js loaded")
const input = document.getElementById("server-url");
const defaultServerUrl = "https://localhost:5876/";
input.setAttribute("placeholder", defaultServerUrl);

function toast(message) {
    const holder = document.getElementById("toast-holder");
    const toast = document.createElement("div");
    toast.classList.add("toast");
    toast.innerText = message;

    holder.appendChild(toast);
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

input.addEventListener("change", function () {
    console.log("[Konvord.popup] server-url changed")
    const url = input.value;
    chrome.storage.local.set({ serverUrl: url }, function () {
        console.log("[Konvord.popup] Server URL is set to " + url);
        toast("Server URL is set to " + url);
    });
});
chrome.storage.local.get("serverUrl", function (data) {
    input.value = data.serverUrl || defaultServerUrl;
});


const button = document.getElementById('toggle-button');
button.addEventListener('click', e => {
    if (button.classList.contains('on')) {
        button.classList.remove('on');
        chrome.storage.local.set({ enabled: false }, function () {
            console.log("[Konvord.popup] Extension is disabled");
            toast("Extension is disabled");
        });
      } else {
        button.classList.add('on');
        chrome.storage.local.set({ enabled: true }, function () {
            console.log("[Konvord.popup] Extension is enabled");
            toast("Extension is enabled");
        });
      }
});
chrome.storage.local.get("enabled", function (data) {
    const enabled = data.enabled ?? false;
    if (enabled) {
        button.classList.add('on');
    } else {
        button.classList.remove('on');
    }
});

const purposeElem = document.getElementById('purpose');
purposeElem.addEventListener('input', e => {
    const purpose = purposeElem.value;
    chrome.storage.local.set({ purpose: purpose }, function () {
        console.log("[Konvord.popup] Purpose is set to " + purpose);
        toast("Purpose is set to " + purpose);
    });
});
chrome.storage.local.get("purpose", function (data) {
    purposeElem.value = data.purpose || "";
});

document.getElementById("resubmit-button").addEventListener("click", function () {
    rand = Math.random().toString(36).substring(7);
    chrome.storage.local.set({resubmit: rand}, function () {
        console.log("[Konvord.popup] resubmit is set to true");
        toast("resubmit is set to true");
    });
});