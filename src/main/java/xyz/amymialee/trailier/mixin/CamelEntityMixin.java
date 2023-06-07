package xyz.amymialee.trailier.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CamelEntity.class)
public abstract class CamelEntityMixin extends AbstractHorseEntity {
    protected CamelEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void trailier$mounting(CallbackInfo ci) {
        if (this.getPassengerList().size() == 1 && this.getControllingPassenger() instanceof PlayerEntity) {
            List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.2F, 0.2F, 0.2F), EntityPredicates.canBePushedBy(this).and((e) -> !(e instanceof HostileEntity) && !(e instanceof PlayerEntity)));
            if (!list.isEmpty()) {
                boolean bl = !this.getWorld().isClient;
                for (Entity entity : list) {
                    if (!entity.hasPassenger(this)) {
                        if (bl && !entity.hasVehicle() && entity instanceof LivingEntity && !(entity instanceof WaterCreatureEntity)) {
                            entity.startRiding(this);
                        } else {
                            this.pushAwayFrom(entity);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void trailier$dismount(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.shouldCancelInteraction() && this.getPassengerList().size() > 0) {
            if (!(this.getControllingPassenger() instanceof PlayerEntity)) {
                this.removeAllPassengers();
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}