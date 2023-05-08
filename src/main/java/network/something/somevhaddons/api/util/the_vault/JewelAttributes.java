package network.something.somevhaddons.api.util.the_vault;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JewelAttributes {

    private static List<JewelAttribute> ATTRIBUTES = new ArrayList<>();

    public static JewelAttribute SIZE = create("the_vault:jewel_size", Float.class, 218, 218, 218);
    public static JewelAttribute WOODEN_AFFINITY = create("the_vault:wooden_affinity", Boolean.class, 178, 88, 11);
    public static JewelAttribute ORNATE_AFFINITY = create("the_vault:ornate_affinity", Boolean.class, 233, 37, 37);
    public static JewelAttribute GILDED_AFFINITY = create("the_vault:gilded_affinity", Boolean.class, 202, 161, 18);
    public static JewelAttribute LIVING_AFFINITY = create("the_vault:living_affinity", Boolean.class, 113, 252, 64);
    public static JewelAttribute COIN_AFFINITY = create("the_vault:coin_affinity", Boolean.class, 252, 252, 0);
    public static JewelAttribute PICKING = create("the_vault:picking", Boolean.class, 231, 231, 231);
    public static JewelAttribute AXING = create("the_vault:axing", Boolean.class, 194, 170, 120);
    public static JewelAttribute SHOVELING = create("the_vault:shovelling", Boolean.class, 223, 227, 157);
    public static JewelAttribute HAMMER_SIZE = create("the_vault:hammer_size", Integer.class, 26, 145, 113);
    public static JewelAttribute MINING_SPEED = create("the_vault:mining_speed", Float.class, 71, 184, 245);
    public static JewelAttribute COPIOUSLY = create("the_vault:copiously", Float.class, 244, 70, 126);
    public static JewelAttribute ITEM_QUANTITY = create("the_vault:item_quantity", Float.class, 232, 138, 18);
    public static JewelAttribute ITEM_RARITY = create("the_vault:item_rarity", Float.class, 229, 184, 18);
    public static JewelAttribute DURABILITY = create("the_vault:durability", Integer.class, 223, 208, 254);
    public static JewelAttribute TRAP_DISARMING = create("the_vault:trap_disarming", Float.class, 127, 66, 252);
    public static JewelAttribute REACH = create("the_vault:reach", Float.class, 132, 215, 255);
    public static JewelAttribute IMMORTALITY = create("the_vault:immortality", Float.class, 173, 139, 193);
    public static JewelAttribute SOULBOUND = create("the_vault:soulbound", Boolean.class, 150, 100, 253);
    public static JewelAttribute PULVERIZING = create("the_vault:pulverizing", Boolean.class, 114, 177, 114);
    public static JewelAttribute SMELTING = create("the_vault:smelting", Boolean.class, 252, 68, 0);

    private static JewelAttribute create(String id, Class<?> valueType, int red, int green, int blue) {
        var attr = new JewelAttribute(id, valueType, red, green, blue);
        ATTRIBUTES.add(attr);
        return attr;
    }

    public static JewelAttribute get(String id) {
        return ATTRIBUTES.stream()
                .filter(attr -> Objects.equals(attr.id, id))
                .toList().get(0);
    }


    private JewelAttributes() {
    }
}
