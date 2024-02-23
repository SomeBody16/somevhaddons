package network.something.somevhaddons.addon.applied_energetics_2.export.strategy;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import com.google.common.primitives.Ints;
import com.mojang.logging.LogUtils;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.PlayerVaultAltarData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import network.something.somevhaddons.SomeVHAddons;
import org.slf4j.Logger;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class AltarExportStrategy implements StackExportStrategy {

    public static boolean isCompatible(ServerLevel level, BlockPos pos, Direction fromSide) {
        return level.getBlockState(pos).is(ModBlocks.VAULT_ALTAR);
    }

    public static final Logger LOGGER = LogUtils.getLogger();

    protected ServerLevel level;
    protected BlockPos fromPos;
    protected Direction fromSide;

    public AltarExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        LOGGER.info("Created altar export strategy for {}:{}", fromPos, fromSide);
        this.level = level;
        this.fromPos = fromPos;
        this.fromSide = fromSide;
    }

    protected long altarIdle(StackTransferContext ctx, Actionable mode, VaultAltarTileEntity altar) {
        var storage = ctx.getInternalStorage();
        var what = AEItemKey.of(ModItems.VAULT_ROCK);
        var extracted = StorageHelper.poweredExtraction(
                ctx.getEnergySource(),
                storage.getInventory(),
                what,
                1,
                ctx.getActionSource(),
                Actionable.SIMULATE
        );

        LOGGER.info("IDLE: extracted {} of {} ({})", extracted, what, mode);
        if (extracted > 0) {
            if (mode == Actionable.MODULATE) {
                extracted = StorageHelper.poweredExtraction(
                        ctx.getEnergySource(),
                        storage.getInventory(),
                        what,
                        1,
                        ctx.getActionSource(),
                        Actionable.MODULATE
                );

                LOGGER.info("IDLE: extracted {} of {} (MODULATE ACTION)", extracted, what);
                var vaultRock = what.toStack(1);
                var player = (ServerPlayer) level.getPlayerByUUID(altar.getOwner());
                altar.onAddVaultRock(player, vaultRock);
            }

            return 1;
        }

        return 0;
    }

    protected long altarAccepting(StackTransferContext ctx, long amount, Actionable mode, VaultAltarTileEntity altar) {
        if (altar.getRecipe().isComplete()) {
            return 0;
        }

        var altarData = PlayerVaultAltarData.get(level);
        var recipe = altarData.getRecipe(altar.getOwner());
        if (recipe == null) return 0;

        var storage = ctx.getInternalStorage();
        for (var required : recipe.getRequiredItems()) { // Check pools (4 corners of altar)
            if (required.isComplete()) continue;

            for (var item : required.getItems()) { // Each pool can accept multiple items
                var what = AEItemKey.of(item);
                var toInsert = Math.min(amount, required.getAmountRequired());

                // Leave one item in network
                var amountInNetwork = storage.getCachedInventory().get(what);
                if (amountInNetwork - toInsert <= 0) {
                    toInsert -= 1;
                }

                // Check if item can be supplied
                var extracted = StorageHelper.poweredExtraction(
                        ctx.getEnergySource(),
                        storage.getInventory(),
                        what,
                        toInsert,
                        ctx.getActionSource(),
                        Actionable.SIMULATE
                );

                if (extracted > 0) { // It can
                    if (mode == Actionable.MODULATE) { // Apply to altar
                        extracted = StorageHelper.poweredExtraction(
                                ctx.getEnergySource(),
                                storage.getInventory(),
                                what,
                                toInsert,
                                ctx.getActionSource(),
                                Actionable.MODULATE
                        );

                        var newAltarAmount = required.getCurrentAmount() + Ints.saturatedCast(extracted);
                        required.setCurrentAmount(newAltarAmount);
                        altar.sendUpdates();
                        PlayerVaultAltarData.get().setDirty();
                    }

                    return extracted;
                }


            }

        }

        return 0;
    }

    @Override
    public long transfer(StackTransferContext ctx, AEKey what, long amount, Actionable mode) {
        var altar = this.getAltar();
        if (altar == null) return 0;

        var state = altar.getAltarState();
        return switch (state) {
            case IDLE -> altarIdle(ctx, mode, altar);
            case ACCEPTING -> altarAccepting(ctx, amount, mode, altar);
            default -> 0;
        };
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        SomeVHAddons.LOGGER.error("PUSH NOT SUPPORTED IN AltarExportStrategy");
        return 0;
    }

    @Nullable
    protected VaultAltarTileEntity getAltar() {
        var blockEntity = this.level.getBlockEntity(fromPos);
        if (blockEntity instanceof VaultAltarTileEntity) {
            return (VaultAltarTileEntity) blockEntity;
        }
        return null;
    }
}
