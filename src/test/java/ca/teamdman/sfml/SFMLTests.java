package ca.teamdman.sfml;

import ca.teamdman.sfm.client.ProgramSyntaxHighlightingHelper;
import ca.teamdman.sfm.client.ProgramTokenContextActions;
import ca.teamdman.sfml.ast.ASTBuilder;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import com.google.common.collect.Sets;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
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

@SuppressWarnings("unchecked")
public class SFMLTests {
    public ArrayList<String> getCompileErrors(String input) {
        var lexer = new SFMLLexer(CharStreams.fromString(input));
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);
        var builder = new ASTBuilder();
        var errors = new ArrayList<String>();
        lexer.removeErrorListeners();
        lexer.addErrorListener(new Program.ListErrorListener(errors));
        parser.removeErrorListeners();
        parser.addErrorListener(new Program.ListErrorListener(errors));
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
    public void resourceIdentifierClassLoadingRegression() {
        new ResourceIdentifier<ItemStack, Item, IItemHandler>("stone");
    }

    public Program compile(String input) {
        var lexer = new SFMLLexer(CharStreams.fromString(input));
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);
        var builder = new ASTBuilder();
        var context = parser.program();
        return builder.visitProgram(context);
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
    public void resource1() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input item:minecraft:stick from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>("item", "minecraft", "stick")),
                program.referencedResources()
        );
    }

    @Test
    public void resource2() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input item::stick from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>("item", ".*", "stick")),
                program.referencedResources()
        );
    }

    @Test
    public void resource3() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input item::stick from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>("item", ".*", "stick")),
                program.referencedResources()
        );
    }

    @Test
    public void resource4() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input stick from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>("item", ".*", "stick")),
                program.referencedResources()
        );
    }

    @Test
    public void resource5() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input fluid::water from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>("fluid", ".*", "water")),
                program.referencedResources()
        );
    }

    @Test
    public void resource6() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input fluid:minecraft:water from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>(
                        "fluid",
                        "minecraft",
                        "water"
                )),
                program.referencedResources()
        );
    }

    @Test
    public void resource7() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input fluid:: from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>("fluid", ".*", ".*")),
                program.referencedResources()
        );
    }

    @Test
    public void badResource() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input :fluid:: from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void resource8() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input forge_energy:forge:energy from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>(
                        "forge_energy",
                        "forge",
                        "energy"
                )),
                program.referencedResources()
        );
    }

    @Test
    public void resource9() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input forge_energy:forge:energy from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>(
                        "forge_energy",
                        "forge",
                        "energy"
                )),
                program.referencedResources()
        );
    }

    @Test
    public void resource10() {
        var input = """
                    name "hello world"
                                    
                    every 20 ticks do
                        input gas::ethylene from a
                    end
                """;
        var errors = getCompileErrors(input);
        assertTrue(errors.isEmpty());
        var program = compile(input);
        assertEquals(
                Sets.newHashSet(new ResourceIdentifier<FluidStack, Fluid, IFluidHandler>("gas", ".*", "ethylene")),
                program.referencedResources()
        );
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
        assertTrue(errors.isEmpty()); // redstone is now a valid resource id
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
        assertTrue(errors.isEmpty()); // redstone is now a valid resource id
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
        assertTrue(errors.isEmpty()); // redstone is now a valid resource id
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
    public void syntaxHighlighting1() {
        var rawInput = """
                EVERY 20 TICKS DO
                                
                    INPUT FROM a''" -- hehehehaw
                    -- we want there to be no issues highlighting even if errors are present
                    "'''''
                    
                    -- we want to test to make sure whitespace is preserved
                    -- in the
                    
                    -- syntax highlighting
                    
                    INPUT FROM hehehehehehehehehhe
                    
                    OUTPUT stone to b
                END
                """.stripIndent();
        var errors = getCompileErrors(rawInput);
        assertFalse(errors.isEmpty());
        var lines = rawInput.split("\n", -1);

        var colouredLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(rawInput, false);
        String colouredInput = colouredLines.stream().map(Component::getString).collect(Collectors.joining("\n"));

        assertEquals(rawInput, colouredInput);

        // newlines should not be present
        // instead, each line should be its own component
        assertFalse(colouredLines.stream().anyMatch(x -> x.getString().contains("\n")));

        assertEquals(lines.length, colouredLines.size());
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], colouredLines.get(i).getString());
        }
    }


    @Test
    public void syntaxHighlighting2() {
        var rawInput = """
                EVERY 20 TICKS DO
                                
                    INPUT FROM a
                    INPUT FROM hehehehehehehehehhe
                    
                    OUTPUT stone to b
                END
                """.stripIndent();
        var errors = getCompileErrors(rawInput);
        assertTrue(errors.isEmpty());
        var lines = rawInput.split("\n", -1);

        var colouredLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(rawInput, false);
        String colouredInput = colouredLines.stream().map(Component::getString).collect(Collectors.joining("\n"));

        assertEquals(rawInput, colouredInput);

        // newlines should not be present
        // instead, each line should be its own component
        assertFalse(colouredLines.stream().anyMatch(x -> x.getString().contains("\n")));

        assertEquals(lines.length, colouredLines.size());
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], colouredLines.get(i).getString());
        }
    }

    @Test
    public void syntaxHighlightingWhitespaceRegression1() {
        // the empty newline is important
        var rawInput = """
                    EVERY 20 TICKS DO
                --test
                        INPUT FROM a
                        OUTPUT TO b
                    END""";
        var errors = getCompileErrors(rawInput);
        assertTrue(errors.isEmpty());
        var lines = rawInput.split("\n", -1);

        var colouredLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(rawInput, false);
        String colouredInput = colouredLines.stream().map(Component::getString).collect(Collectors.joining("\n"));

        assertEquals(rawInput, colouredInput);

        // newlines should not be present
        // instead, each line should be its own component
        assertFalse(colouredLines.stream().anyMatch(x -> x.getString().contains("\n")));

        assertEquals(lines.length, colouredLines.size());
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], colouredLines.get(i).getString());
        }
    }

    @Test
    public void syntaxHighlightingWhitespaceRegression2() {
        // the empty newline is important
        var rawInput = """
                    
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT TO b
                END""";
        var errors = getCompileErrors(rawInput);
        assertTrue(errors.isEmpty());
        var lines = rawInput.split("\n", -1);

        var colouredLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(rawInput, false);
        String colouredInput = colouredLines.stream().map(Component::getString).collect(Collectors.joining("\n"));

        assertEquals(rawInput, colouredInput);

        // newlines should not be present
        // instead, each line should be its own component
        assertFalse(colouredLines.stream().anyMatch(x -> x.getString().contains("\n")));

        assertEquals(lines.length, colouredLines.size());
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], colouredLines.get(i).getString());
        }
    }


    @Test
    public void syntaxHighlighting3() {
        var rawRawInput = """
                EVERY 20 TICKS DO
                                
                    INPUT FROM a
                    INPUT FROM hehehehehehehehehhe
                    
                    OUTPUT stone to b
                END
                """.stripIndent();
        String[] rawRawLines = rawRawInput.split("\n");
        for (int i = 0; i < rawRawLines.length; i++) {
            var rawInput = java.util.Arrays.stream(rawRawLines, 0, i)
                    .collect(Collectors.joining("\n"));
            var lines = rawInput.split("\n", -1);

            var colouredLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(rawInput, false);
            String colouredInput = colouredLines.stream().map(Component::getString).collect(Collectors.joining("\n"));

            assertEquals(rawInput, colouredInput);

            // newlines should not be present
            // instead, each line should be its own component
            assertFalse(colouredLines.stream().anyMatch(x -> x.getString().contains("\n")));

            assertEquals(lines.length, colouredLines.size());
            for (int j = 0; j < lines.length; j++) {
                assertEquals(lines[j], colouredLines.get(j).getString());
            }
        }
    }


    @Test
    public void syntaxHighlightingUnusedToken() {
        var rawInput = """
                EVERY 20 TICKS DO
                                
                    INPUT FROM a
                    INPUT FROM hehehehehehehehehhe=
                    
                    OUTPUT stone to b
                END
                """.stripIndent();
        var errors = getCompileErrors(rawInput);
        assertFalse(errors.isEmpty());
        var lines = rawInput.split("\n", -1);

        var colouredLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(rawInput, false);
        String colouredInput = colouredLines.stream().map(Component::getString).collect(Collectors.joining("\n"));

        assertEquals(rawInput, colouredInput);

        // newlines should not be present
        // instead, each line should be its own component
        assertFalse(colouredLines.stream().anyMatch(x -> x.getString().contains("\n")));

        assertEquals(lines.length, colouredLines.size());
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], colouredLines.get(i).getString());
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
        assertEquals("sfm:item:.*:wool", identifier.toString());
    }


    @Test
    public void demos() throws IOException {
        var rootDir = System.getProperty("user.dir");
        var examplesDir = Paths.get(rootDir, "examples").toFile();
        var found = 0;
        //noinspection DataFlowIssue
        for (var entry : examplesDir.listFiles()) {
            if (!FileNameUtils.getExtension(entry.getPath()).equals("sfm")) continue;
            System.out.println("Reading " + entry);
            var content = Files.readString(entry.toPath());
            var errors = getCompileErrors(content);
            assertTrue(errors.isEmpty());
            found++;
        }
        assertNotEquals(0, found);
    }

    @Test
    public void templates() throws IOException {
        var rootDir = System.getProperty("user.dir");
        var examplesDir = Paths.get(rootDir, "src/main/resources/assets/sfm/template_programs").toFile();
        var found = 0;
        //noinspection DataFlowIssue
        for (var entry : examplesDir.listFiles()) {
            assertEquals("sfml", FileNameUtils.getExtension(entry.getPath()));
            System.out.println("Reading " + entry);
            var content = Files.readString(entry.toPath());
            content = content.replace("$REPLACE_RESOURCE_TYPES_HERE$", "");
            var errors = getCompileErrors(content);
            assertTrue(errors.isEmpty());
            found++;
        }
        assertNotEquals(0, found);
    }

    @Test
    public void symbolUnderCursor1() {
        var programString = """
                NAME "test"
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT TO b
                END
                """.stripTrailing().stripIndent();
        var cursorPos = programString.indexOf("INPUT") + 2;
        var x = ProgramTokenContextActions.getContextAction(programString, cursorPos);
        assertTrue(x.isPresent());
    }
}
