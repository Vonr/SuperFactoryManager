package ca.teamdman.sfm.test.draw;

import ca.teamdman.sfm.client.screen.SFMDrawCanvasModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SFMDrawCanvasModelTests {
    @Test
    public void startsWithOnlyPrimaryCursor() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();

        assertEquals(1, canvas.cursors().size());
        assertEquals(0, canvas.focusedCursorIndex());
        assertTrue(canvas.cursors().get(0).active());
    }

    @Test
    public void shiftFocusIncludesCursorInActiveSet() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        canvas.addCursor(64, 0);
        canvas.focusPreviousCursor(false);

        canvas.focusNextCursor(true);

        assertEquals(1, canvas.focusedCursorIndex());
        assertTrue(canvas.cursors().get(0).active());
        assertTrue(canvas.cursors().get(1).active());
    }

    @Test
    public void soloFocusActivatesOnlyFocusedCursor() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        canvas.addCursor(64, 0);
        canvas.focusPreviousCursor(false);
        canvas.focusNextCursor(true);

        canvas.focusPreviousCursor(false);

        assertEquals(0, canvas.focusedCursorIndex());
        assertTrue(canvas.cursors().get(0).active());
        assertFalse(canvas.cursors().get(1).active());
    }

    @Test
    public void typingWritesAtAllActiveCursors() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        canvas.addCursor(64, 0);

        canvas.typeGlyph("x", 1);

        assertEquals(2, canvas.glyphs().size());
        assertEquals(0, canvas.glyphs().get(0).x());
        assertEquals(64, canvas.glyphs().get(1).x());
        assertEquals(1, canvas.cursors().get(0).x());
        assertEquals(65, canvas.cursors().get(1).x());
    }

    @Test
    public void movingActiveCursorsToSamePositionCollapsesDuplicates() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        canvas.addCursor(64, 0);

        canvas.setActiveCursors(10, 20);

        assertEquals(1, canvas.cursors().size());
        assertEquals(10, canvas.cursorCanvasX());
        assertEquals(20, canvas.cursorCanvasY());
        assertTrue(canvas.focusedCursor().active());
    }

    @Test
    public void backspaceDeletesAtAllActiveCursors() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        canvas.addCursor(64, 0);
        canvas.typeGlyph("a", 1);

        canvas.deleteLeft();

        assertEquals(0, canvas.glyphs().size());
        assertEquals(0, canvas.cursors().get(0).x());
        assertEquals(64, canvas.cursors().get(1).x());
    }

    @Test
    public void ensureCursorClosestToEachGlyphCreatesMissingCursors() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        typeTextAt(canvas, "abc", 0, 0);
        canvas.setCursor(0, 0);

        canvas.ensureCursorClosestToEachGlyph();

        assertEquals(3, canvas.cursors().size());
        assertEquals(0, canvas.cursors().get(0).x());
        assertEquals(1, canvas.cursors().get(1).x());
        assertEquals(2, canvas.cursors().get(2).x());
        assertTrue(canvas.cursors().get(0).active());
        assertTrue(canvas.cursors().get(1).active());
        assertTrue(canvas.cursors().get(2).active());
    }

    @Test
    public void ensureCursorClosestToEachGlyphDoesNotDuplicateCoveredGlyphs() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        typeTextAt(canvas, "abc", 0, 0);
        canvas.setCursor(0, 0);
        canvas.ensureCursorClosestToEachGlyph();

        canvas.ensureCursorClosestToEachGlyph();

        assertEquals(3, canvas.cursors().size());
    }

    @Test
    public void ctrlATargetsThenBackspaceDeletesTransactionally() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        typeTextAt(canvas, "asd", 0, 0);
        canvas.setCursor(0, 0);
        canvas.ensureCursorClosestToEachGlyph();

        canvas.deleteLeft();

        assertEquals(0, canvas.glyphs().size());
    }

    @Test
    public void ctrlATargetsThenDeleteDeletesTransactionally() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        typeTextAt(canvas, "asd", 0, 0);
        canvas.setCursor(0, 0);
        canvas.ensureCursorClosestToEachGlyph();

        canvas.deleteNearestAndMoveRight();

        assertEquals(0, canvas.glyphs().size());
    }

    @Test
    public void enterRepeatedlyAdvancesBlankLines() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        int lineHeight = 9;
        for (char c : "Hello, world!".toCharArray()) {
            canvas.typeGlyph(Character.toString(c), 1);
        }

        canvas.moveCursorToNextLine(lineHeight);
        assertEquals(lineHeight, canvas.cursorCanvasY());

        canvas.moveCursorToNextLine(lineHeight);
        assertEquals(lineHeight * 2, canvas.cursorCanvasY());

        canvas.moveCursorToNextLine(lineHeight);
        assertEquals(lineHeight * 3, canvas.cursorCanvasY());
    }

    @Test
    public void leftWrapsFromBlankLineToPreviousLineEnd() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc
                def
                |
                """);

        canvas.moveCursorLeft();

        assertEquals("""
                abc
                def|
                """, toFixture(canvas));
    }

    @Test
    public void leftMovesWithinLineByCaretPosition() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc
                def|
                """);

        canvas.moveCursorLeft();

        assertEquals("""
                abc
                de|f
                """, toFixture(canvas));
    }

    @Test
    public void backspaceFromBlankLineDeletesPreviousLineLastGlyph() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc
                def
                |
                """);

        canvas.deleteLeft();

        assertEquals("""
                abc
                de|
                """, toFixture(canvas));
    }

    @Test
    public void backspaceWithinLineDeletesGlyphToLeft() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc
                de|f
                """);

        canvas.deleteLeft();

        assertEquals("""
                abc
                d| f
                """, toFixture(canvas));
    }

    @Test
    public void repeatedBackspaceDeletesRepeatedlyToTheLeft() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc|
                """);

        canvas.deleteLeft();
        canvas.deleteLeft();
        canvas.deleteLeft();

        assertEquals("""
                |
                """, toFixture(canvas));
    }

    @Test
    public void deleteDeletesNearestGlyphAndMovesRight() {
        SFMDrawCanvasModel canvas = fromFixture("""
                a|bc
                """);

        canvas.deleteNearestAndMoveRight();

        assertEquals("""
                a |c
                """, toFixture(canvas));
    }

    @Test
    public void repeatedDeleteDeletesRepeatedlyToTheRight() {
        SFMDrawCanvasModel canvas = fromFixture("""
                |abc
                """);

        canvas.deleteNearestAndMoveRight();
        canvas.deleteNearestAndMoveRight();
        canvas.deleteNearestAndMoveRight();

        assertEquals("""
                  |
                """, toFixture(canvas));
    }

    @Test
    public void deleteMovesToNextLineWhenDeletedLineIsExtinguished() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc
                  |d
                ef
                """);

        canvas.deleteNearestAndMoveRight();

        assertEquals("""
                abc

                |ef
                """, toFixture(canvas));
    }

    @Test
    public void homeMovesToBeginningOfLine() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc
                 d|ef
                """);

        canvas.moveCursorToLineStart();

        assertEquals("""
                abc
                 |def
                """, toFixture(canvas));
    }

    @Test
    public void endMovesToEndOfLine() {
        SFMDrawCanvasModel canvas = fromFixture("""
                abc
                 d|ef
                """);

        canvas.moveCursorToLineEnd();

        assertEquals("""
                abc
                 def|
                """, toFixture(canvas));
    }

    @Test
    public void controlHomeMovesToTopmostThenLeftmostGlyph() {
        SFMDrawCanvasModel canvas = fromFixture("""
                  bc
                a
                   |def
                """);

        canvas.moveCursorToDocumentStart();

        assertEquals("""
                  |bc
                a
                   def
                """, toFixture(canvas));
    }

    @Test
    public void controlEndMovesToBottommostThenRightmostGlyphEnd() {
        SFMDrawCanvasModel canvas = fromFixture("""
                  bc
                a
                d|ef
                 gh
                """);

        canvas.moveCursorToDocumentEnd();

        assertEquals("""
                  bc
                a
                def
                 gh|
                """, toFixture(canvas));
    }

    @Test
    public void upMovesIntoBlankGapWhenPreviousGlyphLineIsFarEnoughAway() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        int lineHeight = 9;
        typeTextAt(canvas, "every 20 ticks do", 0, 0);
        typeTextAt(canvas, "end", 0, lineHeight * 2);
        canvas.setCursor(3, lineHeight * 2);

        canvas.moveCursorUp(lineHeight);

        assertEquals(3, canvas.cursorCanvasX());
        assertEquals(lineHeight, canvas.cursorCanvasY());
    }

    @Test
    public void upToGlyphSkipsBlankGap() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        int lineHeight = 9;
        typeTextAt(canvas, "every 20 ticks do", 0, 0);
        typeTextAt(canvas, "end", 0, lineHeight * 2);
        canvas.setCursor(3, lineHeight * 2);

        canvas.moveCursorUpToGlyph(lineHeight);

        assertEquals(3, canvas.cursorCanvasX());
        assertEquals(0, canvas.cursorCanvasY());
    }

    @Test
    public void downToGlyphSkipsBlankGap() {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        int lineHeight = 9;
        typeTextAt(canvas, "every 20 ticks do", 0, 0);
        typeTextAt(canvas, "end", 0, lineHeight * 2);
        canvas.setCursor(3, 0);

        canvas.moveCursorDownToGlyph(lineHeight);

        assertEquals(2, canvas.cursorCanvasX());
        assertEquals(lineHeight * 2, canvas.cursorCanvasY());
    }

    @Test
    public void upStillSnapsToAdjacentLineWhenNoBlankGapExists() {
        SFMDrawCanvasModel canvas = fromFixture("""
                every 20 ticks do
                end|
                """);

        canvas.moveCursorUp(1);

        assertEquals("""
                eve|ry 20 ticks do
                end
                """, toFixture(canvas));
    }

    private static SFMDrawCanvasModel fromFixture(String fixture) {
        SFMDrawCanvasModel canvas = new SFMDrawCanvasModel();
        String[] lines = fixture.stripTrailing().split("\n", -1);
        double cursorX = 0.0D;
        double cursorY = 0.0D;
        boolean cursorFound = false;
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y].stripTrailing();
            int glyphX = 0;
            for (int index = 0; index < line.length(); index++) {
                char c = line.charAt(index);
                if (c == '|') {
                    cursorX = glyphX;
                    cursorY = y;
                    cursorFound = true;
                    continue;
                }
                if (c != ' ') {
                    canvas.setCursor(glyphX, y);
                    canvas.typeGlyph(Character.toString(c), 1);
                }
                glyphX++;
            }
        }
        if (!cursorFound) {
            throw new IllegalArgumentException("Fixture must contain a | cursor marker.");
        }
        canvas.setCursor(cursorX, cursorY);
        return canvas;
    }

    private static void typeTextAt(
            SFMDrawCanvasModel canvas,
            String text,
            double x,
            double y
    ) {
        canvas.setCursor(x, y);
        for (char c : text.toCharArray()) {
            canvas.typeGlyph(Character.toString(c), 1);
        }
    }

    private static String toFixture(SFMDrawCanvasModel canvas) {
        int maxY = (int) canvas.cursorCanvasY();
        int maxX = (int) canvas.cursorCanvasX();
        for (SFMDrawCanvasModel.CanvasGlyph glyph : canvas.glyphs()) {
            maxY = Math.max(maxY, (int) glyph.y());
            maxX = Math.max(maxX, (int) glyph.x() + glyph.width());
        }

        StringBuilder out = new StringBuilder();
        for (int y = 0; y <= maxY; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x <= maxX; x++) {
                if (Double.compare(canvas.cursorCanvasX(), x) == 0
                    && Double.compare(canvas.cursorCanvasY(), y) == 0) {
                    line.append('|');
                }
                SFMDrawCanvasModel.CanvasGlyph glyph = glyphAt(canvas, x, y);
                if (glyph != null) {
                    line.append(glyph.text());
                } else if (x < maxX) {
                    line.append(' ');
                }
            }
            out.append(line.toString().stripTrailing()).append('\n');
        }
        return out.toString();
    }

    private static SFMDrawCanvasModel.CanvasGlyph glyphAt(
            SFMDrawCanvasModel canvas,
            int x,
            int y
    ) {
        for (SFMDrawCanvasModel.CanvasGlyph glyph : canvas.glyphs()) {
            if (Double.compare(glyph.x(), x) == 0 && Double.compare(glyph.y(), y) == 0) {
                return glyph;
            }
        }
        return null;
    }
}
