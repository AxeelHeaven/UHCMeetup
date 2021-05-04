package com.axeelheaven.meetup.nms.versions;

import java.lang.reflect.Field;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.nms.NMS;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;


public class V1_8_R3 implements NMS {

	@Override
	public void BiomeSwap(final Main main) {
		Field biomesField = null;
		try {
			biomesField = BiomeBase.class.getDeclaredField("biomes");
			biomesField.setAccessible(true);
			if (biomesField.get(null) instanceof BiomeBase[]) {
				BiomeBase[] biomes = (BiomeBase[])biomesField.get(null);
				for(int i = 0; i < 40; i++) {
					biomes[BiomeBase.getBiome(i).id] = this.getRandomBiome();
				}
				biomesField.set(null, biomes);
			}
		}catch (Exception e) {}
	}

	@Override
	public void moutPlayer(final Player player) {
		final Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
		((LivingEntity) horse).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		this.freezeEntity(horse);
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.setPassenger(player);
	}

	@Override
	public void sendTitle(final Player player, String title, String subtitle, final int fadeIn, final int stay, final int fadeOut){
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
     
        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        connection.sendPacket(packetPlayOutTimes);
        if (subtitle != null){
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }
        if (title != null){
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }
    }

	@Override
	public void sendActionBar(final Player player, final String string){
		IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', string) + "\"}");
	    PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, (byte)2);
	    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
	
	}
	
	private void freezeEntity(final org.bukkit.entity.Entity en) {
	    Entity nmsEn = ((CraftEntity) en).getHandle();
	    NBTTagCompound compound = new NBTTagCompound();
	    nmsEn.c(compound);
	    compound.setByte("NoAI", (byte) 1);
	    nmsEn.f(compound);
	}
	
	private Random random = new Random();
	
	private BiomeBase getRandomBiome() {
		if(this.random.nextInt(100) >= 50) {
			return BiomeBase.PLAINS;
		} else {
			return BiomeBase.DESERT;
		}
	}
	
}