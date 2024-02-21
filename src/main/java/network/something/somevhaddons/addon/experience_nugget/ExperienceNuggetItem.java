package network.something.somevhaddons.addon.experience_nugget;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExperienceNuggetItem extends Item {

    protected final int experienceValue;

    public ExperienceNuggetItem(int experienceValue) {
        super(
                new Item.Properties()
                        .tab(CreativeModeTab.TAB_FOOD)
                        .stacksTo(64)
        );
        this.experienceValue = experienceValue;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var itemInHand = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            pLevel.playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS,
                    .5f, 1);
            return InteractionResultHolder.consume(itemInHand);
        }

        int amountUsed = pPlayer.isShiftKeyDown() ? 1 : itemInHand.getCount();
        int total = Mth.ceil(amountUsed * experienceValue);

        pPlayer.giveExperiencePoints(total);
        itemInHand.shrink(amountUsed);

        if (!itemInHand.isEmpty())
            return InteractionResultHolder.success(itemInHand);

        pPlayer.setItemInHand(pUsedHand, ItemStack.EMPTY);
        return InteractionResultHolder.consume(itemInHand);
    }
}
