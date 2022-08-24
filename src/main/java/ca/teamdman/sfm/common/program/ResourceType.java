package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, CAP> {
    public final Capability<CAP> CAPABILITY;

    public ResourceType(Capability<CAP> CAPABILITY) {
        this.CAPABILITY = CAPABILITY;
    }

    public abstract int getCount(STACK stack);

    public abstract STACK getStackInSlot(CAP cap, int slot);

    public abstract STACK extract(CAP cap, int slot, int amount, boolean simulate);

    public abstract int getSlots(CAP handler);

    public abstract STACK insert(CAP cap, int slot, STACK stack, boolean simulate);

    public abstract boolean isEmpty(STACK stack);

    public abstract boolean matchesStackType(Object o);

    public boolean test(ResourceIdentifier<STACK, CAP> id, Object o) {
        // match everything if all wildcards
        if (id.type().equals("*") && id.domain().equals("*") && id.value().equals("*")) return true;
        // must match type otherwise
        if (!matchesStackType(o)) return false;
        // match wildcard
        if (id.domain().equals("*") && id.value().equals("*")) return true;

        // lookup key for other
        var key = getKey((STACK) o);
        if (key == null) return false;

        // match if either wildcards
        if (id.domain().equals("*")) return id.value().equals(key.getPath());
        if (id.value().equals("*")) return id.domain().equals(key.getNamespace());

        // match both properties
        return id.domain().equals(key.getNamespace()) && id.value().equals(key.getPath());
    }


    public abstract boolean matchesCapType(Object o);

    public Stream<CAP> getCaps(
            ProgramContext programContext, LabelAccess labelAccess
    ) {
        var disk = programContext.getManager().getDisk();
        if (disk.isEmpty()) return Stream.empty();
        return SFMLabelNBTHelper
                .getPositions(disk.get(), labelAccess.labels())
                .map(programContext.getNetwork()::getInventory)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap((
                                 prov -> labelAccess
                                         .directions()
                                         .stream()
                                         .map(direction -> prov.getCapability(CAPABILITY, direction))
                         ))
                .map(x -> x.orElse(null))
                .filter(Objects::nonNull);
    }

    public abstract Stream<STACK> collect(CAP cap, LabelAccess labelAccess);

    public abstract boolean containsKey(ResourceLocation location);

    public abstract ResourceLocation getKey(STACK stack);
}
