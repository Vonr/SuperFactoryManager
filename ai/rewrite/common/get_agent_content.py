def get_agent_content(test: str) -> str:
    region_start = "        // begin agent code"
    region_end = "        // end agent code"
    start = test.find(region_start)
    end = test.find(region_end)
    return test[start+len(region_start)+1:end]