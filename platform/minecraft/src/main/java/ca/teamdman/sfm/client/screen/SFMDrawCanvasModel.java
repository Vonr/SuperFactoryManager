package ca.teamdman.sfm.client.screen;

import java.util.ArrayList;
import java.util.List;

public class SFMDrawCanvasModel {
    public static final int PRIMARY_CURSOR_COLOR = 0xFFE6EDF3;
    public static final int SECONDARY_CURSOR_COLOR = 0xFF7DD3FC;

    private final List<CanvasGlyph> glyphs = new ArrayList<>();
    private List<CanvasCursor> cursors = new ArrayList<>();
    private int focusedCursorIndex;

    public SFMDrawCanvasModel() {
        ensureCursors();
    }

    public List<CanvasGlyph> glyphs() {
        return glyphs;
    }

    public List<CanvasCursor> cursors() {
        ensureCursors();
        return cursors;
    }

    public int focusedCursorIndex() {
        ensureCursors();
        return focusedCursorIndex;
    }

    public CanvasCursor focusedCursor() {
        ensureCursors();
        return cursors.get(focusedCursorIndex);
    }

    public double cursorCanvasX() {
        return focusedCursor().x();
    }

    public double cursorCanvasY() {
        return focusedCursor().y();
    }

    public void setCursorCanvasX(double cursorCanvasX) {
        focusedCursor().setX(cursorCanvasX);
        collapseDuplicateCursors();
    }

    public void setCursorCanvasY(double cursorCanvasY) {
        focusedCursor().setY(cursorCanvasY);
        collapseDuplicateCursors();
    }

    public void setCursor(
            double cursorCanvasX,
            double cursorCanvasY
    ) {
        focusedCursor().set(cursorCanvasX, cursorCanvasY);
        collapseDuplicateCursors();
    }

    public void setActiveCursors(
            double cursorCanvasX,
            double cursorCanvasY
    ) {
        ensureCursors();
        for (CanvasCursor cursor : cursors) {
            if (cursor.active()) {
                cursor.set(cursorCanvasX, cursorCanvasY);
            }
        }
        collapseDuplicateCursors();
    }

    public void setAllCursors(
            double cursorCanvasX,
            double cursorCanvasY
    ) {
        ensureCursors();
        for (CanvasCursor cursor : cursors) {
            cursor.set(cursorCanvasX, cursorCanvasY);
        }
        collapseDuplicateCursors();
    }

    public void addCursor(
            double cursorCanvasX,
            double cursorCanvasY
    ) {
        ensureCursors();
        cursors.add(new CanvasCursor(cursorCanvasX, cursorCanvasY, nextCursorColor(), true));
        focusedCursorIndex = cursors.size() - 1;
        collapseDuplicateCursors();
    }

    public void addCursorAvoidingCrowding(
            double cursorCanvasX,
            double cursorCanvasY,
            int emptyAreaMinimumWidth,
            int lineHeight
    ) {
        ensureCursors();
        CanvasCursor candidate = new CanvasCursor(cursorCanvasX, cursorCanvasY, nextCursorColor(), true);
        CanvasGlyph glyphAtCandidate = glyphAt(candidate, lineHeight);
        if (glyphAtCandidate != null) {
            if (cursorClosestToGlyph(glyphAtCandidate) != null) {
                return;
            }
            addCursor(cursorCanvasX, cursorCanvasY);
            return;
        }
        int safeMinimumWidth = Math.max(1, emptyAreaMinimumWidth);
        int safeLineHeight = Math.max(1, lineHeight);
        for (CanvasCursor cursor : cursors) {
            double dx = Math.abs(cursor.x() - cursorCanvasX);
            double dy = Math.abs(cursor.y() - cursorCanvasY);
            if (dx < safeMinimumWidth && dy < safeLineHeight) {
                return;
            }
        }
        addCursor(cursorCanvasX, cursorCanvasY);
    }

    public void focusPreviousCursor(boolean include) {
        focusCursor(-1, include);
    }

    public void focusNextCursor(boolean include) {
        focusCursor(1, include);
    }

    public void collapseToFocusedCursor() {
        ensureCursors();
        CanvasCursor focused = focusedCursor();
        cursors.clear();
        cursors.add(focused);
        focusedCursorIndex = 0;
        focused.setActive(true);
    }

    public void ensureCursorClosestToEachGlyph() {
        ensureCursors();
        for (CanvasGlyph glyph : glyphs) {
            CanvasCursor cursor = cursorClosestToGlyph(glyph);
            if (cursor == null) {
                cursors.add(new CanvasCursor(glyph.x(), glyph.y(), nextCursorColor(), true));
                focusedCursorIndex = cursors.size() - 1;
            } else {
                cursor.setActive(true);
            }
        }
        collapseDuplicateCursors();
    }

    public void ensureCursorClosestToEachGlyphOnActiveCursorLines(int lineHeight) {
        List<CanvasCursor> active = activeCursorsSnapshot();
        List<CanvasGlyph> nearestGlyphs = new ArrayList<>();
        for (CanvasCursor cursor : active) {
            CanvasGlyph nearest = nearestGlyph(cursor);
            if (nearest != null && !nearestGlyphs.contains(nearest)) {
                nearestGlyphs.add(nearest);
            }
        }
        List<CanvasGlyph> targetGlyphs = glyphsIntersectingAnyGlyphBounds(nearestGlyphs, lineHeight);
        if (!targetGlyphs.isEmpty() && eachGlyphHasClosestCursor(targetGlyphs)) {
            List<CanvasGlyph> nextLineGlyphs = nextVisualLineBelow(targetGlyphs, lineHeight);
            if (!nextLineGlyphs.isEmpty()) {
                targetGlyphs = nextLineGlyphs;
            }
        }
        ensureCursorClosestToEachGlyph(targetGlyphs);
        collapseDuplicateCursors();
    }

    public void discardCursorsNotClosestToAnyGlyph() {
        ensureCursors();
        if (glyphs.isEmpty()) {
            collapseToFocusedCursor();
            return;
        }

        CanvasCursor originalFocusedCursor = focusedCursor();
        List<CanvasCursor> retained = new ArrayList<>();
        for (CanvasGlyph glyph : glyphs) {
            CanvasCursor closest = closestCursorToGlyph(glyph);
            if (closest != null && !retained.contains(closest)) {
                retained.add(closest);
            }
        }
        if (retained.isEmpty()) {
            collapseToFocusedCursor();
            return;
        }

        cursors = retained;
        focusedCursorIndex = Math.max(0, retained.indexOf(originalFocusedCursor));
        for (CanvasCursor cursor : cursors) {
            cursor.setActive(true);
        }
        collapseDuplicateCursors();
    }

    public void typeGlyph(
            String text,
            int width
    ) {
        for (CanvasCursor cursor : activeCursorsSnapshot()) {
            glyphs.add(new CanvasGlyph(text, cursor.x(), cursor.y(), width));
            cursor.move(width, 0.0D);
        }
        collapseDuplicateCursors();
    }

    public void typeText(
            String text,
            GlyphWidthReader glyphWidthReader,
            int lineHeight
    ) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\r') {
                continue;
            }
            if (c == '\n') {
                moveCursorToNextLine(lineHeight);
            } else {
                String glyphText = Character.toString(c);
                typeGlyph(glyphText, glyphWidthReader.width(glyphText));
            }
        }
    }

    public void backspace() {
        deleteLeft();
    }

    public void moveCursorRaw(
            double deltaX,
            double deltaY
    ) {
        applyToActiveCursors(() -> focusedCursor().move(deltaX, deltaY));
    }

    public void deleteLeft() {
        List<CanvasCursor> active = activeCursorsSnapshot();
        if (active.size() <= 1) {
            applyToActiveCursors(this::deleteLeftFocused);
            return;
        }
        deleteNearestGlyphsTransactionally(active, false);
    }

    public void deleteNearestAndMoveRight() {
        List<CanvasCursor> active = activeCursorsSnapshot();
        if (active.size() <= 1) {
            applyToActiveCursors(this::deleteNearestAndMoveRightFocused);
            return;
        }
        deleteNearestGlyphsTransactionally(active, true);
    }

    public void moveCursorLeft() {
        moveCursorLeft(1);
    }

    public void moveCursorLeft(int lineHeight) {
        applyToActiveCursors(() -> moveCursorLeftFocused(lineHeight));
    }

    public void moveCursorRight() {
        applyToActiveCursors(this::moveCursorRightFocused);
    }

    public void moveCursorUp() {
        moveCursorUp(1);
    }

    public void moveCursorDown() {
        moveCursorDown(1);
    }

    public void moveCursorUp(int lineHeight) {
        applyToActiveCursors(() -> moveCursorVertically(-1, lineHeight, false));
    }

    public void moveCursorDown(int lineHeight) {
        applyToActiveCursors(() -> moveCursorVertically(1, lineHeight, false));
    }

    public void moveCursorUpToGlyph(int lineHeight) {
        applyToActiveCursors(() -> moveCursorVertically(-1, lineHeight, true));
    }

    public void moveCursorDownToGlyph(int lineHeight) {
        applyToActiveCursors(() -> moveCursorVertically(1, lineHeight, true));
    }

    public void moveCursorToLineStart() {
        applyToActiveCursors(this::moveCursorToLineStartFocused);
    }

    public void moveCursorToLineEnd() {
        applyToActiveCursors(this::moveCursorToLineEndFocused);
    }

    public void moveCursorToDocumentStart() {
        applyToActiveCursors(this::moveCursorToDocumentStartFocused);
    }

    public void moveCursorToDocumentEnd() {
        applyToActiveCursors(this::moveCursorToDocumentEndFocused);
    }

    public void moveCursorToNextLine(int lineHeight) {
        applyToActiveCursors(() -> moveCursorToNextLineFocused(lineHeight));
    }

    public void insertLineBreak(int lineHeight) {
        List<CanvasCursor> active = activeCursorsSnapshot();
        List<Double> breakRows = distinctSortedRows(active);
        List<CursorLineBreakTarget> cursorTargets = new ArrayList<>();
        for (CanvasCursor cursor : active) {
            cursorTargets.add(new CursorLineBreakTarget(
                    cursor,
                    cursor.y(),
                    lineStartX(cursor.y()).orElse(cursor.x())
            ));
        }

        List<CanvasGlyph> movedGlyphs = new ArrayList<>();
        for (CanvasGlyph glyph : glyphs) {
            int shiftCount = countRowsBefore(breakRows, glyph.y());
            movedGlyphs.add(new CanvasGlyph(glyph.text(), glyph.x(), glyph.y() + shiftCount * lineHeight, glyph.width()));
        }
        glyphs.clear();
        glyphs.addAll(movedGlyphs);

        for (CanvasCursor cursor : cursors) {
            int shiftCount = countRowsBefore(breakRows, cursor.y());
            if (shiftCount != 0) {
                cursor.setY(cursor.y() + shiftCount * lineHeight);
            }
        }
        for (CursorLineBreakTarget target : cursorTargets) {
            int shiftCount = countRowsBefore(breakRows, target.originalY());
            target.cursor().set(target.lineStartX(), target.originalY() + shiftCount * lineHeight + lineHeight);
        }
        collapseDuplicateCursors();
    }

    private void deleteLeftFocused() {
        if (glyphs.isEmpty()) {
            return;
        }
        moveCursorLeftFocused(1);
        CanvasGlyph deleted = glyphAtCursor();
        if (deleted == null) {
            moveCursorLeftFocused(1);
            deleted = glyphAtCursor();
        }
        if (deleted != null) {
            glyphs.remove(deleted);
        }
    }

    private void deleteNearestAndMoveRightFocused() {
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

    private void moveCursorLeftFocused(int lineHeight) {
        if (glyphs.isEmpty()) {
            focusedCursor().move(-1.0D, 0.0D);
            return;
        }

        List<CanvasGlyph> line = glyphsOnVisualLine(cursorCanvasY(), lineHeight);
        if (!line.isEmpty()) {
            CanvasGlyph containing = glyphContainingX(line, cursorCanvasX());
            CanvasGlyph left = containing == null
                               ? rightMostGlyphBefore(line, cursorCanvasX())
                               : rightMostGlyphBefore(line, containing.x());
            if (left != null) {
                setCursor(left.x(), left.y());
                return;
            }
            moveCursorToEndOfPreviousLine(line.get(0).y());
            return;
        }

        CanvasGlyph nearest = nearestGlyph();
        if (nearest == null) {
            focusedCursor().move(-1.0D, 0.0D);
            return;
        }
        List<CanvasGlyph> nearestLine = glyphsOnLine(nearest.y());
        if (cursorCanvasY() > nearest.y()) {
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

    private void moveCursorRightFocused() {
        List<CanvasGlyph> line = glyphsOnLine(cursorCanvasY());
        if (line.isEmpty()) {
            CanvasGlyph nearest = nearestGlyph();
            if (nearest == null) {
                focusedCursor().move(1.0D, 0.0D);
                return;
            }
            setCursor(nearest.x(), nearest.y());
            return;
        }
        CanvasGlyph next = leftMostGlyphAtOrAfter(line, cursorCanvasX());
        if (next == null) {
            moveCursorToStartOfNextLine(cursorCanvasY());
            return;
        }
        setCursor(next.x() + next.width(), next.y());
    }

    private void moveCursorToLineStartFocused() {
        List<CanvasGlyph> line = currentOrNearestLine();
        if (!line.isEmpty()) {
            CanvasGlyph first = line.get(0);
            setCursor(first.x(), first.y());
        } else {
            focusedCursor().setX(0.0D);
        }
    }

    private void moveCursorToLineEndFocused() {
        List<CanvasGlyph> line = currentOrNearestLine();
        moveCursorToEndOfLine(line);
    }

    private void moveCursorToDocumentStartFocused() {
        CanvasGlyph first = topmostThenLeftmostGlyph();
        if (first != null) {
            setCursor(first.x(), first.y());
        } else {
            setCursor(0.0D, 0.0D);
        }
    }

    private void moveCursorToDocumentEndFocused() {
        CanvasGlyph last = bottommostThenRightmostGlyph();
        if (last != null) {
            setCursor(last.x() + last.width(), last.y());
        }
    }

    private void moveCursorToNextLineFocused(int lineHeight) {
        CanvasGlyph nearest = nearestGlyph();
        if (nearest == null) {
            focusedCursor().move(0.0D, lineHeight);
            return;
        }

        CanvasGlyph leftMost = leftMostGlyphOnLine(nearest);
        double previousCursorCanvasY = cursorCanvasY();
        focusedCursor().set(leftMost.x(), leftMost.y() + lineHeight);
        if (nearest == leftMost && previousCursorCanvasY > nearest.y()) {
            focusedCursor().setY(Math.max(cursorCanvasY() + lineHeight, previousCursorCanvasY + lineHeight));
        }
    }

    private List<Double> distinctSortedRows(List<CanvasCursor> cursors) {
        List<Double> rows = new ArrayList<>();
        for (CanvasCursor cursor : cursors) {
            if (!rows.contains(cursor.y())) {
                rows.add(cursor.y());
            }
        }
        rows.sort(Double::compare);
        return rows;
    }

    private int countRowsBefore(
            List<Double> rows,
            double y
    ) {
        int count = 0;
        for (Double row : rows) {
            if (row < y) {
                count++;
            }
        }
        return count;
    }

    private java.util.OptionalDouble lineStartX(double y) {
        Double lineStartX = null;
        for (CanvasGlyph glyph : glyphs) {
            if (Double.compare(glyph.y(), y) == 0 && (lineStartX == null || glyph.x() < lineStartX)) {
                lineStartX = glyph.x();
            }
        }
        return lineStartX == null ? java.util.OptionalDouble.empty() : java.util.OptionalDouble.of(lineStartX);
    }

    private void deleteNearestGlyphsTransactionally(
            List<CanvasCursor> active,
            boolean moveRight
    ) {
        List<CanvasGlyph> targets = new ArrayList<>();
        List<CursorTarget> cursorTargets = new ArrayList<>();
        for (CanvasCursor cursor : active) {
            CanvasGlyph target = nearestGlyph(cursor);
            if (target == null) {
                continue;
            }
            if (!targets.contains(target)) {
                targets.add(target);
            }
            cursorTargets.add(new CursorTarget(cursor, target));
        }

        for (CursorTarget cursorTarget : cursorTargets) {
            CanvasGlyph target = cursorTarget.target();
            if (moveRight) {
                CanvasGlyph right = firstGlyphAfterOnLine(target.y(), target.x(), targets);
                if (right != null) {
                    cursorTarget.cursor().set(right.x(), right.y());
                    continue;
                }
                CanvasGlyph nextLineFirst = firstGlyphOnNextLine(target.y(), targets);
                if (nextLineFirst != null) {
                    cursorTarget.cursor().set(nextLineFirst.x(), nextLineFirst.y());
                    continue;
                }
            }
            cursorTarget.cursor().set(target.x(), target.y());
        }

        glyphs.removeAll(targets);
        collapseDuplicateCursors();
    }

    private void focusCursor(
            int direction,
            boolean include
    ) {
        ensureCursors();
        focusedCursorIndex = Math.floorMod(focusedCursorIndex + direction, cursors.size());
        if (include) {
            focusedCursor().setActive(true);
        } else {
            for (int i = 0; i < cursors.size(); i++) {
                cursors.get(i).setActive(i == focusedCursorIndex);
            }
        }
    }

    private List<CanvasCursor> activeCursorsSnapshot() {
        ensureCursors();
        List<CanvasCursor> active = new ArrayList<>();
        for (CanvasCursor cursor : cursors) {
            if (cursor.active()) {
                active.add(cursor);
            }
        }
        if (active.isEmpty()) {
            focusedCursor().setActive(true);
            active.add(focusedCursor());
        }
        return active;
    }

    private void applyToActiveCursors(CursorOperation operation) {
        List<CanvasCursor> active = activeCursorsSnapshot();
        int originalFocusedCursorIndex = focusedCursorIndex;
        for (CanvasCursor cursor : active) {
            int cursorIndex = cursors.indexOf(cursor);
            if (cursorIndex == -1) {
                continue;
            }
            focusedCursorIndex = cursorIndex;
            operation.apply();
        }
        focusedCursorIndex = cursors.isEmpty() ? 0 : clampIndex(originalFocusedCursorIndex, cursors.size());
        collapseDuplicateCursors();
    }

    private void collapseDuplicateCursors() {
        ensureCursors();
        for (int i = cursors.size() - 1; i >= 0; i--) {
            CanvasCursor cursor = cursors.get(i);
            for (int j = 0; j < i; j++) {
                CanvasCursor kept = cursors.get(j);
                if (Double.compare(cursor.x(), kept.x()) == 0 && Double.compare(cursor.y(), kept.y()) == 0) {
                    kept.setActive(kept.active() || cursor.active());
                    cursors.remove(i);
                    if (focusedCursorIndex == i) {
                        focusedCursorIndex = j;
                    } else if (focusedCursorIndex > i) {
                        focusedCursorIndex--;
                    }
                    break;
                }
            }
        }
        if (cursors.isEmpty()) {
            cursors.add(new CanvasCursor(0.0D, 0.0D, PRIMARY_CURSOR_COLOR, true));
            focusedCursorIndex = 0;
        }
        focusedCursorIndex = clampIndex(focusedCursorIndex, cursors.size());
        focusedCursor().setActive(true);
    }

    private int nextCursorColor() {
        ensureCursors();
        return switch (cursors.size() % 4) {
            case 1 -> SECONDARY_CURSOR_COLOR;
            case 2 -> 0xFFFBBF24;
            case 3 -> 0xFFA78BFA;
            default -> PRIMARY_CURSOR_COLOR;
        };
    }

    private void ensureCursors() {
        if (cursors == null) {
            cursors = new ArrayList<>();
        }
        if (cursors.isEmpty()) {
            cursors.add(new CanvasCursor(0.0D, 0.0D, PRIMARY_CURSOR_COLOR, true));
            focusedCursorIndex = 0;
        }
        focusedCursorIndex = clampIndex(focusedCursorIndex, cursors.size());
    }

    private static int clampIndex(
            int index,
            int size
    ) {
        return Math.max(0, Math.min(index, size - 1));
    }

    private CanvasGlyph nearestGlyph() {
        return nearestGlyph(focusedCursor());
    }

    private CanvasGlyph nearestGlyph(CanvasCursor cursor) {
        CanvasGlyph nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (CanvasGlyph glyph : glyphs) {
            double dx = cursor.x() - glyph.x();
            double dy = cursor.y() - glyph.y();
            double distance = dx * dx + dy * dy;
            if (distance < nearestDistance) {
                nearest = glyph;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private CanvasCursor cursorClosestToGlyph(CanvasGlyph target) {
        for (CanvasCursor cursor : cursors) {
            if (nearestGlyph(cursor) == target) {
                return cursor;
            }
        }
        return null;
    }

    private CanvasCursor closestCursorToGlyph(CanvasGlyph glyph) {
        CanvasCursor closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (CanvasCursor cursor : cursors) {
            double dx = cursor.x() - glyph.x();
            double dy = cursor.y() - glyph.y();
            double distance = dx * dx + dy * dy;
            if (distance < closestDistance) {
                closest = cursor;
                closestDistance = distance;
            }
        }
        return closest;
    }

    private void ensureCursorClosestToEachGlyph(List<CanvasGlyph> targetGlyphs) {
        for (CanvasGlyph glyph : targetGlyphs) {
            CanvasCursor cursor = cursorClosestToGlyph(glyph);
            if (cursor == null) {
                cursors.add(new CanvasCursor(glyph.x(), glyph.y(), nextCursorColor(), true));
                focusedCursorIndex = cursors.size() - 1;
            } else {
                cursor.setActive(true);
            }
        }
    }

    private boolean eachGlyphHasClosestCursor(List<CanvasGlyph> targetGlyphs) {
        for (CanvasGlyph glyph : targetGlyphs) {
            if (cursorClosestToGlyph(glyph) == null) {
                return false;
            }
        }
        return true;
    }

    private List<CanvasGlyph> glyphsIntersectingAnyGlyphBounds(
            List<CanvasGlyph> targets,
            int lineHeight
    ) {
        List<CanvasGlyph> result = new ArrayList<>();
        for (CanvasGlyph glyph : glyphs) {
            if (intersectsAnyGlyphBounds(glyph, targets, lineHeight)) {
                result.add(glyph);
            }
        }
        result.sort((left, right) -> {
            int yCompare = Double.compare(left.y(), right.y());
            if (yCompare != 0) {
                return yCompare;
            }
            return Double.compare(left.x(), right.x());
        });
        return result;
    }

    private List<CanvasGlyph> nextVisualLineBelow(
            List<CanvasGlyph> currentLineGlyphs,
            int lineHeight
    ) {
        Double currentBottomY = null;
        for (CanvasGlyph glyph : currentLineGlyphs) {
            double glyphBottomY = glyph.y() + Math.max(1, lineHeight);
            if (currentBottomY == null || glyphBottomY > currentBottomY) {
                currentBottomY = glyphBottomY;
            }
        }
        if (currentBottomY == null) {
            return List.of();
        }

        CanvasGlyph nextSeed = null;
        for (CanvasGlyph glyph : glyphs) {
            if (glyph.y() >= currentBottomY && (nextSeed == null || glyph.y() < nextSeed.y())) {
                nextSeed = glyph;
            }
        }
        if (nextSeed == null) {
            return List.of();
        }
        return glyphsIntersectingAnyGlyphBounds(List.of(nextSeed), lineHeight);
    }

    private boolean intersectsAnyGlyphBounds(
            CanvasGlyph glyph,
            List<CanvasGlyph> targets,
            int lineHeight
    ) {
        for (CanvasGlyph target : targets) {
            if (glyphVerticalBoundsIntersect(glyph, target, lineHeight)) {
                return true;
            }
        }
        return false;
    }

    private boolean glyphVerticalBoundsIntersect(
            CanvasGlyph left,
            CanvasGlyph right,
            int lineHeight
    ) {
        int safeLineHeight = Math.max(1, lineHeight);
        double leftTop = left.y();
        double leftBottom = left.y() + safeLineHeight;
        double rightTop = right.y();
        double rightBottom = right.y() + safeLineHeight;
        return leftTop < rightBottom && rightTop < leftBottom;
    }

    private CanvasGlyph glyphAtCursor() {
        for (CanvasGlyph glyph : glyphs) {
            if (Double.compare(glyph.x(), cursorCanvasX()) == 0 && Double.compare(glyph.y(), cursorCanvasY()) == 0) {
                return glyph;
            }
        }
        return null;
    }

    private CanvasGlyph glyphAt(
            CanvasCursor cursor,
            int lineHeight
    ) {
        int safeLineHeight = Math.max(1, lineHeight);
        for (CanvasGlyph glyph : glyphs) {
            if (cursor.x() >= glyph.x()
                && cursor.x() < glyph.x() + glyph.width()
                && cursor.y() >= glyph.y()
                && cursor.y() < glyph.y() + safeLineHeight) {
                return glyph;
            }
        }
        return null;
    }

    private List<CanvasGlyph> glyphsOnLine(double y) {
        return glyphsOnLine(y, List.of());
    }

    private List<CanvasGlyph> glyphsOnVisualLine(
            double y,
            int lineHeight
    ) {
        List<CanvasGlyph> line = new ArrayList<>();
        int safeLineHeight = Math.max(1, lineHeight);
        for (CanvasGlyph glyph : glyphs) {
            if (y >= glyph.y() && y < glyph.y() + safeLineHeight) {
                line.add(glyph);
            }
        }
        line.sort((a, b) -> Double.compare(a.x(), b.x()));
        return line;
    }

    private List<CanvasGlyph> glyphsOnLine(
            double y,
            List<CanvasGlyph> excluded
    ) {
        List<CanvasGlyph> line = new ArrayList<>();
        for (CanvasGlyph glyph : glyphs) {
            if (!excluded.contains(glyph) && Double.compare(glyph.y(), y) == 0) {
                line.add(glyph);
            }
        }
        line.sort((a, b) -> Double.compare(a.x(), b.x()));
        return line;
    }

    private List<CanvasGlyph> currentOrNearestLine() {
        List<CanvasGlyph> line = glyphsOnLine(cursorCanvasY());
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

    private CanvasGlyph glyphContainingX(
            List<CanvasGlyph> line,
            double x
    ) {
        for (CanvasGlyph glyph : line) {
            if (x > glyph.x() && x < glyph.x() + glyph.width()) {
                return glyph;
            }
        }
        return null;
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
            focusedCursor().setX(0.0D);
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
        return firstGlyphOnNextLine(y, List.of());
    }

    private CanvasGlyph firstGlyphOnNextLine(
            double y,
            List<CanvasGlyph> excluded
    ) {
        Double nextY = null;
        for (CanvasGlyph glyph : glyphs) {
            if (!excluded.contains(glyph) && glyph.y() > y && (nextY == null || glyph.y() < nextY)) {
                nextY = glyph.y();
            }
        }
        if (nextY == null) {
            return null;
        }
        List<CanvasGlyph> line = glyphsOnLine(nextY, excluded);
        return line.isEmpty() ? null : line.get(0);
    }

    private CanvasGlyph firstGlyphAfterOnLine(
            double y,
            double x,
            List<CanvasGlyph> excluded
    ) {
        CanvasGlyph first = null;
        for (CanvasGlyph glyph : glyphs) {
            if (excluded.contains(glyph)) {
                continue;
            }
            if (Double.compare(glyph.y(), y) == 0 && glyph.x() > x && (first == null || glyph.x() < first.x())) {
                first = glyph;
            }
        }
        return first;
    }

    private void moveCursorVertically(
            int direction,
            int lineHeight,
            boolean snapToGlyph
    ) {
        Double targetY = null;
        for (CanvasGlyph glyph : glyphs) {
            boolean candidate = direction < 0 ? glyph.y() < cursorCanvasY() : glyph.y() > cursorCanvasY();
            boolean better = targetY == null || (direction < 0 ? glyph.y() > targetY : glyph.y() < targetY);
            if (candidate && better) {
                targetY = glyph.y();
            }
        }
        if (targetY == null) {
            return;
        }
        double gapStart = direction < 0 ? targetY + lineHeight : cursorCanvasY() + lineHeight;
        double gapEnd = direction < 0 ? cursorCanvasY() : targetY;
        if (!snapToGlyph && gapEnd - gapStart >= lineHeight) {
            focusedCursor().move(0.0D, direction * lineHeight);
            return;
        }
        List<CanvasGlyph> line = glyphsOnLine(targetY);
        CanvasGlyph target = nearestGlyphByX(line, cursorCanvasX());
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

    private record CursorTarget(
            CanvasCursor cursor,
            CanvasGlyph target
    ) {
    }

    private record CursorLineBreakTarget(
            CanvasCursor cursor,
            double originalY,
            double lineStartX
    ) {
    }

    public static class CanvasCursor {
        private double x;
        private double y;
        private final int color;
        private boolean active;

        public CanvasCursor(
                double x,
                double y,
                int color,
                boolean active
        ) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.active = active;
        }

        public double x() {
            return x;
        }

        public double y() {
            return y;
        }

        public int color() {
            return color;
        }

        public boolean active() {
            return active;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void set(
                double x,
                double y
        ) {
            this.x = x;
            this.y = y;
        }

        public void move(
                double deltaX,
                double deltaY
        ) {
            this.x += deltaX;
            this.y += deltaY;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    private interface CursorOperation {
        void apply();
    }

    public interface GlyphWidthReader {
        int width(String text);
    }
}
