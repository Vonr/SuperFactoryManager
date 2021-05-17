package ca.teamdman.sfm.client.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public class BakedModelDelegate implements IBakedModel {

	private final IBakedModel DELEGATE;

	public BakedModelDelegate(IBakedModel DELEGATE) {
		this.DELEGATE = DELEGATE;
	}

	@Override
	@Deprecated
	public List<BakedQuad> getQuads(
		@Nullable BlockState state,
		@Nullable Direction side,
		Random rand
	) {
		return DELEGATE.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return DELEGATE.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return DELEGATE.isGui3d();
	}

	@Override
	public boolean isSideLit() {
		return DELEGATE.isSideLit();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return DELEGATE.isBuiltInRenderer();
	}

	@Override
	@Deprecated
	public TextureAtlasSprite getParticleTexture() {
		return DELEGATE.getParticleTexture();
	}

	@Override
	@Deprecated
	public ItemCameraTransforms getItemCameraTransforms() {
		return DELEGATE.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return DELEGATE.getOverrides();
	}
}
