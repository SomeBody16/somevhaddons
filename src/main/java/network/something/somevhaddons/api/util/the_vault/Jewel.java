package network.something.somevhaddons.api.util.the_vault;

import iskallia.vault.gear.data.VaultGearData;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Jewel {

    public final ItemStack item;
    public final Integer level;
    public final Map<String, Object> attributes = new HashMap<>();

    @SuppressWarnings("ConstantConditions")
    public Jewel(ItemStack item) {
        this.item = item;

        var data = VaultGearData.read(item);
        this.level = data.getItemLevel();

        data.getAllAttributes().forEach(attr -> {
            var key = attr.getAttribute().getRegistryName().toString();
            var value = attr.getValue();
            attributes.put(key, value);
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttrValue(JewelAttribute attr) {
        return (T) attributes.get(attr.id);
    }

    public int getSize() {
        return getAttrValue(JewelAttributes.SIZE);
    }
}

