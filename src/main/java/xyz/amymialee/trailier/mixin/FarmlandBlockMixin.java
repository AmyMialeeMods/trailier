package xyz.amymialee.trailier.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
    @Inject(method = "isWaterNearby", at = @At("HEAD"), cancellable = true)
    private static void trailier$draught(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, 1, -1), pos.add(1, 1, 1))) {
            if (world.getBlockState(blockPos).isOf(Blocks.TORCHFLOWER)) {
                cir.setReturnValue(false);
                return;
            }
        }
        if (world.getBlockState(pos.add(0, 1, 0)).isOf(Blocks.TORCHFLOWER_CROP)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isWaterNearby", at = @At("RETURN"), cancellable = true)
    private static void trailier$waterNearby(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            for (BlockPos blockPos : BlockPos.iterate(pos.add(-6, 1, -6), pos.add(6, 1, 6))) {
                if (world.getBlockState(blockPos).isOf(Blocks.PITCHER_PLANT)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
            if (world.getBlockState(pos.add(0, 1, 0)).isOf(Blocks.PITCHER_CROP)) {
                cir.setReturnValue(true);
            }
        }
    }
}