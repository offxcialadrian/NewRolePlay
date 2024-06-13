package de.newrp.Entertainment.Pets.model;

import de.newrp.Entertainment.Pets.types.PetType;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Getter
public class Pet{

    private final UUID owner;
    private final PetType type;
    private final String variant;
    private final String name;
    private final NPC npc;
    private final BukkitTask task;
    private final int health;
    @Setter
    private boolean sitting;

    public Pet(UUID owner, PetType type, String variant, String name, NPC npc, BukkitTask task, int health, boolean sitting) {
        this.owner = owner;
        this.type = type;
        this.variant = variant;
        this.name = name;
        this.npc = npc;
        this.task = task;
        this.health = health;
        this.sitting = sitting;
    }
}
