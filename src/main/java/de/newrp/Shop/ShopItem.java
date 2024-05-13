package de.newrp.Shop;

import de.newrp.API.Debug;
import de.newrp.API.ItemBuilder;
import de.newrp.API.Script;
import de.newrp.API.ShishaType;
import de.newrp.House.HouseAddon;
import de.newrp.Medic.Medikamente;
import de.newrp.Vehicle.CarType;
import de.newrp.Waffen.Weapon;
import de.newrp.NewRoleplayMain;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum ShopItem {

    BROT(0, "§fBrot", new ItemBuilder(Material.BREAD).setName("§fBrot").setAmount(8).build(), 2, 1, 20, 4, 2400, false, true, false, new ShopType[] {ShopType.SUPERMARKET}),
    KAFFEE(1, "§fKaffee", new ItemBuilder(Material.FLOWER_POT).setName("§rKaffee").build(), 3, 1, 20, 2, 1500, false, true, false, new ShopType[] {ShopType.CAFE}),
    LOTTOSCHEIN(2, "§7Lottoschein", new ItemBuilder(Material.PAPER).setName("§7Lottoschein").build(), 1, 1, 20, 25, 7500, false, true, false, new ShopType[] {ShopType.NEWS}),
    HAUSKASSE(3, "§7Hauskasse", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.HAUSKASSE.getPrice(), 6000, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    MIETERSLOT(4, "§7Mieterslot", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.SLOT.getPrice(), 10000, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    WAFFENSCHRANK(5, "§7Waffenschrank", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.WAFFENSCHRANK.getPrice(), 14000, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    ALARMANLAGE(7, "§7Alarmanlage", new ItemStack(Material.REDSTONE), 10, 1, 20, HouseAddon.ALARM.getPrice(), 12000, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    KUEHLSCHRANK(9, "§7Kühlschrank", new ItemStack(Material.CHEST), 10, 1, 20, HouseAddon.KUEHLSCHRANK.getPrice(), 1200, false, false, false, new ShopType[] {ShopType.HOUSEADDON}),
    PISTOLE(10, "§7Glory", new ItemStack(Material.IRON_HORSE_ARMOR), 15, 1, 1, 2000, 12000, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AMMO_9MM(11, "§79mm Munition (" + Weapon.PISTOLE.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.PISTOLE.getMagazineSize()).build(), 1, 1, 1, 15, 6000, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AK47(12, "§7Peacekeeper", new ItemStack(Material.DIAMOND_HORSE_ARMOR), 20, 1, 1, 5000, 22000, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    HEISSE_SCHOKOLADE(43, "§rHeiße Schokolade",new ItemBuilder(Material.FLOWER_POT).setName("§rHeiße Schokolade").build(), 3, 2, 25, 3, 1800, true, true, false, new ShopType[] {ShopType.CAFE}),
    FILTERKAFFEE(44, "§rFilterkaffee", new ItemBuilder(Material.FLOWER_POT).setName("§rFilterkaffee").build(), 3, 2, 25, 3, 1800, true, true, false, new ShopType[] {ShopType.CAFE}),
    LATTE_MACCHIATO(45, "§rLatte Macchiato", new ItemBuilder(Material.FLOWER_POT).setName("§rLatte Macchiato").build(), 3, 2, 25, 3, 1800, true, true, false, new ShopType[] {ShopType.CAFE}),
    ESPRESSO(46, "§rEspresso", new ItemBuilder(Material.FLOWER_POT).setName("§rEspresso").build(), 3, 2, 25, 4, 2100, true, true, false, new ShopType[] {ShopType.CAFE}),
    CRAPPUCHINO(47, "§rCrappuchino", new ItemBuilder(Material.FLOWER_POT).setName("§rCrappuchino").build(), 3, 2, 25, 3, 1800, true, true, false, new ShopType[] {ShopType.CAFE}),
    Zeitung(48, de.newrp.News.Zeitung.zeitung.getItemMeta().getDisplayName(), de.newrp.News.Zeitung.zeitung, 3, 2, 25, de.newrp.News.Zeitung.getBuyPrice(), 1800, true, false, false, new ShopType[] {ShopType.CAFE, ShopType.SUPERMARKET, ShopType.NEWS}),
    SCHMERZMITTEL(49, "§fSchmerzmittel", new ItemBuilder(Material.PAPER).setName("§fSchmerzmittel").setAmount(Medikamente.SCHMERZMITTEL.getNeeded()).build(), 2, 2, 25, 45, 4900, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    BASEBALLSCHLAEGER(50, "§7Baseballschläger", new ItemBuilder(Material.BONE).setName("§7Baseballschläger").build(), 5, 1, 1, 750, 4500, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SAMSUNG_HANDY(52, "§cSamstar", new ItemBuilder(Material.GOLD_INGOT).setName("§cSamstar").build(), 5, 1, 1, 100, 3500, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    HUAWEI_HANDY(53, "§cHawaii P55", new ItemBuilder(Material.IRON_INGOT).setName("§cHawaii P55").build(), 5, 1, 1, 50, 600, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    APPLE_HANDY(55, "§cyouPhone 15", new ItemBuilder(Material.NETHERITE_INGOT).setName("§cyouPhone 15").build(), 5, 1, 1, 300, 11000, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    MAP(56, "§7Karte", new ItemBuilder(Material.MAP).setName("§7Karte").build(), 3, 1, 1, 10, 1100, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.NEWS}),
    VERBAND(57, "§7Verband", new ItemBuilder(Material.PAPER).setName("§7Verband").build(), 3, 1, 1, 60, 4300, false, true, false, new ShopType[] {ShopType.PHARMACY}),
    TRINKWASSER(58, "§7Trinkwasser", new ItemBuilder(Material.POTION).setName("§7Trinkwasser").build(), 3, 1, 1, 2, 500, false, true, false, new ShopType[] {ShopType.SUPERMARKET, ShopType.CAFE, ShopType.NEWS}),
    AMMO_762MM(59, "§7.762mm Munition (" + Weapon.AK47.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.AK47.getMagazineSize()).build(), 1, 1, 1, 30, 3000, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    KEVLAR(51, "§7Schutzweste", Script.kevlar(1), 20, 1, 1, 500, 14900, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    SCHMERZMITTEL_HIGH(60, "§fSchmerzmittel (High)", new ItemBuilder(Material.PAPER).setName("§fSchmerzmittel (High)").setAmount(Medikamente.SCHMERZMITTEL_HIGH.getNeeded()).build(), 2, 2, 25, 65, 6100, true, true, false, new ShopType[] {ShopType.PHARMACY }),
    ANTIBIOTIKA(61, "§fAntibiotika", new ItemBuilder(Material.PAPER).setName("§fAntibiotika").setAmount(Medikamente.ANTIBIOTIKA.getNeeded()).build(), 5, 2, 25, 75, 4900, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    HUSTENSAFT(62, "§fHustensaft", new ItemBuilder(Material.PAPER).setName("§fHustensaft").setAmount(Medikamente.HUSTENSAFT.getNeeded()).build(), 5, 2, 25, 60, 3400, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    ENTZUENDUNGSHEMMENDE_SALBE(63, "§fEntzündungshemmende Salbe", new ItemBuilder(Material.PAPER).setName("§fEntzündungshemmende Salbe").setAmount(Medikamente.ENTZUENDUNGSHEMMENDE_SALBE.getNeeded()).build(), 5, 2, 25, 30, 2600, true, true, false, new ShopType[] {ShopType.PHARMACY}),
    CD_1(65, "§6Gold", new ItemStack(Material.MUSIC_DISC_11), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_2(66, "§6Green", new ItemStack(Material.MUSIC_DISC_13), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_3(67, "§6Blocks", new ItemStack(Material.MUSIC_DISC_BLOCKS), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_4(68, "§6Chirp", new ItemStack(Material.MUSIC_DISC_CHIRP), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_5(69, "§6Far", new ItemStack(Material.MUSIC_DISC_FAR), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_6(70, "§6Mall", new ItemStack(Material.MUSIC_DISC_MALL), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_7(71, "§6Mellohi", new ItemStack(Material.MUSIC_DISC_MELLOHI), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_8(72, "§6Stal", new ItemStack(Material.MUSIC_DISC_STAL), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_9(73, "§6Strad", new ItemStack(Material.MUSIC_DISC_STRAD),   1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_10(74, "§6Ward", new ItemStack(Material.MUSIC_DISC_WARD), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    CD_11(75, "§6Wait", new ItemStack(Material.MUSIC_DISC_WAIT), 1, 1, 1, 1, 1, false, true, false, new ShopType[] {ShopType.MUSIC}),
    ROSE(76, "§7Rose", new ItemBuilder(Material.WHITE_TULIP).setName("§7Rose").build(), 1, 1, 1, 10, 600, false, true, false, new ShopType[] {ShopType.FLOWER}),
    TULPE(77, "§7Tulpe", new ItemBuilder(Material.RED_TULIP).setName("§7Tulpe").build(), 1, 1, 1, 5, 300, false, true, false, new ShopType[] {ShopType.FLOWER}),
    MARGARITE(78, "§7Margarite", new ItemBuilder(Material.OXEYE_DAISY).setName("§7Margarite").build(), 1, 1, 1, 3, 150, false, true, false, new ShopType[] {ShopType.FLOWER}),
    GERBERA(79, "§7Gerbera", new ItemBuilder(Material.ORANGE_TULIP).setName("§7Gerbera").build(), 1, 1, 1, 3, 150, false, true, false, new ShopType[] {ShopType.FLOWER}),
    LILIEN(80, "§7Lilien", new ItemBuilder(Material.WHITE_TULIP).setName("§7Lilien").build(), 1, 1, 1, 3, 150, false, true, false, new ShopType[] {ShopType.FLOWER}),
    MARGARITE_PINK(81, "§7Pink Margarite", new ItemBuilder(Material.PINK_TULIP).setName("§7Pink Margarite").build(), 1, 1, 1, 4, 200, false, true, false, new ShopType[] {ShopType.FLOWER}),
    WATER_BUCKET(82, "§7Wasser", Script.setNameAndLore(new ItemStack(Material.WATER_BUCKET), "§9Wasser", "§65/5"), 5, 1, 1, 40, 2000, false, false, false, new ShopType[] {ShopType.FLOWER}),
    DUENGER(83, "§7Dünger", new ItemBuilder(Material.INK_SAC).setName("§7Dünger").build(), 1, 1, 1, 50, 2600, false, false, false, new ShopType[] {ShopType.FLOWER}),
    EINZELZIMMER(84, "§7Einzelzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    DOPPELZIMMER(85, "§7Doppelzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    MEHRBETTZIMMER(86, "§7Mehrbettzimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    SUITE(87, "§7Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    EXECUTIVE_SUITE(88, "§7Executive Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    DELUXE_ZIMMER(89, "§7Deluxe Zimmer", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    PRAESIDENTEN_SUITE(90, "§7Präsidenten Suite", new ItemStack(Material.OAK_DOOR), 1, 1, 1, 1, 1, false, false, false, new ShopType[] {ShopType.HOTEL}),
    EINZELFAHRASUSWEIS(91, "§6UBahn-Ticket [Einzelfahrausweis]", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [Einzelfahrausweis]").setLore("Verbleibende Fahrten: 1").build(), 1, 1, 1, 3, 1300, false, false, false, new ShopType[] {ShopType.NEWS}),
    WOCHENFAHRASUSWEIS(92, "§6UBahn-Ticket [7 Fahrten]", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [7 Fahrten]").setLore("Verbleibende Fahrten: 7").build(), 1, 1, 1, 10, 2400, false, false, false, new ShopType[] {ShopType.NEWS}),
    MONATSFAHRASUSWEIS(93, "§6UBahn-Ticket [30 Fahrten]", new ItemBuilder(Material.PAPER).setName("§6UBahn-Ticket [30 Fahrten]").setLore("Verbleibende Fahrten: 30").build(), 1, 1, 1, 20, 5100, false, false, false, new ShopType[] {ShopType.NEWS}),
    FALLSCHIRM(94, "§7Fallschirm", new ItemBuilder(Material.ELYTRA).setName("§7Fallschirm").build(), 5, 1, 1, 600, 4100, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    KABELBINDER(95, "§7Kabelbinder", new ItemBuilder(Material.STRING).setName("§7Kabelbinder").build(), 3, 1, 1, 100, 1900, false, true, false, new ShopType[] {ShopType.GUNSHOP, ShopType.SUPERMARKET}),
    JAGDFLINTE(96, "§7Guardian", new ItemStack(Material.DIAMOND_HOE), 5, 1, 1, 5500, 10000, false, false, false, new ShopType[] {ShopType.JAGDHUETTE}),
    SCHROT(97, "§7Schrot (" + Weapon.JAGDFLINTE.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.JAGDFLINTE.getMagazineSize()).build(), 2, 1, 1, 50, 4500, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    DEAGLE(98, "§7Ivory", new ItemStack(Material.GOLDEN_HOE), 15, 1, 1, 4000, 16600, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    AMMO_50AE(99, "§7.50AE Munition (" + Weapon.DESERT_EAGLE.getName() + ")", new ItemBuilder(Material.ARROW).setAmount(Weapon.DESERT_EAGLE.getMagazineSize()).build(), 2, 1, 1, 25, 4500, false, false, false, new ShopType[] {ShopType.GUNSHOP}),
    ANGELRUTE(100, "§7Angelrute", new ItemBuilder(Material.FISHING_ROD).setName("§7Angelrute").build(), 10, 1, 1, 25, 1590, false, true, false, new ShopType[] {ShopType.ANGELSHOP}),
    STEAK(101, "§fSteak", new ItemBuilder(Material.COOKED_BEEF).setName("§fSteak").setAmount(8).build(), 2, 1, 20, 2, 2000, false, true, false, new ShopType[] {ShopType.FASTFOOD}),
    POMMES(102, "§fPommes", new ItemBuilder(Material.BAKED_POTATO).setName("§fPommes").setAmount(16).build(), 2, 1, 20, 3, 2400, false, true, false, new ShopType[] {ShopType.FASTFOOD}),
    CHICKEN(103, "§fHähnchen", new ItemBuilder(Material.COOKED_BEEF).setName("§fHähnchen").setAmount(8).build(), 2, 1, 20, 1, 2400, false, true, false, new ShopType[] {ShopType.FASTFOOD}),
    SHOE_1(104, "§7Schuh Schwarz", Shoe(Color.BLACK), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_2(105, "§7Schuh Blau", Shoe(Color.BLUE), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_3(106, "§7Schuh Grün", Shoe(Color.GREEN), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_4(107, "§7Schuh Rot", Shoe(Color.RED), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_5(108, "§7Schuh Lila", Shoe(Color.PURPLE), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_6(109, "§7Schuh Gelb", Shoe(Color.YELLOW), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_7(110, "§7Schuh Weiß", Shoe(Color.WHITE), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_8(111, "§7Schuh Orange", Shoe(Color.ORANGE), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    SHOE_9(112, "§7Schuh Fuchisa", Shoe(Color.FUCHSIA), 1, 1, 1, 50, 500, false, true, false, new ShopType[] {ShopType.SHOE_STORE}),
    CARROTT(113, "§fKarotte", new ItemBuilder(Material.CARROT).setName("§fKarotte").setAmount(16).build(), 2, 1, 20, 1, 2000, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    POTATO(114, "§fBeeren", new ItemBuilder(Material.SWEET_BERRIES).setName("§fBeeren").setAmount(16).build(), 2, 1, 20, 1, 2000, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    APPLE(115, "§fApfel", new ItemBuilder(Material.APPLE).setName("§fApfel").setAmount(8).build(), 2, 1, 20, 3, 2000, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    MELON(116, "§fMelone", new ItemBuilder(Material.MELON_SLICE).setName("§fMelone").setAmount(16).build(), 2, 1, 20, 2, 2000, false, true, false, new ShopType[] {ShopType.GEMUESE}),
    HANDY_REPAIR(117, "§7Handy Reparatur", new ItemBuilder(Material.ANVIL).setName("§7Handy Reparatur").build(), 1, 1, 1, 25, 5000, false, false, false, new ShopType[] {ShopType.ELECTRONIC}),
    POWERBANK(118, "§3Powerbank", Script.setNameAndLore(Material.NAME_TAG, "§3Powerbank", "§c5§7/§c5"), 1, 1, 1, 10, 5000, false, true, false, new ShopType[] {ShopType.ELECTRONIC}),
    MEHL(119, "§fMehl", new ItemBuilder(Material.WHITE_DYE).setName("§fMehl").build(), 2, 1, 20, 20, 2000, false, true, false, new ShopType[] {ShopType.SUPERMARKET}),
    MASK(120, "§7Maske", new ItemBuilder(Material.CARVED_PUMPKIN).setName("§7Maske").build(), 1, 1, 1, 10, 500, false, true, false, new ShopType[] {ShopType.SUPERMARKET}),
    SHISHA_1(121, "§6Shisha " + ShishaType.DOPPELAPFEL, Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.DOPPELAPFEL.getName(), "§c" + ShishaType.DOPPELAPFEL.getName(), "§7" + ShishaType.DOPPELAPFEL.getDuration() + "/" + ShishaType.DOPPELAPFEL.getDuration()), 1, 1, 1, 10, 500, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_2(122, "§6Shisha " + ShishaType.PFIRSICH_MINZE, Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.PFIRSICH_MINZE.getName(), "§c" + ShishaType.PFIRSICH_MINZE.getName(), "§7" + ShishaType.PFIRSICH_MINZE.getDuration() + "/" + ShishaType.PFIRSICH_MINZE.getDuration()), 1, 1, 1, 10, 500, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_3(123, "§6Shisha " + ShishaType.WASSERMELONE, Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.WASSERMELONE.getName(), "§c" + ShishaType.WASSERMELONE.getName(), "§7" + ShishaType.WASSERMELONE.getDuration() + "/" + ShishaType.WASSERMELONE.getDuration()), 1, 1, 1, 10, 500, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_4(124, "§6Shisha " + ShishaType.ZITRONE, Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.ZITRONE.getName(), "§c" + ShishaType.ZITRONE.getName(), "§7" + ShishaType.ZITRONE.getDuration() + "/" + ShishaType.ZITRONE.getDuration()), 1, 1, 1, 10, 500, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_5(125, "§6Shisha " + ShishaType.SPECIAL, Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.SPECIAL.getName(), "§c" + ShishaType.SPECIAL.getName(), "§7" + ShishaType.SPECIAL.getDuration() + "/" + ShishaType.SPECIAL.getDuration()), 1, 1, 1, 10, 500, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SHISHA_6(126, "§6Shisha " + ShishaType.TRAUBE, Script.setNameAndLore(Material.STICK, "§6Shisha " + ShishaType.TRAUBE.getName(), "§c" + ShishaType.TRAUBE.getName(), "§7" + ShishaType.TRAUBE.getDuration() + "/" + ShishaType.TRAUBE.getDuration()), 1, 1, 1, 10, 500, false, true, false, new ShopType[] {ShopType.SHISHA}),
    SCHWERE_KEVLAR(127, "§7Schwere Schutzweste", Script.kevlar(2), 20, 1, 1, 1000, 14900, false, true, false, new ShopType[] {ShopType.GUNSHOP}),
    OPPEL(128,CarType.OPPEL.getName(), Script.setName(new ItemStack(CarType.OPPEL.getMaterial(), 1), "§e" + CarType.OPPEL.getName()), 1, 1, 1, 7000, 10000, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    VOLTSWAGEN(129, CarType.VOLTSWAGEN.getName(), Script.setName(new ItemStack(CarType.VOLTSWAGEN.getMaterial(), 1), "§e" + CarType.VOLTSWAGEN.getName()), 1, 1, 1, 12000, 8000, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    NMW(130, CarType.NMW.getName(), Script.setName(new ItemStack(CarType.NMW.getMaterial(), 1), "§e" + CarType.NMW.getName()), 1, 1, 1, 17000, 14000, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    AWDI(131, CarType.AWDI.getName(), Script.setName(new ItemStack(CarType.AWDI.getMaterial(), 1), "§e" + CarType.AWDI.getName()), 1, 1, 1, 25000, 20000, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    MERCADAS(132, CarType.MERCADAS.getName(), Script.setName(new ItemStack(CarType.MERCADAS.getMaterial(), 1), "§e" + CarType.MERCADAS.getName()), 1, 1, 1, 40000, 40000, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    PAWSCHE(133, CarType.PAWSCHE.getName(), Script.setName(new ItemStack(CarType.PAWSCHE.getMaterial(), 1), "§e" + CarType.PAWSCHE.getName()), 1, 1, 1, 60000, 70000, false, false, false, new ShopType[] {ShopType.CARDEALER}),
    KANISTER(134, "Kanister", Script.setName(new ItemStack(Material.RED_SHULKER_BOX, 1), "§cKanister"), 1, 1, 1, 50, 1200, false, true, false, new ShopType[] {ShopType.GAS_STATION}),
    INSURANCE(135, "Versicherung", Script.setName(new ItemStack(Material.PAPER, 1), "§aVersicherung"), 1, 1, 1, 1000, 5400, false, true, false, new ShopType[] {ShopType.CARDEALER}),
    LICENSE(136, "Kennzeichen", Script.setName(new ItemStack(Material.NAME_TAG, 1), "§bKennzeichen"), 1, 1, 1, 1500, 3600, false, true, false, new ShopType[] {ShopType.CARDEALER}),
    TOOLS(137, "Werkzeug", Script.setName(new ItemStack(Material.SHEARS, 1), "§7Werkzeug"), 1, 1, 1, 100, 500, false, true, false, new ShopType[] {ShopType.GAS_STATION});


    private final int id;
    private final String name;
    private final ItemStack is;
    private final int size;
    private final int min;
    private final int max;
    private final int buyPrice;
    private final int licensePrice;
    private final boolean reopen;
    private final boolean addtoinv;
    private final boolean premium;
    private ShopType[] types;

    ShopItem(int id, String name, ItemStack is, int size, int min, int max, int buyPrice, int licensePrice, boolean reopen, boolean addtoinv, boolean premium, ShopType[] types) {
        this.id = id;
        this.name = name;
        this.is = is;
        this.size = size;
        this.min = min;
        this.max = max;
        this.buyPrice = buyPrice;
        this.licensePrice = licensePrice;
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
            ItemStack businessItem = a.getItemStack();
            if (businessItem == null) continue;

            ItemMeta itemMeta = businessItem.getItemMeta();
            if (itemMeta == null) continue;
            if (!itemMeta.hasDisplayName()) continue;

            if (itemMeta.getDisplayName().equals(displayName)) return a;
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

}
