package xyz.amymialee.trailier.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TallPlantBlock.class)
public class TallPlantBlockMixin implements FluidFillable, FluidDrainable {
    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        TallPlantBlock block = (TallPlantBlock) (Object) this;
        return block == Blocks.PITCHER_PLANT && fluid == Fluids.WATER;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.isOf(Blocks.PITCHER_PLANT);
    }

    @Override
    public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        return state.isOf(Blocks.PITCHER_PLANT) ? new ItemStack(Items.WATER_BUCKET) : ItemStack.EMPTY;
    }

    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        return Fluids.WATER.getBucketFillSound();
    }

    @Inject(method = "onBreakInCreative", at = @At("HEAD"), cancellable = true)
    private static void trailier$dontPitch(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (state.isOf(Blocks.PITCHER_CROP)) ci.cancel();
    }

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    public void trailier$floatingPitcher(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(Blocks.PITCHER_PLANT) && state.get(TallPlantBlock.HALF) == DoubleBlockHalf.LOWER && !world.getBlockState(pos.down()).isAir()) {
            cir.setReturnValue(true);
        }
    }
}