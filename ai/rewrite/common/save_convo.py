def save_convo(messages):
    content = ""
    for msg in messages:
        content += f"# ~=~ {msg['role']}\n{msg['content']}\n"
        if "function_call" in msg:
            content += f"---\n{msg['function_call']}\n"
    with open("convo.md", "w") as f:
        f.write(content)