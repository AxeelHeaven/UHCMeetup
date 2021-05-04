package com.axeelheaven.meetup.listeners;

import java.util.UUID;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.potion.*;

import com.axeelheaven.meetup.*;
import com.axeelheaven.meetup.enums.*;
import com.axeelheaven.meetup.storage.SPlayer;
import com.axeelheaven.meetup.util.*;

public class LobbyListener implements Listener {

	private Main plugin;
	
	public LobbyListener(final Main plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void AsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
		final UUID uuid = event.getUniqueId();
		if(!plugin.getPlayerManager().getData().containsKey(uuid)) {
			this.plugin.getPlayerManager().getData().put(uuid, new SPlayer(plugin, uuid));
		}
	}
	
	@EventHandler
	public void PlayerLogin(final PlayerLoginEvent event) {
		if(this.plugin.getGameManager().isGameState(GameState.LOADING)) {
			event.disallow(Result.KICK_OTHER, "Loading...");
		} else if(this.plugin.getGameManager().isGameState(GameState.WAITTING) && (this.plugin.getPlayerManager().getPlayers().size() >= this.plugin.getGameManager().getMaxPlayers())) {
			event.disallow(Result.KICK_OTHER, "The game is full.");
		} else if(this.plugin.getGameManager().isGameState(GameState.STARTING)) {
			event.disallow(Result.KICK_OTHER, "The game is starting.");
		} else if(this.plugin.getGameManager().isGameState(GameState.INGAME)) {
			event.disallow(Result.KICK_OTHER, "Game in running.");
		} else if(this.plugin.getGameManager().isGameState(GameState.END)) {
			event.disallow(Result.KICK_OTHER, "The game is ending.");
		}
	}
	
	
	@EventHandler
	public void PlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		event.setJoinMessage(null);
		player.setHealth(20D);
		player.setFoodLevel(20);
		player.setExp(0);
		player.setLevel(0);
		player.getEnderChest().clear();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		for(final PotionEffect pe: player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
		player.setFireTicks(0);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGameMode(GameMode.ADVENTURE);
		player.setFallDistance(0);
		if(this.plugin.getGameManager().getSpawnpoint() == null) {
			player.sendMessage("§cPlease set the starting point with /spawnpoint");
		} else {
			player.teleport(this.plugin.getGameManager().getSpawnpoint().add(0, 0.5D, 0));
		}
		this.plugin.getPlayerManager().getPlayers().add(player);
		for(final Player players : Bukkit.getOnlinePlayers()) {
			players.sendMessage(this.plugin.text(players, this.plugin.getLang().getString("player_join").replace("<name>", player.getName())));
		}
		player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.vote_scenarios.slot")-1, ItemConfig.get().item("waitting.vote_scenarios"));
		player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.stats.slot")-1, ItemConfig.get().item("waitting.stats"));
		player.getInventory().setItem(plugin.getConfig().getInt("items_settings.waitting.leave.slot")-1, ItemConfig.get().item("waitting.leave"));
		if(!this.plugin.getTaskManager().getTeleportingTask().isRunning()) {
			if(this.plugin.getPlayerManager().getPlayers().size() >= this.plugin.getGameManager().getMaxPlayers()) {
				this.plugin.getTaskManager().getTeleportingTask().start(this.plugin.getConfig().getInt("settings.teleporting_full_seconds"));
			} else if(this.plugin.getPlayerManager().getPlayers().size() >= this.plugin.getGameManager().getMinPlayers()) {
				this.plugin.getTaskManager().getTeleportingTask().start(this.plugin.getConfig().getInt("settings.teleporting_nofull_seconds"));
			}
		}
	}
	
	@EventHandler
	public void PlayerKick(final PlayerKickEvent event) {
		final Player player = event.getPlayer();
		if(this.plugin.getPlayerManager().getPlayers().contains(player)) {
			this.plugin.getPlayerManager().getPlayers().remove(player);
		}
		if(this.plugin.getKitsManager().getEditing().contains(player)) {
			this.plugin.getKitsManager().getEditing().remove(player);
		}
		if(this.plugin.getPlayerManager().getSpectators().contains(player)) {
			this.plugin.getPlayerManager().getSpectators().remove(player);
		}
		if(this.plugin.getGameManager().isGameState(GameState.STARTING)) {
			if(player.isInsideVehicle()) {
				player.getVehicle().remove();
			}
		}
		if(this.plugin.getPlayerManager().getData().containsKey(player.getUniqueId())) {
			this.plugin.getPlayerManager().getData().remove(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void PlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		event.setQuitMessage(null);
		if(this.plugin.getPlayerManager().getPlayers().contains(player)) {
			this.plugin.getPlayerManager().getPlayers().remove(player);
		}
		if(this.plugin.getPlayerManager().getSpectators().contains(player)) {
			this.plugin.getPlayerManager().getSpectators().remove(player);
		}
		if(this.plugin.getKitsManager().getEditing().contains(player)) {
			this.plugin.getKitsManager().getEditing().remove(player);
		}
		if(this.plugin.getGameManager().isGameState(GameState.WAITTING)) {
			for(final Player players : Bukkit.getOnlinePlayers()) {
				players.sendMessage(this.plugin.text(players, this.plugin.getLang().getString("player_quit").replace("<name>", player.getName())));
			}
			if(this.plugin.getTaskManager().getTeleportingTask().isRunning() && this.plugin.getPlayerManager().getPlayers().size() < this.plugin.getGameManager().getMinPlayers()) {
				this.plugin.getTaskManager().getTeleportingTask().cancel();
				Bukkit.broadcastMessage(this.plugin.text(this.plugin.getLang().getString("cancel_teleporting")));
			}
		}
		if(this.plugin.getGameManager().isGameState(GameState.STARTING)) {
			if(player.isInsideVehicle()) {
				player.getVehicle().remove();
			}
		}
		if(this.plugin.getPlayerManager().getData().containsKey(player.getUniqueId())) {
			this.plugin.getPlayerManager().getData().remove(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void BlockPlace(final BlockBreakEvent event) {
		if(!this.plugin.getGameManager().isGameState(GameState.INGAME)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void BlockPlace(final BlockPlaceEvent event) {
		if(!this.plugin.getGameManager().isGameState(GameState.INGAME)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void EntityDamage(final EntityDamageEvent event) {
		if(!this.plugin.getGameManager().isGameState(GameState.INGAME)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void InventoryClick(final InventoryClickEvent event) {
		if(!(this.plugin.getKitsManager().getEditing().contains((Player)event.getWhoClicked()) || this.plugin.getGameManager().isGameState(GameState.INGAME) || this.plugin.getGameManager().isGameState(GameState.STARTING))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerDropItem(final PlayerDropItemEvent event) {
		if(!(this.plugin.getGameManager().isGameState(GameState.INGAME) || this.plugin.getGameManager().isGameState(GameState.STARTING))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void EntityDamageByEntity(final EntityDamageByEntityEvent event) {
		if(!this.plugin.getGameManager().isGameState(GameState.INGAME)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerPickupItem(final PlayerPickupItemEvent event) {
		if(!(this.plugin.getGameManager().isGameState(GameState.INGAME) || this.plugin.getGameManager().isGameState(GameState.STARTING))) {
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void PlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		if(event.getMessage().contains("/spawnpoint") && (player.isOp() || player.hasPermission("hmeetup.setup.spawnpoint"))) {
			this.plugin.getCache().set("spawnpoint", LocationUtil.getString(player.getLocation(), false));
			FileConfig.getInstance().save("cache.yml");
			player.sendMessage("§aLocation set successfully, please reboot the server.");
			event.setCancelled(true);
		}
	}

}
