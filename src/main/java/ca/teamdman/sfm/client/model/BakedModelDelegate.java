package ca.teamdman.sfm.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

public class BakedModelDelegate implements IBakedModel {

	@Override
	@Deprecated
	public List<BakedQuad> getQuads(
		@Nullable BlockState p_200117_1_,
		@Nullable Direction p_200117_2_,
		Random p_200117_3_
	) {
		return DELEGATE.getQuads(p_200117_1_, p_200117_2_, p_200117_3_);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return DELEGATE.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return DELEGATE.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return DELEGATE.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return DELEGATE.isCustomRenderer();
	}

	@Override
	@Deprecated
	public TextureAtlasSprite getParticleIcon() {
		return DELEGATE.getParticleIcon();
	}

	@Override
	@Deprecated
	public ItemCameraTransforms getTransforms() {
		return DELEGATE.getTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return DELEGATE.getOverrides();
	}

	@Override
	public IBakedModel getBakedModel() {
		return DELEGATE.getBakedModel();
	}

	@Override
	@Nonnull
	public List<BakedQuad> getQuads(
		@Nullable BlockState state,
		@Nullable Direction side,
		@Nonnull Random rand,
		@Nonnull IModelData extraData
	) {
		return DELEGATE.getQuads(state, side, rand, extraData);
	}

	@Override
	public boolean isAmbientOcclusion(BlockState state) {
		return DELEGATE.isAmbientOcclusion(state);
	}

	@Override
	public boolean doesHandlePerspectives() {
		return DELEGATE.doesHandlePerspectives();
	}

	@Override
	public IBakedModel handlePerspective(
		TransformType cameraTransformType,
		MatrixStack mat
	) {
		return DELEGATE.handlePerspective(cameraTransformType, mat);
	}

	@Override
	@Nonnull
	public IModelData getModelData(
		@Nonnull IBlockDisplayReader world,
		@Nonnull BlockPos pos,
		@Nonnull BlockState state,
		@Nonnull IModelData tileData
	) {
		return DELEGATE.getModelData(world, pos, state, tileData);
	}

	@Override
	public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
		return DELEGATE.getParticleTexture(data);
	}

	@Override
	public boolean isLayered() {
		return DELEGATE.isLayered();
	}

	@Override
	public List<Pair<IBakedModel, RenderType>> getLayerModels(
		ItemStack itemStack,
		boolean fabulous
	) {
		return DELEGATE.getLayerModels(itemStack, fabulous);
	}

	private final IBakedModel DELEGATE;

	public BakedModelDelegate(IBakedModel DELEGATE) {
		this.DELEGATE = DELEGATE;
	}

}
