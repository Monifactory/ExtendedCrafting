package com.witherschat.extendedcrafting.client.screen.button;

import com.blakebr0.cucumber.client.screen.button.IconButton;
import com.witherschat.extendedcrafting.client.screen.CompressorScreen;
import com.witherschat.extendedcrafting.network.NetworkHandler;
import com.witherschat.extendedcrafting.network.message.EjectModeSwitchMessage;
import net.minecraft.core.BlockPos;

public class EjectModeSwitchButton extends IconButton {
    public EjectModeSwitchButton(int x, int y, BlockPos pos) {
        super(x, y, 11, 9, 195, 32, CompressorScreen.BACKGROUND, button -> {
            NetworkHandler.INSTANCE.sendToServer(new EjectModeSwitchMessage(pos));
        });
    }

    @Override
    protected int getYImage() {
        return this.isHovered ? 0 : 10;
    }
}
