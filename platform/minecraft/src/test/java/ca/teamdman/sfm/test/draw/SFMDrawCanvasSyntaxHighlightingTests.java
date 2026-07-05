package ca.teamdman.sfm.test.draw;

import ca.teamdman.sfm.client.screen.SFMDrawCanvasModel;
import ca.teamdman.sfm.client.screen.SFMDrawCanvasSyntaxHighlightingHelper;
import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SFMDrawCanvasSyntaxHighlightingTests {
    private static final int DEFAULT_COLOUR = 0xFFE6EDF3;
    private static final int SPACE_WIDTH = 1;

    @Test
    public void projectionMapsCanvasGlyphsToSourceText() {
        List<SFMDrawCanvasModel.CanvasGlyph> glyphs = glyphsFromLines("EVERY 20 TICKS DO", "END");

        var projection = SFMDrawCanvasSyntaxHighlightingHelper.projectCanvasDocument(glyphs, SPACE_WIDTH);

        assertEquals("EVERY 20 TICKS DO\nEND", projection.text());
        assertEquals(glyphs.get(0), projection.glyphsByCharIndex().get(0));
        assertEquals(glyphs.get(5), projection.glyphsByCharIndex().get(5));
        assertEquals(glyphs.get(17), projection.glyphsByCharIndex().get(18));
    }

    @Test
    public void syntaxHighlightingColoursCanvasGlyphs() {
        List<SFMDrawCanvasModel.CanvasGlyph> glyphs = glyphsFromLines("EVERY 20 TICKS DO", "END");

        var colours = SFMDrawCanvasSyntaxHighlightingHelper.buildSyntaxHighlightColours(glyphs, SPACE_WIDTH, DEFAULT_COLOUR);

        assertEquals(formattingToRgb(ChatFormatting.BLUE), colours.get(glyphs.get(0))); // E in EVERY
        assertEquals(formattingToRgb(ChatFormatting.AQUA), colours.get(glyphs.get(6))); // 2 in 20
        assertEquals(formattingToRgb(ChatFormatting.GOLD), colours.get(glyphs.get(9))); // T in TICKS
        assertEquals(formattingToRgb(ChatFormatting.BLUE), colours.get(glyphs.get(17))); // E in END
    }

    private static List<SFMDrawCanvasModel.CanvasGlyph> glyphsFromLines(String... lines) {
        List<SFMDrawCanvasModel.CanvasGlyph> glyphs = new ArrayList<>();
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                glyphs.add(new SFMDrawCanvasModel.CanvasGlyph(Character.toString(line.charAt(x)), x, y, 1));
            }
        }
        return glyphs;
    }

    private static int formattingToRgb(ChatFormatting formatting) {
        Integer colour = formatting.getColor();
        return colour == null ? DEFAULT_COLOUR : 0xFF000000 | colour;
    }
}
