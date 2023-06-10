package ca.teamdman.sfm;

import ca.teamdman.sfm.client.gui.screen.ProgramEditScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.junit.jupiter.api.Test;

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
    public void deindentTrimming() {
        assertEquals("abc", ProgramEditScreen.leftTrim4(" abc"));
        assertEquals("abc", ProgramEditScreen.leftTrim4("  abc"));
        assertEquals("abc", ProgramEditScreen.leftTrim4("   abc"));
        assertEquals("abc", ProgramEditScreen.leftTrim4("    abc"));
        assertEquals(" abc", ProgramEditScreen.leftTrim4("     abc"));
        assertEquals("  abc", ProgramEditScreen.leftTrim4("      abc"));
    }
}
