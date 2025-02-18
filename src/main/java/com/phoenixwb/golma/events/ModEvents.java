package com.phoenixwb.golma.events;

import com.phoenixwb.golma.Golma;
import com.phoenixwb.golma.entity.EntityInit;
import com.phoenixwb.golma.entity.GolmaEntity;
import com.phoenixwb.golma.item.ItemInit;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Golma.MODID, bus = Bus.MOD)
public class ModEvents {
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(EntityInit.GOLMA.get(), GolmaEntity.createAttributes().build());
	}

	@SubscribeEvent
	public static void addItemsToCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> key = event.getTabKey();
		if (key == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(ItemInit.SERVING_PLATE);
		} else if (key == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(ItemInit.CARROT_CAKE);
		} else if (key == CreativeModeTabs.SPAWN_EGGS) {
			event.accept(ItemInit.GOLMA_SPAWN_EGG);
		}
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		GolmaEntity.bootStrapFood();
	}
}