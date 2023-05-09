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
        SFMLLexer lexer = new SFMLLexer(CharStreams.fromString(programString));
        lexer.INCLUDE_UNUSED = true;
        CommonTokenStream tokens = new CommonTokenStream(lexer) {
            // This is a hack to make hidden tokens show up in the token stream
            @Override
            public List<Token> getHiddenTokensToRight(int tokenIndex, int channel) {
                if (channel == Token.DEFAULT_CHANNEL) {
                    return getHiddenTokensToRight(tokenIndex, Token.HIDDEN_CHANNEL);
                } else {
                    return super.getHiddenTokensToRight(tokenIndex, channel);
                }
            }
        };
        List<MutableComponent> textComponents = new ArrayList<>();
        MutableComponent lineComponent = Component.empty();

        for (Token token = tokens.LT(1); tokens.LA(1) != Token.EOF; token = tokens.LT(1)) {
            // the token may contain newlines in it, so we need to split it up
            String[] lines = token.getText().split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                if (i != 0) {
                    textComponents.add(lineComponent);
                    lineComponent = Component.empty();
                }
                String line = lines[i];
                if (!line.isEmpty()) {
                    var text = Component.literal(line).withStyle(getStyle(token));
                    lineComponent = lineComponent.append(text);
                }
            }
            List<Token> hiddenTokens = tokens.getHiddenTokensToRight(tokens.index(), Token.DEFAULT_CHANNEL);
            if (hiddenTokens != null) {
                for (Token hiddenToken : hiddenTokens) {
                    // the whitespace token often contains newlines, so we need to split it up
                    var whitespace = hiddenToken.getText();
                    String[] wsLines = whitespace.split("\n", -1);
                    for (int i = 0; i < wsLines.length; i++) {
                        if (i != 0) {
                            textComponents.add(lineComponent);
                            lineComponent = Component.empty();
                        }
                        String wsLine = wsLines[i];
                        if (!wsLine.isEmpty()) {
                            var ws = Component.literal(wsLine).withStyle(getStyle(hiddenToken));
                            lineComponent = lineComponent.append(ws);
                        }
                    }
                }
            }
            tokens.consume();
        }

        // Add the last lineComponent to textComponents
//        if (!lineComponent.equals(Component.empty())) {
        textComponents.add(lineComponent);
//        }

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
            case SFMLLexer.NAME:
            case SFMLLexer.EVERY:
            case SFMLLexer.END:
            case SFMLLexer.DO:
            case SFMLLexer.IF:
            case SFMLLexer.ELSE:
                return ChatFormatting.BLUE;
            case SFMLLexer.IDENTIFIER:
            case SFMLLexer.STRING:
                return ChatFormatting.GREEN;
            case SFMLLexer.TICKS:
            case SFMLLexer.SLOTS:
                return ChatFormatting.GOLD;
            case SFMLLexer.NUMBER:
                return ChatFormatting.AQUA;
            case SFMLLexer.UNUSED:
                return ChatFormatting.RED;
            default:
                return ChatFormatting.WHITE;
        }
    }
}
