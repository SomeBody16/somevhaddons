package network.something.somevhaddons.mixin;

import appeng.api.behaviors.StackExportStrategy;
import appeng.parts.automation.StackWorldBehaviors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.applied_energetics_2.export.ExportStrategyProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StackWorldBehaviors.class)
public abstract class MixinCreateExportFacade {

    @Inject(
            method = "createExportFacade",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private static void createExportFacade(ServerLevel level, BlockPos fromPos, Direction fromSide,
                                           CallbackInfoReturnable<StackExportStrategy> cir) {
        var addonStrategy = ExportStrategyProvider.get(level, fromPos, fromSide);
        SomeVHAddons.LOGGER.info("FOUND STRATEGY FOR {}:{} = {}", fromPos, fromSide, addonStrategy);
        if (addonStrategy != null) {
            cir.setReturnValue(addonStrategy);
        }
    }

}
