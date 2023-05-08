package network.something.somevhaddons.api.util.the_vault;

import java.util.Objects;

public class JewelAttribute {

    public final String id;
    public final Class<?> valueType;

    public final int red;
    public final int green;
    public final int blue;

    JewelAttribute(String id, Class<?> valueType, int red, int green, int blue) {
        this.id = id;
        this.valueType = valueType;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int intColor() {
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

    public boolean is(JewelAttribute other) {
        return Objects.equals(other.id, id);
    }

}
