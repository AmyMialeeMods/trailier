package xyz.amymialee.trailier.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SnifferStickItem extends Item {
    public SnifferStickItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack snifferStick = user.getStackInHand(hand);
        if (!world.isClient) {
            Entity entity = user.getControllingVehicle();
            if (user.hasVehicle() && entity instanceof ItemSteerable itemSteerable && entity.getType() == EntityType.SNIFFER && itemSteerable.consumeOnAStickItem()) {
                snifferStick.damage(1, user, p -> p.sendToolBreakStatus(hand));
                if (snifferStick.isEmpty()) {
                    ItemStack rod = new ItemStack(Items.FISHING_ROD);
                    rod.setNbt(snifferStick.getNbt());
                    return TypedActionResult.success(rod);
                }
                return TypedActionResult.success(snifferStick);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return TypedActionResult.pass(snifferStick);
    }
}