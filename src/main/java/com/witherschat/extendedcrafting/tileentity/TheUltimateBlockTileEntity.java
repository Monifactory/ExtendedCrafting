package com.witherschat.extendedcrafting.tileentity;

import com.blakebr0.cucumber.tileentity.BaseTileEntity;
import com.witherschat.extendedcrafting.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TheUltimateBlockTileEntity extends BaseTileEntity {
    public TheUltimateBlockTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.THE_ULTIMATE_BLOCK.get(), pos, state);
    }
}
