package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static final Class<?> CLASS_CRAFTITEMSTACK = Reflection.getCraftBukkitClass("inventory.CraftItemStack");
    public static final Method METHOD_ASNMSCOPY = Reflection.getMethod(CLASS_CRAFTITEMSTACK, "asNMSCopy");

    public static void sendPacket(Player p, Object packet) {
        try {
            Object nmsPlayer = getNMSPlayer(p);
            Object connection = nmsPlayer.getClass()
                    .getField("playerConnection").get(nmsPlayer);
            connection.getClass()
                    .getMethod("sendPacket", getNMSClass("Packet"))
                    .invoke(connection, packet);
        } catch (Exception e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Field getField(Class<?> clazz, String field) {
        Field re = null;
        try {
            re = clazz.getDeclaredField(field);
        } catch (NoSuchFieldException | SecurityException e) {
            NewRoleplayMain.handleError(e);
        }
        return re;
    }

    public static void sendPacket(Player p, String packetName,
                                  Class<?>[] parameterclass, Object... parameters) {
        try {
            Object nmsPlayer = getNMSPlayer(p);
            Object connection = nmsPlayer.getClass()
                    .getField("playerConnection").get(nmsPlayer);
            Object packet = Class
                    .forName(
                            nmsPlayer.getClass().getPackage().getName() + "."
                                    + packetName)
                    .getConstructor(parameterclass).newInstance(parameters);
            connection.getClass()
                    .getMethod("sendPacket", getNMSClass("Packet"))
                    .invoke(connection, packet);
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }

    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1) + ".";
    }

    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
        return clazz;
    }

    public static Class<?> getCraftBukkitClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
        return clazz;
    }

    public static Object asNMSCopy(ItemStack i) {
        Object re = null;

        try {
            re = METHOD_ASNMSCOPY.invoke(null, i);
        } catch (IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException e) {
            NewRoleplayMain.handleError(e);
        }

        return re;
    }

    public static Field getField(Field f) {
        f.setAccessible(true);
        return f;
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... param) {
        Constructor<?> re = null;
        try {
            re = clazz.getConstructor(param);
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
        return re;
    }

    public static Object getHandle(Entity entity) {
        Object object = null;

        try {
            object = getMethod(entity.getClass(), "getHandle").invoke(entity);
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }

        return object;
    }

    public static Object getNMSPlayer(Player player) {
        return getHandle(player);
    }

    public static Method getMethod(Class<?> c, String methodName) {
        Method returnMethod = null;

        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                returnMethod = m;
            }
        }

        return returnMethod;
    }
}
