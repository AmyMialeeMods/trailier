package xyz.amymialee.trailier.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.trailier.util.SnifferSaddleHolder;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "tickControlled", at = @At("TAIL"), cancellable = true)
    private void trailier$move(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof SnifferEntity sniffer && sniffer instanceof SnifferSaddleHolder holder) {
            sniffer.setRotation(controllingPlayer.getYaw(), controllingPlayer.getPitch() * 0.5F);
            sniffer.prevYaw = sniffer.bodyYaw = sniffer.headYaw = sniffer.getYaw();
            holder.getSnifferSaddleComponent().tickBoost();
        }
    }

    @Inject(method = "getControlledMovementInput", at = @At("HEAD"), cancellable = true)
    protected void trailier$input(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof SnifferEntity) {
            cir.setReturnValue(new Vec3d(0, 0, 1));
        }
    }

    @Inject(method = "getSaddledSpeed", at = @At("HEAD"), cancellable = true)
    protected void trailier$speed(PlayerEntity controllingPlayer, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof SnifferEntity sniffer && self instanceof SnifferSaddleHolder holder) {
            cir.setReturnValue((float) (sniffer.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.225 * holder.getSnifferSaddleComponent().getMovementSpeedMultiplier()));
        }
    }
}