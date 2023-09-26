def focus_intellij():
    try:
        title = next(x for x in gw.getAllTitles() if "TestChambers.java" in x)
        windows = gw.getWindowsWithTitle(title)
        assert len(windows) > 0, f"Window not found: {title}"
        windows[0].activate()
    except Exception as e:
        print(f"Error focusing window: {e}")