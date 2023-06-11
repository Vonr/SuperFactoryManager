package ca.teamdman.sfm.client.gui;

public class IndentationUtils {
    private static int findLineStart(String content, int cursorPos) {
        while (cursorPos > 0 && content.charAt(cursorPos - 1) != '\n') {
            cursorPos--;
        }
        return cursorPos;
    }

    private static int findLineEnd(String content, int cursorPos) {
        while (cursorPos < content.length() && content.charAt(cursorPos) != '\n') {
            cursorPos++;
        }
        return cursorPos;
    }

    /**
     * Indents the given content, and updates the cursor and selection.
     *
     * @param content            The content to indent
     * @param cursorPos          The index within the string of the cursor
     * @param selectionCursorPos The index within the string of the selection cursor. If equal to cursorPosition, no selection is present.
     * @return The indented content, and the new cursor and selection cursor positions
     */
    public static IndentationResult indent(String content, int cursorPos, int selectionCursorPos) {
        StringBuilder sb = new StringBuilder(content);
        int lineStart = findLineStart(content, Math.min(cursorPos, selectionCursorPos));
        int lineEnd = findLineEnd(content, Math.max(cursorPos, selectionCursorPos));
        if (lineStart == lineEnd) {
            sb.insert(lineStart, "    ");
            if (lineStart <= cursorPos) {
                cursorPos += 4;
            }
            if (lineStart <= selectionCursorPos) {
                selectionCursorPos += 4;
            }
        } else {
            while (lineStart < lineEnd) {
                sb.insert(lineStart, "    ");
                lineEnd += 4;
                if (lineStart < cursorPos) {
                    cursorPos += 4;
                }
                if (lineStart < selectionCursorPos) {
                    selectionCursorPos += 4;
                }
                lineStart = findLineEnd(sb.toString(), lineStart) + 1;
            }
        }
        return new IndentationResult(sb.toString(), cursorPos, selectionCursorPos);
    }

    /**
     * Deindents the given content, and updates the cursor and selection.
     *
     * @param content            The content to deindent
     * @param cursorPos          The index within the string of the cursor
     * @param selectionCursorPos The index within the string of the selection cursor. If equal to cursorPosition, no selection is present.
     * @return The deindented content, and the new cursor and selection cursor positions
     */
    public static IndentationResult deindent(String content, int cursorPos, int selectionCursorPos) {
        StringBuilder sb = new StringBuilder(content);
        int lineStart = findLineStart(content, Math.min(cursorPos, selectionCursorPos));
        int lineEnd = findLineEnd(content, Math.max(cursorPos, selectionCursorPos));

        while (lineStart < lineEnd) {
            for (int i = 0; i < 4 && lineStart < sb.length() && sb.charAt(lineStart) == ' '; i++) {
                sb.deleteCharAt(lineStart);
                lineEnd--;
                if (lineStart < cursorPos) {
                    cursorPos--;
                }
                if (lineStart < selectionCursorPos) {
                    selectionCursorPos--;
                }
            }
            lineStart = findLineEnd(sb.toString(), lineStart) + 1;
        }
        return new IndentationResult(sb.toString(), cursorPos, selectionCursorPos);
    }

    public record IndentationResult(
            String content,
            int cursorPosition,
            int selectionCursorPosition
    ) {
    }
}
