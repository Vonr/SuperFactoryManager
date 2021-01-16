package ca.teamdman.sfm.common.util;

public class SlotsRule {
	private String definition;

	public SlotsRule(String definition) {
		setDefinition(definition);
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition.replaceAll("[^\\d, \\-+]", "");
	}

	public boolean matches(int slot) {
		if (definition.length() == 0) {
			return true;
		}
		//todo: implement rule logic
		return false;
	}
}
