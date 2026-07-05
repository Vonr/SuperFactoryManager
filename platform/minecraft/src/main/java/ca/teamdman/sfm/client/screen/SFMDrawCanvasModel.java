package ca.teamdman.sfm.client.screen;

import java.util.ArrayList;
import java.util.List;

public class SFMDrawCanvasModel {
    private final List<CanvasGlyph> glyphs = new ArrayList<>();
    private double cursorCanvasX;
    private double cursorCanvasY;

    public List<CanvasGlyph> glyphs() {
        return glyphs;
    }

    public double cursorCanvasX() {
        return cursorCanvasX;
    }

    public double cursorCanvasY() {
        return cursorCanvasY;
    }

    public void setCursorCanvasX(double cursorCanvasX) {
        this.cursorCanvasX = cursorCanvasX;
    }

    public void setCursorCanvasY(double cursorCanvasY) {
        this.cursorCanvasY = cursorCanvasY;
    }

    public void setCursor(
            double cursorCanvasX,
            double cursorCanvasY
    ) {
        this.cursorCanvasX = cursorCanvasX;
        this.cursorCanvasY = cursorCanvasY;
    }

    public void typeGlyph(
            String text,
            int width
    ) {
        glyphs.add(new CanvasGlyph(text, cursorCanvasX, cursorCanvasY, width));
        cursorCanvasX += width;
    }

    public void backspace() {
        deleteLeft();
    }

    public void moveCursorRaw(
            double deltaX,
            double deltaY
    ) {
        cursorCanvasX += deltaX;
        cursorCanvasY += deltaY;
    }

    public void deleteLeft() {
        if (glyphs.isEmpty()) {
            return;
        }
        moveCursorLeft();
        CanvasGlyph deleted = glyphAtCursor();
        if (deleted == null) {
            moveCursorLeft();
            deleted = glyphAtCursor();
        }
        if (deleted != null) {
            glyphs.remove(deleted);
        }
    }

    public void deleteNearestAndMoveRight() {
        if (glyphs.isEmpty()) {
            return;
        }
        CanvasGlyph deleted = nearestGlyph();
        if (deleted == null) {
            return;
        }
        setCursor(deleted.x(), deleted.y());
        glyphs.remove(deleted);
        CanvasGlyph right = leftMostGlyphAfter(glyphsOnLine(deleted.y()), deleted.x());
        if (right != null) {
            setCursor(right.x(), right.y());
            return;
        }
        CanvasGlyph nextLineFirst = firstGlyphOnNextLine(deleted.y());
        if (nextLineFirst != null) {
            setCursor(nextLineFirst.x(), nextLineFirst.y());
        }
    }

    public void moveCursorLeft() {
        if (glyphs.isEmpty()) {
            cursorCanvasX -= 1.0D;
            return;
        }

        List<CanvasGlyph> line = glyphsOnLine(cursorCanvasY);
        if (!line.isEmpty()) {
            CanvasGlyph left = rightMostGlyphBefore(line, cursorCanvasX);
            if (left != null) {
                setCursor(left.x(), left.y());
                return;
            }
            moveCursorToEndOfPreviousLine(cursorCanvasY);
            return;
        }

        CanvasGlyph nearest = nearestGlyph();
        if (nearest == null) {
            cursorCanvasX -= 1.0D;
            return;
        }
        List<CanvasGlyph> nearestLine = glyphsOnLine(nearest.y());
        if (cursorCanvasY > nearest.y()) {
            moveCursorToEndOfLine(nearestLine);
            return;
        }

        CanvasGlyph left = rightMostGlyphBefore(nearestLine, nearest.x());
        if (left != null) {
            setCursor(left.x(), left.y());
            return;
        }
        moveCursorToEndOfPreviousLine(nearest.y());
    }

    public void moveCursorRight() {
        List<CanvasGlyph> line = glyphsOnLine(cursorCanvasY);
        if (line.isEmpty()) {
            CanvasGlyph nearest = nearestGlyph();
            if (nearest == null) {
                cursorCanvasX += 1.0D;
                return;
            }
            setCursor(nearest.x(), nearest.y());
            return;
        }
        CanvasGlyph next = leftMostGlyphAtOrAfter(line, cursorCanvasX);
        if (next == null) {
            moveCursorToStartOfNextLine(cursorCanvasY);
            return;
        }
        setCursor(next.x() + next.width(), next.y());
    }

    public void moveCursorUp() {
        moveCursorUp(1);
    }

    public void moveCursorDown() {
        moveCursorDown(1);
    }

    public void moveCursorUp(int lineHeight) {
        moveCursorVertically(-1, lineHeight, false);
    }

    public void moveCursorDown(int lineHeight) {
        moveCursorVertically(1, lineHeight, false);
    }

    public void moveCursorUpToGlyph(int lineHeight) {
        moveCursorVertically(-1, lineHeight, true);
    }

    public void moveCursorDownToGlyph(int lineHeight) {
        moveCursorVertically(1, lineHeight, true);
    }

    public void moveCursorToLineStart() {
        List<CanvasGlyph> line = currentOrNearestLine();
        if (!line.isEmpty()) {
            CanvasGlyph first = line.get(0);
            setCursor(first.x(), first.y());
        }
    }

    public void moveCursorToLineEnd() {
        List<CanvasGlyph> line = currentOrNearestLine();
        moveCursorToEndOfLine(line);
    }

    public void moveCursorToDocumentStart() {
        CanvasGlyph first = topmostThenLeftmostGlyph();
        if (first != null) {
            setCursor(first.x(), first.y());
        }
    }

    public void moveCursorToDocumentEnd() {
        CanvasGlyph last = bottommostThenRightmostGlyph();
        if (last != null) {
            setCursor(last.x() + last.width(), last.y());
        }
    }

    public void moveCursorToNextLine(int lineHeight) {
        CanvasGlyph nearest = nearestGlyph();
        if (nearest == null) {
            cursorCanvasY += lineHeight;
            return;
        }

        CanvasGlyph leftMost = leftMostGlyphOnLine(nearest);
        double previousCursorCanvasY = cursorCanvasY;
        cursorCanvasX = leftMost.x();
        cursorCanvasY = leftMost.y() + lineHeight;
        if (nearest == leftMost) {
            cursorCanvasY = Math.max(cursorCanvasY + lineHeight, previousCursorCanvasY + lineHeight);
        }
    }

    private CanvasGlyph nearestGlyph() {
        CanvasGlyph nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (CanvasGlyph glyph : glyphs) {
            double dx = cursorCanvasX - glyph.x();
            double dy = cursorCanvasY - glyph.y();
            double distance = dx * dx + dy * dy;
            if (distance < nearestDistance) {
                nearest = glyph;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private CanvasGlyph glyphAtCursor() {
        for (CanvasGlyph glyph : glyphs) {
            if (Double.compare(glyph.x(), cursorCanvasX) == 0 && Double.compare(glyph.y(), cursorCanvasY) == 0) {
                return glyph;
            }
        }
        return null;
    }

    private List<CanvasGlyph> glyphsOnLine(double y) {
        List<CanvasGlyph> line = new ArrayList<>();
        for (CanvasGlyph glyph : glyphs) {
            if (Double.compare(glyph.y(), y) == 0) {
                line.add(glyph);
            }
        }
        line.sort((a, b) -> Double.compare(a.x(), b.x()));
        return line;
    }

    private List<CanvasGlyph> currentOrNearestLine() {
        List<CanvasGlyph> line = glyphsOnLine(cursorCanvasY);
        if (!line.isEmpty()) {
            return line;
        }
        CanvasGlyph nearest = nearestGlyph();
        return nearest == null ? List.of() : glyphsOnLine(nearest.y());
    }

    private CanvasGlyph rightMostGlyphBefore(
            List<CanvasGlyph> line,
            double x
    ) {
        CanvasGlyph left = null;
        for (CanvasGlyph glyph : line) {
            if (glyph.x() < x && (left == null || glyph.x() > left.x())) {
                left = glyph;
            }
        }
        return left;
    }

    private CanvasGlyph leftMostGlyphAtOrAfter(
            List<CanvasGlyph> line,
            double x
    ) {
        for (CanvasGlyph glyph : line) {
            if (glyph.x() >= x) {
                return glyph;
            }
        }
        return null;
    }

    private CanvasGlyph leftMostGlyphAfter(
            List<CanvasGlyph> line,
            double x
    ) {
        for (CanvasGlyph glyph : line) {
            if (glyph.x() > x) {
                return glyph;
            }
        }
        return null;
    }

    private CanvasGlyph leftMostGlyphOnLine(CanvasGlyph nearest) {
        CanvasGlyph leftMost = nearest;
        for (CanvasGlyph glyph : glyphs) {
            if (Double.compare(glyph.y(), nearest.y()) == 0 && glyph.x() < leftMost.x()) {
                leftMost = glyph;
            }
        }
        return leftMost;
    }

    private void moveCursorToEndOfLine(List<CanvasGlyph> line) {
        if (line.isEmpty()) {
            return;
        }
        CanvasGlyph rightMost = line.get(line.size() - 1);
        setCursor(rightMost.x() + rightMost.width(), rightMost.y());
    }

    private void moveCursorToEndOfPreviousLine(double y) {
        Double previousY = null;
        for (CanvasGlyph glyph : glyphs) {
            if (glyph.y() < y && (previousY == null || glyph.y() > previousY)) {
                previousY = glyph.y();
            }
        }
        if (previousY == null) {
            cursorCanvasX = 0.0D;
            return;
        }
        moveCursorToEndOfLine(glyphsOnLine(previousY));
    }

    private void moveCursorToStartOfNextLine(double y) {
        CanvasGlyph first = firstGlyphOnNextLine(y);
        if (first != null) {
            setCursor(first.x(), first.y());
        }
    }

    private CanvasGlyph firstGlyphOnNextLine(double y) {
        Double nextY = null;
        for (CanvasGlyph glyph : glyphs) {
            if (glyph.y() > y && (nextY == null || glyph.y() < nextY)) {
                nextY = glyph.y();
            }
        }
        if (nextY == null) {
            return null;
        }
        List<CanvasGlyph> line = glyphsOnLine(nextY);
        return line.isEmpty() ? null : line.get(0);
    }

    private void moveCursorVertically(
            int direction,
            int lineHeight,
            boolean snapToGlyph
    ) {
        Double targetY = null;
        for (CanvasGlyph glyph : glyphs) {
            boolean candidate = direction < 0 ? glyph.y() < cursorCanvasY : glyph.y() > cursorCanvasY;
            boolean better = targetY == null || (direction < 0 ? glyph.y() > targetY : glyph.y() < targetY);
            if (candidate && better) {
                targetY = glyph.y();
            }
        }
        if (targetY == null) {
            return;
        }
        double gapStart = direction < 0 ? targetY + lineHeight : cursorCanvasY + lineHeight;
        double gapEnd = direction < 0 ? cursorCanvasY : targetY;
        if (!snapToGlyph && gapEnd - gapStart >= lineHeight) {
            cursorCanvasY += direction * lineHeight;
            return;
        }
        List<CanvasGlyph> line = glyphsOnLine(targetY);
        CanvasGlyph target = nearestGlyphByX(line, cursorCanvasX);
        if (target != null) {
            setCursor(target.x(), target.y());
        }
    }

    private CanvasGlyph nearestGlyphByX(
            List<CanvasGlyph> line,
            double x
    ) {
        CanvasGlyph nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (CanvasGlyph glyph : line) {
            double distance = Math.abs(glyph.x() - x);
            if (distance < nearestDistance) {
                nearest = glyph;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private CanvasGlyph topmostThenLeftmostGlyph() {
        CanvasGlyph first = null;
        for (CanvasGlyph glyph : glyphs) {
            if (first == null
                || glyph.y() < first.y()
                || (Double.compare(glyph.y(), first.y()) == 0 && glyph.x() < first.x())) {
                first = glyph;
            }
        }
        return first;
    }

    private CanvasGlyph bottommostThenRightmostGlyph() {
        CanvasGlyph last = null;
        for (CanvasGlyph glyph : glyphs) {
            if (last == null
                || glyph.y() > last.y()
                || (Double.compare(glyph.y(), last.y()) == 0 && glyph.x() > last.x())) {
                last = glyph;
            }
        }
        return last;
    }

    public record CanvasGlyph(
            String text,
            double x,
            double y,
            int width
    ) {
    }
}
