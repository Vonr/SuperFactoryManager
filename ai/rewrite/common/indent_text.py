def indent_text(text, spaces=4):
    space_str = " " * spaces
    return "\n".join(f"{space_str}{line}" for line in text.split("\n"))