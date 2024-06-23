package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ItemBuilder {

    private final ItemStack is;
    private final ItemMeta im;

    public ItemBuilder(Material material, short subID) {
        is = new ItemStack(material, 1, subID);
        im = is.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(material, (short) 0);
    }

    public ItemBuilder(ItemStack itemStack) {
        is = itemStack;
        im = is.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        im.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        im.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder setNBTString(String key, String value) {
        im.getPersistentDataContainer().set(new NamespacedKey(NewRoleplayMain.getInstance(), key), PersistentDataType.STRING, value);
        return this;
    }

    public ItemBuilder setNoDrop() {
        return setNBTString("noDrop", "true");
    }

    public boolean isNoDrop() {
        return hasNBTString("noDrop");
    }

    public boolean hasNBTString(String key) {
        if(im == null) {
            return false;
        }
        return im.getPersistentDataContainer().has(new NamespacedKey(NewRoleplayMain.getInstance(), key), PersistentDataType.STRING);
    }

    public ItemStack build() {
        is.setItemMeta(im);
        return is;
    }
}
