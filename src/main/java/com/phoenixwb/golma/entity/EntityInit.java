package com.phoenixwb.golma.entity;

import com.phoenixwb.golma.Golma;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityInit {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
			.create(BuiltInRegistries.ENTITY_TYPE, Golma.MODID);

	public static final DeferredHolder<EntityType<?>, EntityType<GolmaEntity>> GOLMA = ENTITY_TYPES.register("golma",
			() -> EntityType.Builder.<GolmaEntity>of(GolmaEntity::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW)
					.sized(0.7F, 1.9F).eyeHeight(1.7F).clientTrackingRange(8)
					.build(ResourceLocation.fromNamespaceAndPath(Golma.MODID, "golma").toString()));
}
