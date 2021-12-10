package ca.teamdman.sfml;

import ca.teamdman.sfml.ast.ASTBuilder;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

public class SFML {
    @Test
    public void Test() {
        //        var input = """
        //                     world
        //                         blood_altar
        //                         input_chests
        //                         output_chests
        //                     end
        //
        //                     program
        //                         every 20 ticks do
        //                             input from blood_altar
        //                             if blood_altar has gt 1 bucket bloodmagic:blood:
        //                                 output to blood_altar
        //                             end
        //                         end
        //                     end
        //                """;
        var input = """
                    WORLD
                        in
                        out
                        altar
                    END
                    PROGRAM
                        EVERY 20 TICKS DO
                            INPUT FROM input_chests
                            OUTPUT TO blood_altar
                        END
                    END
                """;
        var lexer   = new SFMLLexer(CharStreams.fromString(input));
        var tokens  = new CommonTokenStream(lexer);
        var parser  = new SFMLParser(tokens);
        var builder = new ASTBuilder();

        //        parser.addErrorListener(new ConsoleErrorListener());
        var context = parser.start();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new RuntimeException("syn error");
        }

        var start = builder.visitStart(context);
        System.out.println("Good!");
    }
}
