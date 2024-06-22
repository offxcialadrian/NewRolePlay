package de.newrp.API;

import de.newrp.Administrator.Notifications;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Call.Call;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Organisationen.Contract.model.Contract;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.Fesseln;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import de.newrp.features.deathmatcharena.data.DeathmatchArenaStats;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class FriedhofListener implements Listener {

    public static final HashMap<String, EntityDamageEvent.DamageCause> DEATH_REASON = new HashMap<>();
    private final IDeathmatchArenaService deathmatchArenaService = DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        if (!e.getEntity().getType().equals(EntityType.PLAYER)) return;

        Player p = e.getEntity();

        if (Friedhof.getDead(p) != null) {
            e.setCancelled(true);
            return;
        }

        if(this.deathmatchArenaService.isInDeathmatch(p.getPlayer(), false)) {
            p.sendMessage(Messages.INFO + "Weil du in der Deathmatch Arena bist, bist du direkt respawned");
            final DeathmatchArenaStats stats = this.deathmatchArenaService.getStats(p);
            if(stats != null) {
                stats.deaths(stats.deaths() + 1);
            }

            if(p.getKiller() == null) {
                this.deathmatchArenaService.sendMessageToArenaMembers(this.deathmatchArenaService.getPrefix() + Script.getName(p) + " ist gestorben");
            } else {
                this.deathmatchArenaService.sendMessageToArenaMembers(this.deathmatchArenaService.getPrefix() + Script.getName(p) + " wurde von " + Script.getName(p.getKiller()) + " getötet");
                final DeathmatchArenaStats killerStats = this.deathmatchArenaService.getStats(p.getKiller());
                if(killerStats != null) {
                    killerStats.kills(killerStats.kills() + 1);
                }

            }
            return;
        }

        Chair.NO_TELEPORT.add(p.getName());
        if (p.isInsideVehicle()) p.leaveVehicle();
        if(Fesseln.isTiedUp(p)) Fesseln.untie(p);

        int deathtime = 480;
        if(GangwarCommand.isInGangwar(p)) {
            deathtime = 120;
        } else if(DependencyContainer.getContainer().getDependency(IBizWarService.class).isMemberOfBizWar(p)) {
            deathtime = 60;
        }

        World w = p.getWorld();
        Location deathLocation = p.getLocation();

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(p);
        meta.setDisplayName("§6" + p.getName());
        head.setItemMeta(meta);

        Player killer = p.getKiller();

        if ((Organisation.getOrganisation(killer) == Organisation.HITMEN) && (!DependencyContainer.getContainer().getDependency(IBizWarService.class).isMemberOfBizWar(p)) && (!DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class).isInDeathmatch(p, false))) {
            Contract ct = Contract.getContract(p);
            if(ct != null) {
                Script.addEXP(killer, Script.getRandom(8, 16), true);
                Organisation.HITMEN.addExp(Script.getRandom(20, 30));
                Organisation.HITMEN.sendMessage(Contract.PREFIX + Script.getName(killer) + " hat " + Script.getName(p) + " getötet.");
                Organisation.HITMEN.addKasse(ct.getPrice());
                p.sendMessage(Contract.PREFIX + "Du wurdest getötet weil ein Kopfgeld auf dich ausgesetzt wurde.");
                Activity.grantActivity(Script.getNRPID(killer), Activities.CT_KILL);
                Contract.remove(ct);

                Friedhof friedhof = new Friedhof(Script.getNRPID(p), p.getName(), new Location(Script.WORLD, 0D, 1000D, 0D), System.currentTimeMillis(), 1200, 0, null);
                Friedhof.setDead(p, friedhof);
                Script.setMoney(p, PaymentType.CASH, 0);
                p.getInventory().clear();
                p.getItemOnCursor().setType(Material.AIR);

                if(Call.isOnCall(p)) {
                    if (Call.isWaitingForCall(p)) {
                        Call.deny(p);
                    } else if (Call.getParticipants(Call.getCallIDByPlayer(p)).size() == 1) {
                        Call.abort(p);
                    } else {
                        Call.hangup(p);
                    }
                }

                Notifications.sendMessage(Notifications.NotificationType.DEAD, Script.getName(p) + " ist gestorben (Kopfgeld) " + (killer!=null ? Messages.ARROW + " " + Script.getName(killer):Messages.ARROW + " " + (p.getLastDamageCause() != null?p.getLastDamageCause().getCause().name():"")));
                return;
            }
        }

        Corpse.spawnNPC(p);
        int cash = Script.getMoney(p, PaymentType.CASH);
        Script.setMoney(p, PaymentType.CASH, 0);

        ItemStack[] inventoryContent = p.getInventory().getContents();
        p.getInventory().clear();
        p.getItemOnCursor().setType(Material.AIR);


        if(Call.isOnCall(p)) {
            if (Call.isWaitingForCall(p)) {
                Call.deny(p);
            } else if (Call.getParticipants(Call.getCallIDByPlayer(p)).size() == 1) {
                Call.abort(p);
            } else {
                Call.hangup(p);
            }
        }

        if(p.getLastDamageCause() != null) {
            if (p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.WITHER) {
                Health.THIRST.add(Script.getNRPID(p), (Health.THIRST.getMax() / 2));
            }
        }

        Friedhof friedhof = new Friedhof(Script.getNRPID(p), p.getName(), deathLocation, System.currentTimeMillis(), deathtime, cash, inventoryContent);
        Friedhof.setDead(p, friedhof);
        Notifications.sendMessage(Notifications.NotificationType.DEAD, Script.getName(p) + " ist gestorben " + (killer!=null ? Messages.ARROW + " " + Script.getName(killer):Messages.ARROW + " " + (p.getLastDamageCause() != null?p.getLastDamageCause().getCause().name():"")));

        Utils.alkLevel.remove(p.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Friedhof.isDead(p)) {
            Friedhof f = Friedhof.getDead(p);
            long deathtime = f.getDeathTime();
            long current = System.currentTimeMillis();

            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(current - deathtime);
            if (seconds > 0) {
                int duration = f.getDuration() - seconds;
                if (duration > 0) {
                    Script.executeAsyncUpdate("INSERT INTO friedhof (id, time) VALUES (" + f.getUserID() + ", " + duration + ") ON DUPLICATE KEY UPDATE time = " + duration);
                }
            }
            if(Corpse.npcMap.containsKey(p.getUniqueId())) Corpse.removeNPC(p);
            if (f.getTaskID() != 0) Bukkit.getScheduler().cancelTask(f.getTaskID());
            Friedhof.FRIEDHOF.remove(p.getName());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (Friedhof.isDead(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(!(e.getDamager() instanceof Player)) {
                return;
            }
            Player damager = (Player) e.getDamager();
            if (Friedhof.isDead(p) || Friedhof.isDead(damager)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        int id = Script.getNRPID(p);
        int i = Friedhof.getDeathtimeDatabase(p);
        if (i > 0) {
            Friedhof.setDead(p, new Friedhof(id, p.getName(), null, System.currentTimeMillis(), i,  0, null));
        }
    }

    @EventHandler
    public void onHeadDespawn(ItemDespawnEvent e) {
        Item item = e.getEntity();
        if (item.getItemStack().getType() == Material.PLAYER_HEAD) {
            e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (Friedhof.isDead(p)) {
            if (!e.getMessage().startsWith("/friedhof") && !e.getMessage().startsWith("/passwort") && !e.getMessage().startsWith("/password") && !e.getMessage().startsWith("/afk") && !e.getMessage().startsWith("/aduty") && !e.getMessage().startsWith("/tc") && !e.getMessage().startsWith("/sduty") && !e.getMessage().startsWith("/ticket") && !e.getMessage().startsWith("/report") && !e.getMessage().startsWith("/help") && !SDuty.isSDuty(p)) {
                e.setCancelled(true);
                p.sendMessage(Messages.ERROR + "Du kannst während du tot bist keine Befehle ausführen.");
            }
        }
    }
}
