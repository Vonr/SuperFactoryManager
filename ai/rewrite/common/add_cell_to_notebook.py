def add_cell_to_notebook(notebook_path: str, contents: str):
    import json
    with open(notebook_path, 'r') as f:
        notebook = json.load(f)
    notebook["cells"].append({
        "cell_type": "code",
        "execution_count": None,
        "metadata": {},
        "outputs": [],
        "source": contents.split("\n")
    })
    with open(notebook_path, 'w') as f:
        json.dump(notebook, f, indent=1)