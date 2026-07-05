package ca.teamdman.sfm.client.screen;

import ca.teamdman.sfm.client.text_styling.ProgramSyntaxHighlightingHelper;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SFMDrawCanvasSyntaxHighlightingHelper {
    public static Map<SFMDrawCanvasModel.CanvasGlyph, Integer> buildSyntaxHighlightColours(
            List<SFMDrawCanvasModel.CanvasGlyph> sourceGlyphs,
            int spaceWidth,
            int defaultColour
    ) {
        CanvasDocumentProjection projection = projectCanvasDocument(sourceGlyphs, spaceWidth);
        Map<SFMDrawCanvasModel.CanvasGlyph, Integer> colours = new IdentityHashMap<>();
        if (projection.text().isEmpty()) {
            return colours;
        }
        for (ProgramSyntaxHighlightingHelper.TokenHighlight highlight : ProgramSyntaxHighlightingHelper.getTokenHighlights(projection.text())) {
            int colour = formattingToRgb(highlight.colour(), defaultColour);
            for (int index = highlight.startIndex(); index <= highlight.stopIndex() && index < projection.glyphsByCharIndex().size(); index++) {
                SFMDrawCanvasModel.CanvasGlyph glyph = projection.glyphsByCharIndex().get(index);
                if (glyph != null) {
                    colours.put(glyph, colour);
                }
            }
        }
        return colours;
    }

    public static CanvasDocumentProjection projectCanvasDocument(
            List<SFMDrawCanvasModel.CanvasGlyph> sourceGlyphs,
            int spaceWidth
    ) {
        List<SFMDrawCanvasModel.CanvasGlyph> glyphs = new ArrayList<>(sourceGlyphs);
        glyphs.sort((left, right) -> {
            int yCompare = Double.compare(left.y(), right.y());
            if (yCompare != 0) {
                return yCompare;
            }
            return Double.compare(left.x(), right.x());
        });

        StringBuilder text = new StringBuilder();
        List<SFMDrawCanvasModel.CanvasGlyph> glyphsByCharIndex = new ArrayList<>();
        Double currentY = null;
        double lineEndX = 0.0D;
        int safeSpaceWidth = Math.max(1, spaceWidth);
        for (SFMDrawCanvasModel.CanvasGlyph glyph : glyphs) {
            if (currentY == null || Double.compare(currentY, glyph.y()) != 0) {
                if (currentY != null) {
                    text.append('\n');
                    glyphsByCharIndex.add(null);
                }
                currentY = glyph.y();
                lineEndX = 0.0D;
            }
            int inferredSpaces = Math.max(0, (int) Math.floor((glyph.x() - lineEndX) / safeSpaceWidth));
            for (int i = 0; i < inferredSpaces; i++) {
                text.append(' ');
                glyphsByCharIndex.add(null);
            }
            for (int i = 0; i < glyph.text().length(); i++) {
                text.append(glyph.text().charAt(i));
                glyphsByCharIndex.add(glyph);
            }
            lineEndX = Math.max(lineEndX, glyph.x() + glyph.width());
        }
        return new CanvasDocumentProjection(text.toString(), glyphsByCharIndex);
    }

    private static int formattingToRgb(
            ChatFormatting formatting,
            int defaultColour
    ) {
        Integer colour = formatting.getColor();
        return colour == null ? defaultColour : 0xFF000000 | colour;
    }

    public record CanvasDocumentProjection(
            String text,
            List<SFMDrawCanvasModel.CanvasGlyph> glyphsByCharIndex
    ) {
    }
}
