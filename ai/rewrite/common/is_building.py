def is_building():
    import pyautogui
    return pyautogui.pixel(1947, 622) == (95, 173, 101)