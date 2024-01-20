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
                }, Elevator.ElevatorAPI.STADTHALLE, 1),
        KRANKENHAUS_ETAGE_RECHTER_AUFZUG_0(12, new Location(Script.WORLD, 267, 76, 1230),
                new Location[]{
                        new Location(Script.WORLD, 271, 76, 1229)},
                new Location[]{
                        new Location(Script.WORLD, 270, 77, 1230),
                        new Location(Script.WORLD, 270, 77, 1231),
                        new Location(Script.WORLD, 270, 77, 1232),
                        new Location(Script.WORLD, 270, 76, 1230),
                        new Location(Script.WORLD, 270, 76, 1231),
                        new Location(Script.WORLD, 270, 76, 1232),
                        new Location(Script.WORLD, 270, 75, 1230),
                        new Location(Script.WORLD, 270, 75, 1231),
                        new Location(Script.WORLD, 270, 75, 1232),
                }, Elevator.ElevatorAPI.KRANKENHAUS_2, 0),

        KRANKENHAUS_ETAGE_LINKER_AUFZUG_0(12,new Location(Script.WORLD, 267, 76, 1236),
                new Location[]{
                        new Location(Script.WORLD, 271, 76, 1237)},
                new Location[]{
                        new Location(Script.WORLD, 270, 77, 1234),
                        new Location(Script.WORLD, 270, 77, 1235),
                        new Location(Script.WORLD, 270, 77, 1236),
                        new Location(Script.WORLD, 270, 76, 1234),
                        new Location(Script.WORLD, 270, 76, 1235),
                        new Location(Script.WORLD, 270, 76, 1236),
                        new Location(Script.WORLD, 270, 75, 1234),
                        new Location(Script.WORLD, 270, 75, 1235),
                        new Location(Script.WORLD, 270, 75, 1236),
                }, Elevator.ElevatorAPI.KRANKENHAUS, 0),

        KRANKENHAUS_ETAGE_1_RECHTER_AUFZUG_0(12, new Location(Script.WORLD, 267, 84, 1230),
                new Location[]{
                        new Location(Script.WORLD, 271, 84, 1229)},
                new Location[]{
                        new Location(Script.WORLD, 270, 85, 1230),
                        new Location(Script.WORLD, 270, 85, 1231),
                        new Location(Script.WORLD, 270, 85, 1232),
                        new Location(Script.WORLD, 270, 84, 1230),
                        new Location(Script.WORLD, 270, 84, 1231),
                        new Location(Script.WORLD, 270, 84, 1232),
                        new Location(Script.WORLD, 270, 83, 1230),
                        new Location(Script.WORLD, 270, 83, 1231),
                        new Location(Script.WORLD, 270, 83, 1232),
                }, Elevator.ElevatorAPI.KRANKENHAUS_2, 1),

        KRANKENHAUS_ETAGE_1_LINKER_AUFZUG_0(12, new Location(Script.WORLD, 267, 84, 1236),
          new Location[]{
            new Location(Script.WORLD, 271, 84, 1237)},
                new Location[]{
            new Location(Script.WORLD, 270, 85, 1234),
                    new Location(Script.WORLD, 270, 85, 1235),
                    new Location(Script.WORLD, 270, 85, 1236),
                    new Location(Script.WORLD, 270, 84, 1234),
                    new Location(Script.WORLD, 270, 84, 1235),
                    new Location(Script.WORLD, 270, 84, 1236),
                    new Location(Script.WORLD, 270, 83, 1234),
                    new Location(Script.WORLD, 270, 83, 1235),
                    new Location(Script.WORLD, 270, 83, 1236),
        }, Elevator.ElevatorAPI.KRANKENHAUS, 1),

        KRANKENHAUS_ETAGE_2_RECHTER_AUFZUG_0(12, new Location(Script.WORLD, 267, 92, 1230),
          new Location[]{
            new Location(Script.WORLD, 271, 92, 1229)},
                new Location[]{
            new Location(Script.WORLD, 270, 93, 1230),
                    new Location(Script.WORLD, 270, 93, 1231),
                    new Location(Script.WORLD, 270, 93, 1232),
                    new Location(Script.WORLD, 270, 92, 1230),
                    new Location(Script.WORLD, 270, 92, 1231),
                    new Location(Script.WORLD, 270, 92, 1232),
                    new Location(Script.WORLD, 270, 91, 1230),
                    new Location(Script.WORLD, 270, 91, 1231),
                    new Location(Script.WORLD, 270, 91, 1232),
        }, Elevator.ElevatorAPI.KRANKENHAUS_2, 2),

        KRANKENHAUS_ETAGE_2_LINKER_AUFZUG_0(12, new Location(Script.WORLD, 267, 92, 1236),
                new Location[]{
                        new Location(Script.WORLD, 271, 92, 1237)},
                new Location[]{
                        new Location(Script.WORLD, 270, 93, 1234),
                        new Location(Script.WORLD, 270, 93, 1235),
                        new Location(Script.WORLD, 270, 93, 1236),
                        new Location(Script.WORLD, 270, 92, 1234),
                        new Location(Script.WORLD, 270, 92, 1235),
                        new Location(Script.WORLD, 270, 92, 1236),
                        new Location(Script.WORLD, 270, 91, 1235),
                        new Location(Script.WORLD, 270, 91, 1236),
                        new Location(Script.WORLD, 270, 91, 1234),
                }, Elevator.ElevatorAPI.KRANKENHAUS, 2),
        CASINO_0(13, new Location(Script.WORLD, 780, 78, 850),
                new Location[]{
                        new Location(Script.WORLD, 783, 78, 855)},
                new Location[]{
                        new Location(Script.WORLD, 782, 80, 853),
                        new Location(Script.WORLD, 781, 80, 853),
                        new Location(Script.WORLD, 780, 80, 853),
                        new Location(Script.WORLD, 779, 80, 853),
                        new Location(Script.WORLD, 782, 79, 853),
                        new Location(Script.WORLD, 781, 79, 853),
                        new Location(Script.WORLD, 780, 79, 853),
                        new Location(Script.WORLD, 779, 79, 853),
                        new Location(Script.WORLD, 782, 78, 853),
                        new Location(Script.WORLD, 781, 78, 853),
                        new Location(Script.WORLD, 780, 78, 853),
                        new Location(Script.WORLD, 779, 78, 853),
                        new Location(Script.WORLD, 782, 77, 853),
                        new Location(Script.WORLD, 781, 77, 853),
                        new Location(Script.WORLD, 780, 77, 853),
                        new Location(Script.WORLD, 779, 77, 853),
                }, Elevator.ElevatorAPI.CASINO, 0),

        CASINO_1(13, new Location(Script.WORLD, 780, 86, 850),
                new Location[]{
                        new Location(Script.WORLD, 783, 86, 855)},
                new Location[]{
                        new Location(Script.WORLD, 782, 88, 853),
                        new Location(Script.WORLD, 781, 88, 853),
                        new Location(Script.WORLD, 780, 88, 853),
                        new Location(Script.WORLD, 779, 88, 853),
                        new Location(Script.WORLD, 782, 87, 853),
                        new Location(Script.WORLD, 781, 87, 853),
                        new Location(Script.WORLD, 780, 87, 853),
                        new Location(Script.WORLD, 779, 87, 853),
                        new Location(Script.WORLD, 782, 86, 853),
                        new Location(Script.WORLD, 781, 86, 853),
                        new Location(Script.WORLD, 780, 86, 853),
                        new Location(Script.WORLD, 779, 86, 853),
                        new Location(Script.WORLD, 782, 85, 853),
                        new Location(Script.WORLD, 781, 85, 853),
                        new Location(Script.WORLD, 780, 85, 853),
                        new Location(Script.WORLD, 779, 85, 853),
                }, Elevator.ElevatorAPI.CASINO, 1),

        CASINO_2(13, new Location(Script.WORLD, 780, 94, 850),
                new Location[]{
                        new Location(Script.WORLD, 783, 94, 855)},
                new Location[]{
                        new Location(Script.WORLD, 782, 96, 853),
                        new Location(Script.WORLD, 781, 96, 853),
                        new Location(Script.WORLD, 780, 96, 853),
                        new Location(Script.WORLD, 779, 96, 853),
                        new Location(Script.WORLD, 782, 95, 853),
                        new Location(Script.WORLD, 781, 95, 853),
                        new Location(Script.WORLD, 780, 95, 853),
                        new Location(Script.WORLD, 779, 95, 853),
                        new Location(Script.WORLD, 782, 94, 853),
                        new Location(Script.WORLD, 781, 94, 853),
                        new Location(Script.WORLD, 780, 94, 853),
                        new Location(Script.WORLD, 779, 94, 853),
                        new Location(Script.WORLD, 782, 93, 853),
                        new Location(Script.WORLD, 781, 93, 853),
                        new Location(Script.WORLD, 780, 93, 853),
                        new Location(Script.WORLD, 779, 93, 853),
                }, Elevator.ElevatorAPI.CASINO, 2),

        CASINO_3(13, new Location(Script.WORLD, 780, 102, 850),
                new Location[]{
                        new Location(Script.WORLD, 783, 102, 855)},
                new Location[]{
                        new Location(Script.WORLD, 782, 104, 853),
                        new Location(Script.WORLD, 781, 104, 853),
                        new Location(Script.WORLD, 780, 104, 853),
                        new Location(Script.WORLD, 779, 104, 853),
                        new Location(Script.WORLD, 782, 103, 853),
                        new Location(Script.WORLD, 781, 103, 853),
                        new Location(Script.WORLD, 780, 103, 853),
                        new Location(Script.WORLD, 779, 103, 853),
                        new Location(Script.WORLD, 782, 102, 853),
                        new Location(Script.WORLD, 781, 102, 853),
                        new Location(Script.WORLD, 780, 102, 853),
                        new Location(Script.WORLD, 779, 102, 853),
                        new Location(Script.WORLD, 782, 101, 853),
                        new Location(Script.WORLD, 781, 101, 853),
                        new Location(Script.WORLD, 780, 101, 853),
                        new Location(Script.WORLD, 779, 101, 853),
                }, Elevator.ElevatorAPI.CASINO, 3),

        CASINO_4(13, new Location(Script.WORLD, 780, 110, 850),
                new Location[]{
                        new Location(Script.WORLD, 783, 110, 855)},
                new Location[]{
                        new Location(Script.WORLD, 782, 112, 853),
                        new Location(Script.WORLD, 781, 112, 853),
                        new Location(Script.WORLD, 780, 112, 853),
                        new Location(Script.WORLD, 779, 112, 853),
                        new Location(Script.WORLD, 782, 111, 853),
                        new Location(Script.WORLD, 781, 111, 853),
                        new Location(Script.WORLD, 780, 111, 853),
                        new Location(Script.WORLD, 779, 111, 853),
                        new Location(Script.WORLD, 782, 110, 853),
                        new Location(Script.WORLD, 781, 110, 853),
                        new Location(Script.WORLD, 780, 110, 853),
                        new Location(Script.WORLD, 779, 110, 853),
                        new Location(Script.WORLD, 782, 109, 853),
                        new Location(Script.WORLD, 781, 109, 853),
                        new Location(Script.WORLD, 780, 109, 853),
                        new Location(Script.WORLD, 779, 109, 853),
                }, Elevator.ElevatorAPI.CASINO, 4),
        FPA_0(14, new Location(Script.WORLD, 724, 74, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 74, 814)}, // tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 724, 75, 813),
                        new Location(Script.WORLD, 723, 75, 813),
                        new Location(Script.WORLD, 724, 74, 813),
                        new Location(Script.WORLD, 723, 74, 813),
                        new Location(Script.WORLD, 724, 73, 813),
                        new Location(Script.WORLD, 723, 73, 813),

                }, Elevator.ElevatorAPI.FPA, 0),

        FPA_1(14, new Location(Script.WORLD, 724, 79, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 79, 814)},
                new Location[]{
                        new Location(Script.WORLD, 724, 80, 813),
                        new Location(Script.WORLD, 723, 80, 813),
                        new Location(Script.WORLD, 724, 79, 813),
                        new Location(Script.WORLD, 723, 79, 813),
                        new Location(Script.WORLD, 724, 78, 813),
                        new Location(Script.WORLD, 723, 78, 813),

                }, Elevator.ElevatorAPI.FPA, 1),

        FPA_2(14, new Location(Script.WORLD, 724, 84, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 84, 814)},
                new Location[]{
                        new Location(Script.WORLD, 724, 85, 813),
                        new Location(Script.WORLD, 723, 85, 813),
                        new Location(Script.WORLD, 724, 84, 813),
                        new Location(Script.WORLD, 723, 84, 813),
                        new Location(Script.WORLD, 724, 83, 813),
                        new Location(Script.WORLD, 723, 83, 813),
                }, Elevator.ElevatorAPI.FPA, 2),

        FPA_3(14, new Location(Script.WORLD, 724, 89, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 89, 814)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 724, 90, 813),
                        new Location(Script.WORLD, 723, 90, 813),
                        new Location(Script.WORLD, 724, 89, 813),
                        new Location(Script.WORLD, 723, 89, 813),
                        new Location(Script.WORLD, 724, 88, 813),
                        new Location(Script.WORLD, 723, 88, 813),
                }, Elevator.ElevatorAPI.FPA, 3),

        FPA_4(14, new Location(Script.WORLD, 724, 94, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 94, 814)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 724, 95, 813),
                        new Location(Script.WORLD, 723, 95, 813),
                        new Location(Script.WORLD, 724, 94, 813),
                        new Location(Script.WORLD, 723, 94, 813),
                        new Location(Script.WORLD, 724, 93, 813),
                        new Location(Script.WORLD, 723, 93, 813),
                }, Elevator.ElevatorAPI.FPA, 4),

        FPA_5(14, new Location(Script.WORLD, 724, 99, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 99, 814)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 724, 100, 813),
                        new Location(Script.WORLD, 723, 100, 813),
                        new Location(Script.WORLD, 724, 99, 813),
                        new Location(Script.WORLD, 723, 99, 813),
                        new Location(Script.WORLD, 724, 98, 813),
                        new Location(Script.WORLD, 723, 98, 813),
                }, Elevator.ElevatorAPI.FPA, 5),

        FPA_6(14, new Location(Script.WORLD, 724, 104, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 104, 814)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 724, 105, 813),
                        new Location(Script.WORLD, 723, 105, 813),
                        new Location(Script.WORLD, 724, 104, 813),
                        new Location(Script.WORLD, 723, 104, 813),
                        new Location(Script.WORLD, 724, 103, 813),
                        new Location(Script.WORLD, 723, 103, 813),
                }, Elevator.ElevatorAPI.FPA, 6),

        FPA_7(14, new Location(Script.WORLD, 724, 109, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 109, 814)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 724, 110, 813),
                        new Location(Script.WORLD, 723, 110, 813),
                        new Location(Script.WORLD, 724, 109, 813),
                        new Location(Script.WORLD, 723, 109, 813),
                        new Location(Script.WORLD, 724, 108, 813),
                        new Location(Script.WORLD, 723, 108, 813),
                }, Elevator.ElevatorAPI.FPA, 7),

        FPA_8(14, new Location(Script.WORLD, 724, 116, 812), //Fun Park Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 724, 116, 814)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 724, 117, 813),
                        new Location(Script.WORLD, 723, 117, 813),
                        new Location(Script.WORLD, 724, 116, 813),
                        new Location(Script.WORLD, 723, 116, 813),
                        new Location(Script.WORLD, 724, 115, 813),
                        new Location(Script.WORLD, 723, 115, 813),
                }, Elevator.ElevatorAPI.FPA, 8),
        SelfS_0(15, new Location(Script.WORLD, 1017, 69, 1175), //Selfstorage Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 1019, 69, 1179)}, // tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 1018, 71, 1178),
                        new Location(Script.WORLD, 1017, 71, 1178),
                        new Location(Script.WORLD, 1016, 71, 1178),
                        new Location(Script.WORLD, 1018, 70, 1178),
                        new Location(Script.WORLD, 1017, 70, 1178),
                        new Location(Script.WORLD, 1016, 70, 1178),
                        new Location(Script.WORLD, 1018, 69, 1178),
                        new Location(Script.WORLD, 1017, 69, 1178),
                        new Location(Script.WORLD, 1016, 69, 1178),
                        new Location(Script.WORLD, 1018, 68, 1178),
                        new Location(Script.WORLD, 1017, 68, 1178),
                        new Location(Script.WORLD, 1016, 68, 1178),

                }, Elevator.ElevatorAPI.SelfS, 0),

        SelfS_1(15, new Location(Script.WORLD, 1017, 61, 1175), //Self Storage GUI
                new Location[]{
                        new Location(Script.WORLD, 1019, 61, 1179)}, // tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 1018, 63, 1178),
                        new Location(Script.WORLD, 1017, 63, 1178),
                        new Location(Script.WORLD, 1016, 63, 1178),
                        new Location(Script.WORLD, 1018, 62, 1178),
                        new Location(Script.WORLD, 1017, 62, 1178),
                        new Location(Script.WORLD, 1016, 62, 1178),
                        new Location(Script.WORLD, 1018, 61, 1178),
                        new Location(Script.WORLD, 1017, 61, 1178),
                        new Location(Script.WORLD, 1016, 61, 1178),
                        new Location(Script.WORLD, 1018, 60, 1178),
                        new Location(Script.WORLD, 1017, 60, 1178),
                        new Location(Script.WORLD, 1016, 60, 1178),

                }, Elevator.ElevatorAPI.SelfS, 1), // -1


        SelfS_2(15, new Location(Script.WORLD, 1017, 52, 1175), //Selfstorage Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 1019, 52, 1179)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 1018, 54, 1178),
                        new Location(Script.WORLD, 1017, 54, 1178),
                        new Location(Script.WORLD, 1016, 54, 1178),
                        new Location(Script.WORLD, 1018, 53, 1178),
                        new Location(Script.WORLD, 1017, 53, 1178),
                        new Location(Script.WORLD, 1016, 53, 1178),
                        new Location(Script.WORLD, 1018, 52, 1178),
                        new Location(Script.WORLD, 1017, 52, 1178),
                        new Location(Script.WORLD, 1016, 52, 1178),
                        new Location(Script.WORLD, 1018, 51, 1178),
                        new Location(Script.WORLD, 1017, 51, 1178),
                        new Location(Script.WORLD, 1016, 51, 1178),

                }, Elevator.ElevatorAPI.SelfS, 2), // -2


        SelfS_3(15, new Location(Script.WORLD, 1017, 43, 1175), //Selfstorage Aussicht GUI
                new Location[]{
                        new Location(Script.WORLD, 1019, 43, 1179)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 1018, 45, 1178),
                        new Location(Script.WORLD, 1017, 45, 1178),
                        new Location(Script.WORLD, 1016, 45, 1178),
                        new Location(Script.WORLD, 1018, 44, 1178),
                        new Location(Script.WORLD, 1017, 44, 1178),
                        new Location(Script.WORLD, 1016, 44, 1178),
                        new Location(Script.WORLD, 1018, 43, 1178),
                        new Location(Script.WORLD, 1017, 43, 1178),
                        new Location(Script.WORLD, 1016, 43, 1178),
                        new Location(Script.WORLD, 1018, 42, 1178),
                        new Location(Script.WORLD, 1017, 42, 1178),
                        new Location(Script.WORLD, 1016, 42, 1178),

                }, Elevator.ElevatorAPI.SelfS, 3);


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