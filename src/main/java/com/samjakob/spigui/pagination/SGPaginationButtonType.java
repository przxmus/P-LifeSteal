package com.samjakob.spigui.pagination;

public enum SGPaginationButtonType {

    PREV_BUTTON(3),
    CURRENT_BUTTON(4),
    NEXT_BUTTON(5),
    UNASSIGNED(0);

    private final int slot;

    SGPaginationButtonType(final int slot) {
        this.slot = slot;
    }

    public static SGPaginationButtonType forSlot(final int slot) {
        for (final SGPaginationButtonType buttonType : values()) {
            if (buttonType.slot == slot) return buttonType;
        }

        return UNASSIGNED;
    }

    public int getSlot() {
        return this.slot;
    }

}
