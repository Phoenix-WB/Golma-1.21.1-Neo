package com.phoenixwb.golma.block.blockentity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ServingPlateBlockEntity extends BlockEntity implements Clearable {
	private ItemStack servedItem = ItemStack.EMPTY;

	public ServingPlateBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityInit.SERVING_PLATE.get(), pos, blockState);
	}

	@Override
	public void clearContent() {
		servedItem = ItemStack.EMPTY;
	}

	public boolean isEmpty() {
		return servedItem.isEmpty();
	}

	public ItemStack getItem() {
		return servedItem;
	}

	public void takeItem(@Nullable Entity entity) {
		clearContent();
		this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(),
				GameEvent.Context.of(entity, this.getBlockState()));
		markUpdated();
	}

	public void placeItem(ItemStack stack, @Nullable Entity entity) {
		if (stack == null) {
			servedItem = ItemStack.EMPTY;
		} else {
			servedItem = stack;
		}
		this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(),
				GameEvent.Context.of(entity, this.getBlockState()));
		markUpdated();
	}

	private void markUpdated() {
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, Provider registries) {
		super.loadAdditional(tag, registries);
		clearContent();
		if (tag.contains("servedItem")) {
			servedItem = ItemStack.parse(registries, tag.getCompound("servedItem")).orElse(ItemStack.EMPTY);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, Provider registries) {
		super.saveAdditional(tag, registries);
		if (!servedItem.isEmpty()) {
			tag.put("servedItem", servedItem.save(registries));
		}
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag compoundtag = new CompoundTag();
		compoundtag.put("servedItem", servedItem.saveOptional(registries));
		return compoundtag;
	}

	@Override
	protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		NonNullList<ItemStack> items = NonNullList.withSize(1, servedItem);
		components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		tag.remove("servedItem");
	}
}
