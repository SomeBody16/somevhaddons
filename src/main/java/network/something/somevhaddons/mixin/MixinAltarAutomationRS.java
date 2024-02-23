package network.something.somevhaddons.mixin;

import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExporterNetworkNode;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.PlayerVaultAltarData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.util.Debounced;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ExporterNetworkNode.class)
public abstract class MixinAltarAutomationRS {

    @Shadow
    @Final
    private UpgradeItemHandler upgrades;
    @Unique
    private final Debounced somevhaddons$debounced = new Debounced(5);


    @Inject(
            method = "update",
            at = @At(value = "INVOKE", target = "Lcom/refinedmods/refinedstorage/util/LevelUtils;getItemHandler(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/items/IItemHandler;"),
            cancellable = true,
            remap = false
    )
    public void update(CallbackInfo ci) {
        var This = (ExporterNetworkNode) (Object) this;
        if (This.getFacingBlockEntity() instanceof VaultAltarTileEntity altar
                && This.getLevel() instanceof ServerLevel serverLevel
                && This.getNetwork() != null) {

            AtomicBoolean workDone = new AtomicBoolean(false);
            var state = altar.getAltarState();
            switch (state) {
                case IDLE -> somevhaddons$debounced.run(() -> {
                    var result = somevhaddons$handleIdle(altar, serverLevel);
                    workDone.set(result);
                });
                case ACCEPTING -> somevhaddons$debounced.run(() -> {
                    var result = somevhaddons$handleAccepting(altar, serverLevel);
                    workDone.set(result);
                });
            }

            if (workDone.get()) {
                somevhaddons$debounced.skipDelay();
            }

            ci.cancel();
        }
    }

    @Unique
    private boolean somevhaddons$handleIdle(VaultAltarTileEntity altar, ServerLevel level) {
        var This = (ExporterNetworkNode) (Object) this;
        var vaultRock = new ItemStack(ModItems.VAULT_ROCK);
        var took = This.getNetwork().extractItem(vaultRock, 1, IComparer.COMPARE_NBT, Action.SIMULATE);

        if (took.isEmpty()) {
//            if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
//                var request = new SlottedCraftingRequest(This, filterSlot);
//                This.getNetwork().getCraftingManager().request(request, vaultRock, 1);
//            }
            return false;
        }

        took = This.getNetwork().extractItem(vaultRock, 1, IComparer.COMPARE_NBT, Action.PERFORM);
        var player = (ServerPlayer) level.getPlayerByUUID(altar.getOwner());
        altar.onAddVaultRock(player, took);
        return true;
    }

    @Unique
    private boolean somevhaddons$handleAccepting(VaultAltarTileEntity altar, ServerLevel level) {
        var This = (ExporterNetworkNode) (Object) this;
        if (altar.getRecipe().isComplete()) {
            return false;
        }

        var altarData = PlayerVaultAltarData.get(level);
        var recipe = altarData.getRecipe(altar.getOwner());
        if (recipe == null) return false;

        var stackInteractCount = upgrades.getStackInteractCount();

        for (var required : recipe.getRequiredItems()) {
            if (required.isComplete()) continue;

            // Check if anything in network
            var canSupplyItems = new HashMap<ItemStack, Integer>();
            for (var item : required.getItems()) {
                var amount = somevhaddons$getNetworkAmount(item);
                if (amount > 1) {
                    canSupplyItems.put(item, amount);
                }
            }

            SomeVHAddons.LOGGER.info("canSupplyItems: {}", canSupplyItems);

            // If nothing in network, try craft then skip pool
            if (canSupplyItems.isEmpty()) {
                if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
                    for (var item : required.getItems()) {
                        var pattern = This.getNetwork().getCraftingManager().getPattern(item);
                        if (pattern == null) continue;

                        var amount = Math.min(
                                required.getAmountRequired() + 1,
                                upgrades.getStackInteractCount()
                        );
                        This.getNetwork().getCraftingManager().request(this, item, amount);
                        break;
                    }
                }
                continue;
            }

            AtomicBoolean workDone = new AtomicBoolean(false);
            canSupplyItems.forEach((item, amountInNetwork) -> {
                var extractSize = Math.min(
                        Math.min(item.getMaxStackSize(), stackInteractCount),
                        required.getAmountRequired()
                );

                // Leave one item in network
                if (amountInNetwork - extractSize <= 0) {
                    extractSize = amountInNetwork - 1;
                }

                var took = This.getNetwork().extractItem(item, extractSize, IComparer.COMPARE_NBT, Action.PERFORM);

                var newAltarAmount = required.getCurrentAmount() + took.getCount();
                required.setCurrentAmount(newAltarAmount);

                altar.sendUpdates();
                PlayerVaultAltarData.get().setDirty();
                workDone.set(true);
            });
            return workDone.get();
        }
        return false;
    }

    @Unique
    private int somevhaddons$getNetworkAmount(ItemStack toCheck) {
        var This = (ExporterNetworkNode) (Object) this;
        if (This.getNetwork() == null || toCheck.isEmpty()) {
            return 0;
        }

        ItemStack stored = This.getNetwork().getItemStorageCache().getList().get(toCheck, IComparer.COMPARE_NBT);
        return stored != null ? stored.getCount() : 0;
    }

}
