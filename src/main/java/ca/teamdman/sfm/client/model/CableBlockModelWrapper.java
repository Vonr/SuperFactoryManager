package ca.teamdman.sfm.client.model;

import ca.teamdman.sfm.common.block.CableFacadeBlock;
import ca.teamdman.sfm.common.util.FacadeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CableBlockModelWrapper extends BakedModelWrapper<BakedModel> {

    private static final ChunkRenderTypeSet SOLID = ChunkRenderTypeSet.of(RenderType.solid());
    private static final ChunkRenderTypeSet ALL = ChunkRenderTypeSet.all();

    public CableBlockModelWrapper(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            BlockState state,
            Direction side,
            @NotNull RandomSource rand,
            @NotNull ModelData extraData,
            RenderType renderType
    ) {
        BlockState mimicState = extraData.get(CableFacadeBlock.FACADE_BLOCK_STATE);
        if (mimicState == null) {
            return originalModel.getQuads(state, side, rand, ModelData.EMPTY, renderType);
        }

        BakedModel mimicModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimicState);
        ChunkRenderTypeSet renderTypes = mimicModel.getRenderTypes(mimicState, rand, extraData);

        if (renderType == null || renderTypes.contains(renderType)) {
            return mimicModel.getQuads(mimicState, side, rand, ModelData.EMPTY, renderType);
        }

        return List.of();
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(
            @NotNull BlockState state,
            @NotNull RandomSource rand,
            @NotNull ModelData data
    ) {
        return state.getValue(CableFacadeBlock.FACADE_TYPE_PROP) == FacadeType.TRANSLUCENT ? ALL : SOLID;
    }
}
