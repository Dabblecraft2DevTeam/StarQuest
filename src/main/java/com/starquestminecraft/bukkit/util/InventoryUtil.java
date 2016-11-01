package com.starquestminecraft.bukkit.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.starquestminecraft.bukkit.cardboardbox.CardboardBox;
import com.starquestminecraft.bukkit.cardboardbox.Knapsack;

public class InventoryUtil {

    private InventoryUtil() {

    }

    public static void giveOrDropItems(final Player player, final ItemStack... items) {

        Map<Integer, ItemStack> overflow = player.getInventory().addItem(items);

        if(!overflow.isEmpty()) {

            Location loc = player.getLocation();

            for(ItemStack overflow_item : overflow.values()) {
                loc.getWorld().dropItemNaturally(loc, overflow_item);
            }

        }

    }

    public static void writePlayer(DataOutputStream os, Player p) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(new Knapsack(p));
            out.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static Knapsack readPlayer(DataInputStream ips) {
        Knapsack playerKnap = null;
        try {
            ObjectInputStream in = new ObjectInputStream(ips);
            playerKnap = (Knapsack)in.readObject();
            in.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return playerKnap;
    }

    public static void writeInventory(DataOutputStream os, Inventory inventory) {
        
        try {

            os.writeInt(inventory.getSize());

            try(ObjectOutputStream out = new ObjectOutputStream(os)) {

                ItemStack[] is = inventory.getContents();
                CardboardBox[] cardboardBoxes = new CardboardBox[is.length];

                for(int i = 0; i < is.length; i++) {
                    cardboardBoxes[i] = pack(is[i]);
                }

                out.writeObject(cardboardBoxes);

            }

        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

    }

    public static Inventory readInventory(DataInputStream din, InventoryType type) {

        int length = 27;

        try {
            length = din.readInt();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        Inventory inv = getInventory(length, type);

        try(ObjectInputStream in = new ObjectInputStream(din)) {

            CardboardBox[] cardboardBoxes = (CardboardBox[])in.readObject();
            ItemStack[] is = new ItemStack[cardboardBoxes.length];

            for(int i = 0; i < cardboardBoxes.length; i++) {
                is[i] = unpack(cardboardBoxes[i]);
            }

            inv.setContents(is);

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return inv;
    }

    private static CardboardBox pack(ItemStack item) {

        if(item != null) {
            return new CardboardBox(item);
        }

        return null;

    }

    private static ItemStack unpack(CardboardBox box) {

        if(box != null) {
            return box.unbox();
        }

        return null;

    }

    private static Inventory getInventory(int length, InventoryType type) {

        if(type != null) {
            return Bukkit.createInventory(null, type);
        }

        return Bukkit.createInventory(null, length);

    }

    public static void wipePlayerInventory(final Player player) {

		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		for(PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

	}

}
