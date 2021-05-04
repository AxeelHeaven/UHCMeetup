package com.axeelheaven.meetup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.axeelheaven.meetup.*;
import com.axeelheaven.meetup.api.*;
import com.axeelheaven.meetup.enums.*;
import com.axeelheaven.meetup.storage.*;
import com.axeelheaven.meetup.util.Util;

public class GameListener implements Listener {
	
	private Main plugin;
	private Util util;
	
	public GameListener(final Main plugin) {
		this.plugin = plugin;
		this.util = Util.get();
	}

	@EventHandler (priority=EventPriority.HIGHEST)
	public void PlayerDeath(final PlayerDeathEvent event) {
		if(this.plugin.getGameManager().isGameState(GameState.INGAME)) {
			final Player player = event.getEntity();
			final SPlayer data = this.plugin.getPlayerManager().getData(player.getUniqueId());
			this.plugin.getPlayerManager().getPlayers().remove(player);
			data.addDeaths();
			data.removeElo(this.plugin.getEloManager().getRemove());
			event.setDeathMessage(null);
			event.getDrops().add(this.plugin.getGameManager().getGoldenHead());
			final Location finalLocation = player.getLocation().clone().add(0, 1.0, 0);
			player.setGameMode(GameMode.SPECTATOR);
			new BukkitRunnable() {
				@Override
				public void run() {
					player.spigot().respawn();
					player.teleport(finalLocation);
				}
			}.runTaskLater(this.plugin, 5L);
			if((player.getKiller() instanceof Player) && (player.getKiller() != null)) {
				final Player killer = player.getKiller();
				final SPlayer kd = this.plugin.getPlayerManager().getData(killer.getUniqueId());
				kd.addElo(this.plugin.getEloManager().getAddKill());
				kd.addKills();
				for(final String string : this.plugin.getLang().getStringList("killer_info")) {
					player.sendMessage(this.plugin.text(player, string).replace("<health>", String.valueOf(((int) ((Damageable) killer).getHealth()))).replace("<kills>", String.valueOf(kd.getKills())).replace("<name>", killer.getName()));
				}
				for(final Player players : Bukkit.getOnlinePlayers()) {
					players.sendMessage(this.plugin.text(players, this.plugin.getLang().getString("killer_player")).replace("<killer-kills>", String.valueOf(kd.getKills())).replace("<killer>", killer.getName()).replace("<death-kills>", String.valueOf(data.getKills())).replace("<victim>", player.getName()));
				}
			} else if((player instanceof Player) && (player != null)) {
				for(final Player players : Bukkit.getOnlinePlayers()) {
					players.sendMessage(this.plugin.text(players, this.plugin.getLang().getString("death_player")).replace("<death-kills>", String.valueOf(data.getKills())).replace("<victim>", player.getName()));
				}
			}
			for(final Player players : Bukkit.getOnlinePlayers()) {
				players.playSound(players.getLocation(), Sound.AMBIENCE_THUNDER, 10F, 10F);
			}
			if(this.plugin.getPlayerManager().getPlayers().size() == 1) {
				final Player final_player = this.plugin.getPlayerManager().getPlayers().get(0);
				final SPlayer final_data =this.plugin.getPlayerManager().getData(final_player.getUniqueId());
				Bukkit.getPluginManager().callEvent(new MeetupWinEvent(final_player, final_data));
			} else if(this.plugin.getPlayerManager().getPlayers().size() == 0) {
				Bukkit.getPluginManager().callEvent(new MeetupRestartEvent());
			}
		}
	}
	@EventHandler (priority=EventPriority.HIGHEST)
	public void MeetupWin(final MeetupWinEvent event) {
		this.plugin.getGameManager().setGameState(GameState.END);
		final Player player = event.getPlayer();
		final SPlayer data = event.getData();
		data.addWins();
		data.addElo(this.plugin.getEloManager().getAddWin());
		this.plugin.getGameManager().setWinner(player);
		for(final SPlayer datas : this.plugin.getPlayerManager().getData().values()) {
			datas.save();
		}
		for(final Player players : Bukkit.getOnlinePlayers()) {
			for(final String string : this.plugin.getLang().getStringList("winner_message")) {
				players.sendMessage(this.plugin.text(players, string.replace("<kills>", String.valueOf(data.getKills())).replace("<name>", player.getName())));
			}
		}
		this.plugin.getTaskManager().getRestartingTask().start();
	}
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void MeetupUpdateRank(final MeetupUpdateRankEvent event) {
		final Player player = event.getPlayer();
		Bukkit.broadcastMessage(this.plugin.text(this.plugin.getLang().getString("update_rank")).replace("<rank>", event.getNewRank()).replace("<name>", player.getName()));
		this.util.spawnFireworks(player.getLocation(), 2);
	}
	
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void MeetupRestart(final MeetupRestartEvent event) {
		if(this.plugin.getConfig().getBoolean("bungeecord.enabled")) {
			for(final Player player : Bukkit.getOnlinePlayers()) {
				this.util.connectPlayer(player, this.plugin.getConfig().getString("bungeecord.server"));
			}
		}
		
		for(final Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer("Restarting server...");
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("restart_command"));
			}
		}.runTaskLater(this.plugin, 20L);
	}
	
	
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void EntityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
		if(this.plugin.getGameManager().isGameState(GameState.INGAME) && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player player = (Player) event.getDamager();
			this.plugin.getPlayerManager().getData(player.getUniqueId()).addDamageCaused(event.getDamage());
		}
	}

	@EventHandler (priority=EventPriority.HIGHEST)
	public void CreatureSpawn(final CreatureSpawnEvent event){
		if(!event.getEntity().getType().equals(EntityType.HORSE)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void ChunkUnload(final ChunkUnloadEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void VehicleExit(final VehicleExitEvent event){
		if (this.plugin.getGameManager().isGameState(GameState.STARTING)) {
			event.setCancelled(true);
		}
	}

	@EventHandler (priority=EventPriority.HIGHEST)
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
		if(event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
			final Player player = (Player) event.getPlayer();
			final SPlayer pd = (SPlayer) this.plugin.getPlayerManager().getData(player.getUniqueId());
			pd.addApplesEaten();
			if(event.getItem().equals(this.plugin.getGameManager().getGoldenHead())) {
				event.setCancelled(true);
				if (event.getItem().getAmount() > 1) {
					player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
				} else {
					player.getInventory().remove(event.getItem());
				}
				for(final String string : this.plugin.getConfig().getConfigurationSection("goldenhead.effects").getKeys(false)) {
					final int time = Integer.valueOf(this.plugin.getConfig().getString("goldenhead.effects." + string.toUpperCase()).split(";")[0]);
					final int level = Integer.valueOf(this.plugin.getConfig().getString("goldenhead.effects." + string.toUpperCase()).split(";")[1]);
					final PotionEffect effect = new PotionEffect(PotionEffectType.getByName(string.toUpperCase()), time * 20, level - 1);
					player.addPotionEffect(effect, true);
				}
				player.setFoodLevel(player.getFoodLevel() + 8);
			}
		}
    }
	
	
}
