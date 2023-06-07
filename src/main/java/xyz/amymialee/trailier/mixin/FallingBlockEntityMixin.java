package xyz.amymialee.trailier.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Inject(method = "spawnFromBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private static void trailier$blockEntity(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<FallingBlockEntity> cir, @Local(ordinal = 0) FallingBlockEntity fallingBlockEntity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            fallingBlockEntity.blockEntityData = blockEntity.createNbt();
        }
    }
}