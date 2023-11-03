package ca.teamdman.sfm.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class TestBarrelBlock extends BarrelBlock {
    public TestBarrelBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
//        var container = (Container) pLevel.getBlockEntity(pPos);
//        int stacks = IntStream.range(0, container.getContainerSize()).mapToObj(container::getItem).mapToInt(ItemStack::getCount).sum();
//        System.out.println("test barrel removed " + stacks/64);
        if (!pState.is(pNewState.getBlock())) {
            pLevel.removeBlockEntity(pPos);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}
