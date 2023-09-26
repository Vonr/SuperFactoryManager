def archive_run(test_path):
    from common.get_agent_content import get_agent_content
    from common.read_test import read_test
    from pathlib import Path
    msgdir = Path("./messages")

    with open(msgdir / "run.txt", "r") as f:
        results = f.read()

    archive_content = get_agent_content(read_test(test_path)) + "\n\n" + results
    # clean up the file by renaming it with the time
    from datetime import datetime
    now = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
    (msgdir / f"run_{now}.txt").write_text(archive_content)
    (msgdir / "run.txt").unlink()
