package group.skids.requiem.utils.value.impl;

import group.skids.requiem.utils.value.Value;

import java.awt.*;


public class ColorValue extends Value<Integer> {

    public ColorValue(String label, int value) {
        super(label, value);
    }

    public ColorValue(String label, int value, Value parentValueObject, String parentValue) {
        super(label, value, parentValueObject, parentValue);
    }

    @Override
    public void setValue(String value) {

    }
    public Color getColor() {
        return new Color(getValue());
    }
}
