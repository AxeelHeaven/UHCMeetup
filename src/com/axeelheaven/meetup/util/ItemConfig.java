package com.axeelheaven.meetup.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.axeelheaven.meetup.Main;

public class ItemConfig {
	
	private static ItemConfig instance;
	private Main plugin;
	
	public ItemConfig(final Main plugin) {
		this.plugin = plugin;
		instance = this;
	}
	
	public static ItemConfig get() {
		return instance;
	}
	
	public ItemStack item(final Material material, final int amount, final int data, final String name) {
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(plugin.text(name));
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack item(final Material material, final int amount, final int data, final String name, final List<String> lore) {
		ItemStack item = new ItemStack(material, amount, (short)data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(plugin.text(name));
		if(!lore.isEmpty()) {
			List<String> lore2 = new ArrayList<String>();
			for(final String string : lore) {
				lore2.add(this.plugin.text(string));
			}
			meta.setLore(lore2);
		}
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack item(final String path) {
		final String[] itema = this.plugin.getConfig().getString("items_settings." + path + ".item").split(";");
		ItemStack item;
		if(this.isNumeric(itema[0])) {
			item = new ItemStack(Material.getMaterial(Integer.valueOf(itema[0])), Integer.valueOf(itema[1]), Short.valueOf(itema[2]));
		} else {
			item = new ItemStack(Material.getMaterial(itema[0].toUpperCase()), Integer.valueOf(itema[1]), Short.valueOf(itema[2]));
		}
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.plugin.text(this.plugin.getConfig().getString("items_settings." + path + ".name")));
		
		if(this.plugin.getConfig().getStringList("items_settings." + path + ".lore") != null) {
			List<String> list = new ArrayList<>();
			for(final String s : this.plugin.getConfig().getStringList("items_settings." + path + ".lore")) {
				list.add(this.plugin.text(s));
			}
			meta.setLore(list);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	private boolean isNumeric(final String string) {
		try {
			Integer.valueOf(string);
			return true;
		} catch(final Exception e) {
			
		}
		return false;
	}
	
	
	
}
