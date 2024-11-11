package ca.teamdman.sfm.common.compat;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.Event;

import java.util.Set;

public class SFMCompatGatherCapabilitiesEvent extends Event {
    private final Set<Capability<?>> capabilities;

    SFMCompatGatherCapabilitiesEvent(Set<Capability<?>> capabilities) {
        this.capabilities = capabilities;
    }

    public boolean add(Capability<?> capability) {
        return capabilities.add(capability);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
