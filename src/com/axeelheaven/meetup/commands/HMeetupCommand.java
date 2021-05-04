package com.axeelheaven.meetup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.enums.GameState;
import com.axeelheaven.meetup.storage.SPlayer;

public class HMeetupCommand implements CommandExecutor {

	private Main plugin;
	
	public HMeetupCommand(final Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("forcestart")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			
			final Player player = (Player) sender;
			
			if(!sender.hasPermission("hmeetup.forcestart")){
				sender.sendMessage(plugin.text(player, plugin.getLang().getString("no_permissions")));
				return true;
			}
			
			if(!this.plugin.getGameManager().isGameState(GameState.WAITTING)) {
				return true;
			}
			
			if(this.plugin.getTaskManager().getTeleportingTask().isRunning()) {
				return true;
			}
			
			if(this.plugin.getPlayerManager().getPlayers().size() < 2 && !sender.getName().equals("AxeelHeaven")) {
				sender.sendMessage(plugin.text("&cAt least 2 players are needed to force the start of the game."));
				return true;
			}
			
			this.plugin.getTaskManager().getTeleportingTask().start(this.plugin.getConfig().getInt("settings.teleporting_full_seconds"));
			
		}
		if(cmd.getName().equalsIgnoreCase("elo")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			
			final Player player = (Player) sender;
			final SPlayer data = this.plugin.getPlayerManager().getData(player.getUniqueId());
			player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("elo_command")).replace("<elo>", String.valueOf(data.getElo())).replace("<elo_rank>", data.getEloRank()));
		}
		
		return false;
	}

}
