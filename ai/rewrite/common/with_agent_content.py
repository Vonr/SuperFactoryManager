def with_agent_content(test: str, new_content: str) -> str:
    """
    We want to remove any previous attempts at the test

    // begin agent code
    item.setPos(Vec3.atCenterOf(helper.absolutePos(pressurePlatePos).offset(0,3,0)));
    // end agent code

    should remove the content between the two comments
    """
    region_start = "        // begin agent code"
    region_end = "        // end agent code"
    start = test.find(region_start)
    end = test.find(region_end)
    return test[:start+len(region_start)] + "\n" + new_content + ("\n" if new_content != "" else "") + test[end:]