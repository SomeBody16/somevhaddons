package network.something.somevhaddons.addon.applied_energetics_2.export;

import appeng.api.behaviors.StackExportStrategy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import network.something.somevhaddons.addon.applied_energetics_2.export.strategy.AltarExportStrategy;
import network.something.somevhaddons.addon.applied_energetics_2.export.strategy.MultiExportStrategy;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ExportStrategyProvider {

    protected static Map<ExportStrategyFactory, IsCompatible> strategies = new HashMap<>();

    public static void init() {
        registerExportStrategy(AltarExportStrategy::new, AltarExportStrategy::isCompatible);
    }

    @Nullable
    public static StackExportStrategy get(ServerLevel level, BlockPos pos, Direction fromSide) {
        var compatibleStrategies = strategies.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isCompatible(level, pos, fromSide))
                .map(Map.Entry::getKey)
                .map(factory -> factory.create(level, pos, fromSide))
                .toList();
        if (!compatibleStrategies.isEmpty()) {
            return new MultiExportStrategy(compatibleStrategies);
        }
        return null;
    }

    public static void registerExportStrategy(ExportStrategyFactory factory, IsCompatible isCompatible) {
        strategies.put(factory, isCompatible);
    }

    @FunctionalInterface
    public interface IsCompatible {
        boolean isCompatible(ServerLevel level, BlockPos pos, Direction side);
    }

    @FunctionalInterface
    public interface ExportStrategyFactory {
        StackExportStrategy create(ServerLevel level, BlockPos pos, Direction side);
    }

}
