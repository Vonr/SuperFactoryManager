def get_guts(java_content: str) -> str:
    start = java_content.find("public static void")
    start = java_content.find("{", start)
    # find the matching brace
    brace_count = 1
    x = start
    while brace_count > 0:
        x += 1
        if java_content[x] == "{":
            brace_count += 1
        elif java_content[x] == "}":
            brace_count -= 1
    body = java_content[start+1:x]
    # strip indent
    lines = body.split("\n")
    lines = [line for line in lines]
    body = "\n".join(lines)
    return body


    