package com.axeelheaven.meetup.kits;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;

public class Kits {
	
	private ItemStack[] inventory;
	private ItemStack[] armor;
	
	public Kits(final ItemStack[] inventory, final ItemStack[] armor) {
		this.inventory = inventory;
		this.armor = armor;
	}
	
	public ItemStack[] getInventory() {
		return this.inventory;
	}
	
	public ItemStack[] getArmor() {
		return this.armor;
	}
	
	public void setupContents(final Player player) {
		player.getInventory().setArmorContents(this.armor);
		player.getInventory().setContents(this.inventory);
	}
	
	public void setInventory(final ItemStack[] inventory) {
		this.inventory = inventory;
	}
	
	public void setArmor(final ItemStack[] armor) {
		this.armor = armor;
	}
	
}
