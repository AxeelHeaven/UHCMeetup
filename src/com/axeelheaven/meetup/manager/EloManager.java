package com.axeelheaven.meetup.manager;

import org.bukkit.ChatColor;

import com.axeelheaven.meetup.Main;

public class EloManager {
	
	private int remove = 0;
	private int add_kill = 1;
	private int add_win = 10;
	private Main plugin;
	
	public EloManager(final Main plugin) {
		this.plugin = plugin;
		this.remove = plugin.getConfig().getInt("elo_settings.per_death");
		this.add_kill = plugin.getConfig().getInt("elo_settings.per_kill");
		this.add_win = plugin.getConfig().getInt("elo_settings.per_win");
	}
	
	
	public int getRemove() {
		return this.remove;
	}
	
	
	public int getAddKill() {
		return this.add_kill;
	}
	
	
	public int getAddWin() {
		return this.add_win;
	}
	
	public String getRank(final int elo) {
		for(final String path : this.plugin.getConfig().getConfigurationSection("elo_settings.ranks").getKeys(false)) {
			final String[] strings = this.plugin.getConfig().getString("elo_settings.ranks." + path + ".elo").split(":");
			final int min = Integer.parseInt(strings[0]);
			final int max = Integer.parseInt(strings[1]);
			if(elo > (min - 1) && elo < max) {
				return ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("elo_settings.ranks." + path + ".rank"));
			}
		}
		return ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("elo_settings.default_rank"));
	}
	
	
}
