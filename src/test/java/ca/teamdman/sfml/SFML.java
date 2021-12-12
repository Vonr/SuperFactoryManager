package ca.teamdman.sfml;

import ca.teamdman.sfml.ast.ASTBuilder;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

public class SFML {
    @Test
    public void Test() {
        var input = """
                    NAME "NAME TEST WITH \\"ESCAPED STR\\" INGS"
                    EVERY 20 TICKS DO
                        INPUT FROM input_chests TOP SIDE
                        OUTPUT TO altar
                    END
                """;
        var lexer   = new SFMLLexer(CharStreams.fromString(input));
        var tokens  = new CommonTokenStream(lexer);
        var parser  = new SFMLParser(tokens);
        var builder = new ASTBuilder();

        //        parser.addErrorListener(new ConsoleErrorListener());
        var context = parser.program();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new RuntimeException("syn error");
        }

        var program = builder.visitProgram(context);
        System.out.println("Good!");
    }
}
