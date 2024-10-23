package ca.teamdman.sfm.common.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public enum FacadeType implements StringRepresentable {
    NONE, OPAQUE_FACADE, TRANSLUCENT_FACADE;
    public static final EnumProperty<FacadeType> FACADE_TYPE = EnumProperty.create("facade_type", FacadeType.class);

    FacadeType() {}

    @Override
    public String getSerializedName() {
        return switch (this) {
            case NONE -> "none";
            case OPAQUE_FACADE -> "opaque";
            case TRANSLUCENT_FACADE -> "translucent";
        };
    }
}