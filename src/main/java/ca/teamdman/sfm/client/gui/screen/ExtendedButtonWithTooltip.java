package ca.teamdman.sfm.client.gui.screen;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class ExtendedButtonWithTooltip extends ExtendedButton {
    public ExtendedButtonWithTooltip(
            int xPos,
            int yPos,
            int width,
            int height,
            Component displayString,
            OnPress handler,
            Tooltip tooltip
    ) {
        super(xPos, yPos, width, height, displayString, handler);
        setTooltip(tooltip);
    }
}
