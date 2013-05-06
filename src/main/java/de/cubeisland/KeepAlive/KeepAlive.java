package de.cubeisland.KeepAlive;

import java.lang.reflect.Method;
import java.util.*;

import org.bukkit.plugin.java.JavaPlugin;

public class KeepAlive extends JavaPlugin {
    private Timer timer;

    private class KeepAliveTimer extends TimerTask {
        private final Object playerList;
        private final Random random = new Random();

        public KeepAliveTimer() throws Exception {
            Object server = getServer().getClass().getMethod("getServer").invoke(getServer());
            playerList = server.getClass().getMethod("getPlayerList").invoke(server);
        }

        @Override
        public void run() {
            try {
                List players = (List) playerList.getClass().getField("players").get(playerList);
                for (Object player : players) {
                    Object playerConnection = player.getClass().getField("playerConnection").get(player);
                    Object packet = getKeepAlivePacket(playerConnection);
                    playerConnection.getClass().getMethod("sendPacket", packet.getClass().getSuperclass()).invoke(playerConnection, packet);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private Object getKeepAlivePacket(Object playerConnection) throws Exception {
            Method[] methods = playerConnection.getClass().getMethods();
            for (Method method : methods) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1) {
                    Class<?> first = params[0];
                    String name = first.getName();
                    if (name.contains("Packet0KeepAlive")) return first.getConstructor(int.class).newInstance(random.nextInt());
                }
            }
            return null;
        }
    }

    @Override
    public void onDisable() {
        timer.cancel();
        timer = null;
    }

    @Override
    public void onEnable() {
        timer = new Timer("keepAliveTimer");
        try {
            timer.schedule(new KeepAliveTimer(), 5 * 1000, 5 * 1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
