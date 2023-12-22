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

        X3_ETAGE_0(0, new Location(Script.WORLD, 677, 72, 990),
                new Location[]{
                        new Location(Script.WORLD, 674, 72, 993),
                        new Location(Script.WORLD, 680, 72, 990),
                        new Location(Script.WORLD, 677, 72, 993)},
                new Location[]{
                        new Location(Script.WORLD, 679, 73, 991),
                        new Location(Script.WORLD, 679, 73, 992),
                        new Location(Script.WORLD, 679, 72, 991),
                        new Location(Script.WORLD, 679, 72, 992),
                        new Location(Script.WORLD, 679, 71, 991),
                        new Location(Script.WORLD, 679, 71, 992),
                        new Location(Script.WORLD, 675, 73, 992),
                        new Location(Script.WORLD, 675, 73, 991),
                        new Location(Script.WORLD, 675, 72, 992),
                        new Location(Script.WORLD, 675, 72, 991),
                        new Location(Script.WORLD, 675, 71, 991),
                        new Location(Script.WORLD, 675, 71, 992)
                }, Elevator.ElevatorAPI.X3, 0),
        X3_ETAGE_1(1, new Location(Script.WORLD, 677, 84, 990),
                new Location[]{
                        new Location(Script.WORLD, 677, 84, 990),
                        new Location(Script.WORLD, 680, 84, 990),
                        new Location(Script.WORLD, 674, 84, 990)},
                new Location[]{
                        new Location(Script.WORLD, 679, 85, 991),
                        new Location(Script.WORLD, 679, 85, 992),
                        new Location(Script.WORLD, 679, 84, 991),
                        new Location(Script.WORLD, 679, 84, 992),
                        new Location(Script.WORLD, 679, 83, 991),
                        new Location(Script.WORLD, 679, 83, 992),
                        new Location(Script.WORLD, 675, 85, 992),
                        new Location(Script.WORLD, 675, 85, 991),
                        new Location(Script.WORLD, 675, 84, 992),
                        new Location(Script.WORLD, 675, 84, 991),
                        new Location(Script.WORLD, 675, 83, 992),
                        new Location(Script.WORLD, 675, 83, 991)
                }, Elevator.ElevatorAPI.X3, 1),

        X3_ETAGE_2(2, new Location(Script.WORLD, 677, 91, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 91, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 92, 992),
                        new Location(Script.WORLD, 675, 92, 991),
                        new Location(Script.WORLD, 675, 91, 992),
                        new Location(Script.WORLD, 675, 91, 991),
                        new Location(Script.WORLD, 675, 90, 992),
                        new Location(Script.WORLD, 675, 90, 991),
                }, Elevator.ElevatorAPI.X3, 2),

        X3_ETAGE_3(3,new Location(Script.WORLD, 677, 97, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 97, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 98, 992),
                        new Location(Script.WORLD, 675, 98, 991),
                        new Location(Script.WORLD, 675, 97, 992),
                        new Location(Script.WORLD, 675, 97, 991),
                        new Location(Script.WORLD, 675, 96, 992),
                        new Location(Script.WORLD, 675, 96, 991),
                }, Elevator.ElevatorAPI.X3, 3),

        X3_ETAGE_4(4, new Location(Script.WORLD, 677, 103, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 103, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 104, 992),
                        new Location(Script.WORLD, 675, 104, 991),
                        new Location(Script.WORLD, 675, 103, 992),
                        new Location(Script.WORLD, 675, 103, 991),
                        new Location(Script.WORLD, 675, 102, 992),
                        new Location(Script.WORLD, 675, 103, 991),
                        new Location(Script.WORLD, 675, 102, 991),
                }, Elevator.ElevatorAPI.X3, 4),

        X3_ETAGE_5(5, new Location(Script.WORLD, 677, 109, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 109, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 110, 992),
                        new Location(Script.WORLD, 675, 110, 991),
                        new Location(Script.WORLD, 675, 109, 992),
                        new Location(Script.WORLD, 675, 109, 991),
                        new Location(Script.WORLD, 675, 108, 992),
                        new Location(Script.WORLD, 675, 108, 991),
                }, Elevator.ElevatorAPI.X3, 5),

        X3_ETAGE_6(6, new Location(Script.WORLD, 677, 115, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 115, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 116, 992),
                        new Location(Script.WORLD, 675, 116, 991),
                        new Location(Script.WORLD, 675, 115, 992),
                        new Location(Script.WORLD, 675, 115, 991),
                        new Location(Script.WORLD, 675, 114, 992),
                        new Location(Script.WORLD, 675, 114, 991),
                }, Elevator.ElevatorAPI.X3, 6),

        X3_ETAGE_7(7, new Location(Script.WORLD, 677, 121, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 121, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 122, 992),
                        new Location(Script.WORLD, 675, 122, 991),
                        new Location(Script.WORLD, 675, 121, 992),
                        new Location(Script.WORLD, 675, 121, 991),
                        new Location(Script.WORLD, 675, 120, 992),
                        new Location(Script.WORLD, 675, 120, 991),
                }, Elevator.ElevatorAPI.X3, 7),

        X3_ETAGE_8(8, new Location(Script.WORLD, 677, 127, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 127, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 128, 992),
                        new Location(Script.WORLD, 675, 128, 991),
                        new Location(Script.WORLD, 675, 127, 992),
                        new Location(Script.WORLD, 675, 127, 991),
                        new Location(Script.WORLD, 675, 126, 992),
                        new Location(Script.WORLD, 675, 126, 991),
                }, Elevator.ElevatorAPI.X3, 8),

        X3_ETAGE_9(9, new Location(Script.WORLD, 677, 133, 993),
                new Location[]{
                        new Location(Script.WORLD, 674, 133, 993)},
                new Location[]{
                        new Location(Script.WORLD, 675, 134, 992),
                        new Location(Script.WORLD, 675, 134, 991),
                        new Location(Script.WORLD, 675, 133, 992),
                        new Location(Script.WORLD, 675, 133, 991),
                        new Location(Script.WORLD, 675, 132, 992),
                        new Location(Script.WORLD, 675, 132, 991),
                }, Elevator.ElevatorAPI.X3, 9),
        AEKI_ETAGE_0(0, new Location(Script.WORLD, 682, 69, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 69, 911)},
                new Location[]{
                        new Location(Script.WORLD, 684, 70, 912),
                        new Location(Script.WORLD, 683, 70, 912),
                        new Location(Script.WORLD, 682, 70, 912),
                        new Location(Script.WORLD, 684, 69, 912),
                        new Location(Script.WORLD, 683, 69, 912),
                        new Location(Script.WORLD, 682, 69, 912),
                        new Location(Script.WORLD, 684, 68, 912),
                        new Location(Script.WORLD, 683, 68, 912),
                        new Location(Script.WORLD, 682, 68, 912),
                }, Elevator.ElevatorAPI.AEKI, 0),
        AEKI_ETAGE_1(1, new Location(Script.WORLD, 682, 78, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 78, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 79, 912),
                        new Location(Script.WORLD, 683, 79, 912),
                        new Location(Script.WORLD, 684, 79, 912),
                        new Location(Script.WORLD, 682, 78, 912),
                        new Location(Script.WORLD, 683, 78, 912),
                        new Location(Script.WORLD, 684, 78, 912),
                        new Location(Script.WORLD, 682, 77, 912),
                        new Location(Script.WORLD, 683, 77, 912),
                        new Location(Script.WORLD, 684, 77, 912),
                }, Elevator.ElevatorAPI.AEKI, 1),
        AEKI_ETAGE_2(2, new Location(Script.WORLD, 682, 85, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 85, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 86, 912),
                        new Location(Script.WORLD, 683, 86, 912),
                        new Location(Script.WORLD, 684, 86, 912),
                        new Location(Script.WORLD, 682, 85, 912),
                        new Location(Script.WORLD, 683, 85, 912),
                        new Location(Script.WORLD, 684, 85, 912),
                        new Location(Script.WORLD, 682, 84, 912),
                        new Location(Script.WORLD, 683, 84, 912),
                        new Location(Script.WORLD, 684, 84, 912),
                }, Elevator.ElevatorAPI.AEKI, 2),

        AEKI_ETAGE_3(3, new Location(Script.WORLD, 682, 92, 914),
                new Location[]{
                        new Location(Script.WORLD, 682, 92, 914)},
                new Location[]{
                        new Location(Script.WORLD, 682, 93, 912),
                        new Location(Script.WORLD, 683, 93, 912),
                        new Location(Script.WORLD, 684, 93, 912),
                        new Location(Script.WORLD, 682, 92, 912),
                        new Location(Script.WORLD, 683, 92, 912),
                        new Location(Script.WORLD, 684, 92, 912),
                        new Location(Script.WORLD, 682, 91, 912),
                        new Location(Script.WORLD, 683, 91, 912),
                        new Location(Script.WORLD, 684, 91, 912),
                }, Elevator.ElevatorAPI.AEKI, 3),

        AEKI_ETAGE_4(4, new Location(Script.WORLD, 682, 99, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 99, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 100, 912),
                        new Location(Script.WORLD, 683, 100, 912),
                        new Location(Script.WORLD, 684, 100, 912),
                        new Location(Script.WORLD, 682, 99, 912),
                        new Location(Script.WORLD, 683, 99, 912),
                        new Location(Script.WORLD, 684, 99, 912),
                        new Location(Script.WORLD, 682, 98, 912),
                        new Location(Script.WORLD, 683, 98, 912),
                        new Location(Script.WORLD, 684, 98, 912),
                }, Elevator.ElevatorAPI.AEKI, 4),


        AEKI_ETAGE_5(5, new Location(Script.WORLD, 682, 106, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 106, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 107, 912),
                        new Location(Script.WORLD, 683, 107, 912),
                        new Location(Script.WORLD, 684, 107, 912),
                        new Location(Script.WORLD, 682, 106, 912),
                        new Location(Script.WORLD, 683, 106, 912),
                        new Location(Script.WORLD, 684, 106, 912),
                        new Location(Script.WORLD, 682, 105, 912),
                        new Location(Script.WORLD, 683, 105, 912),
                        new Location(Script.WORLD, 684, 105, 912),
                }, Elevator.ElevatorAPI.AEKI, 5),


        AEKI_ETAGE_6(6, new Location(Script.WORLD, 682, 113, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 113, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 114, 912),
                        new Location(Script.WORLD, 683, 114, 912),
                        new Location(Script.WORLD, 684, 114, 912),
                        new Location(Script.WORLD, 682, 113, 912),
                        new Location(Script.WORLD, 683, 113, 912),
                        new Location(Script.WORLD, 684, 113, 912),
                        new Location(Script.WORLD, 682, 112, 912),
                        new Location(Script.WORLD, 683, 112, 912),
                        new Location(Script.WORLD, 684, 112, 912),
                }, Elevator.ElevatorAPI.AEKI, 6),


        AEKI_ETAGE_7(7, new Location(Script.WORLD, 682, 120, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 120, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 121, 912),
                        new Location(Script.WORLD, 683, 121, 912),
                        new Location(Script.WORLD, 684, 121, 912),
                        new Location(Script.WORLD, 682, 120, 912),
                        new Location(Script.WORLD, 683, 120, 912),
                        new Location(Script.WORLD, 684, 120, 912),
                        new Location(Script.WORLD, 682, 119, 912),
                        new Location(Script.WORLD, 683, 119, 912),
                        new Location(Script.WORLD, 684, 119, 912),
                }, Elevator.ElevatorAPI.AEKI, 7),

        AEKI_ETAGE_8(8, new Location(Script.WORLD, 682, 127, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 127, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 127, 912),
                        new Location(Script.WORLD, 683, 127, 912),
                        new Location(Script.WORLD, 684, 127, 912),
                        new Location(Script.WORLD, 682, 126, 912),
                        new Location(Script.WORLD, 683, 126, 912),
                        new Location(Script.WORLD, 684, 126, 912),
                }, Elevator.ElevatorAPI.AEKI, 8),
        AEKI_ETAGE_9(9, new Location(Script.WORLD, 682, 132, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 132, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 133, 912),
                        new Location(Script.WORLD, 683, 133, 912),
                        new Location(Script.WORLD, 684, 133, 912),
                        new Location(Script.WORLD, 682, 132, 912),
                        new Location(Script.WORLD, 683, 132, 912),
                        new Location(Script.WORLD, 684, 132, 912),
                        new Location(Script.WORLD, 682, 131, 912),
                        new Location(Script.WORLD, 683, 131, 912),
                        new Location(Script.WORLD, 684, 131, 912),
                }, Elevator.ElevatorAPI.AEKI, 9),
        AEKI_ETAGE_10(9, new Location(Script.WORLD, 682, 139, 914),
                new Location[]{
                        new Location(Script.WORLD, 685, 139, 911)},
                new Location[]{
                        new Location(Script.WORLD, 682, 140, 912),
                        new Location(Script.WORLD, 683, 140, 912),
                        new Location(Script.WORLD, 684, 140, 912),
                        new Location(Script.WORLD, 682, 139, 912),
                        new Location(Script.WORLD, 683, 139, 912),
                        new Location(Script.WORLD, 684, 139, 912),
                        new Location(Script.WORLD, 682, 138, 912),
                        new Location(Script.WORLD, 683, 138, 912),
                        new Location(Script.WORLD, 684, 138, 912),
                }, Elevator.ElevatorAPI.AEKI, 10),
        STADTHALLE_ETAGE_0(10, new Location(Script.WORLD, 546, 71, 993),
                new Location[]{
                        new Location(Script.WORLD, 548, 71, 990),
                        new Location(Script.WORLD, 542, 71, 993)},
                new Location[]{
                        new Location(Script.WORLD, 543, 70, 991),
                        new Location(Script.WORLD, 543, 71, 991),
                        new Location(Script.WORLD, 543, 72, 991),
                        new Location(Script.WORLD, 543, 70, 992),
                        new Location(Script.WORLD, 543, 71, 992),
                        new Location(Script.WORLD, 543, 72, 992),
                        new Location(Script.WORLD, 547, 70, 992),
                        new Location(Script.WORLD, 547, 71, 992),
                        new Location(Script.WORLD, 547, 72, 992),
                        new Location(Script.WORLD, 547, 70, 991),
                        new Location(Script.WORLD, 547, 71, 991),
                        new Location(Script.WORLD, 547, 72, 991)
                }, Elevator.ElevatorAPI.STADTHALLE, 0),
        STADTHALLE_ETAGE_1(11, new Location(Script.WORLD, 546, 79, 993),
                new Location[]{
                        new Location(Script.WORLD, 548, 79, 990),
                        new Location(Script.WORLD, 542, 79, 993)},
                new Location[]{
                        new Location(Script.WORLD, 543, 78, 991),
                        new Location(Script.WORLD, 543, 79, 991),
                        new Location(Script.WORLD, 543, 80, 991),
                        new Location(Script.WORLD, 543, 78, 992),
                        new Location(Script.WORLD, 543, 79, 992),
                        new Location(Script.WORLD, 543, 80, 992),
                        new Location(Script.WORLD, 547, 78, 992),
                        new Location(Script.WORLD, 547, 79, 992),
                        new Location(Script.WORLD, 547, 80, 992),
                        new Location(Script.WORLD, 547, 79, 991),
                        new Location(Script.WORLD, 547, 78, 991),
                        new Location(Script.WORLD, 547, 80, 991)
                }, Elevator.ElevatorAPI.STADTHALLE, 1);


        int id;
        Location[] button;
        Location loc;
        Location[] blocks;
        Elevator.ElevatorAPI elevator;
        int etage;

        ElevatorDoors(int id, Location loc, Location[] button, Location[] blocks, Elevator.ElevatorAPI elevator, int etage) {
            this.id = id;
            this.loc = loc;
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

        public Location getDriveLoc() {
            return loc;
        }

        public static ElevatorDoors getDoorByLoc(Location loc) {
            for (ElevatorDoors elevatorDoors : ElevatorDoors.values()) {
                for (Location loc2 : elevatorDoors.getButtons()) {
                    if (loc2.equals(loc)) return elevatorDoors;
                }
            }
            return null;
        }

        public static ElevatorDoors getDoorByDriveLoc(Location loc) {
            for (ElevatorDoors elevatorDoors : ElevatorDoors.values()) {
                if (elevatorDoors.getDriveLoc().equals(loc)) {
                    return elevatorDoors;
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