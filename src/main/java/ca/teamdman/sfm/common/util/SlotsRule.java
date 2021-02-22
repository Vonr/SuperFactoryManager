package ca.teamdman.sfm.common.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class SlotsRule {

	private static final Pattern RANGE = Pattern.compile("(?<start>\\d+)\\s*-\\s*(?<end>\\d+)");
	private static final Pattern LOWER_BOUND = Pattern.compile("(?<start>\\d+)\\s*\\+");
	private static final Pattern CONST = Pattern.compile("(?<num>\\d+)");
	private String definition;

	public SlotsRule(String definition) {
		setDefinition(definition);
	}

	public SlotsRule(SlotsRule other) {
		this(other.definition);
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition.replaceAll("[^\\d, \\-+]", "");
	}

	public SlotsRule copy() {
		return new SlotsRule(this);
	}

	public boolean isValidDefinition(String definition) {
		return definition.matches("[\\d, \\-+]*");
	}

	public IntStream getSlots(int maxSlot) {
		if (definition.length() == 0) {
			return IntStream.range(0, maxSlot);
		}
		return Arrays.stream(definition.split(","))
			.flatMapToInt(rule -> evaluateRule(rule, maxSlot))
			.distinct();
	}

	public IntStream evaluateRule(String rule, int maxSlot) {
		try {
			Matcher matcher;

			matcher = RANGE.matcher(rule);
			if (matcher.matches()) {
				// range of slots, e.g., "5-12"
				int lowerBound = Integer.parseInt(matcher.group("start"));
				int upperBound = Integer.parseInt(matcher.group("end"));
				return IntStream.range(lowerBound, upperBound);
			}

			matcher = LOWER_BOUND.matcher(rule);
			if (matcher.matches()) {
				// lower bound, e.g., "16+"
				int lowerBound = Integer.parseInt(matcher.group("start"));
				return IntStream.range(lowerBound, maxSlot);
			}

			matcher = CONST.matcher(rule);
			if (matcher.matches()) {
				// constant number, e.g., "5"
				int num = Integer.parseInt(matcher.group("num"));
				return IntStream.of(num);
			}

		} catch (NumberFormatException e) {
			return IntStream.empty();
		}
		return IntStream.empty();
	}
}
