package de.newrp.API;

import org.bukkit.Location;

public enum Event {
    LASERTAG("lasertag", Script.setDirection(new Location(Script.WORLD, 72, 69, -284), Direction.EAST)),
    DOUBLE_XP("doublexp", null),
    DOUBLE_XP_WEEKEND("doublexp_weekend", null),
    VOTE("vote_event", null),
    PURGE("purge", null),
    FRIEND_WEEK("friend_week", null),
    TRIPPLE_XP("tripplexp", null);

    private final String name;
    private final Location loc;

    Event(String name, Location loc) {
        this.name = name;
        this.loc = loc;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.loc;
    }

    public static Event getEvent(String name) {
        for(Event e : Event.values()) {
            if(e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }
}
