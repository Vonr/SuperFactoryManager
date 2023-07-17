package ca.teamdman.sfm.client;

import ca.teamdman.sfm.common.net.ServerboundInputInspectionRequestPacket;
import ca.teamdman.sfm.common.net.ServerboundLabelInspectionRequestPacket;
import ca.teamdman.sfm.common.net.ServerboundOutputInspectionRequestPacket;
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
                    .map(node -> getContextAction(programString, builder, node))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        } catch (Throwable t) {
            return Optional.of(() -> ClientStuff.showProgramEditScreen("-- Encountered error, program parse failed:\n--"
                                                                       + t.getMessage(), next -> {
            }));
        }
    }

    public static Optional<Runnable> getContextAction(String programString, ASTBuilder builder, ASTNode node) {
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
            int nodeIndex = builder.getIndexForNode(node);
            return Optional.of(() -> SFMPackets.INSPECTION_CHANNEL.sendToServer(new ServerboundInputInspectionRequestPacket(
                    programString,
                    nodeIndex
            )));
        } else if (node instanceof OutputStatement) {
            int nodeIndex = builder.getIndexForNode(node);
            return Optional.of(() -> SFMPackets.INSPECTION_CHANNEL.sendToServer(new ServerboundOutputInspectionRequestPacket(
                    programString,
                    nodeIndex
            )));
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
