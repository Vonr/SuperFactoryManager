def generate_gpt4(prompt: str, **kwargs) -> str:
    import requests
    import json

    url = "http://127.0.0.1:5000/api/v1/generate"
    payload = json.dumps({
        "prompt": prompt,
        "max_new_tokens": 64,
        **kwargs
    })
    headers = {
        'Content-Type': 'application/json'
    }
    response = requests.request("POST", url, headers=headers, data=payload)
    return response.json()["results"][0]["text"]
