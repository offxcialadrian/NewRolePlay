package de.newrp.API;

import de.newrp.NewRoleplayMain;
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

        AEKI_3(1, new Location(Script.WORLD, 682, 92, 914), //AEKI GUI
                new Location[]{
                        new Location(Script.WORLD, 685, 92, 911)}, // Tür öffen
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

                }, Elevator.ElevatorAPI.SelfS, 3),
        KRANKENHAUS_NOTAUFNAHME(17, new Location(Script.WORLD, 347, 77, 1270), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 350, 77, 1273)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 349, 78, 1270),
                        new Location(Script.WORLD, 349, 78, 1271),
                        new Location(Script.WORLD, 349, 78, 1272),
                        new Location(Script.WORLD, 349, 77, 1270),
                        new Location(Script.WORLD, 349, 77, 1271),
                        new Location(Script.WORLD, 349, 77, 1272),
                        new Location(Script.WORLD, 349, 76, 1270),
                        new Location(Script.WORLD, 349, 76, 1271),
                        new Location(Script.WORLD, 349, 76, 1272),

                }, Elevator.ElevatorAPI.KHN, 0),


        KRANKENHAUS_NOTAUFNAHME_1(17, new Location(Script.WORLD, 347, 84, 1270), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 350, 84, 1273)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 349, 85, 1270),
                        new Location(Script.WORLD, 349, 85, 1271),
                        new Location(Script.WORLD, 349, 85, 1272),
                        new Location(Script.WORLD, 349, 84, 1270),
                        new Location(Script.WORLD, 349, 84, 1271),
                        new Location(Script.WORLD, 349, 84, 1272),
                        new Location(Script.WORLD, 349, 83, 1270),
                        new Location(Script.WORLD, 349, 83, 1271),
                        new Location(Script.WORLD, 349, 83, 1272),

                }, Elevator.ElevatorAPI.KHN, 1),
        KRANKENHAUS_VERWALTUNG_0(17, new Location(Script.WORLD, 353, 79, 1171), // GUI
                new Location[]{
                        new Location(Script.WORLD, 352, 79, 1168)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 353, 81, 1169),
                        new Location(Script.WORLD, 354, 81, 1169),
                        new Location(Script.WORLD, 355, 81, 1169),
                        new Location(Script.WORLD, 356, 81, 1169),
                        new Location(Script.WORLD, 353, 80, 1169),
                        new Location(Script.WORLD, 354, 80, 1169),
                        new Location(Script.WORLD, 355, 80, 1169),
                        new Location(Script.WORLD, 356, 80, 1169),
                        new Location(Script.WORLD, 353, 79, 1169),
                        new Location(Script.WORLD, 354, 79, 1169),
                        new Location(Script.WORLD, 355, 79, 1169),
                        new Location(Script.WORLD, 356, 79, 1169),
                        new Location(Script.WORLD, 353, 78, 1169),
                        new Location(Script.WORLD, 354, 78, 1169),
                        new Location(Script.WORLD, 355, 78, 1169),
                        new Location(Script.WORLD, 356, 78, 1169),
                }, Elevator.ElevatorAPI.KRANKENHAUS_VERWALTUNG, 0),


        KRANKENHAUS_VERWALTUNG_LINKS_1(17, new Location(Script.WORLD, 353, 87, 1171), // GUI
                new Location[]{
                        new Location(Script.WORLD, 352, 87, 1168)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 353, 89, 1169),
                        new Location(Script.WORLD, 354, 89, 1169),
                        new Location(Script.WORLD, 355, 89, 1169),
                        new Location(Script.WORLD, 356, 89, 1169),
                        new Location(Script.WORLD, 353, 88, 1169),
                        new Location(Script.WORLD, 354, 88, 1169),
                        new Location(Script.WORLD, 355, 88, 1169),
                        new Location(Script.WORLD, 356, 88, 1169),
                        new Location(Script.WORLD, 353, 87, 1169),
                        new Location(Script.WORLD, 354, 87, 1169),
                        new Location(Script.WORLD, 355, 87, 1169),
                        new Location(Script.WORLD, 356, 87, 1169),
                        new Location(Script.WORLD, 353, 86, 1169),
                        new Location(Script.WORLD, 354, 86, 1169),
                        new Location(Script.WORLD, 355, 86, 1169),
                        new Location(Script.WORLD, 356, 86, 1169),
                }, Elevator.ElevatorAPI.KRANKENHAUS_VERWALTUNG, 1),

        KRANKENHAUS_VERWALTUNG_LINKS_2(17, new Location(Script.WORLD, 353, 95, 1171), // GUI
                new Location[]{
                        new Location(Script.WORLD, 352, 95, 1168)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 353, 97, 1169),
                        new Location(Script.WORLD, 354, 97, 1169),
                        new Location(Script.WORLD, 355, 97, 1169),
                        new Location(Script.WORLD, 356, 97, 1169),
                        new Location(Script.WORLD, 353, 96, 1169),
                        new Location(Script.WORLD, 354, 96, 1169),
                        new Location(Script.WORLD, 355, 96, 1169),
                        new Location(Script.WORLD, 356, 96, 1169),
                        new Location(Script.WORLD, 353, 95, 1169),
                        new Location(Script.WORLD, 354, 95, 1169),
                        new Location(Script.WORLD, 355, 95, 1169),
                        new Location(Script.WORLD, 356, 95, 1169),
                        new Location(Script.WORLD, 353, 94, 1169),
                        new Location(Script.WORLD, 354, 94, 1169),
                        new Location(Script.WORLD, 355, 94, 1169),
                        new Location(Script.WORLD, 356, 94, 1169),
                }, Elevator.ElevatorAPI.KRANKENHAUS_VERWALTUNG, 2),
        KRANKENHAUS_VERWALTUNG_RECHTS_0(17, new Location(Script.WORLD, 310, 79, 1171), // GUI
                new Location[]{
                        new Location(Script.WORLD, 309, 79, 1168)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 310, 81, 1169),
                        new Location(Script.WORLD, 311, 81, 1169),
                        new Location(Script.WORLD, 312, 81, 1169),
                        new Location(Script.WORLD, 313, 81, 1169),
                        new Location(Script.WORLD, 310, 80, 1169),
                        new Location(Script.WORLD, 311, 80, 1169),
                        new Location(Script.WORLD, 312, 80, 1169),
                        new Location(Script.WORLD, 313, 80, 1169),
                        new Location(Script.WORLD, 310, 79, 1169),
                        new Location(Script.WORLD, 311, 79, 1169),
                        new Location(Script.WORLD, 312, 79, 1169),
                        new Location(Script.WORLD, 313, 79, 1169),
                        new Location(Script.WORLD, 310, 78, 1169),
                        new Location(Script.WORLD, 311, 78, 1169),
                        new Location(Script.WORLD, 312, 78, 1169),
                        new Location(Script.WORLD, 313, 78, 1169),
                }, Elevator.ElevatorAPI.KRANKENHAUS_VERWALTUNG_LINKS, 0),

        KRANKENHAUS_VERWALTUNG_RECHTS_1(17, new Location(Script.WORLD, 310, 87, 1171), // GUI
                new Location[]{
                        new Location(Script.WORLD, 309, 87, 1168)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 310, 89, 1169),
                        new Location(Script.WORLD, 311, 89, 1169),
                        new Location(Script.WORLD, 312, 89, 1169),
                        new Location(Script.WORLD, 313, 89, 1169),
                        new Location(Script.WORLD, 310, 88, 1169),
                        new Location(Script.WORLD, 311, 88, 1169),
                        new Location(Script.WORLD, 312, 88, 1169),
                        new Location(Script.WORLD, 313, 88, 1169),
                        new Location(Script.WORLD, 310, 87, 1169),
                        new Location(Script.WORLD, 311, 87, 1169),
                        new Location(Script.WORLD, 312, 87, 1169),
                        new Location(Script.WORLD, 313, 87, 1169),
                        new Location(Script.WORLD, 310, 86, 1169),
                        new Location(Script.WORLD, 311, 86, 1169),
                        new Location(Script.WORLD, 312, 86, 1169),
                        new Location(Script.WORLD, 313, 86, 1169),
                }, Elevator.ElevatorAPI.KRANKENHAUS_VERWALTUNG_LINKS, 1),

        KRANKENHAUS_VERWALTUNG_RECHTS_2(17, new Location(Script.WORLD, 310, 95, 1171), // GUI
                new Location[]{
                        new Location(Script.WORLD, 309, 95, 1168)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 310, 97, 1169),
                        new Location(Script.WORLD, 311, 97, 1169),
                        new Location(Script.WORLD, 312, 97, 1169),
                        new Location(Script.WORLD, 313, 97, 1169),
                        new Location(Script.WORLD, 310, 96, 1169),
                        new Location(Script.WORLD, 311, 96, 1169),
                        new Location(Script.WORLD, 312, 96, 1169),
                        new Location(Script.WORLD, 313, 96, 1169),
                        new Location(Script.WORLD, 310, 95, 1169),
                        new Location(Script.WORLD, 311, 95, 1169),
                        new Location(Script.WORLD, 312, 95, 1169),
                        new Location(Script.WORLD, 313, 95, 1169),
                        new Location(Script.WORLD, 310, 94, 1169),
                        new Location(Script.WORLD, 311, 94, 1169),
                        new Location(Script.WORLD, 312, 94, 1169),
                        new Location(Script.WORLD, 313, 94, 1169),
                }, Elevator.ElevatorAPI.KRANKENHAUS_VERWALTUNG_LINKS, 2),
        HGA_0(16, new Location(Script.WORLD, 703, 70, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 70, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 71, 874),
                        new Location(Script.WORLD, 703, 71, 874),
                        new Location(Script.WORLD, 704, 71, 874),
                        new Location(Script.WORLD, 702, 70, 874),
                        new Location(Script.WORLD, 703, 70, 874),
                        new Location(Script.WORLD, 704, 70, 874),
                        new Location(Script.WORLD, 702, 70, 874),
                        new Location(Script.WORLD, 703, 69, 874),
                        new Location(Script.WORLD, 704, 69, 874),
                        new Location(Script.WORLD, 704, 69, 874),

                }, Elevator.ElevatorAPI.HGA, 0),

        HGA_1(16, new Location(Script.WORLD, 703, 70, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 70, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 80, 874),
                        new Location(Script.WORLD, 703, 80, 874),
                        new Location(Script.WORLD, 704, 80, 874),
                        new Location(Script.WORLD, 702, 79, 874),
                        new Location(Script.WORLD, 703, 79, 874),
                        new Location(Script.WORLD, 704, 79, 874),
                        new Location(Script.WORLD, 702, 78, 874),
                        new Location(Script.WORLD, 703, 78, 874),
                        new Location(Script.WORLD, 704, 78, 874),

                }, Elevator.ElevatorAPI.HGA, 1),

        HGA_2(16, new Location(Script.WORLD, 703, 87, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 87, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 80, 874),
                        new Location(Script.WORLD, 703, 80, 874),
                        new Location(Script.WORLD, 704, 80, 874),
                        new Location(Script.WORLD, 702, 79, 874),
                        new Location(Script.WORLD, 703, 79, 874),
                        new Location(Script.WORLD, 704, 79, 874),
                        new Location(Script.WORLD, 702, 78, 874),
                        new Location(Script.WORLD, 703, 78, 874),
                        new Location(Script.WORLD, 704, 78, 874),

                }, Elevator.ElevatorAPI.HGA, 2),

        HGA_3(16, new Location(Script.WORLD, 703, 87, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 87, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 88, 874),
                        new Location(Script.WORLD, 703, 88, 874),
                        new Location(Script.WORLD, 704, 88, 874),
                        new Location(Script.WORLD, 702, 87, 874),
                        new Location(Script.WORLD, 703, 87, 874),
                        new Location(Script.WORLD, 704, 87, 874),
                        new Location(Script.WORLD, 702, 86, 874),
                        new Location(Script.WORLD, 703, 86, 874),
                        new Location(Script.WORLD, 704, 86, 874),

                }, Elevator.ElevatorAPI.HGA, 3),

        HGA_4(16, new Location(Script.WORLD, 703, 87, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 87, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 96, 874),
                        new Location(Script.WORLD, 703, 96, 874),
                        new Location(Script.WORLD, 704, 96, 874),
                        new Location(Script.WORLD, 702, 95, 874),
                        new Location(Script.WORLD, 703, 95, 874),
                        new Location(Script.WORLD, 704, 95, 874),
                        new Location(Script.WORLD, 702, 94, 874),
                        new Location(Script.WORLD, 703, 94, 874),
                        new Location(Script.WORLD, 704, 94, 874),

                }, Elevator.ElevatorAPI.HGA, 4),

        HGA_5(16, new Location(Script.WORLD, 703, 105, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 105, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 106, 874),
                        new Location(Script.WORLD, 703, 106, 874),
                        new Location(Script.WORLD, 704, 106, 874),
                        new Location(Script.WORLD, 702, 105, 874),
                        new Location(Script.WORLD, 703, 105, 874),
                        new Location(Script.WORLD, 704, 105, 874),
                        new Location(Script.WORLD, 702, 104, 874),
                        new Location(Script.WORLD, 703, 104, 874),
                        new Location(Script.WORLD, 704, 104, 874),

                }, Elevator.ElevatorAPI.HGA, 5),

        HGA_6(16, new Location(Script.WORLD, 703, 114, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 114, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 115, 874),
                        new Location(Script.WORLD, 703, 115, 874),
                        new Location(Script.WORLD, 704, 115, 874),
                        new Location(Script.WORLD, 702, 114, 874),
                        new Location(Script.WORLD, 703, 114, 874),
                        new Location(Script.WORLD, 704, 114, 874),
                        new Location(Script.WORLD, 702, 113, 874),
                        new Location(Script.WORLD, 703, 113, 874),
                        new Location(Script.WORLD, 704, 113, 874),

                }, Elevator.ElevatorAPI.HGA, 6),

        HGA_7(16, new Location(Script.WORLD, 703, 122, 877), // Haus ggü. Apotheke GUI
                new Location[]{
                        new Location(Script.WORLD, 701, 114, 873)}, // Tür öffnen
                new Location[]{
                        new Location(Script.WORLD, 702, 123, 874),
                        new Location(Script.WORLD, 703, 123, 874),
                        new Location(Script.WORLD, 704, 123, 874),
                        new Location(Script.WORLD, 702, 122, 874),
                        new Location(Script.WORLD, 703, 122, 874),
                        new Location(Script.WORLD, 704, 122, 874),
                        new Location(Script.WORLD, 702, 121, 874),
                        new Location(Script.WORLD, 703, 121, 874),
                        new Location(Script.WORLD, 704, 121, 874),

                }, Elevator.ElevatorAPI.HGA, 7),
        HRVAEKI_Links_0(20, new Location(Script.WORLD, 725, 72, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 724, 72, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 73, 908),
                        new Location(Script.WORLD, 726, 73, 908),
                        new Location(Script.WORLD, 725, 72, 908),
                        new Location(Script.WORLD, 726, 72, 908),
                        new Location(Script.WORLD, 725, 71, 908),
                        new Location(Script.WORLD, 726, 71, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_LINKS, 0),

        HRVAEKI_Links_1(20, new Location(Script.WORLD, 725, 81, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 724, 81, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 82, 908),
                        new Location(Script.WORLD, 726, 82, 908),
                        new Location(Script.WORLD, 725, 81, 908),
                        new Location(Script.WORLD, 726, 81, 908),
                        new Location(Script.WORLD, 725, 80, 908),
                        new Location(Script.WORLD, 726, 80, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_LINKS, 1),

        HRVAEKI_Links_2(20, new Location(Script.WORLD, 725, 88, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 724, 88, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 89, 908),
                        new Location(Script.WORLD, 726, 89, 908),
                        new Location(Script.WORLD, 725, 88, 908),
                        new Location(Script.WORLD, 726, 88, 908),
                        new Location(Script.WORLD, 725, 87, 908),
                        new Location(Script.WORLD, 726, 87, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_LINKS, 2),

        HRVAEKI_Links_3(20, new Location(Script.WORLD, 725, 95, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 724, 95, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 96, 908),
                        new Location(Script.WORLD, 726, 96, 908),
                        new Location(Script.WORLD, 725, 95, 908),
                        new Location(Script.WORLD, 726, 95, 908),
                        new Location(Script.WORLD, 725, 94, 908),
                        new Location(Script.WORLD, 726, 94, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_LINKS, 3),

        HRVAEKI_Links_4(20, new Location(Script.WORLD, 725, 102, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 724, 102, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 103, 908),
                        new Location(Script.WORLD, 726, 103, 908),
                        new Location(Script.WORLD, 725, 102, 908),
                        new Location(Script.WORLD, 726, 102, 908),
                        new Location(Script.WORLD, 725, 101, 908),
                        new Location(Script.WORLD, 726, 101, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_LINKS, 4),

        HRVAEKI_Links_5(20, new Location(Script.WORLD, 724, 109, 907), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 725, 109, 910)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 110, 908),
                        new Location(Script.WORLD, 726, 110, 908),
                        new Location(Script.WORLD, 725, 109, 908),
                        new Location(Script.WORLD, 726, 109, 908),
                        new Location(Script.WORLD, 725, 108, 908),
                        new Location(Script.WORLD, 726, 108, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_LINKS, 5),


        HRVAEKI_Rechts_0(20, new Location(Script.WORLD, 721, 72, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 722, 72, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 720, 73, 908),
                        new Location(Script.WORLD, 721, 73, 908),
                        new Location(Script.WORLD, 720, 72, 908),
                        new Location(Script.WORLD, 721, 72, 908),
                        new Location(Script.WORLD, 720, 71, 908),
                        new Location(Script.WORLD, 721, 71, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_RECHTS, 0),

        HRVAEKI_Rechts_1(20, new Location(Script.WORLD, 721, 81, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 722, 81, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 720, 82, 908),
                        new Location(Script.WORLD, 721, 82, 908),
                        new Location(Script.WORLD, 720, 81, 908),
                        new Location(Script.WORLD, 721, 81, 908),
                        new Location(Script.WORLD, 720, 80, 908),
                        new Location(Script.WORLD, 721, 80, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_RECHTS, 1),

        HRVAEKI_Rechts_2(20, new Location(Script.WORLD, 721, 88, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 722, 88, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 720, 89, 908),
                        new Location(Script.WORLD, 721, 89, 908),
                        new Location(Script.WORLD, 720, 88, 908),
                        new Location(Script.WORLD, 721, 88, 908),
                        new Location(Script.WORLD, 720, 87, 908),
                        new Location(Script.WORLD, 721, 87, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_RECHTS, 2),

        HRVAEKI_Rechts_3(20, new Location(Script.WORLD, 721, 95, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 722, 95, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 720, 96, 908),
                        new Location(Script.WORLD, 721, 96, 908),
                        new Location(Script.WORLD, 720, 95, 908),
                        new Location(Script.WORLD, 721, 95, 908),
                        new Location(Script.WORLD, 720, 94, 908),
                        new Location(Script.WORLD, 721, 94, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_RECHTS, 3),

        HHRVAEKI_Rechts_4(20, new Location(Script.WORLD, 721, 102, 910), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 722, 102, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 103, 908),
                        new Location(Script.WORLD, 726, 103, 908),
                        new Location(Script.WORLD, 725, 102, 908),
                        new Location(Script.WORLD, 726, 102, 908),
                        new Location(Script.WORLD, 725, 101, 908),
                        new Location(Script.WORLD, 726, 101, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_RECHTS, 4),

        HRVAEKI_Recht_5(20, new Location(Script.WORLD, 724, 109, 907), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 722, 109, 907)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 725, 110, 908),
                        new Location(Script.WORLD, 726, 110, 908),
                        new Location(Script.WORLD, 725, 109, 908),
                        new Location(Script.WORLD, 726, 109, 908),
                        new Location(Script.WORLD, 725, 108, 908),
                        new Location(Script.WORLD, 726, 108, 908),
                }, Elevator.ElevatorAPI.HRVAEKI_RECHTS, 5),

        BKA_ETAGE_0(21, new Location(Script.WORLD, 822, 58, 1027), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 819, 58, 1023)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 823, 57, 1024),
                        new Location(Script.WORLD, 822, 57, 1024),
                        new Location(Script.WORLD, 821, 57, 1024),
                        new Location(Script.WORLD, 820, 57, 1024),
                        new Location(Script.WORLD, 823, 58, 1024),
                        new Location(Script.WORLD, 822, 58, 1024),
                        new Location(Script.WORLD, 821, 58, 1024),
                        new Location(Script.WORLD, 820, 58, 1024),
                        new Location(Script.WORLD, 823, 59, 1024),
                        new Location(Script.WORLD, 822, 59, 1024),
                        new Location(Script.WORLD, 821, 59, 1024),
                        new Location(Script.WORLD, 820, 59, 1024),
                }, Elevator.ElevatorAPI.BKA, 0),

        BKA_ETAGE_1(21, new Location(Script.WORLD, 822, 65, 1027), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 819, 65, 1023)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 823, 64, 1024),
                        new Location(Script.WORLD, 822, 64, 1024),
                        new Location(Script.WORLD, 821, 64, 1024),
                        new Location(Script.WORLD, 820, 64, 1024),
                        new Location(Script.WORLD, 823, 65, 1024),
                        new Location(Script.WORLD, 822, 65, 1024),
                        new Location(Script.WORLD, 821, 65, 1024),
                        new Location(Script.WORLD, 820, 65, 1024),
                        new Location(Script.WORLD, 823, 66, 1024),
                        new Location(Script.WORLD, 822, 66, 1024),
                        new Location(Script.WORLD, 821, 66, 1024),
                        new Location(Script.WORLD, 820, 66, 1024),
                }, Elevator.ElevatorAPI.BKA, 1),

        BKA_ETAGE_2(21, new Location(Script.WORLD, 822, 72, 1027), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 819, 72, 1023)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 823, 71, 1024),
                        new Location(Script.WORLD, 822, 71, 1024),
                        new Location(Script.WORLD, 821, 71, 1024),
                        new Location(Script.WORLD, 820, 71, 1024),
                        new Location(Script.WORLD, 823, 72, 1024),
                        new Location(Script.WORLD, 822, 72, 1024),
                        new Location(Script.WORLD, 821, 72, 1024),
                        new Location(Script.WORLD, 820, 72, 1024),
                        new Location(Script.WORLD, 823, 73, 1024),
                        new Location(Script.WORLD, 822, 73, 1024),
                        new Location(Script.WORLD, 821, 73, 1024),
                        new Location(Script.WORLD, 820, 73, 1024),
                }, Elevator.ElevatorAPI.BKA, 2),

        BKA_ETAGE_3(21, new Location(Script.WORLD, 822, 81, 1027), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 819, 81, 1023)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 823, 80, 1024),
                        new Location(Script.WORLD, 822, 80, 1024),
                        new Location(Script.WORLD, 821, 80, 1024),
                        new Location(Script.WORLD, 820, 80, 1024),
                        new Location(Script.WORLD, 823, 81, 1024),
                        new Location(Script.WORLD, 822, 81, 1024),
                        new Location(Script.WORLD, 821, 81, 1024),
                        new Location(Script.WORLD, 820, 81, 1024),
                        new Location(Script.WORLD, 823, 82, 1024),
                        new Location(Script.WORLD, 822, 82, 1024),
                        new Location(Script.WORLD, 821, 82, 1024),
                        new Location(Script.WORLD, 820, 82, 1024),
                }, Elevator.ElevatorAPI.BKA, 3),

        BKA_ETAGE_4(21, new Location(Script.WORLD, 822, 89, 1027), //AEKI GUI HAUS Rechts v. AEKI
                new Location[]{
                        new Location(Script.WORLD, 819, 89, 1023)}, // Tür öffen
                new Location[]{
                        new Location(Script.WORLD, 823, 88, 1024),
                        new Location(Script.WORLD, 822, 88, 1024),
                        new Location(Script.WORLD, 821, 88, 1024),
                        new Location(Script.WORLD, 820, 88, 1024),
                        new Location(Script.WORLD, 823, 89, 1024),
                        new Location(Script.WORLD, 822, 89, 1024),
                        new Location(Script.WORLD, 821, 89, 1024),
                        new Location(Script.WORLD, 820, 89, 1024),
                        new Location(Script.WORLD, 823, 90, 1024),
                        new Location(Script.WORLD, 822, 90, 1024),
                        new Location(Script.WORLD, 821, 90, 1024),
                        new Location(Script.WORLD, 820, 90, 1024),
                }, Elevator.ElevatorAPI.BKA, 4);



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
                        }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 5);
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
                }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 5);
            }
        }
    }
}