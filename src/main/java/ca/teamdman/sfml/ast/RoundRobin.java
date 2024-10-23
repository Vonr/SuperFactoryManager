package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.LabelPositionHolder;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class RoundRobin implements ASTNode {
    private final Behaviour behaviour;
    private int nextIndex = 0;

    public RoundRobin(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public static RoundRobin disabled() {
        return new RoundRobin(Behaviour.UNMODIFIED);
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    public int next(int length) {
        // this never exists long enough to roll over
        return nextIndex++ % length;
    }

    @Override
    public String toString() {
        return switch (behaviour) {
            case UNMODIFIED -> "NOT ROUND ROBIN";
            case BY_BLOCK -> "ROUND ROBIN BY BLOCK";
            case BY_LABEL -> "ROUND ROBIN BY LABEL";
        };
    }

    public boolean isEnabled() {
        return behaviour != Behaviour.UNMODIFIED;
    }

    public ArrayList<Pair<Label, BlockPos>> getPositionsForLabels(
            List<Label> labels,
            LabelPositionHolder labelPositionHolder
    ) {
        ArrayList<Pair<Label, BlockPos>> positions = new ArrayList<>();
        switch (getBehaviour()) {
            case BY_LABEL -> {
                int index = next(labels.size());
                Label label = labels.get(index);
                for (BlockPos pos : labelPositionHolder.getPositions(label.name())) {
                    positions.add(Pair.of(label, pos));
                }
            }
            case BY_BLOCK -> {
                List<Pair<Label, BlockPos>> candidates = new ArrayList<>();
                LongOpenHashSet seen = new LongOpenHashSet();
                for (Label label : labels) {
                    for (BlockPos pos : labelPositionHolder.getPositions(label.name())) {
                        if (!seen.add(pos.asLong())) continue;
                        candidates.add(Pair.of(label, pos));
                    }
                }
                if (!candidates.isEmpty()) {
                    positions.add(candidates.get(next(candidates.size())));
                }
            }
            case UNMODIFIED -> {
                for (Label label : labels) {
                    for (BlockPos pos : labelPositionHolder.getPositions(label.name())) {
                        positions.add(Pair.of(label, pos));
                    }
                }
            }
        }
        return positions;
    }

    public enum Behaviour {
        UNMODIFIED,
        BY_BLOCK,
        BY_LABEL
    }
}
