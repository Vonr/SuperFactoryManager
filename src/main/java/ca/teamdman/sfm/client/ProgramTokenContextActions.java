package ca.teamdman.sfm.client;

import ca.teamdman.sfm.common.net.ServerboundLabelInspectionRequestPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfml.SFMLLexer;
import ca.teamdman.sfml.SFMLParser;
import ca.teamdman.sfml.ast.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramTokenContextActions {

    public static Optional<Runnable> getContextAction(String programString, int cursorPosition) {
        var lexer = new SFMLLexer(CharStreams.fromString(programString));
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);
        var builder = new ASTBuilder();
        try {
            builder.visitProgram(parser.program());
            System.out.println("hehaw");
            return Stream.concat(
                            builder
                                    .getNodesUnderCursor(cursorPosition)
                                    .stream(),
                            builder
                                    .getNodesUnderCursor(cursorPosition - 1)
                                    .stream()
                    )
                    .map(ProgramTokenContextActions::getContextAction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static Optional<Runnable> getContextAction(ASTNode node) {
        System.out.println("CONTEXT ACTION " + node);
        if (node instanceof ResourceIdentifier<?, ?, ?> rid) {
            return Optional.of(() -> {
                String expansion = rid
                        .expand()
                        .stream()
                        .map(ResourceIdentifier::toStringCondensed)
                        .collect(Collectors.joining(",\n"));
                ClientStuff.showProgramEditScreen(expansion, next -> {
                });
            });
        } else if (node instanceof Label label) {
            return Optional.of(() -> SFMPackets.INSPECTION_CHANNEL.sendToServer(new ServerboundLabelInspectionRequestPacket(
                    label.name()
            )));
        } else if (node instanceof InputStatement) {
            return Optional.of(() -> {
                InputStatement inputStatement = (InputStatement) node;
                System.out.println("SHOWING " + inputStatement
                        .resourceLimits()
                        .resourceLimits()
                        .stream()
                        .map(rl -> rl.resourceId().toString())
                        .collect(Collectors.joining(", ")));
            });
        }
        return Optional.empty();
    }

    public static boolean hasContextAction(Token token) {
        return switch (token.getType()) {
            case SFMLLexer.INPUT, SFMLLexer.OUTPUT, SFMLLexer.IDENTIFIER -> true;
            default -> false;
        };
    }
}
