package network.something.somevhaddons.mixin;

import iskallia.vault.block.TransmogTableBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(TransmogTableBlock.class)
public class TransmogTableMixin {

    @Inject(
            method = "canTransmogModel",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void canTransmogModel(Player player, Collection discoveredModelIds, ResourceLocation modelId, CallbackInfoReturnable<Boolean> cir) {
        var canTransmog = discoveredModelIds.contains(modelId);
        cir.setReturnValue(canTransmog);
    }

}
