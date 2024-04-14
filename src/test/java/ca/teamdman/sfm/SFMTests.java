package ca.teamdman.sfm;

import ca.teamdman.sfm.client.gui.screen.ProgramEditScreen;
import ca.teamdman.sfm.common.net.ServerboundManagerProgramPacket;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfml.ast.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(
                List.of(new BlockPos(0, 0, 0)),
                labelAccess.roundRobin().gather(labelAccess, labelPositions).toList()
        );
        // should not repeat the same block
        assertEquals(
                List.of(new BlockPos(0, 1, 0)),
                labelAccess.roundRobin().gather(labelAccess, labelPositions).toList()
        );
    }

    @Test
    public void ClassName() {
        var name = ServerboundManagerProgramPacket.class.getSimpleName();
        assertEquals("ServerboundManagerProgramPacket", name);
    }
}
