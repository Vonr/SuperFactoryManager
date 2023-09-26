from fastapi import FastAPI, Request, Response
from fastapi.middleware.cors import CORSMiddleware

TEST_PATH = r"D:\Repos\Minecraft\Forge\SuperFactoryManager\src\gametest\java\ca\teamdman\sfm\ai\OpenDoorTest.java"

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://chat.openai.com"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.post("/echo")
async def echo(request: Request):
    body = await request.body()
    headers = dict(request.headers)
    return {
        "body": body.decode("utf-8"),
        "headers": headers
    }

@app.post("/generate_from_clipboard")
async def generate_from_clipboard(request: Request):
    from common.read_test import read_test
    from common.get_guts import get_guts
    test_content = get_guts(read_test(TEST_PATH))
    import pyperclip
    clipboard_content = pyperclip.paste()
    try:
        print("========clipboard content\n", clipboard_content)
        middle = "\n".join(clipboard_content.replace("$test_content", test_content).split("```")[1].split("\n")[1:])
        print("========prompting\n", middle)
        from common.generate import generate
        response = generate(middle)
        print("========response\n", response)
    except Exception as e:
        import traceback
        estr = ''.join(traceback.TracebackException.from_exception(e).format())
        response = "Error occurred, did you forget a markdown code block containing the prompt? Don't forget you can use $test_content to avoid retyping. Error: " + estr

    return Response(content=response, media_type="text/plain")

@app.post("/test_from_clipboard")
async def test_from_clipboard(request: Request):
    from common.read_test import read_test
    from common.get_guts import get_guts
    test_content = get_guts(read_test(TEST_PATH))
    import pyperclip
    clipboard_content = pyperclip.paste()
    try:
        print("========building prompt\n", clipboard_content)
        prompt = "\n".join(clipboard_content.replace("$test_content", test_content).split("```")[1].split("\n")[1:])

        print("========generating\n", prompt)
        from common.generate import generate
        response = generate(prompt)

        print("========building\n", response)
        from common.hot_reload import hot_reload
        success, output = hot_reload()
        print("\nsuccess=",success)
        if not success:
            print("output=",output)
            response = f"{response}\n\nBuild output failed: {output}"
            return Response(content=response, media_type="text/plain")
        
        print("========write test\n", output)
        from common.write_test import write_test
        from common.with_agent_content import with_agent_content
        failing_test = with_agent_content(read_test(TEST_PATH), '')
        assert len(failing_test) > 200
        write_test(failing_test)

        print("========run test\n", output)
        from common.run_test import run_test
        from common.archive_run import archive_run
        result = run_test()
        print(result)
        archive_run(TEST_PATH)
        success = "passed" in result
        print("\nsuccess=",success)
        response = f"{response}\n\nBuild successful.\nTest result: {result}"

    except Exception as e:
        response = "Error occurred, did you forget a markdown code block containing the prompt? Don't forget you can use $test_content to avoid retyping." + str(e)
    return Response(content=response, media_type="text/plain")


@app.post("/manim_from_clipboard")
async def manim_from_clipboard(request: Request):
    from common.add_cell_to_notebook import add_cell_to_notebook
    import pyperclip
    clipboard_content = pyperclip.paste()
    try:
        code_block_contents = clipboard_content.split("```")[1] # grab block contents
        code_block_contents = "\n".join(code_block_contents.split("\n")[1:]) # strip language indicator
        add_cell_to_notebook("../anim/arrow.ipynb", code_block_contents)
        response = "Added cell to notebook."    
    except Exception as e:
        response = "Error occurred, did you forget a markdown code block containing the new code?" + str(e)
    return Response(content=response, media_type="text/plain")