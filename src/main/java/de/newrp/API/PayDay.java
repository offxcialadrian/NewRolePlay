package de.newrp.API;

import de.newrp.Administrator.Checkpoints;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.GFB.GFB;
import de.newrp.Government.Arbeitslosengeld;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.*;
import de.newrp.Shop.Shops;
import de.newrp.Shop.gym.GymBuyHandler;
import de.newrp.NewRoleplayMain;
import de.newrp.Vehicle.Car;
import org.apache.commons.collections4.bag.HashBag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class PayDay extends BukkitRunnable {

    public static HashMap<Player, Integer> paydayTime = new HashMap<>();
    public static HashMap<Player, Integer> paydayMoney = new HashMap<>();

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (AFK.isAFK(p)) continue;
            if (Checkpoints.hasCheckpoints(p)) continue;
            if (Passwort.isLocked(p)) continue;
            if (getPayDayTime(p) < 59) {
                addPayDayTime(p);
                continue;
            }

            if (!Banken.hasBank(p)) {
                p.sendMessage(Messages.INFO + "Du hast kein Geld am PayDay erhalten, da du kein Konto hast.");
                setPayDayTime(p, 0);
                continue;
            }

            int payday = 0;
            int extra = 0;
            int interest = (Script.getMoney(p, PaymentType.BANK) > 0 ? (int) (Banken.getBankByPlayer(p).getInterest() * Script.getMoney(p, PaymentType.BANK)) : (int) (0.02 * Script.getMoney(p, PaymentType.BANK)));
            interest = (int) Math.round(20 * Math.sqrt(interest));
            if (Banken.getBankByPlayer(p) != null) {
                if (interest > Banken.getBankByPlayer(p).getLimit()) interest = Banken.getBankByPlayer(p).getLimit();
            }
            /*if (Script.getMoney(p, PaymentType.BANK) > 50000) interest = interest / 2;
            if (Script.getMoney(p, PaymentType.BANK) > 100000) interest = interest / 3;*/
            double einkommenssteuer = Steuern.Steuer.EINKOMMENSSTEUER.getPercentage();
            double arbeitslosenversicherung = Steuern.Steuer.ARBEITSLOSENVERSICHERUNG.getPercentage();
            double lohnsteuer = Steuern.Steuer.LOHNSTEUER.getPercentage();
            double gfb_lohnsteuer = Steuern.Steuer.GFB_LOHNSTEUER.getPercentage();
            double krankenversicherung = Steuern.Steuer.KRANKENVERSICHERUNG.getPercentage();
            if (BeziehungCommand.isMarried(p)) lohnsteuer = lohnsteuer - 5.0;
            if (BeziehungCommand.isMarried(p)) gfb_lohnsteuer = gfb_lohnsteuer - 5.0;
            p.sendMessage("§9=== §l§ePayDay §9===");
            p.sendMessage("§8" + Messages.ARROW + " §7Kontostand: " + (Script.getMoney(p, PaymentType.BANK) >= 0 ? "§a" : "§c") + Script.getMoney(p, PaymentType.BANK) + "€");
            p.sendMessage("§8" + Messages.ARROW + " §7Kontoführungsgebühr: §c-" + Banken.getBankByPlayer(p).getKontoKosten() + "€");
            payday -= Banken.getBankByPlayer(p).getKontoKosten();
            p.sendMessage("§8" + Messages.ARROW + " §7Zinsen: " + (interest >= 0 ? "§a+" : "§c") + interest + "€");
            payday += interest;
            int other_salary = getPayDayPay(p);
            if (other_salary > 0) {
                p.sendMessage("§8" + Messages.ARROW + " §7Lohn/Gehalt (GFB) §a+" + other_salary + "€");
                payday += other_salary;
                p.sendMessage("§8" + Messages.ARROW + " §7Lohnsteuer (GFB) (" + gfb_lohnsteuer + "%): §c-" + (int) Script.getPercent(gfb_lohnsteuer, other_salary) + "€");
                payday -= (int) Script.getPercent(gfb_lohnsteuer, other_salary);
                Stadtkasse.addStadtkasse((int) Script.getPercent(gfb_lohnsteuer, other_salary), "Lohnsteuer (GFB) von " + Script.getName(p) + " erhalten", Steuern.Steuer.GFB_LOHNSTEUER);

                p.sendMessage("§8" + Messages.ARROW + " §7Arbeitslosenversicherung (GFB) (" + arbeitslosenversicherung + "%): §c-" + (int) Script.getPercent(arbeitslosenversicherung, other_salary) + "€");
                Stadtkasse.addStadtkasse((int) Script.getPercent(arbeitslosenversicherung, other_salary), "Arbeitslosenversicherung (GFB) von " + Script.getName(p) + " erhalten", Steuern.Steuer.ARBEITSLOSENVERSICHERUNG);
                payday -= (int) Script.getPercent(arbeitslosenversicherung, other_salary);

                p.sendMessage("§8" + Messages.ARROW + " §7Krankenversicherung (GFB) (" + krankenversicherung + "%): §c-" + (int) Script.getPercent(krankenversicherung, other_salary) + "€");
                Stadtkasse.addStadtkasse((int) Script.getPercent(krankenversicherung, other_salary), "Krankenversicherung (GFB) von " + Script.getName(p) + " erhalten", Steuern.Steuer.KRANKENVERSICHERUNG);
                payday -= (int) Script.getPercent(krankenversicherung, other_salary);
            }

            if (Beruf.hasBeruf(p)) {

                int salary = Beruf.getSalary(p);
                p.sendMessage("§8" + Messages.ARROW + " §7Lohn/Gehalt: §a+" + salary + "€");
                payday += salary;
                if (!Beruf.getBeruf(p).hasKasse()) {
                    if (Stadtkasse.getStadtkasse() < salary) {
                        Beruf.Berufe.NEWS.sendMessage("§8[§eStadtkasse§8] §eDie Stadtkasse ist Insolvent!");
                        for (Beruf.Berufe beruf : Beruf.Berufe.values()) {
                            if (!beruf.hasKasse()) {
                                for (OfflinePlayer members : beruf.getAllMembers()) {
                                    Script.setInt(members, "berufe", "salary", 0);
                                    if (!members.isOnline()) {
                                        Script.addOfflineMessage(members, "§8[§eBerufskasse§8] §eDie Stadtkasse ist Insolvent. Alle Gehälter wurden auf 0€ gesetzt");
                                    } else {
                                        members.getPlayer().sendMessage("§8[§eBerufskasse§8] §eDie Stadtkasse ist Insolvent. Alle Gehälter wurden auf 0€ gesetzt");
                                    }
                                }
                            }
                        }
                    } else {
                        Stadtkasse.removeStadtkasse(salary, "Gehaltszahlung an " + Script.getName(p) + " von " + Beruf.getBeruf(p).getName());
                    }
                }
                if (Beruf.getBeruf(p).hasKasse()) {
                    if (Beruf.getBeruf(p).getKasse() >= salary) {
                        Beruf.getBeruf(p).removeKasse(salary);
                    } else {
                        Beruf.getBeruf(p).sendMessage("§8[§eBerufskasse§8] §eDie " + Beruf.getBeruf(p).getName() + " ist Insolvent. Alle Gehälter werden auf 0€ gesetzt");
                        Beruf.Berufe.GOVERNMENT.sendMessage("§8[§eBerufskasse§8] §eDie " + Beruf.getBeruf(p).getName() + " ist Insolvent. Alle Gehälter werden auf 0€ gesetzt");
                        for (OfflinePlayer members : Beruf.getBeruf(p).getAllMembers()) {
                            Script.setInt(members, "berufe", "salary", 0);
                            if (!members.isOnline()) {
                                Script.addOfflineMessage(members, "§8[§eBerufskasse§8] §eDie " + Beruf.getBeruf(p).getName() + " ist Insolvent. Alle Gehälter wurden auf 0€ gesetzt");
                            }
                        }
                    }
                }

                if (Beruf.getBeruf(p).hasKasse() || Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST) {
                    p.sendMessage("§8" + Messages.ARROW + " §7Lohnsteuer (" + lohnsteuer + "%): §c-" + (int) Script.getPercent(lohnsteuer, salary) + "€");
                    Stadtkasse.addStadtkasse((int) Script.getPercent(lohnsteuer, salary), "Lohnsteuer von " + Script.getName(p) + " erhalten", Steuern.Steuer.LOHNSTEUER);
                    payday -= (int) Script.getPercent(lohnsteuer, salary);

                    p.sendMessage("§8" + Messages.ARROW + " §7Arbeitslosenversicherung (" + arbeitslosenversicherung + "%): §c-" + (int) Script.getPercent(arbeitslosenversicherung, salary) + "€");
                    Stadtkasse.addStadtkasse((int) Script.getPercent(arbeitslosenversicherung, salary), "Arbeitslosenversicherung von " + Script.getName(p) + " erhalten", Steuern.Steuer.ARBEITSLOSENVERSICHERUNG);
                    payday -= (int) Script.getPercent(arbeitslosenversicherung, salary);

                    p.sendMessage("§8" + Messages.ARROW + " §7Krankenversicherung (" + krankenversicherung + "%): §c-" + (int) Script.getPercent(krankenversicherung, salary) + "€");
                    Stadtkasse.addStadtkasse((int) Script.getPercent(krankenversicherung, salary), "Krankenversicherung von " + Script.getName(p) + " erhalten", Steuern.Steuer.KRANKENVERSICHERUNG);
                    payday -= (int) Script.getPercent(krankenversicherung, salary);
                } else {
                    p.sendMessage("§8" + Messages.ARROW + " §7Lohnsteuer (" + lohnsteuer + "%): " + "§cVerbeamtet");
                    p.sendMessage("§8" + Messages.ARROW + " §7Arbeitslosenversicherung (" + arbeitslosenversicherung + "%): " + "§cVerbeamtet");
                    p.sendMessage("§8" + Messages.ARROW + " §7Krankenversicherung (" + krankenversicherung + "%): " + "§cVerbeamtet");
                }


            } else if (Organisation.hasOrganisation(p)) {
                Organisation org = Organisation.getOrganisation(p);
                int salary = Organisation.getSalary(p);
                if (org.hasKasse()) {
                    if (org.getKasse() >= salary) {
                        org.removeKasse(salary);
                        p.sendMessage("§8" + Messages.ARROW + " §7Lohn/Gehalt: §a+" + salary + "€");
                        payday += salary;
                    } else {
                        org.sendMessage("§8[§eOrganisationskasse§8] §eDie " + org.getName() + " ist Insolvent. Alle Gehälter werden auf 0€ gesetzt");
                        for (OfflinePlayer members : org.getAllMembers()) {
                            Script.setInt(members, "organisation", "salary", 0);
                            if (!members.isOnline()) {
                                Script.addOfflineMessage(members, "§8[§eOrganisationskasse§8] §eDie " + org.getName() + " ist Insolvent. Alle Gehälter wurden auf 0€ gesetzt");
                            }
                        }
                    }
                }
            }

            if (Arbeitslosengeld.hasArbeitslosengeld(p)) {
                p.sendMessage("§8" + Messages.ARROW + " §7Arbeitslosengeld: §a+" + Stadtkasse.getArbeitslosengeld() + "€");
                Stadtkasse.removeStadtkasse(Stadtkasse.getArbeitslosengeld(), "Arbeitslosengeldzahlung an " + Script.getName(p));
                payday += Stadtkasse.getArbeitslosengeld();
            }

            if (Team.getTeam(p) != null) {
                Team.Teams team = Team.getTeam(p);
                if (team != null) {
                    int s = team.getSalary();
                    p.sendMessage("§8" + Messages.ARROW + " §7Team-Gehalt: §a+" + s + "€");
                    extra += s;
                }
            }

            int lvl = Script.getLevel(p);
            if (lvl > 0) {
                double r = 1.99 * new Random().nextFloat() - 0.995;
                int b = (int) Math.round(((200 * Math.log(0.1 * (lvl + 10))) * ((0.25 * ((Math.log(1 + r) - Math.log(1 - r))) / 2) + 1)));
                p.sendMessage("§8" + Messages.ARROW + " §7Level-Bonus: §a+" + b + "€");
                extra += b;
            }

            int shops = 0;
            for (Shops shop : Shops.values()) {
                if (shop.getOwner() == Script.getNRPID(p)) {
                    shops++;
                }
            }

            if (!Car.getCars(p).isEmpty()) {
                int kfz = 0;
                for (Car car : Car.getCars(p)) {
                    if (!car.getLicenseplate().startsWith("N-RP-")) kfz += car.getCarType().getTax();
                }
                p.sendMessage("§8" + Messages.ARROW + " §7KFZ-Steuer: §c-" + kfz + "€");
                Stadtkasse.addStadtkasse(kfz, "KFZ-Steuer von " + Script.getName(p) + " erhalten", Steuern.Steuer.KFZSTEUER);
                payday -= kfz;
            }

            if(!Beruf.hasBeruf(p) || Beruf.getBeruf(p) != Beruf.Berufe.NEWS) {
                int rundfunk = (int) (Steuern.Steuer.RUNDFUNKBEITRAG.getPercentage());
                p.sendMessage("§8" + Messages.ARROW + " §7Rundfunkbeitrag: §c-" + rundfunk + "€");
                int stadtanteil = (int) Script.getPercent(20, rundfunk);
                int newsanteil = (int) Script.getPercent(80, rundfunk);
                Stadtkasse.addStadtkasse(stadtanteil, "Rundfunkbeitrag von " + Script.getName(p) + " erhalten", Steuern.Steuer.RUNDFUNKBEITRAG);
                Beruf.Berufe.NEWS.addKasse(newsanteil);
                payday -= rundfunk;
            }


            if (shops > 0) {
                int tax = (int) (Steuern.Steuer.GEWERBESTEUER.getPercentage()) * shops;
                p.sendMessage("§8" + Messages.ARROW + " §7Gewerbesteuer: §c-" + tax + "€");
                Stadtkasse.addStadtkasse(tax, "Gewerbesteuer von " + Script.getName(p) + " erhalten", Steuern.Steuer.GEWERBESTEUER);
                payday -= tax;
            }

            for (House house : House.getHouses(Script.getNRPID(p))) {
                if (house.getOwner() == Script.getNRPID(p)) continue;
                House.Mieter mieter = house.getMieterByID(Script.getNRPID(p));
                if(Script.getMoney(p, PaymentType.BANK) >= house.getMiete(Script.getNRPID(p)) + mieter.getNebenkosten()) {
                    p.sendMessage("§8" + Messages.ARROW + " §7Miete für Haus " + house.getID() + ": §c-" + house.getMiete(Script.getNRPID(p)) + "€");
                    payday -= house.getMiete(Script.getNRPID(p));
                    house.addKasse(house.getMiete(Script.getNRPID(p)));
                    int n = mieter.getNebenkosten();
                    if (n > 0) {
                        p.sendMessage("§8" + Messages.ARROW + " §7Nebenkosten für Haus " + house.getID() + ": §c-" + n + "€");
                        payday -= n;
                    }
                } else {
                    house.removeMieter(Script.getNRPID(p));
                }
            }

            for (House house : House.getHouses(Script.getNRPID(p))) {
                if (house.getOwner() != Script.getNRPID(p)) continue;
                int grundsteuer = (int) Steuern.Steuer.GRUNDSTEUER.getPercentage();
                grundsteuer = grundsteuer * ((House.getHouses(Script.getNRPID(p)).size() + 1) / 2);
                if (house.hasAddon(HouseAddon.SICHERHEITSTUER)) grundsteuer += 10;
                p.sendMessage("§8" + Messages.ARROW + " §7Grundsteuer für Haus " + house.getID() + ": §c-" + grundsteuer + "€");
                payday -= grundsteuer;
                Stadtkasse.addStadtkasse(grundsteuer, "Grundsteuer von " + Script.getName(p) + " erhalten", Steuern.Steuer.GRUNDSTEUER);
            }

            if (Selfstorage.hasSelfstorage(p)) {
                int price = 10;
                p.sendMessage("§8" + Messages.ARROW + " §7Selfstorage-Room: §c-" + price + "€");
                payday -= price;
            }

            if (GymBuyHandler.isGymMember(p)) {
                int price = 20;
                p.sendMessage("§8" + Messages.ARROW + " §7Fitnessstudio: §c-" + price + "€");
                payday -= price;
                int mehrwertsteur = (int) Steuern.Steuer.MEHRWERTSTEUER.getPercentage();
                Stadtkasse.addStadtkasse((int) Script.getPercent(mehrwertsteur, price), "Mehrwertsteuer von " + Script.getName(p) + " erhalten", Steuern.Steuer.MEHRWERTSTEUER);
                GymBuyHandler.getGym(p).addKasse((int) (price - Script.getPercent(mehrwertsteur, price)));
            }

            /*if(Hotel.hasHotelRoom(p)) {
                int price = Hotel.getHotelRoom(p).getPrice();
                p.sendMessage("§8" + Messages.ARROW + " §7Hotelzimmer: §c-" + price + "€");
                payday -= price;
                Hotel.Hotels hotel = Hotel.getHotelRoom(p).getHotel();
                int mehrwertsteur = (int) Steuern.Steuer.MEHRWERTSTEUER.getPercentage();
                Stadtkasse.addStadtkasse((int) Script.getPercent(mehrwertsteur, price), "Mehrwertsteuer von " + Script.getName(p) + " erhalten", Steuern.Steuer.MEHRWERTSTEUER);
                Shops shop = hotel.getShop();
                shop.addKasse((int) (price-Script.getPercent(mehrwertsteur, price)));
            }*/

            if (Mobile.hasCloud(p)) {
                int price = (Premium.hasPremium(p) ? 5 : 10);
                p.sendMessage("§8" + Messages.ARROW + " §7Handy-Cloud: §c-" + price + "€");
                payday -= price;
            }

            if (payday > 0) {
                p.sendMessage("§8" + Messages.ARROW + " §7Einkommenssteuer (" + einkommenssteuer + "%): §c-" + (int) Script.getPercent(einkommenssteuer, payday) + "€");
                Stadtkasse.addStadtkasse((int) Script.getPercent(einkommenssteuer, payday), "Einkommenssteuer von " + Script.getName(p) + " erhalten", Steuern.Steuer.EINKOMMENSSTEUER);
                payday -= (int) Script.getPercent(einkommenssteuer, payday);
            } else {
                p.sendMessage("§8" + Messages.ARROW + " §7Einkommenssteuer (" + einkommenssteuer + "%): §c-" + 0 + "€");
            }

            if (extra > 0) payday += extra;

            p.sendMessage("§8" + Messages.ARROW + " §7Bilanz: " + (payday >= 0 ? "§a+" : "§c") + payday + "€");
            p.sendMessage("§8" + Messages.ARROW + " §7Neuer Kontostand: " + (Script.getMoney(p, PaymentType.BANK) + payday >= 0 ? "§a" : "§c") + (Script.getMoney(p, PaymentType.BANK) + payday) + "€");
            p.sendMessage("§9================");
            Script.addEXP(p, Script.getRandom(lvl, lvl * 2), true);
            if (payday >= 0) Script.addMoney(p, PaymentType.BANK, payday);
            else Script.removeMoney(p, PaymentType.BANK, payday);

            if (RecruitedCommand.isRecruited(Script.getNRPID(p))) {
                int r = RecruitedCommand.getRecruiter(Script.getNRPID(p));
                if (Script.getPlayer(r) != null) {
                    if (Objects.requireNonNull(Script.getPlayer(r)).isOnline()) {
                        int x = Script.getRandom(10, 30);
                        Script.addEXP(r, x);
                        Script.sendActionBar(Objects.requireNonNull(Script.getPlayer(r)), RecruitedCommand.PREFIX + "§a+" + x + " Exp §7für " + p.getName());
                    }
                }
            }

            setPayDayTime(p, 0);
            setPayDayPay(p, 0);
            Script.executeAsyncUpdate("UPDATE payday SET money = 0 WHERE nrp_id = '" + Script.getNRPID(p) + "'");
            if (Script.getMoney(p, PaymentType.BANK) < 0) {
                p.sendMessage("§8[§cBank§8] §c" + Messages.ARROW + " §7Dein Konto ist überzogen. Du musst den Betrag innerhalb von 7 Tagen ausgleichen.");
                if (Hotel.hasHotelRoom(p)) {
                    p.sendMessage("§8[§cBank§8] §c" + Messages.ARROW + " §7Dein Hotelzimmer wurde automatisch gekündigt.");
                    Script.executeAsyncUpdate("DELETE FROM hotel WHERE nrp_id = " + Script.getNRPID(p));
                }
                if (Mobile.hasCloud(p)) {
                    p.sendMessage("§8[§cBank§8] §c" + Messages.ARROW + " §7Deine Handy-Cloud wurde automatisch gekündigt.");
                    Mobile.getPhone(p).setCloud(p, false);
                }
                if (Selfstorage.hasSelfstorage(p)) {
                    p.sendMessage("§8[§cBank§8] §c" + Messages.ARROW + " §7Dein Selfstorage-Room wurde automatisch gekündigt.");
                    Selfstorage.removeSelfstorage(p, false);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    SDuty.updateScoreboard();
                }
            }.runTaskLater(NewRoleplayMain.getInstance(), 20L);
        }
    }

    public static int getPayDayTime(Player p) {
        return paydayTime.get(p);
    }

    public static void setPayDayTime(Player p, int time) {
        paydayTime.put(p, time);
    }

    public static int getPayDayPay(Player p) {
        return paydayMoney.get(p);
    }

    public static void setPayDayPay(Player p, int money) {
        paydayMoney.put(p, money);
    }

    public static void addPayDayTime(Player p) {
        setPayDayTime(p, getPayDayTime(p) + 1);
    }

    public static void addPayDay(Player p, int money) {
        paydayMoney.put(p, paydayMoney.get(p) + money);
        Script.setInt(p, "payday", "money", getPayDayPay(p) + money);
        p.sendMessage(GFB.PREFIX + "Du bekommst dein Gehalt von " + money + "€ am PayDay.");
    }


}
