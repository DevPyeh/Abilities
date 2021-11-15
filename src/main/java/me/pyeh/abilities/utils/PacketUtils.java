package me.pyeh.abilities.utils;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class PacketUtils {

    public static void updateArmor(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = getEquipmentPackets(player, remove);

        for(Player other : player.getWorld().getPlayers()) {
            if(other == player) continue;

            for(PacketPlayOutEntityEquipment packet : packets) {
                sendPacket(other, packet);
            }
        }

        player.updateInventory();
    }

    private static void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet) packet);
    }

    private static Set<PacketPlayOutEntityEquipment> getEquipmentPackets(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = new HashSet<>();

        for (int slot = 1; slot < 5; slot++) {
            PacketPlayOutEntityEquipment equipment = createEquipmentPacket(player, slot, remove);
            packets.add(equipment);
        }

        return packets;
    }

    private static PacketPlayOutEntityEquipment createEquipmentPacket(Player player, int slot, boolean remove) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        return new PacketPlayOutEntityEquipment(player.getEntityId(), slot, remove ? null : entityPlayer.inventory.armor[slot - 1]);
    }

}
