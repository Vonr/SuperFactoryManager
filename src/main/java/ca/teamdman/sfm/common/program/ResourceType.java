package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
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
        if (!matchesStackType(o)) return false;

        if (isEmpty((STACK) o)) return false;
        var key = getRegistryKey((STACK) o);
        if (key == null) return false;

        var nsPattern    = Pattern.compile(id.resourceNamespace());
        var namePattern  = Pattern.compile(id.resourceName());
        var keyNamespace = key.getNamespace();
        var keyName      = key.getPath();
        return nsPattern.matcher(keyNamespace).matches() && namePattern.matcher(keyName).matches();
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

    public abstract boolean registryKeyExists(ResourceLocation location);

    public abstract ResourceLocation getRegistryKey(STACK stack);
}
