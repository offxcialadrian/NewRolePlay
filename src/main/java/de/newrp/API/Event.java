package de.newrp.API;

import org.bukkit.Location;

public enum Event {
    NO_DAMAGE("no_damage", null),
    DOUBLE_XP("doublexp", null),
    DOUBLE_XP_WEEKEND("doublexp_weekend", null),
    VOTE("vote_event", null),
    PURGE("purge", null),
    FRIEND_WEEK("friend_week", null),
    TRIPPLE_XP("triplexp", null);

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
