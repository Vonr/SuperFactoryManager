def get_build_output():
    import pyautogui
    # store cursor position
    x, y = pyautogui.position()
    # click and drag from 2890, 638 to 2895, 638 to select text without activating links
    pyautogui.moveTo(2890, 638)
    pyautogui.mouseDown()
    pyautogui.moveTo(2895, 638)
    pyautogui.mouseUp()
    pyautogui.hotkey('ctrl', 'a')
    pyautogui.hotkey('ctrl', 'c')
    # restore cursor position
    pyautogui.moveTo(x, y)
    import pyperclip
    return pyperclip.paste().replace('\r', '')