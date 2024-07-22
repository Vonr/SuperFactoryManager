package ca.teamdman.sfm.common.datacomponent;

import ca.teamdman.sfm.common.program.LabelPositionHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DiskItemDataComponent(
        String program,
        LabelPositionHolder labelPositionHolder
) {
    public static final DataComponentType<DiskItemDataComponent> DISK_DATA_COMPONENT_TYPE = DataComponentType
            .<DiskItemDataComponent>builder()
            .cacheEncoding()
            .build();

    // MIGRATION TODO: disk stream codec
    public static final StreamCodec<ByteBuf, DiskItemDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            String::new,
            DiskItemDataComponent::new
    );
}
