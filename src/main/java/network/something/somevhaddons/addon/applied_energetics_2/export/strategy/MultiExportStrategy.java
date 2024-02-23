package network.something.somevhaddons.addon.applied_energetics_2.export.strategy;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;

import java.util.List;

public class MultiExportStrategy implements StackExportStrategy {

    protected final List<StackExportStrategy> strategies;

    public MultiExportStrategy(List<StackExportStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long maxAmount, Actionable mode) {
        for (var strategy : strategies) {
            var result = strategy.transfer(context, what, maxAmount, mode);
            if (result > 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        for (var strategy : strategies) {
            var result = strategy.push(what, amount, mode);
            if (result > 0) {
                return result;
            }
        }
        return 0;
    }
}
