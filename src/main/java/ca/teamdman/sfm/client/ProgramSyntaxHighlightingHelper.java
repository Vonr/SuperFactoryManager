package ca.teamdman.sfm.client;

import ca.teamdman.sfml.SFMLLexer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class ProgramSyntaxHighlightingHelper {


    public static List<MutableComponent> withSyntaxHighlighting(String programString) {
        SFMLLexer              lexer          = new SFMLLexer(CharStreams.fromString(programString));
        CommonTokenStream      tokens         = new CommonTokenStream(lexer);
        List<MutableComponent> textComponents = new ArrayList<>();


        int              currentLine      = tokens.LT(1).getLine();
        MutableComponent lineComponent    = Component.empty();
        int              previousTokenEnd = 0;

        for (Token token = tokens.LT(1); ; token = tokens.LT(1)) {
            if (tokens.LA(1) == Token.EOF) break;
            if (token.getLine() != currentLine) {
                // Add the completed line to textComponents and start a new lineComponent
                textComponents.add(lineComponent);
                lineComponent    = Component.empty();
                currentLine      = token.getLine();
                previousTokenEnd = 0;
            }

            // Add whitespace between tokens based on char position
            int whitespaceCount = token.getCharPositionInLine() - previousTokenEnd;
            if (whitespaceCount > 0) {
                lineComponent = lineComponent.append(Component.literal(" ".repeat(whitespaceCount)));
            }

            lineComponent    = lineComponent.append(Component.literal(token.getText()).withStyle(getStyle(token)));
            previousTokenEnd = token.getCharPositionInLine() + token.getText().length();
            tokens.consume();
        }

// Add the last lineComponent to textComponents
        textComponents.add(lineComponent);

        return textComponents;
    }

    private static ChatFormatting getStyle(Token token) {
        switch (token.getType()) {
            case SFMLLexer.SIDE:
            case SFMLLexer.TOP:
            case SFMLLexer.BOTTOM:
            case SFMLLexer.NORTH:
            case SFMLLexer.SOUTH:
            case SFMLLexer.EAST:
            case SFMLLexer.WEST:
                return ChatFormatting.DARK_PURPLE;
            case SFMLLexer.LINE_COMMENT:
                return ChatFormatting.GRAY;
            case SFMLLexer.INPUT:
            case SFMLLexer.FROM:
            case SFMLLexer.TO:
            case SFMLLexer.OUTPUT:
                return ChatFormatting.LIGHT_PURPLE;
            case SFMLLexer.EVERY:
            case SFMLLexer.END:
            case SFMLLexer.DO:
            case SFMLLexer.IF:
            case SFMLLexer.ELSE:
                return ChatFormatting.BLUE;
            case SFMLLexer.IDENTIFIER:
                return ChatFormatting.GREEN;
            case SFMLLexer.TICKS:
            case SFMLLexer.SLOTS:
                return ChatFormatting.GOLD;
            case SFMLLexer.NUMBER:
                return ChatFormatting.AQUA;
            default:
                return ChatFormatting.WHITE;
        }
    }
}
