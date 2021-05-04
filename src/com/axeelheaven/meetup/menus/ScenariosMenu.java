package com.axeelheaven.meetup.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.util.ItemConfig;

public class ScenariosMenu implements Listener {
	
	private Main plugin;
	private ItemConfig itemConfig;
	
	public ScenariosMenu(final Main plugin) {
		this.plugin = plugin;
		this.itemConfig = ItemConfig.get();
	}
	
	public void open(final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 27, "Scenarios");

		inventory.setItem(10, this.itemConfig.item(Material.DIAMOND_SWORD, this.plugin.getScenarioManager().getVotes("NoClean"), 0, "&aNo Clean", this.plugin.getConfig().getStringList("items_settings.scenarios_menu.noclean_lore")));
		inventory.setItem(11, this.itemConfig.item(Material.CHEST, this.plugin.getScenarioManager().getVotes("TimeBomb"), 0, "&aTime Bomb", this.plugin.getConfig().getStringList("items_settings.scenarios_menu.timebomb_lore")));
		inventory.setItem(12, this.itemConfig.item(Material.FISHING_ROD, this.plugin.getScenarioManager().getVotes("Rodless"), 0, "&aRodless", this.plugin.getConfig().getStringList("items_settings.scenarios_menu.rodless_lore")));
		inventory.setItem(13, this.itemConfig.item(Material.LEATHER, this.plugin.getScenarioManager().getVotes("KillSwitch"), 0, "&aKill Switch", this.plugin.getConfig().getStringList("items_settings.scenarios_menu.killswitch_lore")));
		inventory.setItem(14, this.itemConfig.item(Material.FLINT_AND_STEEL, this.plugin.getScenarioManager().getVotes("Fireless"), 0, "&aFireless", this.plugin.getConfig().getStringList("items_settings.scenarios_menu.fireless_lore")));
		inventory.setItem(15, this.itemConfig.item(Material.GOLDEN_APPLE, this.plugin.getScenarioManager().getVotes("Absorptionless"), 0, "&aAbsorptionless", this.plugin.getConfig().getStringList("items_settings.scenarios_menu.absorptionless_lore")));
		inventory.setItem(16, this.itemConfig.item(Material.FEATHER, this.plugin.getScenarioManager().getVotes("NoFall"), 0, "&aNo Fall", this.plugin.getConfig().getStringList("items_settings.scenarios_menu.nofall_lore")));
		
		player.openInventory(inventory);
	}
	
	@EventHandler
	public void inv(final InventoryClickEvent event) {
		if(event.getInventory().getTitle().equalsIgnoreCase("Scenarios")) {
			if(event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR) && event.getWhoClicked() instanceof Player) {
				final ItemStack item = event.getCurrentItem();
				final ItemMeta meta = item.getItemMeta();
				final Player player = (Player) event.getWhoClicked();
				if(meta.getDisplayName().equals("§aNo Clean")) {
					this.plugin.getScenarioManager().addVote("NoClean");
					player.closeInventory();
					player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("voted_scenario").replace("<scenario>", ChatColor.stripColor(meta.getDisplayName()))));
				} else if(meta.getDisplayName().equals("§aTime Bomb")) {
					this.plugin.getScenarioManager().addVote("TimeBomb");
					player.closeInventory();
					player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("voted_scenario").replace("<scenario>", ChatColor.stripColor(meta.getDisplayName()))));
				} else if(meta.getDisplayName().equals("§aRodless")) {
					this.plugin.getScenarioManager().addVote("Rodless");
					player.closeInventory();
					player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("voted_scenario").replace("<scenario>", ChatColor.stripColor(meta.getDisplayName()))));
				} else if(meta.getDisplayName().equals("§aKill Switch")) {
					this.plugin.getScenarioManager().addVote("KillSwitch");
					player.closeInventory();
					player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("voted_scenario").replace("<scenario>", ChatColor.stripColor(meta.getDisplayName()))));
				} else if(meta.getDisplayName().equals("§aFireless")) {
					this.plugin.getScenarioManager().addVote("Fireless");
					player.closeInventory();
					player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("voted_scenario").replace("<scenario>", ChatColor.stripColor(meta.getDisplayName()))));
				} else if(meta.getDisplayName().equals("§aAbsorptionless")) {
					this.plugin.getScenarioManager().addVote("Absorptionless");
					player.closeInventory();
					player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("voted_scenario").replace("<scenario>", ChatColor.stripColor(meta.getDisplayName()))));
				} else if(meta.getDisplayName().equals("§aNo Fall")) {
					this.plugin.getScenarioManager().addVote("NoFall");
					player.closeInventory();
					player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("voted_scenario").replace("<scenario>", ChatColor.stripColor(meta.getDisplayName()))));
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerInteract(final PlayerInteractEvent event) {
		final ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item != null && item.getItemMeta() != null)) {
			final ItemMeta meta = item.getItemMeta();
			if(item.getType().equals(this.getMaterialFrom("waitting.vote_scenarios")) && meta.getDisplayName().equalsIgnoreCase(plugin.text(plugin.getConfig().getString("items_settings.waitting.vote_scenarios.name")))){
				event.setCancelled(true);
				this.open(player);
			}
		}
	}

	private Object getMaterialFrom(String string) {
		final String integer = this.plugin.getConfig().getString("items_settings." + string + ".item").split(";")[0];
		if(this.isNumeric(integer)) {
			return Material.getMaterial(Integer.parseInt(integer));
		} else {
			return Material.getMaterial(string.toUpperCase());
		}
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
