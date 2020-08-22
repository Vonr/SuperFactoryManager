package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.FlowIconButton;
import ca.teamdman.sfm.client.gui.core.FlowPanel;
import ca.teamdman.sfm.client.gui.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.core.IFlowDeletable;
import ca.teamdman.sfm.client.gui.core.Size;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.InputFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerPositionPacketC2S;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FlowInputButton extends FlowIconButton implements IFlowDeletable, IFlowCloneable {

	public final ManagerFlowController CONTROLLER;
	public final FlowDrawer<FlowItemStack> DRAWER;
	public InputFlowData data;
	private boolean open = false;

	public FlowInputButton(
		ManagerFlowController controller,
		InputFlowData data
	) {
		super(ButtonLabel.INPUT);
		POS.setMovable(true);
		this.data = data;
		this.CONTROLLER = controller;
		this.DRAWER = new FlowDrawer<>(
			this,
			ImmutableList.of(
				new FlowItemStack(new ItemStack(Items.REDSTONE), new Position()),
				new FlowItemStack(new ItemStack(Items.CARVED_PUMPKIN), new Position()),
				new FlowItemStack(new ItemStack(Items.LEAD), new Position()),
				new FlowItemStack(new ItemStack(Items.ACACIA_DOOR), new Position()),
				new FlowItemStack(new ItemStack(Items.BIRCH_BUTTON), new Position()),
				new FlowItemStack(new ItemStack(Items.TRAPPED_CHEST), new Position()),
				new FlowItemStack(new ItemStack(Items.STONE_SWORD), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.DIAMOND_BLOCK), new Position()),
				new FlowItemStack(new ItemStack(Blocks.DIAMOND_ORE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position()),
				new FlowItemStack(new ItemStack(Blocks.STONE), new Position())

			),
			FlowItemStack.ITEM_TOTAL_WIDTH,
			FlowItemStack.ITEM_TOTAL_HEIGHT
		);
		onDataChange();
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		ManagerTileEntity tile = CONTROLLER.SCREEN.CONTAINER.getSource();
		if (tile.getWorld() == null) {
			return;
		}
		this.open = !this.open;
		tile.getCableNeighbours(tile.getPos())
			.distinct()
			.filter(pos -> tile.getWorld().getTileEntity(pos) != null)
			.forEach(p -> {
				System.out.printf(
					"%30s %20s\n",
					tile.getWorld().getBlockState(p).getBlock().getRegistryName().toString(),
					p.toString()
				);
				tile.getWorld().setBlockState(
					p,
					open ? Blocks.DIAMOND_BLOCK.getDefaultState() : Blocks.AIR.getDefaultState()
				);
			});
	}

	@Override
	public Optional<FlowData> getData() {
		return Optional.of(data);
	}

	@Override
	public FlowPanel createPositionBox(Position pos, int width, int height) {
		//noinspection DuplicatedCode
		return new FlowPanel(pos, new Size(width, height)) {
			@Override
			public void onMove(
				int startMouseX, int startMouseY, int finishMouseX, int finishMouseY, int button
			) {
				data.position.setXY(getPosition());
				FlowInputButton.this.onDataChange();
			}

			@Override
			public void onMoveFinished(
				int startMouseX, int startMouseY,
				int finishMouseX, int finishMouseY, int button
			) {
				PacketHandler.INSTANCE.sendToServer(new ManagerPositionPacketC2S(
					CONTROLLER.SCREEN.CONTAINER.windowId,
					CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
					data.getId(),
					getPosition()
				));
			}
		};
	}

	@Override
	public void delete() {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			data.getId()
		));
	}

	@Override
	public void drawGhostAtPosition(
		BaseScreen screen, MatrixStack matrixStack, int x, int y, float deltaTime
	) {
		BACKGROUND.drawGhostAt(screen, matrixStack, x, y);
		ICON.drawGhostAt(screen, matrixStack, x + 4, y + 4);
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateInputPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			new Position(x, y)
		));
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		super.draw(screen, matrixStack, mx, my, deltaTime);
		if (open) {
			DRAWER.draw(screen, matrixStack, mx, my, deltaTime);
		}
	}

	@Override
	public void onDataChange() {
		this.POS.getPosition().setXY(data.position);
		this.DRAWER.onDataChange();
	}
}
