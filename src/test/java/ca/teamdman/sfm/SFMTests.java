package ca.teamdman.sfm;

import ca.teamdman.sfm.client.gui.screen.ProgramEditScreen;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfml.ast.*;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SFMTests {
    @Test
    public void componentSubstring() {
        StringBuilder sb = new StringBuilder();
        var component = Component.literal("");
        sb.append("hello");
        component.append(Component.literal("hello").withStyle(ChatFormatting.GRAY));
        sb.append(" ");
        component.append(" ");
        sb.append("world");
        component.append(Component.literal("world").withStyle(ChatFormatting.RED));
        var content = sb.toString();
        for (int start = 0; start < content.length(); start++) {
            for (int end = start; end < content.length(); end++) {
                MutableComponent substring = ProgramEditScreen.substring(component, start, end);
                assertEquals(content.substring(start, end), substring.getString());
            }
        }
    }

    @Test
    public void roundRobinByBlockDistinct() {
        LabelAccess labelAccess = new LabelAccess(
                Stream.of("a", "b", "c").map(Label::new).toList(),
                new DirectionQualifier(EnumSet.of(Direction.DOWN)),
                NumberRangeSet.MAX_RANGE,
                new RoundRobin(RoundRobin.Behaviour.BY_BLOCK)
        );
        LabelPositionHolder labelPositions = LabelPositionHolder.empty();
        labelPositions.add("a", new BlockPos(0, 0, 0));
        labelPositions.add("b", new BlockPos(0, 0, 0));
        labelPositions.add("c", new BlockPos(0, 0, 0));
        labelPositions.add("c", new BlockPos(0, 1, 0));
        RoundRobin roundRobin1 = labelAccess.roundRobin();
        assertEquals(
                List.of(Pair.of(new Label("a"), new BlockPos(0, 0, 0))),
                roundRobin1.getPositionsForLabels(labelAccess, labelPositions)
        );
        // should not repeat the same block
        RoundRobin roundRobin = labelAccess.roundRobin();
        assertEquals(
                List.of(Pair.of(new Label("c"), new BlockPos(0, 1, 0))),
                roundRobin.getPositionsForLabels(labelAccess, labelPositions)
        );
    }

    @Test
    public void understandFastUtilsLongMap() {
        Map<Long, String> map = new Long2ObjectOpenHashMap<>();
        map.put(123L, "hi");
        assertEquals("hi", map.get(123L));
        assertNull(map.get(124L));
    }

    @Test
    public void testTagMatcher() {
        TagMatcher matcher = TagMatcher.fromNamespaceAndPath("namespace", "path");
        assertTrue(matcher.test("namespace:path"));
        assertFalse(matcher.test("namespace:other"));
        assertFalse(matcher.test("whatever:path"));
        assertFalse(matcher.test("something:here"));
        assertFalse(matcher.test(":"));
        assertFalse(matcher.test(":path"));
        assertFalse(matcher.test("namespace:"));
    }
    @Test
    public void testTagMatcherPattern() {
        TagMatcher matcher = TagMatcher.fromNamespaceAndPath("forge", ".*");
        assertTrue(matcher.test("forge:path"));
        assertTrue(matcher.test("forge:idk"));
        assertTrue(matcher.test("forge:something"));
        assertFalse(matcher.test("forge:who/knows"));
    }

    @Test
    public void testTagMatcherDeepPattern() {
        TagMatcher matcher = TagMatcher.fromNamespaceAndPath("forge", ".*.*");
        assertTrue(matcher.test("forge:path"));
        assertTrue(matcher.test("forge:idk"));
        assertTrue(matcher.test("forge:something"));
        assertTrue(matcher.test("forge:who/knows"));
        assertTrue(matcher.test("forge:who/knows/what"));
        assertFalse(matcher.test("namespace:path"));
        assertFalse(matcher.test("namespace:other"));
        assertFalse(matcher.test("whatever:path"));
        assertFalse(matcher.test("something:here"));
        assertFalse(matcher.test(":"));
        assertFalse(matcher.test(":path"));
        assertFalse(matcher.test("namespace:"));
    }
}
