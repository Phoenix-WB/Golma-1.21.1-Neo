package com.phoenixwb.golma.entity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiPredicate;

import com.phoenixwb.golma.Golma;
import com.phoenixwb.golma.block.BlockInit;
import com.phoenixwb.golma.block.blockentity.ServingPlateBlockEntity;
import com.phoenixwb.golma.item.ItemInit;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

public class GolmaEntity extends AbstractGolem {
	public static final ResourceKey<LootTable> LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE,
			ResourceLocation.fromNamespaceAndPath(Golma.MODID, "entities/golma"));
	public static final Object2IntMap<ItemLike> INGREDIENTS = new Object2IntOpenHashMap<>();
	public static final ArrayList<ItemStack> SERVINGS = new ArrayList<>();
	final ArrayList<BlockPos> knownPlates = new ArrayList<>();
	int remainingCooldownBeforeLocatingNewPlate = Mth.nextInt(this.random, 60, 180);
	int servingTicks = 0;
	int ingredients = 0;

	public GolmaEntity(EntityType<? extends AbstractGolem> entityType, Level level) {
		super(entityType, level);
	}

	public static void bootStrapFood() {
		INGREDIENTS.defaultReturnValue(-1);
		addIngredient(1, Items.SUGAR);
		addIngredient(4, Items.EGG);
		addIngredient(8, Items.WHEAT);
		addIngredient(12, Items.COCOA_BEANS);
		addIngredient(16, Items.MILK_BUCKET);
		addServing(Items.COOKIE, 32);
		addServing(ItemInit.CARROT_CAKE, 12);
		addServing(Items.BREAD, 6);
		addServing(Items.CAKE, 1);
	}

	public static void addIngredient(int weight, ItemLike item) {
		INGREDIENTS.put(item.asItem(), weight);
	}

	public static void addServing(ItemLike item, int count) {
		ItemStack stack = item.asItem().getDefaultInstance();
		stack.setCount(count);
		SERVINGS.add(stack);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new GolmaServePlateGoal());
		this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5F));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.15F);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide()) {
			if (this.level().getBiome(this.blockPosition()).is(BiomeTags.SNOW_GOLEM_MELTS)) {
				this.hurt(this.damageSources().onFire(), 1.0F);
			}

			if (this.remainingCooldownBeforeLocatingNewPlate > 0) {
				this.remainingCooldownBeforeLocatingNewPlate--;
			}

			if (ingredients >= 64 && ++this.servingTicks > 2400) {
				ingredients = 0;
				servingTicks = 0;
			}
		}
	}
	
	public int getIngredients() {
		return ingredients;
	}
	
	public void setIngredients(int ingredients) {
		checkIngredientIncrease(this.ingredients, ingredients);
		this.ingredients = ingredients;
	}
	
	public void checkIngredientIncrease(int oldIngredients, int newIngredients) {
		if(newIngredients - newIngredients % 64 > oldIngredients) {
			this.level().broadcastEntityEvent(this, (byte) 12);
		}
	}

	@Override
	public boolean isSensitiveToWater() {
		return true;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		int val = INGREDIENTS.getInt(itemstack.getItem());
		if (val > 0) {
			if (!this.level().isClientSide()) {
				if (!player.isCreative()) {
					boolean flag = itemstack.is(Items.MILK_BUCKET);
					itemstack.shrink(1);
					if (flag) {
						player.addItem(Items.BUCKET.getDefaultInstance());
					}
				}

				int oldIngredients = ingredients;
				ingredients += val;
				checkIngredientIncrease(oldIngredients, ingredients);
			}

			return InteractionResult.sidedSuccess(this.level().isClientSide());
		} else {
			return InteractionResult.PASS;
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 12) {
			this.addParticlesAroundSelf(ParticleTypes.HEART);
		}
	}

	protected void addParticlesAroundSelf(ParticleOptions particleOption) {
		for (int i = 0; i < 5; i++) {
			double d0 = this.random.nextGaussian() * 0.02;
			double d1 = this.random.nextGaussian() * 0.02;
			double d2 = this.random.nextGaussian() * 0.02;
			this.level().addParticle(particleOption, this.getRandomX(1.0), this.getRandomY() + 1.0,
					this.getRandomZ(1.0), d0, d1, d2);
		}
	}

	@Override
	protected ResourceKey<LootTable> getDefaultLootTable() {
		return LOOT_TABLE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SNOW_GOLEM_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.SNOW_GOLEM_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.SNOW_GOLEM_DEATH;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("ingredients", ingredients);
		CompoundTag positions = new CompoundTag();
		int size = knownPlates.size();
		positions.putInt("size", size);
		for (int i = 0; i < size; i++) {
			BlockPos pos = knownPlates.get(i);
			positions.putInt("x" + i, pos.getX());
			positions.putInt("y" + i, pos.getY());
			positions.putInt("z" + i, pos.getZ());
		}
		compound.put("positions", positions);
		compound.putInt("servingTicks", servingTicks);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.ingredients = compound.getInt("ingredients");
		CompoundTag positions = compound.getCompound("positions");
		for (int i = 0; i < positions.getInt("size"); i++) {
			knownPlates
					.add(new BlockPos(positions.getInt("x" + i), positions.getInt("y" + i), positions.getInt("z" + i)));
		}
		this.servingTicks = compound.getInt("servingTicks");
	}

	class GolmaServePlateGoal extends Goal {
		BlockPos headedPlatePos = null;

		private final BiPredicate<BlockState, BlockPos> VALID_EMPTY_PLATE = (state, pos) -> {
			if (state.is(BlockInit.SERVING_PLATE)
					&& GolmaEntity.this.level().getBlockEntity(pos) instanceof ServingPlateBlockEntity blockEntity) {
				return blockEntity.getItem().isEmpty();
			}
			return false;
		};

		@Override
		public boolean canUse() {
			if (GolmaEntity.this.ingredients < 64 || GolmaEntity.this.remainingCooldownBeforeLocatingNewPlate > 0) {
				return false;
			} else {
				Optional<BlockPos> optional = this.findNearbyEmptyPlate();
				if (optional.isPresent()) {
					headedPlatePos = optional.get();
					GolmaEntity.this.navigation.moveTo((double) headedPlatePos.getX() + 0.5,
							(double) headedPlatePos.getY() + 0.5, (double) headedPlatePos.getZ() + 0.5, 1.2F);
					return true;
				} else {
					GolmaEntity.this.remainingCooldownBeforeLocatingNewPlate = Mth.nextInt(GolmaEntity.this.getRandom(),
							20, 60);
					return false;
				}
			}
		}

		@Override
		public boolean canContinueToUse() {
			if (GolmaEntity.this.ingredients < 64) {
				return false;
			} else if (headedPlatePos == null) {
				return false;
			} else if (GolmaEntity.this.tickCount % 20 == 0 && !isPlateValid(headedPlatePos)) {
				headedPlatePos = null;
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void stop() {
			GolmaEntity.this.navigation.stop();
			GolmaEntity.this.remainingCooldownBeforeLocatingNewPlate = 40;
		}

		@Override
		public void tick() {
			Vec3 vec3 = Vec3.atBottomCenterOf(headedPlatePos).add(0.0, 0.0, 0.0);
			if (vec3.distanceTo(GolmaEntity.this.position()) > 1.5) {
				GolmaEntity.this.getMoveControl().setWantedPosition(vec3.x(), vec3.y(), vec3.z(), 1.5F);
			} else {
				if (GolmaEntity.this.level()
						.getBlockEntity(headedPlatePos) instanceof ServingPlateBlockEntity blockEntity) {
					blockEntity.placeItem(GolmaEntity.SERVINGS
							.get(GolmaEntity.this.level().getRandom().nextInt(GolmaEntity.SERVINGS.size())).copy(),
							GolmaEntity.this);
				}
				GolmaEntity.this.level().playSound(null, headedPlatePos, SoundEvents.SNOW_PLACE, SoundSource.AMBIENT);
				ingredients -= 64;
				headedPlatePos = null;
				GolmaEntity.this.navigation.stop();
			}
		}

		private Optional<BlockPos> findNearbyEmptyPlate() {
			BlockPos blockpos = GolmaEntity.this.blockPosition();
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
			double distance = 10.0D;
			Level level = GolmaEntity.this.level();
			BlockPos closestPlate = null;
			ArrayList<BlockPos> forgottenPlates = new ArrayList<>();

			for (BlockPos platePos : GolmaEntity.this.knownPlates) {
				int plateDist = blockpos.distManhattan(platePos);
				if (plateDist <= 30 && VALID_EMPTY_PLATE.test(level.getBlockState(platePos), platePos)) {
					if (closestPlate == null) {
						closestPlate = platePos;
					} else if (plateDist < blockpos.distManhattan(closestPlate)) {
						closestPlate = platePos;
					}
				} else {
					forgottenPlates.add(platePos);
				}
			}
			GolmaEntity.this.knownPlates.removeAll(forgottenPlates);

			if (closestPlate != null) {
				return Optional.of(closestPlate);
			}

			for (int i = 0; (double) i <= distance; i = i > 0 ? -i : 1 - i) {
				for (int j = 0; (double) j < distance; j++) {
					for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
						for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
							blockpos$mutableblockpos.setWithOffset(blockpos, k, i - 1, l);
							if (blockpos.closerThan(blockpos$mutableblockpos, distance) && VALID_EMPTY_PLATE
									.test(level.getBlockState(blockpos$mutableblockpos), blockpos$mutableblockpos)) {
								if (!GolmaEntity.this.knownPlates.contains(blockpos$mutableblockpos)) {
									GolmaEntity.this.knownPlates.add(blockpos$mutableblockpos);
								}
								return Optional.of(blockpos$mutableblockpos);
							}
						}
					}
				}
			}

			return Optional.empty();
		}

		public boolean isPlateValid(BlockPos pos) {
			Level level = GolmaEntity.this.level();
			return level.isLoaded(pos) && VALID_EMPTY_PLATE.test(level.getBlockState(pos), pos);
		}
	}
}
