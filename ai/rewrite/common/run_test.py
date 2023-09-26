def run_test():
    from pathlib import Path
    msgdir = Path("./messages")
    msgdir.mkdir(parents=True, exist_ok=True)
    print("[Run Test] Running test, ensure you ran `/sfm_ai listen` in Minecraft")
    # touch messages/run.txt
    (msgdir / "run.txt").touch()

    from time import sleep
    # wait for the file to have test results appended
    while not (msgdir / "run.txt").stat().st_size > 0:
        sleep(0.1)
    
    # read the file
    with open(msgdir / "run.txt", "r") as f:
        results = f.read()

    return results