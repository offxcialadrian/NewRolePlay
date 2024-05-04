package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Shop.ShopItem;
import de.newrp.Shop.Shops;
import de.newrp.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class Hotel {

    public static String PREFIX = "§8[§eHotel§8] §e" + Messages.ARROW + " §7";

    public static boolean hasHotelRoom(Player p) {
        return Script.getInt(p, "hotel", "room")>0;
    }

    public static Rooms getHotelRoom(Player p) {
        return Rooms.getRoomById(Script.getInt(p, "hotel", "room"));
    }


    public enum RoomType {
        EINZELZIMMER(1, "Einzelzimmer", ShopItem.EINZELZIMMER, 1, 5),
        DOPPELZIMMER(2, "Doppelzimmer", ShopItem.DOPPELZIMMER, 2, 5),
        MEHRBETTZIMMER(3, "Mehrbettzimmer", ShopItem.MEHRBETTZIMMER, 4, 5),
        SUITE(4, "Suite", ShopItem.SUITE, 1, 20),
        EXECUTIVE_SUITE(5, "Executive Suite", ShopItem.EXECUTIVE_SUITE, 1, 10),
        DELUXE_ZIMMER(6, "Deluxe Zimmer", ShopItem.DELUXE_ZIMMER, 1,30),
        PRAESIDENTEN_SUITE(7, "Präsidenten Suite", ShopItem.PRAESIDENTEN_SUITE, 1,40);

        private final int id;
        private final String name;
        private final ShopItem item;
        private final int capacity;
        private final int maxdistance;

        RoomType(int id, String name, ShopItem item, int capacity, int maxdistance) {
            this.id = id;
            this.name = name;
            this.item = item;
            this.capacity = capacity;
            this.maxdistance = maxdistance;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ShopItem getItem() {
            return item;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getMaxDistance() {
            return maxdistance;
        }

        public static RoomType getRoomById(int id) {
            for (RoomType room : values()) {
                if (room.getId() == id) {
                    return room;
                }
            }
            return null;
        }

        public static RoomType getRoomByName(String name) {
            for (RoomType room : values()) {
                if (room.getName().equalsIgnoreCase(name)) {
                    return room;
                }
            }
            return null;
        }
    }

    public enum Hotels {
        MOTEL99("Motel 99", new Location(null, 0, 0, 0), Shops.MOTEL99),
        HOTELCALIFORNIA("Hotel California", new Location(null, 0, 0, 0), Shops.HOTEL_CALIFORNIA);

        private final String name;
        private final Location location;
        private final Shops shop;

        Hotels(String name, Location location, Shops shop) {
            this.name = name;
            this.location = location;
            this.shop = shop;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }

        public Shops getShop() {
            return shop;
        }

        public static Hotels getHotelByName(String name) {
            for (Hotels hotel : values()) {
                if (hotel.getName().equalsIgnoreCase(name)) {
                    return hotel;
                }
            }
            return null;
        }

        public static Hotels getHotelByLocation(Location location) {
            for (Hotels hotel : values()) {
                if (hotel.getLocation().equals(location)) {
                    return hotel;
                }
            }
            return null;
        }

        public static Hotels getHotelByShop(Shops shop) {
            for (Hotels hotel : values()) {
                if (hotel.getShop().equals(shop)) {
                    return hotel;
                }
            }
            return null;
        }

        public List<Rooms> getRooms() {
            for(Rooms room : Rooms.values()) {
                if(room.getHotel().equals(this)) {
                    return List.of(room);
                }
            }
            return null;
        }

        public List<Rooms> getRentedRooms() {
            for(Rooms room : Rooms.values()) {
                try(Statement stmt = Main.getConnection().createStatement()) {
                    if(stmt.executeQuery("SELECT * FROM hotel WHERE room = " + room.getID()).next()) {
                        return List.of(room);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public Rooms getFreeRoom(RoomType type) {
            for(Rooms room : Rooms.values()) {
                if(room.getHotel().equals(this) && room.getType().equals(type)) {
                    if(!room.isRented()) {
                        return room;
                    }
                }
            }
            return null;
        }

    }

    public enum Rooms {
        ROOM1_MOTEL(1, "Zimmernummer 1", RoomType.MEHRBETTZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 802, 64, 1198, 278.9744f, -7.9327602f)),
        ROOM2_MOTEL(2, "Zimmernummer 2", RoomType.DOPPELZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 792, 64, 1200, 177.24077f, 17.786837f)),
        ROOM3_MOTEL(3, "Zimmernummer 3", RoomType.DOPPELZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 792, 69, 1200, 159.84192f, 5.7868876f)),
        ROOM4_MOTEL(4, "Zimmernummer 4", RoomType.DOPPELZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 801, 69, 1198, 236.93835f, 10.736907f)),
        ROOM5_MOTEL(5, "Zimmernummer 5", RoomType.EINZELZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 811, 69, 1231, 3.277954f, 11.464415f)),
        ROOM6_MOTEL(6, "Zimmernummer 6", RoomType.EINZELZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 807, 69, 1222, 94.32703f, 12.664422f)),
        ROOM7_MOTEL(7, "Zimmernummer 7", RoomType.EINZELZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 807, 69, 1212, 83.67499f, 10.864426f)),
        ROOM8_MOTEL(8, "Zimmernummer 8", RoomType.EINZELZIMMER, Hotels.MOTEL99, new Location(Script.WORLD, 807, 69, 1203, 90.12225f, 8.914443f)),
        ROOM1_CALIFORNIA(9, "Zimmernummer 1", RoomType.PRAESIDENTEN_SUITE, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 267, 79, 959, -222.4063f, 15.135571f)),
        ROOM2_CALIFORNIA(10, "Zimmernummer 2", RoomType.SUITE, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 278, 83, 980, -176.14644f, 41.804695f)),
        ROOM3_CALIFORNIA(11, "Zimmernummer 3", RoomType.SUITE, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 261, 79, 984, -271.99426f, -3.3452861f)),
        ROOM4_CALIFORNIA(12, "Zimmernummer 4", RoomType.EXECUTIVE_SUITE, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 270, 91, 977, -198.38257f, 6.660818f)),
        ROOM5_CALIFORNIA(13, "Zimmernummer 5", RoomType.EXECUTIVE_SUITE, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 270, 91, 998, -354.98135f, 9.960834f)),
        ROOM6_CALIFORNIA(14, "Zimmernummer 6", RoomType.SUITE, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 270, 91, 965, -77.010284f, 4.0074234f)),
        ROOM7_CALIFORNIA(15, "Zimmernummer 7", RoomType.SUITE, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 266, 91, 945, -261.50974f, 15.8574505f)),
        ROOM8_CALIFORNIA(16, "Zimmernummer 8", RoomType.DELUXE_ZIMMER, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 273, 98, 978, -17.759277f, 8.807517f)),
        ROOM9_CALIFORNIA(17, "Zimmernummer 9", RoomType.DELUXE_ZIMMER, Hotels.HOTELCALIFORNIA, new Location(Script.WORLD, 260, 99, 958, -211.21645f, 13.060703f));


        private final int id;
        private final String name;
        private final RoomType type;
        private final Hotels hotel;
        private final Location location;

        Rooms(int id, String name, RoomType type, Hotels hotel, Location location) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.hotel = hotel;
            this.location = location;
        }

        public int getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public RoomType getType() {
            return type;
        }

        public Hotels getHotel() {
            return hotel;
        }

        public Location getLocation() {
            return location;
        }

        public int getPrice() {
            return type.getItem().getPrice(this.getHotel().getShop());
        }

        public static Rooms getRoomById(int id) {
            for (Rooms room : values()) {
                if (room.getID() == id) {
                    return room;
                }
            }
            return null;
        }

        public static Rooms getRoomByName(String name) {
            for (Rooms room : values()) {
                if (room.getName().equalsIgnoreCase(name)) {
                    return room;
                }
            }
            return null;
        }

        public static Rooms getRoomByType(RoomType type) {
            for (Rooms room : values()) {
                if (room.getType().equals(type)) {
                    return room;
                }
            }
            return null;
        }

        public static Rooms getRoomByHotel(Hotels hotel) {
            for (Rooms room : values()) {
                if (room.getHotel().equals(hotel)) {
                    return room;
                }
            }
            return null;
        }

        public static Rooms getRoomByLocation(Location location) {
            for (Rooms room : values()) {
                if (room.getLocation().equals(location)) {
                    return room;
                }
            }
            return null;
        }

        public static Rooms getRoomByPrice(int price) {
            for (Rooms room : values()) {
                if (room.getPrice() == price) {
                    return room;
                }
            }
            return null;
        }

        public boolean isRented() {
            try (Statement stmt = Main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM HOTEL WHERE room='" + getID() + "'")) {
                while (rs.next()) {
                    return rs.getInt("total")>=getType().getCapacity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    }

    public static boolean isInHotelRoom(Player p) {
        if(getHotelRoom(p) == null) return false;
        return getHotelRoom(p).getLocation().distance(p.getLocation()) <= getHotelRoom(p).getType().getMaxDistance();
    }

}
