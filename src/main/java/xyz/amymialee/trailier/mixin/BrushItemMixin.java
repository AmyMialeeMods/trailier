package xyz.amymialee.trailier.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.util.tuples.Triplet;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(BrushItem.class)
public class BrushItemMixin {
    @Unique
    private static final HashMap<Block, Triplet<BlockState, Boolean, ItemStack>> BRUSHABLE_BLOCKS = new HashMap<>();

    @Inject(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"), cancellable = true)
    private void trailier$cleaning(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci, @Local(ordinal = 0) BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Direction direction = blockHitResult.getSide();
        if (user instanceof PlayerEntity player && this.brushBlock(player, blockState, blockPos, direction)) {
            user.stopUsingItem();
            ci.cancel();
        }
    }

    @Unique
    private <T extends Comparable<T>> boolean brushBlock(PlayerEntity player, BlockState targetState, BlockPos targetPos, Direction hitDirection) {
        if (player.getItemUseTime() > 24 && player.getWorld() instanceof ServerWorld world) {
            if (BRUSHABLE_BLOCKS.containsKey(targetState.getBlock())) {
                Triplet<BlockState, Boolean, ItemStack> triple = BRUSHABLE_BLOCKS.get(targetState.getBlock());
                BlockState newState = triple.getA();
                boolean copyStates = triple.getB();
                if (copyStates) {
                    for (Field field : Properties.class.getDeclaredFields()) {
                        try {
                            @SuppressWarnings("unchecked") Property<T> property = (Property<T>) field.get(null);
                            if (targetState.contains(property) && newState.contains(property)) {
                                newState = newState.with(property, targetState.get(property));
                            }
                        } catch (Exception ignored) {}
                    }
                }
                ItemStack stack = triple.getC();
                if (stack != null) {
                    double d = EntityType.ITEM.getWidth();
                    double e = 1.0 - d;
                    double f = d / 2.0;
                    Direction direction = Objects.requireNonNullElse(hitDirection, Direction.UP);
                    BlockPos blockPos = targetPos.offset(direction, 1);
                    double g = (double) blockPos.getX() + 0.5 * e + f;
                    double h = (double) blockPos.getY() + 0.5 + (double) (EntityType.ITEM.getHeight() / 2.0F);
                    double i = (double) blockPos.getZ() + 0.5 * e + f;
                    ItemEntity itemEntity = new ItemEntity(world, g, h, i, stack.copy());
                    itemEntity.setVelocity(Vec3d.ZERO);
                    world.spawnEntity(itemEntity);
                }
                world.syncWorldEvent(WorldEvents.BLOCK_FINISHED_BRUSHING, targetPos, Block.getRawIdFromState(targetState));
                world.setBlockState(targetPos, newState, Block.NOTIFY_ALL);
                return true;
            }
        }
        return false;
    }

    @WrapOperation(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    private void trailier$unbreakableBrush(ItemStack stack, int damage, LivingEntity user, Consumer<LivingEntity> breakCallback, Operation<Void> operation) {}

    static {
        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE, new Triplet<>(Blocks.COBBLESTONE.getDefaultState(), false, new ItemStack(Items.MOSS_BLOCK)));
        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE_STAIRS, new Triplet<>(Blocks.COBBLESTONE_STAIRS.getDefaultState(), true, new ItemStack(Items.MOSS_BLOCK)));
        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE_SLAB, new Triplet<>(Blocks.COBBLESTONE_SLAB.getDefaultState(), true, new ItemStack(Items.MOSS_BLOCK)));
        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE_WALL, new Triplet<>(Blocks.COBBLESTONE_WALL.getDefaultState(), true, new ItemStack(Items.MOSS_BLOCK)));

        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICKS, new Triplet<>(Blocks.STONE_BRICKS.getDefaultState(), false, new ItemStack(Items.MOSS_BLOCK)));
        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICK_STAIRS, new Triplet<>(Blocks.STONE_BRICK_STAIRS.getDefaultState(), true, new ItemStack(Items.MOSS_BLOCK)));
        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICK_SLAB, new Triplet<>(Blocks.STONE_BRICK_SLAB.getDefaultState(), true, new ItemStack(Items.MOSS_BLOCK)));
        BRUSHABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICK_WALL, new Triplet<>(Blocks.STONE_BRICK_WALL.getDefaultState(), true, new ItemStack(Items.MOSS_BLOCK)));

        BRUSHABLE_BLOCKS.put(Blocks.EXPOSED_COPPER, new Triplet<>(Blocks.COPPER_BLOCK.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.EXPOSED_CUT_COPPER, new Triplet<>(Blocks.CUT_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.EXPOSED_CUT_COPPER_STAIRS, new Triplet<>(Blocks.CUT_COPPER_STAIRS.getDefaultState(), true, null));
        BRUSHABLE_BLOCKS.put(Blocks.EXPOSED_CUT_COPPER_SLAB, new Triplet<>(Blocks.CUT_COPPER_SLAB.getDefaultState(), true, null));

        BRUSHABLE_BLOCKS.put(Blocks.WEATHERED_COPPER, new Triplet<>(Blocks.EXPOSED_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WEATHERED_CUT_COPPER, new Triplet<>(Blocks.EXPOSED_CUT_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WEATHERED_CUT_COPPER_STAIRS, new Triplet<>(Blocks.EXPOSED_CUT_COPPER_STAIRS.getDefaultState(), true, null));
        BRUSHABLE_BLOCKS.put(Blocks.WEATHERED_CUT_COPPER_SLAB, new Triplet<>(Blocks.EXPOSED_CUT_COPPER_SLAB.getDefaultState(), true, null));

        BRUSHABLE_BLOCKS.put(Blocks.OXIDIZED_COPPER, new Triplet<>(Blocks.WEATHERED_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.OXIDIZED_CUT_COPPER, new Triplet<>(Blocks.WEATHERED_CUT_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, new Triplet<>(Blocks.WEATHERED_CUT_COPPER_STAIRS.getDefaultState(), true, null));
        BRUSHABLE_BLOCKS.put(Blocks.OXIDIZED_CUT_COPPER_SLAB, new Triplet<>(Blocks.WEATHERED_CUT_COPPER_SLAB.getDefaultState(), true, null));

        BRUSHABLE_BLOCKS.put(Blocks.WAXED_EXPOSED_COPPER, new Triplet<>(Blocks.WAXED_COPPER_BLOCK.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_EXPOSED_CUT_COPPER, new Triplet<>(Blocks.WAXED_CUT_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, new Triplet<>(Blocks.WAXED_CUT_COPPER_STAIRS.getDefaultState(), true, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, new Triplet<>(Blocks.WAXED_CUT_COPPER_SLAB.getDefaultState(), true, null));

        BRUSHABLE_BLOCKS.put(Blocks.WAXED_WEATHERED_COPPER, new Triplet<>(Blocks.WAXED_EXPOSED_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_WEATHERED_CUT_COPPER, new Triplet<>(Blocks.WAXED_EXPOSED_CUT_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, new Triplet<>(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.getDefaultState(), true, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, new Triplet<>(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB.getDefaultState(), true, null));

        BRUSHABLE_BLOCKS.put(Blocks.WAXED_OXIDIZED_COPPER, new Triplet<>(Blocks.WAXED_WEATHERED_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_OXIDIZED_CUT_COPPER, new Triplet<>(Blocks.WAXED_WEATHERED_CUT_COPPER.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, new Triplet<>(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.getDefaultState(), true, null));
        BRUSHABLE_BLOCKS.put(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, new Triplet<>(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB.getDefaultState(), true, null));

        BRUSHABLE_BLOCKS.put(Blocks.GRASS_BLOCK, new Triplet<>(Blocks.DIRT.getDefaultState(), false, new ItemStack(Items.GRASS)));
        BRUSHABLE_BLOCKS.put(Blocks.PODZOL, new Triplet<>(Blocks.DIRT.getDefaultState(), false, new ItemStack(Items.SPRUCE_LEAVES)));
        BRUSHABLE_BLOCKS.put(Blocks.MYCELIUM, new Triplet<>(Blocks.DIRT.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.DIRT_PATH, new Triplet<>(Blocks.DIRT.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.COARSE_DIRT, new Triplet<>(Blocks.DIRT.getDefaultState(), false, null));
        BRUSHABLE_BLOCKS.put(Blocks.ROOTED_DIRT, new Triplet<>(Blocks.DIRT.getDefaultState(), false, new ItemStack(Items.HANGING_ROOTS)));

        BRUSHABLE_BLOCKS.put(Blocks.OAK_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.OAK_SAPLING)));
        BRUSHABLE_BLOCKS.put(Blocks.SPRUCE_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.SPRUCE_SAPLING)));
        BRUSHABLE_BLOCKS.put(Blocks.BIRCH_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.BIRCH_SAPLING)));
        BRUSHABLE_BLOCKS.put(Blocks.JUNGLE_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.JUNGLE_SAPLING)));
        BRUSHABLE_BLOCKS.put(Blocks.ACACIA_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.ACACIA_SAPLING)));
        BRUSHABLE_BLOCKS.put(Blocks.DARK_OAK_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.DARK_OAK_SAPLING)));
        BRUSHABLE_BLOCKS.put(Blocks.MANGROVE_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.MANGROVE_PROPAGULE)));
        BRUSHABLE_BLOCKS.put(Blocks.CHERRY_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.CHERRY_SAPLING)));
        BRUSHABLE_BLOCKS.put(Blocks.AZALEA_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.AZALEA)));
        BRUSHABLE_BLOCKS.put(Blocks.FLOWERING_AZALEA_LEAVES, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.FLOWERING_AZALEA)));
        BRUSHABLE_BLOCKS.put(Blocks.BROWN_MUSHROOM_BLOCK, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.BROWN_MUSHROOM)));
        BRUSHABLE_BLOCKS.put(Blocks.RED_MUSHROOM_BLOCK, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.RED_MUSHROOM)));
        BRUSHABLE_BLOCKS.put(Blocks.NETHER_WART_BLOCK, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.CRIMSON_FUNGUS)));
        BRUSHABLE_BLOCKS.put(Blocks.WARPED_WART_BLOCK, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.WARPED_FUNGUS)));

        BRUSHABLE_BLOCKS.put(Blocks.COBWEB, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.STRING, 3)));
        BRUSHABLE_BLOCKS.put(Blocks.GLOW_LICHEN, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.GLOWSTONE_DUST)));

        BRUSHABLE_BLOCKS.put(Blocks.INFESTED_STONE, new Triplet<>(Blocks.STONE.getDefaultState(), false, new ItemStack(Items.STRING)));
        BRUSHABLE_BLOCKS.put(Blocks.INFESTED_COBBLESTONE, new Triplet<>(Blocks.COBBLESTONE.getDefaultState(), false, new ItemStack(Items.STRING)));
        BRUSHABLE_BLOCKS.put(Blocks.INFESTED_STONE_BRICKS, new Triplet<>(Blocks.STONE_BRICKS.getDefaultState(), false, new ItemStack(Items.STRING)));
        BRUSHABLE_BLOCKS.put(Blocks.INFESTED_MOSSY_STONE_BRICKS, new Triplet<>(Blocks.MOSSY_STONE_BRICKS.getDefaultState(), false, new ItemStack(Items.STRING)));
        BRUSHABLE_BLOCKS.put(Blocks.INFESTED_CRACKED_STONE_BRICKS, new Triplet<>(Blocks.CRACKED_STONE_BRICKS.getDefaultState(), false, new ItemStack(Items.STRING)));
        BRUSHABLE_BLOCKS.put(Blocks.INFESTED_CHISELED_STONE_BRICKS, new Triplet<>(Blocks.CHISELED_STONE_BRICKS.getDefaultState(), false, new ItemStack(Items.STRING)));
        BRUSHABLE_BLOCKS.put(Blocks.INFESTED_DEEPSLATE, new Triplet<>(Blocks.DEEPSLATE.getDefaultState(), false, new ItemStack(Items.STRING)));

        BRUSHABLE_BLOCKS.put(Blocks.REDSTONE_WIRE, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.REDSTONE)));
        BRUSHABLE_BLOCKS.put(Blocks.TRIPWIRE, new Triplet<>(Blocks.AIR.getDefaultState(), false, new ItemStack(Items.STRING)));
        BRUSHABLE_BLOCKS.put(Blocks.REDSTONE_ORE, new Triplet<>(Blocks.REDSTONE_ORE.getDefaultState().with(RedstoneOreBlock.LIT, true), false, null));
    }
}