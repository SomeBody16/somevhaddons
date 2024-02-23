package network.something.somevhaddons.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import network.something.somevhaddons.addon.advancement_reward.AdvancementRewardManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.Util.NIL_UUID;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementMixin {

    @Shadow(remap = false)
    private ServerPlayer player;

    @Inject(
            method = "award",
            at = @At("HEAD"),
            cancellable = true
    )
    private void award(Advancement pAdvancement, String pCriterionKey, CallbackInfoReturnable<Boolean> cir) {
        // Forge: don't grant advancements for fake players
        if (this.player instanceof net.minecraftforge.common.util.FakePlayer) {
            cir.setReturnValue(false);
            return;
        }

        var errorMessage = AdvancementRewardManager.canReward(this.player, pAdvancement);
        if (errorMessage != null) {
            var message = new TextComponent("Nunu, żeby zdobyć osiągnięcie musisz spełnić kryteria!");
            this.player.sendMessage(message, ChatType.CHAT, NIL_UUID);
            this.player.sendMessage(new TextComponent(errorMessage).withStyle(ChatFormatting.RED), ChatType.CHAT, NIL_UUID);
            cir.setReturnValue(false);
        }
    }

}
