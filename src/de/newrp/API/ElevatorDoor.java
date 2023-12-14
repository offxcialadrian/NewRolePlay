package de.newrp.API;

import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ElevatorDoor implements Listener {

    public enum ElevatorDoors {
        KH_ETAGE_UG(0, new Location[]{
                new Location(Script.WORLD, 309, 64, 208),
                new Location(Script.WORLD, 311, 64, 206)}
                , new Location[]{
                new Location(Script.WORLD, 310, 66, 208),
                new Location(Script.WORLD, 310, 66, 207),
                new Location(Script.WORLD, 310, 66, 206),
                new Location(Script.WORLD, 310, 65, 208),
                new Location(Script.WORLD, 310, 65, 207),
                new Location(Script.WORLD, 310, 65, 206),
                new Location(Script.WORLD, 310, 64, 208),
                new Location(Script.WORLD, 310, 64, 207),
                new Location(Script.WORLD, 310, 64, 206),
                new Location(Script.WORLD, 310, 63, 208),
                new Location(Script.WORLD, 310, 63, 207),
                new Location(Script.WORLD, 310, 63, 206),
        }, Elevator.ElevatorAPI.KRANKENHAUS, 0),
        KH_ETAGE_EG(1, new Location[]{
                new Location(Script.WORLD, 309, 70, 208),
                new Location(Script.WORLD, 311, 70, 206)}
                , new Location[]{
                new Location(Script.WORLD, 310, 72, 208),
                new Location(Script.WORLD, 310, 72, 207),
                new Location(Script.WORLD, 310, 72, 206),
                new Location(Script.WORLD, 310, 71, 208),
                new Location(Script.WORLD, 310, 71, 207),
                new Location(Script.WORLD, 310, 71, 206),
                new Location(Script.WORLD, 310, 70, 208),
                new Location(Script.WORLD, 310, 70, 207),
                new Location(Script.WORLD, 310, 70, 206),
                new Location(Script.WORLD, 310, 69, 208),
                new Location(Script.WORLD, 310, 69, 207),
                new Location(Script.WORLD, 310, 69, 206),
        }, Elevator.ElevatorAPI.KRANKENHAUS, 1),
        KH_ETAGE_1(2, new Location[]{
                new Location(Script.WORLD, 309, 76, 208),
                new Location(Script.WORLD, 311, 76, 206)}
                , new Location[]{
                new Location(Script.WORLD, 310, 78, 208),
                new Location(Script.WORLD, 310, 78, 207),
                new Location(Script.WORLD, 310, 78, 206),
                new Location(Script.WORLD, 310, 77, 208),
                new Location(Script.WORLD, 310, 77, 207),
                new Location(Script.WORLD, 310, 77, 206),
                new Location(Script.WORLD, 310, 76, 208),
                new Location(Script.WORLD, 310, 76, 207),
                new Location(Script.WORLD, 310, 76, 206),
                new Location(Script.WORLD, 310, 75, 208),
                new Location(Script.WORLD, 310, 75, 207),
                new Location(Script.WORLD, 310, 75, 206),
        }, Elevator.ElevatorAPI.KRANKENHAUS, 2),
        KH_ETAGE_2(3, new Location[]{
                new Location(Script.WORLD, 309, 82, 208),
                new Location(Script.WORLD, 311, 82, 206)}
                , new Location[]{
                new Location(Script.WORLD, 310, 84, 208),
                new Location(Script.WORLD, 310, 84, 207),
                new Location(Script.WORLD, 310, 84, 206),
                new Location(Script.WORLD, 310, 83, 208),
                new Location(Script.WORLD, 310, 83, 207),
                new Location(Script.WORLD, 310, 83, 206),
                new Location(Script.WORLD, 310, 82, 208),
                new Location(Script.WORLD, 310, 82, 207),
                new Location(Script.WORLD, 310, 82, 206),
                new Location(Script.WORLD, 310, 81, 208),
                new Location(Script.WORLD, 310, 81, 207),
                new Location(Script.WORLD, 310, 81, 206),
        }, Elevator.ElevatorAPI.KRANKENHAUS, 3),
        KH_ETAGE_3(20, new Location[]{
                new Location(Script.WORLD, 309, 88, 208),
                new Location(Script.WORLD, 311, 88, 206)}
                , new Location[]{
                new Location(Script.WORLD, 310, 90, 208),
                new Location(Script.WORLD, 310, 90, 207),
                new Location(Script.WORLD, 310, 90, 206),
                new Location(Script.WORLD, 310, 89, 208),
                new Location(Script.WORLD, 310, 89, 207),
                new Location(Script.WORLD, 310, 89, 206),
                new Location(Script.WORLD, 310, 88, 208),
                new Location(Script.WORLD, 310, 88, 207),
                new Location(Script.WORLD, 310, 88, 206),
                new Location(Script.WORLD, 310, 87, 208),
                new Location(Script.WORLD, 310, 87, 207),
                new Location(Script.WORLD, 310, 87, 206),
        }, Elevator.ElevatorAPI.KRANKENHAUS, 4),
        SH_ETAGE_EG(4, new Location[]{
                new Location(Script.WORLD, 154, 73, 158),
                new Location(Script.WORLD, 157, 73, 156)}
                , new Location[]{
                new Location(Script.WORLD, 156, 74, 156),
                new Location(Script.WORLD, 156, 74, 157),
                new Location(Script.WORLD, 156, 73, 156),
                new Location(Script.WORLD, 156, 73, 157),
                new Location(Script.WORLD, 156, 72, 156),
                new Location(Script.WORLD, 156, 72, 157)
        }, Elevator.ElevatorAPI.STADTHALLE, 0),
        SH_ETAGE_1(5, new Location[]{
                new Location(Script.WORLD, 155, 81, 157),
                new Location(Script.WORLD, 157, 81, 157)}
                , new Location[]{
                new Location(Script.WORLD, 156, 82, 156),
                new Location(Script.WORLD, 156, 82, 157),
                new Location(Script.WORLD, 156, 81, 156),
                new Location(Script.WORLD, 156, 81, 157),
                new Location(Script.WORLD, 156, 80, 156),
                new Location(Script.WORLD, 156, 80, 157)
        }, Elevator.ElevatorAPI.STADTHALLE, 1),
        SH_ETAGE_2(6, new Location[]{
                new Location(Script.WORLD, 148, 90, 157),
                new Location(Script.WORLD, 150, 90, 157)}
                , new Location[]{
                new Location(Script.WORLD, 149, 91, 156),
                new Location(Script.WORLD, 149, 91, 157),
                new Location(Script.WORLD, 149, 90, 156),
                new Location(Script.WORLD, 149, 90, 157),
                new Location(Script.WORLD, 149, 89, 156),
                new Location(Script.WORLD, 149, 89, 157)
        }, Elevator.ElevatorAPI.STADTHALLE, 2),
        SH_ETAGE_3(7, new Location[]{
                new Location(Script.WORLD, 148, 99, 157),
                new Location(Script.WORLD, 150, 99, 157)}
                , new Location[]{
                new Location(Script.WORLD, 149, 100, 156),
                new Location(Script.WORLD, 149, 100, 157),
                new Location(Script.WORLD, 149, 99, 156),
                new Location(Script.WORLD, 149, 99, 157),
                new Location(Script.WORLD, 149, 98, 156),
                new Location(Script.WORLD, 149, 98, 157)
        }, Elevator.ElevatorAPI.STADTHALLE, 3),
        SH_ETAGE_4(8, new Location[]{
                new Location(Script.WORLD, 148, 108, 157),
                new Location(Script.WORLD, 150, 108, 157)}
                , new Location[]{
                new Location(Script.WORLD, 149, 109, 156),
                new Location(Script.WORLD, 149, 109, 157),
                new Location(Script.WORLD, 149, 108, 156),
                new Location(Script.WORLD, 149, 108, 157),
                new Location(Script.WORLD, 149, 107, 156),
                new Location(Script.WORLD, 149, 107, 157)
        }, Elevator.ElevatorAPI.STADTHALLE, 4),
        ZNA_ETAGE_EG(9, new Location[]{
                new Location(Script.WORLD, 225, 70, 212),
                new Location(Script.WORLD, 222, 70, 210)}
                , new Location[]{
                new Location(Script.WORLD, 222, 71, 211),
                new Location(Script.WORLD, 223, 71, 211),
                new Location(Script.WORLD, 224, 71, 211),
                new Location(Script.WORLD, 222, 70, 211),
                new Location(Script.WORLD, 223, 70, 211),
                new Location(Script.WORLD, 224, 70, 211),
                new Location(Script.WORLD, 222, 69, 211),
                new Location(Script.WORLD, 223, 69, 211),
                new Location(Script.WORLD, 224, 69, 211),
        }, Elevator.ElevatorAPI.NOTAUFNAHME, 0),
        ZNA_ETAGE_1(10, new Location[]{
                new Location(Script.WORLD, 225, 76, 212),
                new Location(Script.WORLD, 222, 76, 210)}
                , new Location[]{
                new Location(Script.WORLD, 222, 77, 211),
                new Location(Script.WORLD, 223, 77, 211),
                new Location(Script.WORLD, 224, 77, 211),
                new Location(Script.WORLD, 222, 76, 211),
                new Location(Script.WORLD, 223, 76, 211),
                new Location(Script.WORLD, 224, 76, 211),
                new Location(Script.WORLD, 222, 75, 211),
                new Location(Script.WORLD, 223, 75, 211),
                new Location(Script.WORLD, 224, 75, 211),
        }, Elevator.ElevatorAPI.NOTAUFNAHME, 1),
        NEWS_ETAGE_EG(11, new Location[]{
                new Location(Script.WORLD, -100, 20, -375),
                new Location(Script.WORLD, -102, 20, -372)}
                , new Location[]{
                new Location(Script.WORLD, -101, 21, -374),
                new Location(Script.WORLD, -101, 21, -373),
                new Location(Script.WORLD, -101, 20, -374),
                new Location(Script.WORLD, -101, 20, -373),
                new Location(Script.WORLD, -101, 19, -374),
                new Location(Script.WORLD, -101, 19, -373),
        }, Elevator.ElevatorAPI.NEWS, 0),
        NEWS_ETAGE_1(12, new Location[]{
                new Location(Script.WORLD, -102, 70, -367),
                new Location(Script.WORLD, -104, 70, -365)}
                , new Location[]{
                new Location(Script.WORLD, -103, 71, -365),
                new Location(Script.WORLD, -103, 71, -366),
                new Location(Script.WORLD, -103, 70, -365),
                new Location(Script.WORLD, -103, 70, -366),
                new Location(Script.WORLD, -103, 69, -365),
                new Location(Script.WORLD, -103, 69, -366),
        }, Elevator.ElevatorAPI.NEWS, 1),
        CHERRYS_ETAGE_EG(13, new Location[]{
                new Location(Script.WORLD, 37, 81, 427),
                new Location(Script.WORLD, 40, 81, 429)}
                , new Location[]{
                new Location(Script.WORLD, 38, 82, 428),
                new Location(Script.WORLD, 39, 82, 428),
                new Location(Script.WORLD, 38, 81, 428),
                new Location(Script.WORLD, 39, 81, 428),
                new Location(Script.WORLD, 38, 80, 428),
                new Location(Script.WORLD, 39, 80, 428),
        }, Elevator.ElevatorAPI.CHERRYS, 0),
        CHERRYS_ETAGE_1(14, new Location[]{
                new Location(Script.WORLD, 37, 95, 427),
                new Location(Script.WORLD, 40, 95, 429)}
                , new Location[]{
                new Location(Script.WORLD, 38, 96, 428),
                new Location(Script.WORLD, 39, 96, 428),
                new Location(Script.WORLD, 38, 95, 428),
                new Location(Script.WORLD, 39, 95, 428),
                new Location(Script.WORLD, 38, 94, 428),
                new Location(Script.WORLD, 39, 94, 428),
        }, Elevator.ElevatorAPI.CHERRYS, 1),
        CHERRYS_ETAGE_2(15, new Location[]{
                new Location(Script.WORLD, 37, 101, 427),
                new Location(Script.WORLD, 40, 101, 429)}
                , new Location[]{
                new Location(Script.WORLD, 38, 102, 428),
                new Location(Script.WORLD, 39, 102, 428),
                new Location(Script.WORLD, 38, 101, 428),
                new Location(Script.WORLD, 39, 101, 428),
                new Location(Script.WORLD, 38, 100, 428),
                new Location(Script.WORLD, 39, 100, 428),
        }, Elevator.ElevatorAPI.CHERRYS, 2),
        CHERRYS_ETAGE_3(16, new Location[]{
                new Location(Script.WORLD, 37, 107, 427),
                new Location(Script.WORLD, 40, 107, 429)}
                , new Location[]{
                new Location(Script.WORLD, 38, 108, 428),
                new Location(Script.WORLD, 39, 108, 428),
                new Location(Script.WORLD, 38, 107, 428),
                new Location(Script.WORLD, 39, 107, 428),
                new Location(Script.WORLD, 38, 106, 428),
                new Location(Script.WORLD, 39, 106, 428),
        }, Elevator.ElevatorAPI.CHERRYS, 3),
        CHERRYS_ETAGE_4(17, new Location[]{
                new Location(Script.WORLD, 37, 113, 427),
                new Location(Script.WORLD, 40, 113, 429)}
                , new Location[]{
                new Location(Script.WORLD, 38, 114, 428),
                new Location(Script.WORLD, 39, 114, 428),
                new Location(Script.WORLD, 38, 113, 428),
                new Location(Script.WORLD, 39, 113, 428),
                new Location(Script.WORLD, 38, 112, 428),
                new Location(Script.WORLD, 39, 112, 428),
        }, Elevator.ElevatorAPI.CHERRYS, 4),
        CHERRYS_ETAGE_5(18, new Location[]{
                new Location(Script.WORLD, 37, 119, 427),
                new Location(Script.WORLD, 40, 119, 429)}
                , new Location[]{
                new Location(Script.WORLD, 38, 120, 428),
                new Location(Script.WORLD, 39, 120, 428),
                new Location(Script.WORLD, 38, 119, 428),
                new Location(Script.WORLD, 39, 119, 428),
                new Location(Script.WORLD, 38, 118, 428),
                new Location(Script.WORLD, 39, 118, 428),
        }, Elevator.ElevatorAPI.CHERRYS, 5),
        CHERRYS_ETAGE_6(19, new Location[]{
                new Location(Script.WORLD, 37, 125, 427),
                new Location(Script.WORLD, 40, 125, 429)}
                , new Location[]{
                new Location(Script.WORLD, 38, 126, 428),
                new Location(Script.WORLD, 39, 126, 428),
                new Location(Script.WORLD, 38, 125, 428),
                new Location(Script.WORLD, 39, 125, 428),
                new Location(Script.WORLD, 38, 124, 428),
                new Location(Script.WORLD, 39, 124, 428),
        }, Elevator.ElevatorAPI.CHERRYS, 6);

        int id;
        Location[] button;
        Location[] blocks;
        Elevator.ElevatorAPI elevator;
        int etage;

        ElevatorDoors(int id, Location[] button, Location[] blocks, Elevator.ElevatorAPI elevator, int etage) {
            this.id = id;
            this.button = button;
            this.blocks = blocks;
            this.elevator = elevator;
            this.etage = etage;
        }

        public Location[] getButtons() {
            return button;
        }

        public Location[] getBlocks() {
            return blocks;
        }

        public Elevator.ElevatorAPI getElevator() {
            return elevator;
        }

        public int getEtage() {
            return etage;
        }

        public static ElevatorDoors getDoorByLoc(Location loc) {
            for (ElevatorDoors elevatorDoors : ElevatorDoors.values()) {
                for (Location loc2 : elevatorDoors.getButtons()) {
                    if (loc2.equals(loc)) return elevatorDoors;
                }
            }
            return null;
        }

        public static ElevatorDoors getDoorByData(Elevator.ElevatorAPI elevator, int etage) {
            for (ElevatorDoors elevatorDoors : ElevatorDoors.values()) {
                if (elevatorDoors.getElevator().equals(elevator) && elevatorDoors.getEtage() == etage) {
                    return elevatorDoors;
                }
            }
            return null;
        }

    }


    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.STONE_BUTTON)) {
                ElevatorDoors elevatorDoors = ElevatorDoors.getDoorByLoc(e.getClickedBlock().getLocation());
                if (elevatorDoors != null) {
                    for (Location loc : elevatorDoors.getBlocks()) {
                        loc.getBlock().setType(Material.AIR);
                        new BukkitRunnable() {
                            public void run() {
                                loc.getBlock().setType(Material.IRON_BLOCK);
                            }
                        }.runTaskLater(main.getInstance(), 20L * 5);
                    }
                }
            }
        }
    }

    public static void openDoors(Player p, Elevator.ElevatorAPI elevator, int etage) {
        ElevatorDoors elevatorDoors = ElevatorDoors.getDoorByData(elevator, etage);
        if (elevatorDoors != null) {
            for (Location loc : elevatorDoors.getBlocks()) {
                loc.getBlock().setType(Material.AIR);
                new BukkitRunnable() {
                    public void run() {
                        loc.getBlock().setType(Material.IRON_BLOCK);
                    }
                }.runTaskLater(main.getInstance(), 20L * 5);
            }
        }
    }
}