package net.eq2online.permissions;

import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class ReplicatedPermissionsContainer implements Serializable {

    private static final long serialVersionUID = -764940324881984960L;
    public String modName = "all";
    public Float modVersion = 0f;
    public final Set<String> permissions = new TreeSet<>();
    public long remoteCacheTimeSeconds = 600L;
    public static final String CHANNEL = "PERMISSIONSREPL";

    public ReplicatedPermissionsContainer() {

    }

    public ReplicatedPermissionsContainer(String modName, Float modVersion, Collection<String> permissions) {
        this.modName = modName;
        this.modVersion = modVersion;
        this.permissions.addAll(permissions);
    }

    public void addAll(Collection<String> permissions) {
        this.permissions.addAll(permissions);
    }

    public void sanitise() {
        if (modName == null || modName.length() < 1) modName = "all";
        if (modVersion == null || modVersion < 0f) modVersion = 0f;
        if (remoteCacheTimeSeconds < 0) this.remoteCacheTimeSeconds = 600L;
    }

    public byte[] getBytes() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            new ObjectOutputStream(stream).writeObject(this);
            return stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static ReplicatedPermissionsContainer fromBytes(byte[] data) {
        try {
            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(data));
            return (ReplicatedPermissionsContainer) stream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

}