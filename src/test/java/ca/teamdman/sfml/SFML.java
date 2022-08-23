package ca.teamdman.sfml;

import ca.teamdman.sfml.ast.ASTBuilder;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

public class SFML {
    @Test
    public void Test() {
        var input = """
                name "hello world"
                                
                every 20 ticks do
                    input from a
                    if a has gt 100 iron then
                        output to b
                    else if a has gt 50 iron then
                        output to c
                    else if a has gt 10 iron then
                        output to d
                    else if a has gt 2 iron then
                        output to e
                    end
                end
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


    @Test
    public void TestBooleanHas() {
        var input = """
                name "hello world"
                                
                every 20 ticks do
                    input from a
                    if a has gt 100 energy:minecraft:iron then
                        output to b
                    end
                end
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

    @Test
    public void ResourceIdentifierTest() {
        var x = "wool";
        System.out.println(ResourceIdentifier.fromString(x));
    }
}
