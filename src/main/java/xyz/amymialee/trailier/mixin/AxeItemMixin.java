package xyz.amymialee.trailier.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SignChangingItem;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public class AxeItemMixin implements SignChangingItem {
    @Override
    public boolean useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player) {
        if (signBlockEntity.setWaxed(false)) {
            world.syncWorldEvent(null, WorldEvents.BLOCK_SCRAPED, signBlockEntity.getPos(), 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canUseOnSignText(SignText signText, PlayerEntity player) {
        return true;
    }
}