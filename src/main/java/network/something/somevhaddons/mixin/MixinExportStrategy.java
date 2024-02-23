package network.something.somevhaddons.mixin;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEKey;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.IOBusPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.applied_energetics_2.export.ExportStrategyProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExportBusPart.class)
public abstract class MixinExportStrategy extends IOBusPart implements ICraftingRequester {

    @Shadow
    protected abstract @NotNull StackTransferContext createTransferContext(IStorageService storageService, IEnergyService energyService);

    @Shadow
    protected abstract int getStartingSlot(SchedulingMode schedulingMode, int x);

    @Shadow
    protected abstract boolean craftOnly();

    @Shadow
    protected abstract void attemptCrafting(StackTransferContext context, ICraftingService cg, int slotToExport, AEKey what);

    @Shadow
    protected abstract StackExportStrategy getExportStrategy();

    @Shadow
    protected abstract boolean isCraftingEnabled();

    @Shadow
    protected abstract void updateSchedulingMode(SchedulingMode schedulingMode, int x);

    @Shadow
    private StackExportStrategy exportStrategy;

    @Inject(
            method = "getExportStrategy",
            at = @At("HEAD"),
            remap = false
    )
    private void getExportStrategy(CallbackInfoReturnable<StackExportStrategy> cir) {
        if (exportStrategy == null) {
            var self = this.getHost().getBlockEntity();
            var fromPos = self.getBlockPos().relative(this.getSide());
            var fromSide = getSide().getOpposite();
            SomeVHAddons.LOGGER.info("Trying to get factory for {}:{}", fromPos, fromSide);
            exportStrategy = ExportStrategyProvider.get((ServerLevel) getLevel(), fromPos, fromSide);
        }
    }

    @Inject(
            method = "doBusWork",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void doBusWork(IGrid grid, CallbackInfoReturnable<Boolean> cir) {
        var storageService = grid.getStorageService();
        var cg = grid.getCraftingService();
        var fzMode = getConfigManager().getSetting(Settings.FUZZY_MODE);
        var schedulingMode = getConfigManager().getSetting(Settings.SCHEDULING_MODE);

        var context = createTransferContext(storageService, grid.getEnergyService());

        int x = 0;
        for (; x < availableSlots() && context.hasOperationsLeft(); x++) {
            final int slotToExport = getStartingSlot(schedulingMode, x);
            var what = getConfig().getKey(slotToExport);

            if (craftOnly() && what != null) {
                attemptCrafting(context, cg, slotToExport, what);
                continue;
            }

            var before = context.getOperationsRemaining();

            if (isUpgradedWith(AEItems.FUZZY_CARD)) {
                // When fuzzy exporting, simply attempt export of all items in the set of fuzzy-equals keys
                for (var fuzzyWhat : ImmutableList
                        .copyOf(storageService.getCachedInventory().findFuzzy(what, fzMode))) {
                    // The max amount exported is scaled by the key-space's transfer factor (think millibuckets vs.
                    // items)
                    var transferFactory = fuzzyWhat.getKey().getAmountPerOperation();
                    long amount = (long) context.getOperationsRemaining() * transferFactory;
                    amount = getExportStrategy().transfer(context, fuzzyWhat.getKey(), amount, Actionable.MODULATE);
                    context.reduceOperationsRemaining(Math.max(1, amount / transferFactory));
                    if (!context.hasOperationsLeft()) {
                        break;
                    }
                }
            } else {
                // The max amount exported is scaled by the key-space's transfer factor (think millibuckets vs. items)
                var transferFactor = what.getAmountPerOperation();
                long amount = (long) context.getOperationsRemaining() * transferFactor;
                amount = getExportStrategy().transfer(context, what, amount, Actionable.MODULATE);
                if (amount > 0) {
                    context.reduceOperationsRemaining(Math.max(1, amount / transferFactor));
                }
            }

            if (before == context.getOperationsRemaining() && isCraftingEnabled()) {
                attemptCrafting(context, cg, slotToExport, what);
            }
        }

        // Round-robin should only advance if something was actually exported
        if (context.hasDoneWork()) {
            updateSchedulingMode(schedulingMode, x);
        }

        cir.setReturnValue(context.hasDoneWork());
    }

    public MixinExportStrategy(TickRates tickRates, IPartItem<?> partItem) {
        super(tickRates, partItem);
    }

}
