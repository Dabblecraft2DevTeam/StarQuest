package com.starquestminecraft.bukkit.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.starquestminecraft.bukkit.SQBase;

public class BungeeUtil {
	
	public static void sendPlayer(final Player player, final String server, final String world, final int x, final int y, final int z){
		sendPlayer(player, server, world, x, y, z, false);
	}

	public static void sendPlayer(final Player player, final String server, final String world, final int x, final int y, final int z, final boolean is_bedspawn) {

		sendPlayerCoordinateData(player, server, world, x, y, z, is_bedspawn);

		connectPlayer(player, server);

	}

	public static void connectPlayer(final Player player, final String server) {

		ByteArrayOutputStream b = new ByteArrayOutputStream();

        try(DataOutputStream out = new DataOutputStream(b)) {
            
			out.writeUTF("Connect");
			out.writeUTF(server);

		}
        catch (IOException ex) {
			ex.printStackTrace();
		}

		player.sendPluginMessage(SQBase.getInstance(), "BungeeCord", b.toByteArray());

	}

	private static void sendPlayerCoordinateData(final Player player, final String server, final String world, final int x, final int y, final int z, final boolean is_bedspawn) {

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		// send player data of where they should be teleported to
		try {
			out.writeUTF("Forward");
			out.writeUTF(server);
			out.writeUTF("movecraftPlayer");

			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);

			writePlayerData(msgout, player, server, world, x, y, z, is_bedspawn);

			byte[] outbytes = msgbytes.toByteArray();
			out.writeShort(outbytes.length);
			out.write(outbytes);
			player.sendPluginMessage(SQBase.getInstance(), "BungeeCord", b.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static void writePlayerData(final DataOutputStream out, final Player player, final String server, final String world, final int x, final int y, final int z) throws IOException {
		writePlayerData(out, player, server, world, x, y, z, false);
	}

	public static void writePlayerData(final DataOutputStream out, final Player player, final String server, final String world, final int x, final int y, final int z, boolean is_bedspawn) throws IOException {

		Location loc = player.getLocation();

		out.writeUTF(world);
		out.writeUTF(player.getUniqueId().toString());
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		out.writeDouble(loc.getYaw());
		out.writeDouble(loc.getPitch());

		InventoryUtil.writePlayer(out, player);

		out.writeInt(player.getGameMode().ordinal());
		out.writeBoolean(is_bedspawn);

	}

}
