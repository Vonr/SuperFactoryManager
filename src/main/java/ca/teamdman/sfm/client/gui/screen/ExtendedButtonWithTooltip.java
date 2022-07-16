package ca.teamdman.sfm.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class ExtendedButtonWithTooltip extends ExtendedButton {
    private final OnTooltip TOOLTIP;

    public ExtendedButtonWithTooltip(
            int xPos,
            int yPos,
            int width,
            int height,
            Component displayString,
            OnPress handler,
            OnTooltip tooltip
    ) {
        super(xPos, yPos, width, height, displayString, handler);
        TOOLTIP = tooltip;
    }

    @Override
    public void renderToolTip(PoseStack pose, int mx, int my) {
        if (isHovered && visible) {
            TOOLTIP.onTooltip(this, pose, mx, my);
        }
    }
}
