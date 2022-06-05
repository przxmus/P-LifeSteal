package com.samjakob.spigui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.pagination.SGPaginationButtonBuilder;
import com.samjakob.spigui.pagination.SGPaginationButtonType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

/**
 * SGMenu is used to implement the library's GUIs.
 * <br><br>
 * This is a Minecraft 'inventory' that contains items which can have
 * programmable actions performed when they are clicked. Additionally,
 * it automatically adds 'pagination' items if the menu overflows.
 * <br><br>
 * You do not instantiate this class when you need it - as you would
 * have done with the older version of the library - rather you make a
 * call to {@link SpiGUI#create(String, int)} (or {@link SpiGUI#create(String, int, String)})
 * from your plugin's {@link SpiGUI} instance.
 * <br><br>
 * This creates an inventory that is already associated with your plugin.
 * The reason for this is explained in the {@link SpiGUI#SpiGUI(JavaPlugin)}
 * class constructor implementation notes.
 */
public class SGMenu implements InventoryHolder {

    private final JavaPlugin owner;
    private final SpiGUI spiGUI;
    private final Map<Integer, SGButton> items;
    private final HashSet<Integer> stickiedSlots;
    private String name;
    private String tag;
    private int rowsPerPage;
    private int currentPage;
    private Boolean blockDefaultInteractions;
    private Boolean enableAutomaticPagination;

    private SGPaginationButtonBuilder paginationButtonBuilder;
    private Consumer<SGMenu> onClose;
    private Consumer<SGMenu> onPageChange;

    /**
     * Used by the library internally to construct an SGMenu.
     * The name parameter is color code translated.
     *
     * @param owner       The plugin the inventory should be associated with.
     * @param spiGUI      The SpiGUI that created this inventory.
     * @param name        The display name of the inventory.
     * @param rowsPerPage The number of rows per page.
     * @param tag         The inventory's tag.
     */
    SGMenu(final JavaPlugin owner, final SpiGUI spiGUI, final String name, final int rowsPerPage, final String tag) {
        this.owner = owner;
        this.spiGUI = spiGUI;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.rowsPerPage = rowsPerPage;
        this.tag = tag;

        items = new HashMap<>();
        stickiedSlots = new HashSet<>();

        currentPage = 0;
    }

    /// INVENTORY SETTINGS ///

    /**
     * This is a per-inventory version of {@link SpiGUI#setBlockDefaultInteractions(boolean)}.
     *
     * @param blockDefaultInteractions Whether or not the default behavior of click events should be cancelled.
     * @see SpiGUI#setBlockDefaultInteractions(boolean)
     */
    public void setBlockDefaultInteractions(final boolean blockDefaultInteractions) {
        this.blockDefaultInteractions = blockDefaultInteractions;
    }

    /**
     * This is a per-inventory version of {@link SpiGUI#areDefaultInteractionsBlocked()}.
     *
     * @return Whether or not the default behavior of click events should be cancelled.
     * @see SpiGUI#areDefaultInteractionsBlocked()
     */
    public Boolean areDefaultInteractionsBlocked() {
        return this.blockDefaultInteractions;
    }

    /**
     * This is a per-inventory version of {@link SpiGUI#setEnableAutomaticPagination(boolean)}.
     * If this value is set, it overrides the per-plugin option set in {@link SpiGUI}.
     *
     * @param enableAutomaticPagination Whether or not pagination buttons should be automatically added.
     * @see SpiGUI#setEnableAutomaticPagination(boolean)
     */
    public void setAutomaticPaginationEnabled(final boolean enableAutomaticPagination) {
        this.enableAutomaticPagination = enableAutomaticPagination;
    }

    /**
     * This is a per-inventory version of {@link SpiGUI#isAutomaticPaginationEnabled()}.
     *
     * @return Whether or not pagination buttons should be automatically added.
     * @see SpiGUI#isAutomaticPaginationEnabled()
     */
    public Boolean isAutomaticPaginationEnabled() {
        return this.enableAutomaticPagination;
    }

    /**
     * This is a per-inventory version of ({@link SpiGUI#getDefaultPaginationButtonBuilder()}).
     *
     * @return The default pagination button builder used for GUIs.
     * @see SpiGUI#getDefaultPaginationButtonBuilder()
     */
    public SGPaginationButtonBuilder getPaginationButtonBuilder() {
        return paginationButtonBuilder;
    }

    /**
     * This is a per-inventory version of ({@link SpiGUI#setDefaultPaginationButtonBuilder(SGPaginationButtonBuilder)}).
     *
     * @param paginationButtonBuilder The default pagination button builder used for GUIs.
     * @see SpiGUI#setDefaultPaginationButtonBuilder(SGPaginationButtonBuilder)
     */
    public void setPaginationButtonBuilder(final SGPaginationButtonBuilder paginationButtonBuilder) {
        this.paginationButtonBuilder = paginationButtonBuilder;
    }

    /// INVENTORY OWNER ///

    /**
     * Returns the plugin that the inventory is associated with.
     * As this field is final, this would be the plugin that created
     * the inventory.
     *
     * @return The plugin the inventory is associated with.
     */
    public JavaPlugin getOwner() {
        return this.owner;
    }

    /// INVENTORY SIZE ///

    /**
     * Returns the number of rows (of 9 columns) per page of the inventory.
     * If you want the total number of slots on a page, you should use {@link #getPageSize()}
     * instead.
     *
     * @return The number of rows per page.
     */
    public int getRowsPerPage() {
        return this.rowsPerPage;
    }

    /**
     * Sets the number of rows per page of the inventory.
     * <p>
     * There is no way to set the number of slots per page directly, so if
     * you need to do that, you'll need to divide the number of slots by 9
     * and supply the result to this parameter to achieve that.
     *
     * @param rowsPerPage The number of rows per page.
     */
    public void setRowsPerPage(final int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    /**
     * Returns the number of slots per page of the inventory. This would be
     * associated with the Bukkit/Spigot API's inventory 'size' parameter.
     * <p>
     * So for example if {@link #getRowsPerPage()} was 3, this would be 27,
     * as Minecraft Chest inventories have rows of 9 columns.
     *
     * @return The number of inventory slots per page.
     */
    public int getPageSize() {
        return this.rowsPerPage * 9;
    }

    /// INVENTORY TAG ///

    /**
     * This returns the GUI's tag.
     * <br><br>
     * The tag is used when getting all open inventories ({@link SpiGUI#findOpenWithTag(String)}) with your chosen tag.
     * An example of where this might be useful is with a permission GUI - when
     * the permissions are updated by one user in the GUI, it would be desirable to
     * refresh the state of the permissions GUI for all users observing the GUI.
     *
     * @return The GUI's tag.
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * This sets the GUI's tag.
     *
     * @param tag The GUI's tag.
     * @see #getTag()
     * @see SpiGUI#findOpenWithTag(String)
     */
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /// INVENTORY NAME ///

    /**
     * This sets the inventory's display name <b>without</b> first translating
     * color codes.
     *
     * @param name The display name to set.
     */
    public void setRawName(final String name) {
        this.name = name;
    }

    /**
     * This returns the inventory's display name.
     * <br><br>
     * Note that if you used {@link #setName(String)}, this will have been
     * color code translated already.
     *
     * @return The inventory's display name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * This sets the inventory's display name.
     * <br><br>
     * The name parameter is color code translated before the value is set.
     * If you want to avoid this behavior, you should use {@link #setRawName(String)}
     * which sets the inventory's name directly.
     *
     * @param name The display name to set. (and to be color code translated)
     */
    public void setName(final String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
    }

    /// BUTTONS ///

    /**
     * Adds the provided {@link SGButton}.
     *
     * @param button The button to add.
     */
    public void addButton(final SGButton button) {
        // If slot 0 is empty but it's the 'highest filled slot', then set slot 0 to contain button.
        // (This is an edge case for when the whole inventory is empty).
        if (this.getHighestFilledSlot() == 0 && this.getButton(0) == null) {
            this.setButton(0, button);
            return;
        }

        // Otherwise, add one to the highest filled slot, then use that slot for the new button.
        this.setButton(this.getHighestFilledSlot() + 1, button);
    }

    /**
     * Adds the specified {@link SGButton}s consecutively.
     *
     * @param buttons The buttons to add.
     */
    public void addButtons(final SGButton... buttons) {
        for (final SGButton button : buttons) this.addButton(button);
    }

    /**
     * Adds the provided {@link SGButton} at the position denoted by the
     * supplied slot parameter.
     * <p>
     * If you specify a value larger than the value of the first page,
     * pagination will be automatically applied when the inventory is
     * rendered. An alternative to this is to use {@link #setButton(int, int, SGButton)}.
     *
     * @param slot   The desired location of the button.
     * @param button The button to add.
     * @see #setButton(int, int, SGButton)
     */
    public void setButton(final int slot, final SGButton button) {
        this.items.put(slot, button);
    }

    /**
     * Adds the provided {@link SGButton} at the position denoted by the
     * supplied slot parameter <i>on the page denoted by the supplied page parameter</i>.
     * <p>
     * This is an alias for {@link #setButton(int, SGButton)}, however one where the slot
     * value is mapped to the specified page. So if page is 2 (the third page) and the
     * inventory row count was 3 (so a size of 27), a supplied slot value of 3 would actually map to
     * a slot value of (2 * 27) + 3 = 54. The mathematical formula for this is <code>(page * pageSize) + slot</code>.
     * <p>
     * If the slot value is out of the bounds of the specified page, this function will do nothing.
     *
     * @param page   The page to which the button should be added.
     * @param slot   The position on that page the button should be added at.
     * @param button The button to add.
     * @see #setButton(int, SGButton)
     */
    public void setButton(final int page, final int slot, final SGButton button) {
        if (slot < 0 || slot > this.getPageSize())
            return;

        this.setButton((page * this.getPageSize()) + slot, button);
    }

    /**
     * Removes a button from the specified slot.
     *
     * @param slot The slot containing the button you wish to remove.
     */
    public void removeButton(final int slot) {
        this.items.remove(slot);
    }

    /**
     * An alias for {@link #removeButton(int)} to remove a button from the specified
     * slot on the specified page.
     * <p>
     * If the slot value is out of the bounds of the specified page, this function will do nothing.
     *
     * @param page The page containing the button you wish to remove.
     * @param slot The slot, of that page, containing the button you wish to remove.
     */
    public void removeButton(final int page, final int slot) {
        if (slot < 0 || slot > this.getPageSize())
            return;

        this.removeButton((page * this.getPageSize()) + slot);
    }

    /**
     * Returns the {@link SGButton} in the specified slot.
     * <p>
     * If you attempt to get a slot less than 0 or greater than the slot containing
     * the button at the greatest slot value, this will return null.
     *
     * @param slot The slot containing the button you wish to get.
     * @return The {@link SGButton} that was in that slot or null if the slot was invalid or if there was no button that slot.
     */
    public SGButton getButton(final int slot) {
        if (slot < 0 || slot > this.getHighestFilledSlot())
            return null;

        return this.items.get(slot);
    }

    /**
     * This is an alias for {@link #getButton(int)} that allows you to get a button
     * contained by a slot on a given page.
     *
     * @param page The page containing the button.
     * @param slot The slot, on that page, containing the button.
     * @return The {@link SGButton} that was in that slot or null if the slot was invalid or if there was no button that slot.
     */
    public SGButton getButton(final int page, final int slot) {
        if (slot < 0 || slot > this.getPageSize())
            return null;

        return this.getButton((page * this.getPageSize()) + slot);
    }

    /// PAGINATION ///

    /**
     * Returns the current page of the inventory.
     * This is the page that will be displayed when the inventory is opened and
     * displayed to a player (i.e. rendered).
     *
     * @return The current page of the inventory.
     */
    public int getCurrentPage() {
        return this.currentPage;
    }

    /**
     * Sets the page of the inventory that will be displayed when the inventory is
     * opened and displayed to a player (i.e. rendered).
     *
     * @param page The new current page of the inventory.
     */
    public void setCurrentPage(final int page) {
        currentPage = page;
        if (onPageChange != null) onPageChange.accept(this);
    }

    /**
     * Gets the page number of the final page of the GUI.
     *
     * @return The highest page number that can be viewed.
     */
    public int getMaxPage() {
        return (int) Math.ceil(((double) this.getHighestFilledSlot() + 1) / ((double) this.getPageSize()));
    }

    /**
     * Returns the slot number of the highest filled slot.
     * This is mainly used to calculate the number of pages there needs to be to
     * display the GUI's contents in the rendered inventory.
     *
     * @return The highest filled slot's number.
     */
    public int getHighestFilledSlot() {
        int slot = 0;

        for (final int nextSlot : this.items.keySet()) {
            if (this.items.get(nextSlot) != null && nextSlot > slot)
                slot = nextSlot;
        }

        return slot;
    }

    /**
     * Increments the current page.
     * This will automatically refresh the inventory by calling {@link #refreshInventory(HumanEntity)} if
     * the page was changed.
     *
     * @param viewer The {@link HumanEntity} viewing the inventory.
     * @return Whether or not the page could be changed (false means the max page is currently open).
     */
    public boolean nextPage(final HumanEntity viewer) {
        if (this.currentPage < this.getMaxPage() - 1) {
            this.currentPage++;
            this.refreshInventory(viewer);
            if (onPageChange != null) onPageChange.accept(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Decrements the current page.
     * This will automatically refresh the inventory by calling {@link #refreshInventory(HumanEntity)} if
     * the page was changed.
     *
     * @param viewer The {@link HumanEntity} viewing the inventory.
     * @return Whether or not the page could be changed (false means the first page is currently open).
     */
    public boolean previousPage(final HumanEntity viewer) {
        if (this.currentPage > 0) {
            this.currentPage--;
            this.refreshInventory(viewer);
            if (onPageChange != null) onPageChange.accept(this);
            return true;
        } else {
            return false;
        }
    }


    /// STICKY SLOTS ///

    /**
     * Marks a slot as 'sticky', so that when the page is changed,
     * the slot will always display the value on the first page.
     * <p>
     * This is useful for implementing things like 'toolbars', where
     * you have a set of common items on every page.
     * <p>
     * If the slot is out of the bounds of the first page (i.e. less
     * than 0 or greater than {@link #getPageSize()} - 1) this method
     * will do nothing.
     *
     * @param slot The slot to mark as 'sticky'.
     */
    public void stickSlot(final int slot) {
        if (slot < 0 || slot >= this.getPageSize())
            return;

        stickiedSlots.add(slot);
    }

    /**
     * Un-marks a slot as sticky - thereby meaning that slot will display
     * whatever its value on the current page is.
     *
     * @param slot The slot to un-mark as 'sticky'.
     * @see #stickSlot(int)
     */
    public void unstickSlot(final int slot) {
        stickiedSlots.remove(Integer.valueOf(slot));
    }

    /**
     * This clears all the 'stuck' slots - essentially un-marking all
     * stuck slots.
     *
     * @see #stickSlot(int)
     */
    public void clearStickiedSlots() {
        stickiedSlots.clear();
    }

    /**
     * This checks whether a given slot is sticky.
     * If the slot is out of bounds of the first page (as defined by
     * the same parameters as {@link #stickSlot(int)}), this will return
     * false.
     *
     * @param slot The slot to check.
     * @return True if the slot is sticky, false if it isn't or the slot was out of bounds.
     * @see #stickSlot(int)
     */
    public boolean isStickiedSlot(final int slot) {
        if (slot < 0 || slot >= this.getPageSize())
            return false;

        return stickiedSlots.contains(slot);
    }

    /**
     * This clears all slots in the inventory, except those which
     * have been marked as 'sticky'.
     *
     * @see #stickSlot(int)
     */
    public void clearAllButStickiedSlots() {
        currentPage = 0;
        this.items.entrySet().removeIf(item -> !this.isStickiedSlot(item.getKey()));
    }

    /// EVENTS ///

    /**
     * @return The action to be performed on close.
     * @see #setOnClose(Consumer)
     */
    public Consumer<SGMenu> getOnClose() {
        return onClose;
    }

    /**
     * Used to set an action to be performed on inventory close without
     * registering an {@link org.bukkit.event.inventory.InventoryCloseEvent} specifically
     * for this inventory.
     *
     * @param onClose The action to be performed on close.
     */
    public void setOnClose(final Consumer<SGMenu> onClose) {
        this.onClose = onClose;
    }

    /**
     * @return The action to be performed on page change.
     * @see #setOnPageChange(Consumer)
     */
    public Consumer<SGMenu> getOnPageChange() {
        return onPageChange;
    }

    /**
     * Used to set an action to be performed on inventory page change.
     *
     * @param onPageChange The action to be performed on page change.
     */
    public void setOnPageChange(final Consumer<SGMenu> onPageChange) {
        this.onPageChange = onPageChange;
    }

    /// INVENTORY API ///

    public void refreshInventory(final HumanEntity viewer) {
        // If the open inventory isn't an SGMenu - or if it isn't this inventory, do nothing.
        if (
                !(viewer.getOpenInventory().getTopInventory().getHolder() instanceof SGMenu)
                        || viewer.getOpenInventory().getTopInventory().getHolder() != this
        ) return;

        // If the new size is different, we'll need to open a new inventory.
        if (viewer.getOpenInventory().getTopInventory().getSize() != this.getPageSize() + (this.getMaxPage() > 0 ? 9 : 0)) {
            viewer.openInventory(this.getInventory());
            return;
        }

        // If the name has changed, we'll need to open a new inventory.
        final String newName = this.name.replace("{currentPage}", String.valueOf(this.currentPage + 1))
                .replace("{maxPage}", String.valueOf(this.getMaxPage()));
        if (!viewer.getOpenInventory().getTitle().equals(newName)) {
            viewer.openInventory(this.getInventory());
            return;
        }

        // Otherwise, we can refresh the contents without re-opening the inventory.
        viewer.getOpenInventory().getTopInventory().setContents(this.getInventory().getContents());
    }

    /**
     * Returns the Bukkit/Spigot {@link Inventory} that represents the GUI.
     * This is shown to a player using {@link HumanEntity#openInventory(Inventory)}.
     *
     * @return The created inventory used to display the GUI.
     */
    @Override
    public Inventory getInventory() {
        boolean isAutomaticPaginationEnabled = this.spiGUI.isAutomaticPaginationEnabled();
        if (this.isAutomaticPaginationEnabled() != null) {
            isAutomaticPaginationEnabled = this.isAutomaticPaginationEnabled();
        }

        final boolean needsPagination = this.getMaxPage() > 0 && isAutomaticPaginationEnabled;

        final Inventory inventory = Bukkit.createInventory(this, (
                        (needsPagination)
                                // Pagination enabled: add the bottom toolbar row.
                                ? this.getPageSize() + 9
                                // Pagination not required or disabled.
                                : this.getPageSize()
                ),
                this.name.replace("{currentPage}", String.valueOf(this.currentPage + 1))
                        .replace("{maxPage}", String.valueOf(this.getMaxPage()))
        );

        // Add the main inventory items.
        for (int key = this.currentPage * this.getPageSize(); key < (this.currentPage + 1) * this.getPageSize(); key++) {
            // If we've already reached the maximum assigned slot, stop assigning
            // slots.
            if (key > this.getHighestFilledSlot()) break;

            if (this.items.containsKey(key)) {
                inventory.setItem(key - (this.currentPage * this.getPageSize()), this.items.get(key).getIcon());
            }
        }

        // Update the stickied slots.
        for (final int stickiedSlot : this.stickiedSlots) {
            inventory.setItem(stickiedSlot, this.items.get(stickiedSlot).getIcon());
        }

        // Render the pagination items.
        if (needsPagination) {
            SGPaginationButtonBuilder paginationButtonBuilder = this.spiGUI.getDefaultPaginationButtonBuilder();
            if (this.getPaginationButtonBuilder() != null) {
                paginationButtonBuilder = this.getPaginationButtonBuilder();
            }

            final int pageSize = this.getPageSize();
            for (int i = pageSize; i < pageSize + 9; i++) {
                final int offset = i - pageSize;

                final SGButton paginationButton = paginationButtonBuilder.buildPaginationButton(
                        SGPaginationButtonType.forSlot(offset), this
                );
                inventory.setItem(i, paginationButton != null ? paginationButton.getIcon() : null);
            }
        }

        return inventory;
    }

}
