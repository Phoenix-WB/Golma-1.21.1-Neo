package com.phoenixwb.golma;

import com.phoenixwb.golma.block.BlockInit;
import com.phoenixwb.golma.block.blockentity.BlockEntityInit;
import com.phoenixwb.golma.entity.EntityInit;
import com.phoenixwb.golma.item.ItemInit;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Golma.MODID)
public class Golma {
	public static final String MODID = "golma";

	public Golma(IEventBus modEventBus, ModContainer modContainer) {
		ItemInit.ITEMS.register(modEventBus);
		BlockInit.BLOCKS.register(modEventBus);
		BlockEntityInit.BLOCK_ENTITY_TYPES.register(modEventBus);
		EntityInit.ENTITY_TYPES.register(modEventBus);
	}
}
