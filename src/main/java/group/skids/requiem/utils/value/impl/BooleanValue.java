package group.skids.requiem.utils.value.impl;


import group.skids.requiem.utils.value.Value;
import group.skids.requiem.utils.value.parse.BooleanParser;

import java.util.Optional;

public class BooleanValue extends Value<Boolean> {
    private String description;
    public BooleanValue(String label, String description, Boolean value) {
        super(label, value);
        this.description = description;
    }
    public BooleanValue(String label, Boolean value) {
        super(label, value);
        this.description = label;
    }
    public BooleanValue(String label, String description, Boolean value, Value parentValueObject, String parentValue) {
        super(label, value, parentValueObject, parentValue);
        this.description = description;
    }
    public BooleanValue(String label, Boolean value, Value parentValueObject, String parentValue) {
        super(label, value, parentValueObject, parentValue);
        this.description = label;
    }
    @Override
    public void setValue(String input) {
        Optional<Boolean> result = BooleanParser.parse(input);
        result.ifPresent(aBoolean -> this.value = aBoolean);
    }

    public String getDescription() {
        return description;
    }

    public void toggle() {
        this.value ^= true;
    }

    public boolean isEnabled() {
        if (getParentValueObject() != null && !getParentValueObject().getValueAsString().equalsIgnoreCase(getParentValue())) return false;
        return this.value;
    }

    public void setEnabled(boolean enabled) {
        this.value = enabled;
    }
}