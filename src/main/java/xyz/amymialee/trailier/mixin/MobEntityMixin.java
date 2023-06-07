package xyz.amymialee.trailier.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.trailier.Trailier;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void trailier$sniffControl(CallbackInfoReturnable<LivingEntity> cir) {
        MobEntity self = (MobEntity) (Object) this;
        if (self instanceof SnifferEntity) {
            Entity passenger = this.getFirstPassenger();
            if (passenger instanceof PlayerEntity playerEntity && (playerEntity.getMainHandStack().isOf(Trailier.TORCHFLOWER_SEED_ON_A_STICK) || playerEntity.getOffHandStack().isOf(Trailier.TORCHFLOWER_SEED_ON_A_STICK))) {
                cir.setReturnValue(playerEntity);
            }
        }
    }
}