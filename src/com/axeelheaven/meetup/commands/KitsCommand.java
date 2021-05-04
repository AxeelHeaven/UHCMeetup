package com.axeelheaven.meetup.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.kits.Kits;
import com.axeelheaven.meetup.util.InventoryUtil;
import com.axeelheaven.meetup.util.ItemConfig;

public class KitsCommand implements CommandExecutor {

	private Main plugin;
	
	public KitsCommand(final Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		final Player player = (Player) sender;
		
		if(!sender.hasPermission("hmeetup.kits")){
			sender.sendMessage(plugin.text(player, plugin.getLang().getString("no_permissions")));
			return true;
		}
		
		if(args.length < 1) {
			sender.sendMessage(plugin.text(""));
			sender.sendMessage(plugin.text("&aUse '/kits load <0-" + (this.plugin.getKitsManager().getKits().size()-1) + ">' to edit a kit."));
			sender.sendMessage(plugin.text("&aUse '/kits save &7<optional number>&a' to save a kit."));
			sender.sendMessage(plugin.text(""));
			return true;
		}
		
		if(args[0].equalsIgnoreCase("load")) {
			if(args.length < 2) {
				sender.sendMessage(plugin.text("&c/kits load <0-" + (this.plugin.getKitsManager().getKits().size()-1) + ">"));
				return true;
			}
			if(!this.isNumeric(args[1])) {
				sender.sendMessage(plugin.text("&cPlease use a valid number."));
				return true;
			}
			if(Integer.parseInt(args[1]) > this.plugin.getKitsManager().getKits().size()) {
				sender.sendMessage(plugin.text("&cChoose a number from 0 to " + this.plugin.getKitsManager().getKits().size()));
				return true;
			}
			
			this.plugin.getKitsManager().getEditing().add(player);
			final Kits kit = this.plugin.getKitsManager().getKits().get(Integer.valueOf(args[1]));
			player.getInventory().setContents(kit.getInventory());
			player.getInventory().setArmorContents(kit.getArmor());
			if(!this.plugin.getKitsManager().getEditing().contains(player)) {
				this.plugin.getKitsManager().getEditing().add(player);
			}
			sender.sendMessage(plugin.text("&aTo save the kit correctly use /kits save"));
			return true;
		}
		
		if(args[0].equalsIgnoreCase("save")) {
			if(args.length < 2) {
				final File file = new File(this.plugin.getDataFolder() + "/kits", ("kit" + (this.plugin.getKitsManager().getKits().size()) + ".yml"));
        		if(!file.exists()) {
        			try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
				final Kits kit = new Kits(player.getInventory().getContents(), player.getInventory().getArmorContents());
				this.plugin.getKitsManager().getKits().add(kit);
        		final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				config.set("inventory", InventoryUtil.toString(player.getInventory().getContents()));
				config.set("armor", InventoryUtil.toString(player.getInventory().getArmorContents()));
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.vote_scenarios.slot")-1, ItemConfig.get().item("waitting.vote_scenarios"));
				player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.stats.slot")-1, ItemConfig.get().item("waitting.stats"));
				player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.leave.slot")-1, ItemConfig.get().item("waitting.leave"));
				sender.sendMessage(plugin.text("&aNew kit added correctly, total kits " + this.plugin.getKitsManager().getKits().size()));
			} else {
				if(!this.isNumeric(args[1])) {
					sender.sendMessage(plugin.text("&cPlease use a valid number."));
					return true;
				}
				final Kits kit = this.plugin.getKitsManager().getKits().get(Integer.valueOf(args[1]).intValue());
				kit.setArmor(player.getInventory().getArmorContents());
				kit.setInventory(player.getInventory().getContents());

				final File file = new File(this.plugin.getDataFolder() + "/kits", ("kit" + args[1] + ".yml"));
        		final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				config.set("inventory", InventoryUtil.toString(player.getInventory().getContents()));
				config.set("armor", InventoryUtil.toString(player.getInventory().getArmorContents()));
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(this.plugin.getKitsManager().getEditing().contains(player)) {
					this.plugin.getKitsManager().getEditing().remove(player);
				}
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.vote_scenarios.slot")-1, ItemConfig.get().item("waitting.vote_scenarios"));
				player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.stats.slot")-1, ItemConfig.get().item("waitting.stats"));
				player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.leave.slot")-1, ItemConfig.get().item("waitting.leave"));
				sender.sendMessage(plugin.text("&aKit " + Integer.valueOf(args[1]).intValue() + " was edited correctly."));
			}
			return true;
		}
		
		
		
		
		return false;
	}
	
	private boolean isNumeric(final String string) {
		try {
			Integer.valueOf(string);
			return true;
		}catch(Exception e) {
			return false;
		}
	}

}
