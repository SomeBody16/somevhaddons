package network.something.somevhaddons.mixin;

import iskallia.vault.util.EnchantmentEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentEntry.class)
public class EnchantmentEntryMixin {

    @Shadow(remap = false)
    private int level;
    @Shadow(remap = false)
    private Enchantment enchantment;

    @Inject(
            method = "<init>*",
            at = @At("RETURN"),
            remap = false
    )
    private void init(Enchantment enchantment, int level, CallbackInfo ci) {
        var registryName = this.enchantment.getRegistryName();
        if (registryName != null && registryName.toString().equals("minecraft:fortune")) {
            this.level = 4;
        }
    }

    @Inject(
            method = "isValid()Z",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void isValid(CallbackInfoReturnable<Boolean> ci) {
        ResourceLocation registryName = this.enchantment.getRegistryName();
        if (registryName != null
                && registryName.toString().equals("minecraft:fortune")
                && this.level <= 4) {
            ci.setReturnValue(true);
        }
    }

}
