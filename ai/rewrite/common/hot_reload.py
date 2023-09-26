def hot_reload():
    from common.focus_intellij import focus_intellij
    from common.is_building import is_building
    from common.get_build_output import get_build_output
    from common.is_happy_build_output import is_happy_build_output
    from common.focus_build_tab import focus_build_tab
    from time import sleep
    import pyautogui
    print("[Hot Reload] Focusing IntelliJ")
    focus_intellij()
    print("[Hot Reload] Trigger hot reload")
    pyautogui.hotkey('ctrl', 'alt', 'num0')
    print("[Hot Reload] Wait for build to finish")
    while is_building():
        sleep(0.1)
    sleep(0.5)
    print("[Hot Reload] Focusing build output tab")
    focus_build_tab()
    sleep(0.5)
    print("[Hot Reload] Copy build output")
    output = get_build_output()
    success = is_happy_build_output(output)
    print(f"[Hot Reload] Build output succeeded: {success}")
    return success, output