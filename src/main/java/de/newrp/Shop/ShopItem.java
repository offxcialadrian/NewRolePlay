package de.newrp.Shop;

import de.newrp.API.*;
import de.newrp.House.HouseAddon;
import de.newrp.Medic.Medikamente;
import de.newrp.Vehicle.CarType;
import de.newrp.Waffen.Weapon;
import de.newrp.NewRoleplayMain;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum ShopItem {

    BROT(0, "§7Brot", new ItemBuilder(Material.BREAD).setName("§7Brot").setAmount(8).build(), 2, 1, 20, 4, 2400, 1, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.CAFE}),
    KAFFEE(1, "§rKaffee", new ItemBuilder(Material.FLOWER_POT).setName("§rKaffee").build(), 3, 1, 20, 2, 1500, 1, false, true, false, new ShopType[] {ShopType.CAFE}),
    LOTTOSCHEIN(2, "§7Lottoschein", new ItemBuilder(Material.PAPER).setName("§7Lottoschein").build(), 1, 1, 20, 25, 7500, 2, false, true, false, new ShopType[] {ShopType.NEWS}),
    HAUSKASSE(3, "§7Hauskasse", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.HAUSKASSE.getPrice(), 6000, 8, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    MIETERSLOT(4, "§7Mieterslot", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.SLOT.getPrice(), 10000, 10, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    WAFFENSCHRANK(5, "§7Waffenschrank", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.WAFFENSCHRANK.getPrice(), 14000, 10, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    ALARMANLAGE(7, "§7Alarmanlage", new ItemStack(Material.REDSTONE), 10, 1, 20, HouseAddon.ALARM.getPrice(), 12000, 6, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    KUEHLSCHRANK(9, "§7Kühlschrank", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.KUEHLSCHRANK.getPrice(), 1200, 5, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    PISTOLE(10, "§7Glory", new ItemStack(Material.IRON_HORSE_ARMOR), 15, 1, 1, 2000, 12000, 4, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AMMO_9MM(11, "§79mm Munition (" + Weapon.PISTOLE.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.PISTOLE.getMagazineSize()).build(), 1, 1, 1, 15, 6000, 4, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AK47(12, "§7Peacekeeper", new ItemStack(Material.DIAMOND_HORSE_ARMOR), 20, 1, 1, 5000, 22000, 16, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    HEISSE_SCHOKOLADE(43, "§7Heiße Schokolade",new ItemBuilder(Material.FLOWER_POT).setName("§7Heiße Schokolade").build(), 3, 2, 25, 3, 1800, 1, true, true, false, new ShopType[] {ShopType.CAFE}),
    FILTERKAFFEE(44, "§7Filterkaffee", new ItemBuilder(Material.FLOWER_POT).setName("§7Filterkaffee").build(), 3, 2, 25, 3, 1800, 1, true, true, false, new ShopType[] {ShopType.CAFE}),
    LATTE_MACCHIATO(45, "§7Latte Macchiato", new ItemBuilder(Material.FLOWER_POT).setName("§7Latte Macchiato").build(), 3, 2, 25, 3, 1800, 1, true, true, false, new ShopType[] {ShopType.CAFE}),
    ESPRESSO(46, "§7Espresso", new ItemBuilder(Material.FLOWER_POT).setName("§7Erpresso").build(), 3, 2, 25, 4, 2100, 1, true, true, false, new ShopType[] {ShopType.CAFE}),
    CRAPPUCHINO(47, "§7Crappuchino", new ItemBuilder(Material.FLOWER_POT).setName("§7Crappuchino").build(), 3, 2, 25, 3, 1800, 1, true, true, false, new ShopType[] {ShopType.CAFE}),
    ZEITUNG(48, de.newrp.News.Zeitung.zeitung.getItemMeta().getDisplayName(), de.newrp.News.Zeitung.zeitung, 3, 2, 25, de.newrp.News.Zeitung.getBuyPrice(), 1800, 2, true, false, false, new ShopType[] {ShopType.CAFE, ShopType.SUPERMARKET, ShopType.NEWS}),
    SCHMERZMITTEL(49, "§fSchmerzmittel", new ItemBuilder(Material.PAPER).setName("§fSchmerzmittel").setAmount(Medikamente.SCHMERZMITTEL.getNeeded()).build(), 10, 2, 25, 45, 4900, 5, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    BASEBALLSCHLAEGER(50, "§7Baseballschläger", Baseballschlaeger.getItem(), 1, 1, 1, 320, 4500, 4, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SAMSUNG_HANDY(52, "§cSamstar", new ItemBuilder(Material.GOLD_INGOT).setName("§cSamstar").build(), 5, 1, 1, 100, 3500, 10, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    HUAWEI_HANDY(53, "§cHawaii P55", new ItemBuilder(Material.IRON_INGOT).setName("§cHawaii P55").build(), 5, 1, 1, 50, 600, 8, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    APPLE_HANDY(55, "§cyouPhone 15", new ItemBuilder(Material.NETHERITE_INGOT).setName("§cyouPhone 15").build(), 5, 1, 1, 300, 11000, 12, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    MAP(56, "§7Karte", new ItemBuilder(Material.MAP).setName("§7Karte").build(), 3, 1, 1, 10, 1100, 2, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.NEWS}),
    VERBAND(57, "§7Verband", new ItemBuilder(Material.PAPER).setName("§7Verband").build(), 3, 1, 1, 60, 4300, 4, false, true, false, new ShopType[] {ShopType.PHARMACY}),
    TRINKWASSER(58, "§7Trinkwasser", new ItemBuilder(Material.POTION).setName("§7Trinkwasser").build(), 1, 1, 1, 2, 500, 1, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.CAFE, ShopType.NEWS}),
    AMMO_762MM(59, "§7.762mm Munition (" + Weapon.AK47.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.AK47.getMagazineSize()).build(), 1, 1, 1, 30, 3000, 10, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    KEVLAR(51, "§7Schutzweste", Script.kevlar(1), 1, 1, 1, 1000, 14900, 7, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SCHMERZMITTEL_HIGH(60, "§fSchmerzmittel (High)", new ItemBuilder(Material.PAPER).setName("§fSchmerzmittel (High)").setAmount(Medikamente.SCHMERZMITTEL_HIGH.getNeeded()).build(), 10, 2, 25, 65, 6100, 8, true, true, false, new ShopType[] {ShopType.PHARMACY }),
    ANTIBIOTIKA(61, "§fAntibiotika", new ItemBuilder(Material.PAPER).setName("§fAntibiotika").setAmount(Medikamente.ANTIBIOTIKA.getNeeded()).build(), 10, 2, 25, 75, 4900, 6, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    HUSTENSAFT(62, "§fHustensaft", new ItemBuilder(Material.PAPER).setName("§fHustensaft").setAmount(Medikamente.HUSTENSAFT.getNeeded()).build(), 10, 2, 25, 60, 3400, 6, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    ENTZUENDUNGSHEMMENDE_SALBE(63, "§fEntzündungshemmende Salbe", new ItemBuilder(Material.PAPER).setName("§fEntzündungshemmende Salbe").setAmount(Medikamente.ENTZUENDUNGSHEMMENDE_SALBE.getNeeded()).build(), 10, 2, 25, 30, 2600, 3, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    CD_1(65, "§6Gold", new ItemStack(Material.MUSIC_DISC_11), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_2(66, "§6Green", new ItemStack(Material.MUSIC_DISC_13), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_3(67, "§6Blocks", new ItemStack(Material.MUSIC_DISC_BLOCKS), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_4(68, "§6Chirp", new ItemStack(Material.MUSIC_DISC_CHIRP), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_5(69, "§6Far", new ItemStack(Material.MUSIC_DISC_FAR), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_6(70, "§6Mall", new ItemStack(Material.MUSIC_DISC_MALL), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_7(71, "§6Mellohi", new ItemStack(Material.MUSIC_DISC_MELLOHI), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_8(72, "§6Stal", new ItemStack(Material.MUSIC_DISC_STAL), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_9(73, "§6Strad", new ItemStack(Material.MUSIC_DISC_STRAD),   1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_10(74, "§6Ward", new ItemStack(Material.MUSIC_DISC_WARD), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_11(75, "§6Wait", new ItemStack(Material.MUSIC_DISC_WAIT), 1, 1, 1, 1, 50, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    ROSE(76, "§7Rose", new ItemBuilder(Material.WHITE_TULIP).setName("§7Rose").build(), 1, 1, 1, 10, 600, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    TULPE(77, "§7Tulpe", new ItemBuilder(Material.RED_TULIP).setName("§7Tulpe").build(), 1, 1, 1, 5, 300, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    MARGARITE(78, "§7Margarite", new ItemBuilder(Material.OXEYE_DAISY).setName("§7Margarite").build(), 1, 1, 1, 3, 150, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    GERBERA(79, "§7Gerbera", new ItemBuilder(Material.ORANGE_TULIP).setName("§7Gerbera").build(), 1, 1, 1, 3, 150, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    LILIEN(80, "§7Lilien", new ItemBuilder(Material.WHITE_TULIP).setName("§7Lilien").build(), 1, 1, 1, 3, 150, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    MARGARITE_PINK(81, "§7Pink Margarite", new ItemBuilder(Material.PINK_TULIP).setName("§7Pink Margarite").build(), 1, 1, 1, 4, 200, 1, false, true, false, new ShopType[] {ShopType.FLOWER}),
    WATER_BUCKET(82, "§9Wasser", Script.setNameAndLore(new ItemStack(Material.WATER_BUCKET), "§9Wasser", "§65/5"), 5, 1, 1, 40, 2000, 10, false, false, false, new ShopType[] {ShopType.FLOWER}),
    DUENGER(83, "§7Dünger", new ItemBuilder(Material.INK_SAC).setName("§7Dünger").build(), 1, 1, 1, 50, 2600, 10, false, false, false, new ShopType[] {ShopType.FLOWER}),
    EINZELZIMMER(84, "§7Einzelzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, 0, false, false, false, new ShopType[] {ShopType.HOTEL}),
    DOPPELZIMMER(85, "§7Doppelzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, 0, false, false, false, new ShopType[] {ShopType.HOTEL}),
    MEHRBETTZIMMER(86, "§7Mehrbettzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, 0, false, false, false, new ShopType[] {ShopType.HOTEL}),
    SUITE(87, "§7Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, 0, false, false, false, new ShopType[] {ShopType.HOTEL}),
    EXECUTIVE_SUITE(88, "§7Executive Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, 0, false, false, false, new ShopType[] {ShopType.HOTEL}),
    DELUXE_ZIMMER(89, "§7Deluxe Zimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, 0, false, false, false, new ShopType[] {ShopType.HOTEL}),
    PRAESIDENTEN_SUITE(90, "§7Präsidenten Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, 0, false, false, false, new ShopType[] {ShopType.HOTEL}),
    EINZELFAHRAUSWEIS(91, "§6UBahn-Ticket [Einzelfahrausweis]", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [Einzelfahrausweis]").setLore("Verbleibende Fahrten: 1").build(), 1, 1, 1, 3, 1300, 1, false, false, false, new ShopType[] {ShopType.NEWS}),
    WOCHENFAHRAUSWEIS(92, "§6UBahn-Ticket [7 Fahrten]", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [7 Fahrten]").setLore("Verbleibende Fahrten: 7").build(), 1, 1, 1, 10, 2400, 1, false, false, false, new ShopType[] {ShopType.NEWS}),
    MONATSFAHRAUSWEIS(93, "§6UBahn-Ticket [30 Fahrten]", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [30 Fahrten]").setLore("Verbleibende Fahrten: 30").build(), 1, 1, 1, 20, 5100, 2, false, false, false, new ShopType[] {ShopType.NEWS}),
    FALLSCHIRM(94, "§7Fallschirm", new ItemBuilder(Material.ELYTRA).setName("§7Fallschirm").build(), 5, 1, 1, 600, 4100, 2, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    KABELBINDER(95, "§7Kabelbinder", new ItemBuilder(Material.STRING).setName("§7Kabelbinder").build(), 3, 1, 1, 100, 1900, 3, false, true, false, new ShopType[] {ShopType.GUNSHOP, ShopType.SUPERMARKET}),
    JAGDFLINTE(96, "§7Guardian", new ItemBuilder(Material.DIAMOND_HOE).setName("§7Guardian").build(), 5, 1, 1, 5500, 10000, 5, false, false, false, new ShopType[] {ShopType.JAGDHUETTE}),
    SCHROT(97, "§7Schrot (" + Weapon.JAGDFLINTE.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.JAGDFLINTE.getMagazineSize()).build(), 2, 1, 1, 50, 4500, 3, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    DEAGLE(98, "§7Ivory", new ItemBuilder(Material.GOLDEN_HOE).setName("§7Ivory").build(), 15, 1, 1, 4000, 16600, 6, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AMMO_50AE(99, "§7.50AE Munition (" + Weapon.DESERT_EAGLE.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.DESERT_EAGLE.getMagazineSize()).build(), 2, 1, 1, 25, 4500, 3, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    ANGELRUTE(100, "§7Angelrute", new ItemBuilder(Material.FISHING_ROD).setName("§7Angelrute").build(), 10, 1, 1, 25, 1590, 0, false, true, false, new ShopType[] {ShopType.ANGELSHOP}),
    STEAK(101, "§fSteak", new ItemBuilder(Material.COOKED_BEEF).setName("§fSteak").setAmount(8).build(), 2, 1, 20, 2, 2000, 1, false, true, false, new ShopType[] {ShopType.FASTFOOD}),
    POMMES(102, "§fPommes", new ItemBuilder(Material.BAKED_POTATO).setName("§fPommes").setAmount(16).build(), 2, 1, 20, 3, 2400, 1, false, true, false, new ShopType[] {ShopType.FASTFOOD}),
    CHICKEN(103, "§fHähnchen", new ItemBuilder(Material.COOKED_BEEF).setName("§fHähnchen").setAmount(8).build(), 2, 1, 20, 1, 2400, 1, false, true, false, new ShopType[] {ShopType.FASTFOOD}),
    SHOE_1(104, "§7Schuh Schwarz", Shoe(Color.BLACK), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_2(105, "§7Schuh Blau", Shoe(Color.BLUE), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_3(106, "§7Schuh Grün", Shoe(Color.GREEN), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_4(107, "§7Schuh Rot", Shoe(Color.RED), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_5(108, "§7Schuh Lila", Shoe(Color.PURPLE), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_6(109, "§7Schuh Gelb", Shoe(Color.YELLOW), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_7(110, "§7Schuh Weiß", Shoe(Color.WHITE), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_8(111, "§7Schuh Orange", Shoe(Color.ORANGE), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_9(112, "§7Schuh Fuchisa", Shoe(Color.FUCHSIA), 1, 1, 1, 50, 500, 0, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    CARROTT(113, "§fKarotte", new ItemBuilder(Material.CARROT).setName("§fKarotte").setAmount(16).build(), 2, 1, 20, 1, 2000, 1, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    POTATO(114, "§fBeeren", new ItemBuilder(Material.SWEET_BERRIES).setName("§fBeeren").setAmount(16).build(), 2, 1, 20, 1, 2000, 1, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    APPLE(115, "§fApfel", new ItemBuilder(Material.APPLE).setName("§fApfel").setAmount(8).build(), 2, 1, 20, 3, 2000, 1, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    MELON(116, "§fMelone", new ItemBuilder(Material.MELON_SLICE).setName("§fMelone").setAmount(16).build(), 2, 1, 20, 2, 2000, 1, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    HANDY_REPAIR(117, "§7Handy Reparatur", new ItemBuilder(Material.ANVIL).setName("§7Handy Reparatur").build(), 1, 1, 1, 25, 5000, 6, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    POWERBANK(118, "§3Powerbank", Script.setNameAndLore(Material.NAME_TAG, "§3Powerbank", "§c5§7/§c5"), 1, 1, 1, 10, 5000, 2, false, true, false, new ShopType[] {ShopType.ELECTRONIC}),
    MEHL(119, "§fMehl", new ItemBuilder(Material.WHITE_DYE).setName("§fMehl").build(), 1, 1, 1, 4, 2000, 1, false, true, false, new ShopType[] {ShopType.SUPERMARKET}),
    MASK(120, "§7Maske", new ItemBuilder(Material.CARVED_PUMPKIN).setName("§7Maske").build(), 1, 1, 1, 250, 500, 3, false, true, false, new ShopType[] {ShopType.SUPERMARKET}),
    SHISHA_1(121, "§6Shisha " + ShishaType.DOPPELAPFEL.getName(), Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.DOPPELAPFEL.getName(), "§c" + ShishaType.DOPPELAPFEL.getName(), "§7" + ShishaType.DOPPELAPFEL.getDuration() + "/" + ShishaType.DOPPELAPFEL.getDuration()), 1, 1, 1, 10, 500, 0, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_2(122, "§6Shisha " + ShishaType.PFIRSICH_MINZE.getName(), Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.PFIRSICH_MINZE.getName(), "§c" + ShishaType.PFIRSICH_MINZE.getName(), "§7" + ShishaType.PFIRSICH_MINZE.getDuration() + "/" + ShishaType.PFIRSICH_MINZE.getDuration()), 1, 1, 1, 10, 500, 0, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_3(123, "§6Shisha " + ShishaType.WASSERMELONE.getName(), Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.WASSERMELONE.getName(), "§c" + ShishaType.WASSERMELONE.getName(), "§7" + ShishaType.WASSERMELONE.getDuration() + "/" + ShishaType.WASSERMELONE.getDuration()), 1, 1, 1, 10, 500, 0, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_4(124, "§6Shisha " + ShishaType.ZITRONE.getName(), Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.ZITRONE.getName(), "§c" + ShishaType.ZITRONE.getName(), "§7" + ShishaType.ZITRONE.getDuration() + "/" + ShishaType.ZITRONE.getDuration()), 1, 1, 1, 10, 500, 0, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_5(125, "§6Shisha " + ShishaType.SPECIAL.getName(), Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.SPECIAL.getName(), "§c" + ShishaType.SPECIAL.getName(), "§7" + ShishaType.SPECIAL.getDuration() + "/" + ShishaType.SPECIAL.getDuration()), 1, 1, 1, 10, 500, 0, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_6(126, "§6Shisha " + ShishaType.TRAUBE.getName(), Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.TRAUBE.getName(), "§c" + ShishaType.TRAUBE.getName(), "§7" + ShishaType.TRAUBE.getDuration() + "/" + ShishaType.TRAUBE.getDuration()), 1, 1, 1, 10, 500, 0, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SCHWERE_KEVLAR(127, "§7Schwere Schutzweste", Script.kevlar(2), 1, 1, 1, 2000, 14900, 8, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    OPPEL(128,"§e" + CarType.OPPEL.getName(), Script.setName(new ItemStack(CarType.OPPEL.getMaterial(), 1), "§e" + CarType.OPPEL.getName()), 1, 1, 1, 6000, 10000, 10, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    VOLTSWAGEN(129, "§e" + CarType.VOLTSWAGEN.getName(), Script.setName(new ItemStack(CarType.VOLTSWAGEN.getMaterial(), 1), "§e" + CarType.VOLTSWAGEN.getName()), 1, 1, 1, 10000, 8000, 14, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    NMW(130, "§e" + CarType.NMW.getName(), Script.setName(new ItemStack(CarType.NMW.getMaterial(), 1), "§e" + CarType.NMW.getName()), 1, 1, 1, 16000, 14000, 18, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    AWDI(131, "§e" + CarType.AWDI.getName(), Script.setName(new ItemStack(CarType.AWDI.getMaterial(), 1), "§e" + CarType.AWDI.getName()), 1, 1, 1, 24000, 20000, 22, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    MERCADAS(132, "§e" + CarType.MERCADAS.getName(), Script.setName(new ItemStack(CarType.MERCADAS.getMaterial(), 1), "§e" + CarType.MERCADAS.getName()), 1, 1, 1, 35000, 30000, 26, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    PAWSCHE(133, "§e" + CarType.PAWSCHE.getName(), Script.setName(new ItemStack(CarType.PAWSCHE.getMaterial(), 1), "§e" + CarType.PAWSCHE.getName()), 1, 1, 1, 45000, 40000, 30, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    KANISTER(134, "§cKanister", Script.setName(new ItemStack(Material.RED_SHULKER_BOX, 1), "§cKanister"), 1, 1, 1, 50, 1200, 1, false, true, false, new ShopType[] {ShopType.GAS_STATION}),
    INSURANCE(135, "§aVersicherung", Script.setName(new ItemStack(Material.PAPER, 1), "§aVersicherung"), 1, 1, 1, 1000, 5400, 5, false, true, false, new ShopType[] {ShopType.CARDEALER}),
    LICENSE(136, "§bKennzeichen", Script.setName(new ItemStack(Material.NAME_TAG, 1), "§bKennzeichen"), 1, 1, 1, 1500, 3600, 4, false, true, false, new ShopType[] {ShopType.CARDEALER}),
    TOOLS(137, "§7Werkzeug", Script.setName(new ItemStack(Material.SHEARS, 1), "§7Werkzeug"), 1, 1, 1, 100, 500, 4, false, true, false, new ShopType[] {ShopType.GAS_STATION}),
    MESSER(138, "§7Messer", Messer.getItem(), 1, 1, 1, 150, 4000, 5, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SICHERHEITSTUER(139, "§7Sicherheitstür", new ItemStack(Material.IRON_DOOR), 10, 1, 20, HouseAddon.SICHERHEITSTUER.getPrice(), 13000, 9, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    VODKA(140, "§6Kartoffelwasser", Script.setName(new ItemStack(Material.HONEY_BOTTLE), "§6Kartoffelwasser"), 1, 1, 1, 6, 600, 1, true, true, false, new ShopType[] {ShopType.BAR}),
    BIER(141, "§6Gerstensaft", Script.setName(new ItemStack(Material.HONEY_BOTTLE), "§6Gerstensaft"), 1, 1, 1, 4, 400, 1, true, true, false, new ShopType[] {ShopType.BAR}),
    LIKOER(142, "§6Kräutertrunk", Script.setName(new ItemStack(Material.HONEY_BOTTLE), "§6Kräutertrunk"), 1, 1, 1, 8, 800, 1, true, true, false, new ShopType[] {ShopType.BAR}),
    TEDDY(143, "§dTeddy", createItem(3006, "§dTeddy"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    PANDA(144, "§dPanda", createItem(352, "§dPanda"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    DEER(145, "§dRentier", createItem(69035, "§dRentier"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    UNICORN(146, "§dEinhorn", createItem(25401, "§dEinhorn"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    WHALE(147, "§dWal", createItem(1755, "§dWal"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    SHARK(148, "§dHai", createItem(51444, "§dHai"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    SHEEP(149, "§dSchaf", createItem(62925, "§dSchaf"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    BEE(150, "§dBiene", createItem(31264, "§dBiene"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    FROG(151, "§dFrosch", createItem(51343, "§dFrosch"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    DINO(152, "§dDino", createItem(57302, "§dDino"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    ELEPHANT(153, "§dElefant", createItem(31297, "§dElefant"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    FISH(154, "§dFisch", createItem(22794, "§dFisch"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    PARROT(155, "§dPapagei", createItem(349, "§dPapagei"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    PIG(156, "§dSchwein", createItem(14201, "§dSchwein"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    COW(157, "§dKuh", createItem(51250, "§dKuh"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    TURTLE(158, "§dSchildkröte", createItem(65736, "§dSchildkröte"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    LION(159, "§dLöwe", createItem(4662, "§dLöwe"), 1, 1, 1, 30, 1000, 0, false, true, false, new ShopType[] {ShopType.MERCH}),
    PET_DOG(160, "§aHund", Script.setName(new ItemStack(Material.WOLF_SPAWN_EGG), "§aHund"), 1, 1, 1, 4000, 6000, 30, false, false, false, new ShopType[] {ShopType.PETS}),
    PET_CAT(161, "§aKatze", Script.setName(new ItemStack(Material.CAT_SPAWN_EGG), "§aKatze"), 1, 1, 1, 6000, 8000, 30, false, false, false, new ShopType[] {ShopType.PETS}),
    PET_PARROT(162, "§aPapagei", Script.setName(new ItemStack(Material.PARROT_SPAWN_EGG), "§aPapagei"), 1, 1, 1, 10000, 12000, 30, false, false, false, new ShopType[] {ShopType.PETS}),
    PET_FOX(163, "§aFuchs", Script.setName(new ItemStack(Material.FOX_SPAWN_EGG), "§aFuchs"), 1, 1, 1, 20000, 15000, 30, false, false, false, new ShopType[] {ShopType.PETS}),
    CD_12(164, "§6Pigstep", new ItemStack(Material.MUSIC_DISC_PIGSTEP), 1, 1, 1, 5, 200, 0, false, true, false, new ShopType[] {ShopType.MUSIC}),
    DOENER(165, "§7Döner", new ItemBuilder(Material.COOKED_MUTTON).setName("§7Döner").setAmount(1).build(), 1, 1, 1, 1, 3000, 1, false, true, false, new ShopType[] {ShopType.FASTFOOD}),
    FLAMMI(166, "§cFlammenwerfer", Script.setNameAndLore(Material.BLAZE_POWDER, "§cFlammenwerfer", "§6500§8/§6500"), 8, 1, 1, 4000, 23000, 10, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SUPER_ANGEL(167, "§6§lSuper Angel", new ItemBuilder(Material.FISHING_ROD).addEnchantment(Enchantment.DURABILITY, 3).addEnchantment(Enchantment.LUCK, 2).setName("§6§lSuper Angel").build(), 1, 1, 1, 200, 5000, 10, false, true, false, new ShopType[] {ShopType.ANGELSHOP}),
    JAGDMESSER(168, "§7Jagdmesser", Jagdmesser.getItem(), 1, 1, 1, 600, 8000, 5, false, true, false, new ShopType[] {ShopType.JAGDHUETTE});


    private final int id;
    private final String name;
    private final ItemStack is;
    private final int size;
    private final int min;
    private final int max;
    private final int buyPrice;
    private final int licensePrice;
    private final int tax;
    private final boolean reopen;
    private final boolean addtoinv;
    private final boolean premium;
    private final ShopType[] types;

    ShopItem(int id, String name, ItemStack is, int size, int min, int max, int buyPrice, int licensePrice, int tax, boolean reopen, boolean addtoinv, boolean premium, ShopType[] types) {
        this.id = id;
        this.name = name;
        this.is = is;
        this.size = size;
        this.min = min;
        this.max = max;
        this.buyPrice = buyPrice;
        this.licensePrice = licensePrice;
        this.tax = tax;
        this.reopen = reopen;
        this.addtoinv = addtoinv;
        this.premium = premium;
        this.types = types;
    }

    public static ItemStack getItemStack(int id) {
        for (ShopItem i : values()) {
            if (i.getID() == id) return i.getItemStack();
        }
        return null;
    }

    public static ShopItem getItem(int id) {
        for (ShopItem i : values()) {
            if (i.getID() == id) return i;
        }
        return null;
    }

    public boolean isReopen() {
        return reopen;
    }

    public int getLicensePrice() {
        return licensePrice;
    }

    public int getTax() {
        return tax;
    }

    public static ShopItem getShopItem(ItemStack is) {
        if (is == null) return null;
        if (!is.hasItemMeta()) {
            Debug.debug("[ShopItem] " + is.getType() + " has no itemMeta");
            return null;
        }
        if (!is.getItemMeta().hasDisplayName()) {
            Debug.debug("[ShopItem] " + is.getType() + " has no displayName");
            return null;
        }

        String displayName = is.getItemMeta().getDisplayName();
        for (ShopItem a : values()) {
            if (a.getName().equals(displayName)) return a;
        }


        Debug.debug("Couldnt find a item that matches " + is.getType());
        return null;
    }

    public ShopType[] getShopTypes() {
        return types;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getItemStack() {
        ItemStack is = this.is.clone();
        ItemMeta im = is.getItemMeta();
        if(!im.hasDisplayName()) {
            im.setDisplayName(this.name);
            is.setItemMeta(im);
        }
        return this.is;
    }

    public int getSize() {
        return this.size;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public boolean premiumNeeded() {
        return this.premium;
    }

    public int getBuyPrice() {
        return this.buyPrice;
    }

    public boolean addToInventory() {
        return this.addtoinv;
    }

    public void setPrice(Shops b, int price) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE shopprice SET price=" + price + " WHERE shopID=" + b.getID() + " AND itemID=" + getID());
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + e.getMessage());
        }
    }

    public int getPrice(Shops b) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT price FROM shopprice WHERE shopID=" + b.getID() + " AND itemID=" + getID())) {
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ShopItem getShopItem(String name) {
        for (ShopItem i : values()) {
            if (i.getName().equalsIgnoreCase(name)) return i;
        }
        return null;
    }

    public static ItemStack Shoe(Color color) {
        ItemStack shoe = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) shoe.getItemMeta();
        meta.setColor(color);
        shoe.setItemMeta(meta);
        return shoe;
    }


    public static ItemStack createItem(int headid, String name) {
        ItemStack item = Script.getHead(headid);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
