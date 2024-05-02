package de.newrp.API;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemDecoder {
    // converts an itemstack to a string
    public static String itemToString(ItemStack item) {
        if (item != null) {
            String itemString = "";
            String mat = item.getType().toString();
            String amount = ((Integer) item.getAmount()).toString();
            Map<Enchantment, Integer> enchants = item.getEnchantments();
            String fullEnchantmentString = "";
// item meta
            String displayName = "";
            String loreString = "";
            if (item.hasItemMeta()) {
// write display name to string
                try {
                    displayName = item.getItemMeta().getDisplayName();
                    displayName = displayName.replaceAll(" ", "_");
                } catch (NullPointerException e) {
// if the item doesnt have a display name
                }
// write lore to string
                try {
                    List<String> lore = item.getItemMeta().getLore();
                    String loreString2 = "";
// for every string in the lore
                    for (int x = 0; x < lore.size(); x++) {
                        loreString2 = loreString + lore.get(x);
// if it is not the last string in the lore
                        if (x != lore.size() - 1) {
// | = new line char in decoding method
                            loreString2 = loreString2 + "|";
                        }
                    }
                    loreString = loreString2;
                } catch (NullPointerException e) {
// if the item doesnt have a lore
                }
            }

// Write enchants to string
            Set<Map.Entry<Enchantment, Integer>> exampleEntry = enchants.entrySet();
            for (Map.Entry<Enchantment, Integer> e : exampleEntry) {
                Enchantment ench = e.getKey();
                String lvl = e.getValue().toString();

            }

// ex: bow 1 name:SomeDisplayName lore:SomeLore
            itemString = mat + " " + amount;
            if (!displayName.equals(""))
                itemString = itemString + " name:" + displayName;
            if (!loreString.equals(""))
                itemString = itemString + " lore:" + loreString;
            return itemString;
        }
        return "";
    }


    public static ItemStack stringToItem(String item) {
// seperates each word in the item string into an array
        String[] itemSplit = item.split(" ");
        List<String> itemWordList = Arrays.asList(itemSplit);
// material
        String materialName = itemWordList.get(0);
        Material mat = Material.valueOf(materialName.toUpperCase());
// amount
        int amount = 0;
        try {
            amount = Integer.valueOf(itemWordList.get(1));
        }
// if the config doesnt specify an amount (e.g. bow)
        catch (ArrayIndexOutOfBoundsException e) {
            amount = 1;
        }
// display name
        String name = null;
        for (String word : itemWordList) {
// if config specifies a name in the word
// e.g. name:Test
            if (word.contains("name:")) {
                String[] nameArray = word.split(":");
                name = ChatColor.translateAlternateColorCodes('&', nameArray[1]);
// allow spaces with the use of the character _
                name = name.replaceAll("_", " ");
            }
        }
// lore
        List<String> lore = null;
        for (String word : itemWordList) {
// if config specifies a lore in the word
// e.g. lore:Test
            if (word.contains("lore:")) {
// full lore array is lore, user-defined-lore
                String[] fullLoreArray = word.split(":");
                String loreString = ChatColor.translateAlternateColorCodes('&', fullLoreArray[1]);
// lorestring is the lore as a string, e.g. Test
// usage of an underscore adds a space
                loreString = loreString.replaceAll("_", " ");
// vertical bar represents a new line
                String[] loreArray = loreString.split("\\|");
// loreArray is a list of the lore
                lore = Arrays.asList(loreArray);
            }
        }


        ItemStack itemStack = new ItemStack(mat, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
// if config specifies a name
        if (name != null)
            itemMeta.setDisplayName(name);
        if (lore != null)
            itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
