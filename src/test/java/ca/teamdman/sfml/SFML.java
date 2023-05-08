package ca.teamdman.sfml;

import ca.teamdman.sfm.client.ProgramSyntaxHighlightingHelper;
import ca.teamdman.sfml.ast.ASTBuilder;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import net.minecraft.network.chat.Component;
import org.antlr.v4.runtime.*;
import org.apache.commons.compress.utils.FileNameUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SFML {
    public ArrayList<String> getCompileErrors(String input) {
        var lexer = new SFMLLexer(CharStreams.fromString(input));
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);
        var builder = new ASTBuilder();
        var errors = new ArrayList<String>();
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(
                    Recognizer<?, ?> recognizer,
                    Object offendingSymbol,
                    int line,
                    int charPositionInLine,
                    String msg,
                    RecognitionException e
            ) {
                errors.add("build syntax error: line " + line + ":" + charPositionInLine + " " + msg);
            }
        });
        var context = parser.program();
        if (errors.isEmpty()) { // don't build if syntax errors present
            try {
                //noinspection unused
                var program = builder.visitProgram(context);
            } catch (Exception e) {
                var sw = new StringWriter();
                var pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                errors.add("exception during visitProgram: " + sw);
            }
        }

        for (var error : errors) {
            System.out.println(error);
        }

        return errors;
    }

    @Test
    public void simpleComparisons() {
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
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void wildcardResourceIdentifiers() {
        var input = """
                name "hello world"
                                
                every 20 ticks do
                    INPUT fluid:minecraft:water from a TOP SIDE
                    OUTPUT fluid:*:* to b
                    OUTPUT minecraft:* to b
                    OUTPUT *:iron_ingot to b
                    OUTPUT *:*:* to b
                    OUTPUT *:* to b
                    OUTPUT * to b
                    OUTPUT ".*:.*:.*" to b
                    OUTPUT ".*:.*" to b
                    OUTPUT ".*" to b
                end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void quotedResourceIdentifiers() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT "redstone" to b
                    OUTPUT "minecraft:iron_ingot" to b
                    OUTPUT "item:minecraft:gold_ingot" to b
                END
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void malformedResourceIdentifier1() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT minecraft:"redstone" to b
                END
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void malformedResourceIdentifier2() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT "minecraft":"redstone" to b
                END
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void malformedResourceIdentifier3() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT "item":minecraft:redstone to b
                END
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void malformedResourceIdentifier4() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT item:minecraft:redstone to b
                END
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void malformedResourceIdentifier5() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT minecraft:redstone to b
                END
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void malformedResourceIdentifier6() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT redstone to b
                END
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }


    @Test
    public void comments() {
        var input = """
                EVERY 20 TICKS DO
                    INPUT FROM a -- hehehehaw
                    OUTPUT "minecraft":"redstone" to b
                END
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void syntaxHighlighting() {
        var input = """
                EVERY 20 TICKS DO
                                
                    INPUT FROM a -- hehehehaw
                    
                    -- we want to test to make sure whitespace is preserved
                    -- in the
                    
                    -- syntax highlighting
                    
                    INPUT FROM hehehehehehehehehhe
                    
                    OUTPUT stone to b
                END
                """.stripIndent();
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var colourLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(input);
        var lines = input.split("\n", -1);
        assertEquals(input, colourLines.stream().map(Component::getString).collect(Collectors.joining("\n")));
        assertEquals(lines.length, colourLines.size());
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], colourLines.get(i).getString());
        }
    }

    @Test
    public void booleanHasOperator() {
        var input = """
                name "hello world"
                                
                every 20 ticks do
                    input from a
                    if a has gt 100 energy:minecraft:iron then
                        output to b
                    end
                end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
    }


    @Test
    public void quotedLabels() {
        var input = """
                name "hello world"
                                
                every 20 ticks do
                    input from "hehe beans 😀"
                    output to "haha benis"
                end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void basicResourceIdentifier() {
        var identifier = ResourceIdentifier.fromString("wool");
        assertEquals("sfm:item:minecraft:wool", identifier.toString());
    }


    @Test
    public void demos() throws IOException {
        var rootDir = System.getProperty("user.dir");
        var examplesDir = Paths.get(rootDir, "examples").toFile();
        var found = 0;
        for (var entry : examplesDir.listFiles()) {
            if (!FileNameUtils.getExtension(entry.getPath()).equals("sfm")) continue;
            System.out.println("Reading " + entry);
            var content = Files.readString(entry.toPath());
            var errors = getCompileErrors(content);
            assertTrue(errors.isEmpty());
            found++;
        }
        assertTrue(found > 0);
    }
}
