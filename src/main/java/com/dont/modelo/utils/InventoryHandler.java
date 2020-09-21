package com.dont.modelo.utils;

import com.dont.modelo.Terminal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author don't
 */
public class InventoryHandler {

    private static final List<Integer> ALLOWED = Arrays.asList(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34);

    static {
        JavaPlugin main = Terminal.getInstance();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getSlotType() == SlotType.OUTSIDE || e.getCurrentItem() == null) return;
                if (e.getInventory().getHolder() instanceof HandlerHolder) {
                    e.setCancelled(true);
                    InventoryHandler handler = ((HandlerHolder) e.getInventory().getHolder()).getInventoryHandler();
                    if (handler.getCustomItems().containsKey(e.getSlot())) {
                        handler.getCustomItems().get(e.getSlot()).accept((Player) e.getWhoClicked());
                    } else if (handler.getHandler() != null) {
                        handler.getHandler().accept(e);
                    }
                }
            }
        }, main);
    }

    private Inventory inventory;
    private Consumer<InventoryClickEvent> handler;
    private Map<Integer, Consumer<Player>> customItems;

    public InventoryHandler(String name, int size) {
        this.inventory = Bukkit.createInventory(new HandlerHolder(this), size, name);
        this.customItems = new HashMap<>();
    }

    public InventoryHandler handler(Consumer<InventoryClickEvent> handler) {
        this.handler = handler;
        return this;
    }

    public InventoryHandler item(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    public InventoryHandler item(int slot, ItemStack item, Consumer<Player> consumer) {
        inventory.setItem(slot, item);
        customItems.put(slot, consumer);
        return this;
    }

    public InventoryHandler items(List<ItemStack> items) {
        items(items, ALLOWED);
        return this;
    }

    private InventoryHandler items(List<ItemStack> items, List<Integer> allowed) {
        int lastIndex = 0;
        for (int i = 0; i < 54; i++) {
            if (!allowed.contains(i))
                continue;
            if (lastIndex >= items.size())
                break;
            inventory.setItem(i, items.get(lastIndex));
            lastIndex++;
        }
        return this;
    }

    public InventoryHandler fill(ItemStack item, boolean replace) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || replace) inventory.setItem(i, item);
        }
        return this;
    }

    public InventoryHandler fill(ItemStack item) {
        return fill(item, true);
    }

    public InventoryHandler item(ItemStack item, int... slot) {
        for (int i : slot) inventory.setItem(i, item);
        return this;
    }

    public InventoryHandler items(Consumer<Inventory> consumer) {
        consumer.accept(this.inventory);
        return this;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    private Consumer<InventoryClickEvent> getHandler() {
        return handler;
    }

    private Map<Integer, Consumer<Player>> getCustomItems() {
        return customItems;
    }

    private class HandlerHolder implements InventoryHolder {

        private InventoryHandler handler;

        public HandlerHolder(InventoryHandler handler) {
            super();
            this.handler = handler;
        }

        @Override
        public Inventory getInventory() {
            return handler.inventory;
        }

        public InventoryHandler getInventoryHandler() {
            return handler;
        }

    }

}