package de.newrp.Berufe;

import de.newrp.API.Activity;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import de.newrp.Organisationen.Stuff;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Equiplog implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p) && !Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.isLeader(p, true) && !Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0) {
            int hours = Math.toIntExact(TimeUnit.MICROSECONDS.toHours(System.currentTimeMillis() - Activity.getResetDate(Beruf.hasBeruf(p) ? Beruf.getBeruf(p).getID() : -Organisation.getOrganisation(p).getID())));
            if (Beruf.hasBeruf(p)) {
                p.sendMessage(Equip.PREFIX + "EquipLog des Berufs " + Beruf.getBeruf(p).getName() + " für die letzten " + hours + " Stunden:");
            } else if (Organisation.hasOrganisation(p)) {
                p.sendMessage(Equip.PREFIX + "EquipLog der Organisation " + Organisation.getOrganisation(p).getName() + " für die letzten " + hours + " Stunden:");
            }
            sendEquiplog(p, hours);
            return true;
        }

        if(args.length == 1) {
            if(!Script.isInt(args[0])) {
                if (args[0].equalsIgnoreCase("reset")) {
                    if (Beruf.hasBeruf(p)) {
                        Script.executeUpdate("DELETE FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID());
                        Beruf.getBeruf(p).sendMessage(Equip.PREFIX + "EquipLog wurde von " + Script.getName(p) + " zurückgesetzt.");
                    } else if (Organisation.hasOrganisation(p)) {
                        Script.executeUpdate("DELETE FROM equiplog WHERE beruf=" + -Organisation.getOrganisation(p).getID());
                        Organisation.getOrganisation(p).sendMessage(Equip.PREFIX + "EquipLog wurde von " + Script.getName(p) + " zurückgesetzt.");
                    }
                    p.sendMessage(Equip.PREFIX + "EquipLog wurde zurückgesetzt.");
                    return true;
                } else if (Script.getNRPID(args[0]) > 0) {
                    int hours = Math.toIntExact(TimeUnit.MICROSECONDS.toHours(System.currentTimeMillis() - Activity.getResetDate(Beruf.hasBeruf(p) ? Beruf.getBeruf(p).getID() : -Organisation.getOrganisation(p).getID())));
                    OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
                    if (Script.getNRPID(tg) == 0) {
                        p.sendMessage(Messages.PLAYER_NOT_FOUND);
                        return true;
                    }

                    if (Beruf.hasBeruf(p)) {
                        if (!Beruf.hasBeruf(tg)) {
                            p.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen Beruf.");
                            return true;
                        }

                        if (Beruf.getBeruf(tg).getID() != Beruf.getBeruf(p).getID()) {
                            p.sendMessage(Messages.ERROR + "Dieser Spieler hat nicht den gleichen Beruf wie du<.");
                            return true;
                        }
                    } else if (Organisation.hasOrganisation(p)) {
                        if (!Organisation.hasOrganisation(tg)) {
                            p.sendMessage(Messages.ERROR + "Dieser Spieler ist in keiner Organisation.");
                            return true;
                        }

                        if (Organisation.getOrganisation(tg).getID() != Organisation.getOrganisation(p).getID()) {
                            p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht in der gleichen Organisation wie du.");
                            return true;
                        }
                    }

                    p.sendMessage(Equip.PREFIX + "EquipLog von " + Script.getName(tg) + " für die letzten " + hours + " Stunden §8[§7" + getTotalOfPlayer(tg, hours) + "€§8]:");
                    sendEquiplog(p, tg, hours);
                    return true;
                }

                p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl an.");
                return true;
            }

            int hours = Integer.parseInt(args[0]);
            if (Beruf.hasBeruf(p)) {
                p.sendMessage(Equip.PREFIX + "EquipLog des Berufs " + Beruf.getBeruf(p).getName() + " für die letzten " + hours + " Stunden:");
            } else if (Organisation.hasOrganisation(p)) {
                p.sendMessage(Equip.PREFIX + "EquipLog der Organisation " + Organisation.getOrganisation(p).getName() + " für die letzten " + hours + " Stunden:");
            }
            sendEquiplog(p, hours);
            return true;
        }

        if (!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl an.");
            return true;
        }

        int hours = Integer.parseInt(args[1]);
        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (Beruf.hasBeruf(p)) {
            if (!Beruf.hasBeruf(tg)) {
                p.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen Beruf.");
                return true;
            }

            if (Beruf.getBeruf(tg).getID() != Beruf.getBeruf(p).getID()) {
                p.sendMessage(Messages.ERROR + "Dieser Spieler hat nicht den gleichen Beruf wie du<.");
                return true;
            }
        } else if (Organisation.hasOrganisation(p)) {
            if (!Organisation.hasOrganisation(tg)) {
                p.sendMessage(Messages.ERROR + "Dieser Spieler ist in keiner Organisation.");
                return true;
            }

            if (Organisation.getOrganisation(tg).getID() != Organisation.getOrganisation(p).getID()) {
                p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht in der gleichen Organisation wie du.");
                return true;
            }
        }

        p.sendMessage(Equip.PREFIX + "EquipLog von " + Script.getName(tg) + " für die letzten " + hours + " Stunden §8[§7" + getTotalOfPlayer(tg, hours) + "€§8]:");
        sendEquiplog(p, tg, hours);
        return true;

    }

    public static void sendEquiplog(Player p, int hours) {
        HashMap<Integer, Integer> equiplog = new HashMap<>();
        if (Beruf.hasBeruf(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)))) {
                while (rs.next()) {
                    int item = rs.getInt("nrp_id");
                    int cost = Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost();
                    if (equiplog.containsKey(item)) {
                        equiplog.put(item, equiplog.get(item) + cost);
                    } else {
                        equiplog.put(item, cost);
                    }
                }

                for (int id : equiplog.keySet()) {
                    p.sendMessage(Equip.PREFIX + Script.getOfflinePlayer(id).getName() + " §8× §7" + equiplog.get(id) + "€");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Organisation.hasOrganisation(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + -Organisation.getOrganisation(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)))) {
                while (rs.next()) {
                    int item = rs.getInt("nrp_id");
                    int cost = Stuff.getStuff(rs.getInt("stuffID")).getCost();
                    if (equiplog.containsKey(item)) {
                        equiplog.put(item, equiplog.get(item) + cost);
                    } else {
                        equiplog.put(item, cost);
                    }
                }

                for (int id : equiplog.keySet()) {
                    p.sendMessage(Equip.PREFIX + Script.getOfflinePlayer(id).getName() + " §8× §7" + equiplog.get(id) + "€");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendEquiplog(Player p, Player tg, int hours) {
        if (Beruf.hasBeruf(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(tg))) {
                while (rs.next()) {
                    p.sendMessage(Equip.PREFIX + Script.getName(tg) + " §8× §7" + Equip.Stuff.getStuff(rs.getInt("stuffID")).getName() + " §8× §7" + Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost() + "€");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Organisation.hasOrganisation(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + -Organisation.getOrganisation(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(tg))) {
                while (rs.next()) {
                    p.sendMessage(Equip.PREFIX + Script.getName(tg) + " §8× §7" + Stuff.getStuff(rs.getInt("stuffID")).getName() + " §8× §7" + Stuff.getStuff(rs.getInt("stuffID")).getCost() + "€");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendEquiplog(Player p, OfflinePlayer tg, int hours) {
        if (Beruf.hasBeruf(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(tg))) {

                while (rs.next()) {
                    p.sendMessage(Equip.PREFIX + Script.getName(tg) + " §8× §7" + Equip.Stuff.getStuff(rs.getInt("stuffID")).getName() + " §8× §7" + Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost() + "€");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Organisation.hasOrganisation(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + -Organisation.getOrganisation(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(tg))) {

                while (rs.next()) {
                    p.sendMessage(Equip.PREFIX + Script.getName(tg) + " §8× §7" + Stuff.getStuff(rs.getInt("stuffID")).getName() + " §8× §7" + Stuff.getStuff(rs.getInt("stuffID")).getCost() + "€");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getTotalOfPlayer(Player p, int hours) {
        int total = 0;
        if (Beruf.hasBeruf(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(p))) {
                while (rs.next()) {
                    total += Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return total;
        } else if (Organisation.hasOrganisation(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + -Organisation.getOrganisation(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(p))) {
                while (rs.next()) {
                    total += Stuff.getStuff(rs.getInt("stuffID")).getCost();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public static int getTotalOfPlayer(OfflinePlayer p, int hours) {
        int total = 0;
        if (Beruf.hasBeruf(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(p))) {
                while (rs.next()) {
                    total += Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Organisation.hasOrganisation(p)) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + -Organisation.getOrganisation(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(p))) {
                while (rs.next()) {
                    total += Stuff.getStuff(rs.getInt("stuffID")).getCost();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public static void addToEquipLog(Player p, int id) {
        if (Beruf.hasBeruf(p)) {
            Script.executeUpdate("INSERT INTO equiplog (nrp_id, beruf, stuffID, time) VALUES (" + Script.getNRPID(p) + ", " + Beruf.getBeruf(p).getID() + ", " + id + ", " + System.currentTimeMillis() + ");");
        } else if (Organisation.hasOrganisation(p)) {
            Script.executeUpdate("INSERT INTO equiplog (nrp_id, beruf, stuffID, time) VALUES (" + Script.getNRPID(p) + ", " + -Organisation.getOrganisation(p).getID() + ", " + id + ", " + System.currentTimeMillis() + ");");
        }
    }
}
