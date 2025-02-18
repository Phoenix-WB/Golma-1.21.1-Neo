package com.phoenixwb.golma.item;

import com.phoenixwb.golma.Golma;
import com.phoenixwb.golma.block.BlockInit;
import com.phoenixwb.golma.entity.EntityInit;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemInit {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Golma.MODID);

	public static final DeferredItem<Item> CARROT_CAKE = ITEMS.register("carrot_cake",
			() -> new Item(new Item.Properties().food(Foods.CARROT_CAKE)));

	public static final DeferredItem<BlockItem> SERVING_PLATE = ITEMS.register("serving_plate",
			() -> new BlockItem(BlockInit.SERVING_PLATE.get(), new Item.Properties()));

	public static final DeferredItem<DeferredSpawnEggItem> GOLMA_SPAWN_EGG = ITEMS.register("golma_spawn_egg",
			() -> new DeferredSpawnEggItem(EntityInit.GOLMA, 0xf4f4f4, 0xdd0a23, new Item.Properties()));
}
