package de.cubeisland.KeepAlive;

import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.Packet0KeepAlive;
import net.minecraft.server.v1_5_R3.PlayerList;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class KeepAlive extends JavaPlugin
{
    private Timer timer;

    @Override
    public void onEnable()
    {
        timer = new Timer("keepAliveTimer");
        timer.schedule(new KeepAliveTimer() , 5 * 1000, 5 * 1000);
    }

    @Override
    public void onDisable()
    {
        timer.cancel();
        timer = null;
    }

    private class KeepAliveTimer extends TimerTask
    {
        private final PlayerList playerList;
        private final Random random;

        public KeepAliveTimer()
        {
            this.playerList = ((CraftServer)getServer()).getServer().getPlayerList();
            this.random = new Random();
        }

        @Override
        public void run()
        {
            for (Object player : this.playerList.players)
            {
                if (player instanceof EntityPlayer)
                {
                    ((EntityPlayer)player).playerConnection.sendPacket(new Packet0KeepAlive(random.nextInt()));
                }
            }
        }
    }
}
