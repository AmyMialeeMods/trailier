package xyz.amymialee.trailier.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public class CropBlockMixin {
    @Inject(method = "getAvailableMoisture", at = @At("RETURN"), cancellable = true)
    private static void trailier$flamed(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (block == Blocks.TORCHFLOWER_CROP) {
            cir.setReturnValue(10.0F - cir.getReturnValue());
        }
    }
}