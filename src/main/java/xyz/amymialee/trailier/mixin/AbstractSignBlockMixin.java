package xyz.amymialee.trailier.mixin;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignChangingItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignBlock.class)
public abstract class AbstractSignBlockMixin {
    @Shadow protected abstract boolean isOtherPlayerEditing(PlayerEntity player, SignBlockEntity blockEntity);

    @Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
    private void trailier$unwax(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();
        if (item instanceof SignChangingItem signChanging && item instanceof AxeItem) {
            if (!world.isClient) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                    if (!this.isOtherPlayerEditing(player, signBlockEntity) && signChanging.canUseOnSignText(null, player) && signChanging.useOnSign(world, signBlockEntity, true, player)) {
                        if (!player.isCreative()) {
                            stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
                        }
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getPos(), GameEvent.Emitter.of(player, signBlockEntity.getCachedState()));
                        player.incrementStat(Stats.USED.getOrCreateStat(item));
                        cir.setReturnValue(ActionResult.SUCCESS);
                    }
                }
            } else {
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}