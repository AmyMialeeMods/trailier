package xyz.amymialee.trailier.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrushableBlock.class)
public abstract class BrushableBlockMixin extends BlockWithEntity implements LandingBlock {
    protected BrushableBlockMixin(Settings settings) {
        super(settings);
    }

    @WrapOperation(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;setDestroyedOnLanding()V"))
    private void trailier$notDestroyed(FallingBlockEntity entity, Operation<Void> operation) {}
}