package de.newrp.Medic;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Krankheit;
import de.newrp.Shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Medikamente {

    HUSTENSAFT(1, "Hustensaft", null, new ItemBuilder(Material.PAPER).setName("§fHustensaft").build(), new ItemBuilder(Material.PAPER).setName("Rezept für Hustensaft").build() ,Krankheit.HUSTEN, 10, false, true),
    SCHMERZMITTEL(2, "Schmerzmittel", ShopItem.SCHMERZMITTEL, new ItemBuilder(Material.PAPER).setName("§fSchmerzmittel").build(), new ItemBuilder(Material.PAPER).setName("Rezept für Schmerzmittel").build(), null, 10, false, false);

    int id;
    String name;
    ShopItem item;
    ItemStack is;
    ItemStack rezept;
    Krankheit k;
    int needed;
    boolean rezeptneeded;
    boolean insurancepay;

    Medikamente(int id, String name, ShopItem item, ItemStack is, ItemStack rezept, Krankheit k, int needed, boolean rezeptneeded, boolean insurancepay) {
        this.id = id;
        this.name = name;
        this.item = item;
        this.is = is;
        this.rezept = rezept;
        this.k = k;
        this.needed = needed;
        this.rezeptneeded = rezeptneeded;
        this.insurancepay = insurancepay;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Krankheit getKrankheit() {
        return k;
    }

    public int getNeeded() {
        return needed;
    }

    public boolean isRezeptNeeded() {
        return rezeptneeded;
    }

    public ItemStack getItemStack() {
        return is;
    }

    public ShopItem getShopItem() {
        return item;
    }

    public ItemStack getRezept() {
        return rezept;
    }

    public boolean insurancePays() {
        return insurancepay;
    }

    public static Medikamente getMedikament(String name) {
        for (Medikamente m : Medikamente.values()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public static Medikamente getMedikament(int id) {
        for (Medikamente m : Medikamente.values()) {
            if (m.getID() == id) {
                return m;
            }
        }
        return null;
    }

    public static Medikamente getMedikament(Krankheit k) {
        for (Medikamente m : Medikamente.values()) {
            if (m.getKrankheit().equals(k)) {
                return m;
            }
        }
        return null;
    }

    public static Medikamente getMedikamentByNeeded(int needed) {
        for (Medikamente m : Medikamente.values()) {
            if (m.getNeeded() == needed) {
                return m;
            }
        }
        return null;
    }

    public static Medikamente getMedikamentByItemStack(ItemStack is) {
        for (Medikamente m : Medikamente.values()) {
            if (m.getItemStack().equals(is)) {
                return m;
            }
        }
        return null;
    }

    public static Medikamente getMedikamentByShopItem(ShopItem item) {
        for (Medikamente m : Medikamente.values()) {
            if (m.getShopItem().equals(item)) {
                return m;
            }
        }
        return null;
    }

    public static List<Medikamente> getAllMedikamente() {
        return new ArrayList<>(Arrays.asList(Medikamente.values()));
    }

}
