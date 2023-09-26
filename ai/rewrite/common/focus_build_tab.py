def focus_build_tab():
    import pyautogui
    from time import sleep
    grey = (78, 81, 87)
    blue = (53, 116, 240)
    found = pyautogui.pixel(1950, 640)
    is_focused = found == blue or found == grey
    
    if not is_focused:
        x, y = pyautogui.position()
        pyautogui.moveTo(1950,640)
        sleep(0.1)
        pyautogui.mouseDown()
        sleep(0.1)
        pyautogui.mouseUp()
        sleep(0.1)
        pyautogui.moveTo(x, y)
        sleep(0.1)