package network.something.somevhaddons.addon.applied_energetics_2.export;

import appeng.api.behaviors.StackExportStrategy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import network.something.somevhaddons.addon.applied_energetics_2.export.strategy.AltarExportStrategy;
import network.something.somevhaddons.addon.applied_energetics_2.export.strategy.MultiExportStrategy;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ExportStrategyProvider {

    protected static Map<StrategyFactory, CompatibleCheck> strategies = new HashMap<>();

    public static void init() {
        registerExportStrategy(AltarExportStrategy::new, AltarExportStrategy::isCompatible);
    }

    public static void registerExportStrategy(StrategyFactory factory, CompatibleCheck isCompatible) {
        strategies.put(factory, isCompatible);
    }

    @Nullable
    public static StackExportStrategy get(ServerLevel level, BlockPos pos, Direction fromSide) {
        var blockState = level.getBlockState(pos);
        var compatible = strategies
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isCompatible(level, pos, fromSide, blockState))
                .map(entry -> entry.getKey().create(level, pos, fromSide))
                .toList();

        if (!compatible.isEmpty()) {
            return new MultiExportStrategy(compatible);
        }
        return null;
    }

    @FunctionalInterface
    public interface CompatibleCheck {
        boolean isCompatible(ServerLevel level, BlockPos pos, Direction fromSide, BlockState blockState);
    }

    @FunctionalInterface
    public interface StrategyFactory {
        StackExportStrategy create(ServerLevel level, BlockPos pos, Direction fromSide);
    }

}
