package network.something.somevhaddons.block.jewel_station.inventory.sorter;

import net.minecraft.world.item.ItemStack;
import network.something.somevhaddons.api.util.the_vault.Jewel;
import network.something.somevhaddons.api.util.the_vault.JewelAttribute;
import network.something.somevhaddons.api.util.the_vault.JewelAttributes;

import java.util.Comparator;

public class JewelAttributeSorter implements Comparator<ItemStack> {

    enum Direction {
        ASCENDING,
        DESCENDING;

        public Direction opposite() {
            return this == ASCENDING ? DESCENDING : ASCENDING;
        }
    }

    protected final JewelAttribute attr;

    public JewelAttributeSorter(JewelAttribute attr) {
        this.attr = attr;
    }

    @Override
    public int compare(ItemStack left, ItemStack right) {
        return compare(
                new Jewel(left),
                new Jewel(right)
        );
    }

    public int compare(Jewel left, Jewel right) {
        var direction = Direction.ASCENDING;

        if (attr.is(JewelAttributes.SIZE)) {
            return compareNumber(
                    left.getSize(),
                    right.getSize(),
                    direction
            );
        }

        if (attr.valueType == Boolean.class) {
            direction = direction.opposite();
        }

        return compareNumber(
                getRatioValue(right),
                getRatioValue(left),
                direction
        );
    }

    protected float getRatioValue(Jewel jewel) {
        var value = jewel.getAttrValue(attr);
        float size = jewel.getSize();

        if (value instanceof Boolean) {
            return size;
        }
        if (value instanceof Integer intValue) {
            return intValue / size;
        }
        if (value instanceof Float floatValue) {
            return floatValue / size;
        }
        if (value instanceof Double doubleValue) {
            return doubleValue.floatValue() / size;
        }

        return 0;
    }

    protected int compareNumber(float left, float right, Direction direction) {
        if (left == right) {
            return 0;
        }

        if (direction == Direction.ASCENDING) {
            return (left > right) ? 1 : -1;
        } else {
            return (right > left) ? 1 : -1;
        }
    }
}
