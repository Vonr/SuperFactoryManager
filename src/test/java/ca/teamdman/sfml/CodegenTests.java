package ca.teamdman.sfml;

import ca.teamdman.sfml.ast.Number;
import ca.teamdman.sfml.ast.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodegenTests {

    @Test
    public void resourcesidk() {
        var id = ResourceIdentifier.fromString(".*");
        System.out.println(id);
        System.out.println(id.toStringCondensed());
    }

    @Test
    public void example() {

        var input = """
                    name "hello world"

                    every 20 ticks do
                        input from a
                        if a has gt 10 * then
                            output to b
                        end
                    end
                """;

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
                System.out.println(program);
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


        assertTrue(errors.isEmpty());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void codegen() {
        var aLabel =
                new LabelAccess(
                        List.of(new Label("a")),
                        DirectionQualifier.NULL_DIRECTION,
                        NumberRangeSet.MAX_RANGE,
                        RoundRobin.disabled()
                );
        var program = new Program(
                "hello world",
                List.of(new TimerTrigger(
                        Interval.fromTicks(20),
                        new Block(List.of(
                                new IfStatement(
                                        new ResourceComparer(
                                                ComparisonOperator.GREATER_OR_EQUAL,
                                                new ResourceQuantity(
                                                        new Number(10L),
                                                        ResourceQuantity.IdExpansionBehaviour.NO_EXPAND
                                                ),
                                                ResourceIdentifier.fromString("sfm:item:.*:.*")
                                        ).toBooleanExpression(SetOperator.OVERALL, aLabel, "gt 10 sfm:item:.*:.*"),
                                        new Block(List.of( // if true
                                                           new InputStatement(
                                                                   aLabel,
                                                                   new ResourceLimits(
                                                                           List.of(ResourceLimit.TAKE_ALL_LEAVE_NONE),
                                                                           ResourceIdSet.EMPTY
                                                                   ),
                                                                   // empty exclusion list
                                                                   false
                                                                   // each=false
                                                           )
                                        )),
                                        new Block(List.of()) // if false
                                )
                        ))
                )),
                Collections.emptySet(), // not needed for codegen
                Collections.emptySet() // not needed for codegen
        );
        System.out.println(program);
        /* outputs:
        NAME "hello world"
        EVERY 20 TICKS DO
         IF a HAS gt 10 sfm:item:.*:.* THEN
          INPUT .* FROM a
         END
        END
         */
    }
}
