package com.axeelheaven.meetup.task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.enums.GameState;
import com.axeelheaven.meetup.util.TimeUtil;

public class StartingTask extends BukkitRunnable  {

	private boolean running = false;
	private Main plugin;
	private int seconds;
	
	public StartingTask(final Main plugin) {
		this.plugin = plugin;
		this.seconds = plugin.getConfig().getInt("settings.starting_seconds");
	}

	@Override
	public void run() {
		this.seconds--;
		if(this.seconds == 60 || this.seconds == 50 || this.seconds == 40 || this.seconds == 30 || this.seconds == 20 ||
				this.seconds == 10 || this.seconds == 5 || this.seconds == 4 || this.seconds == 3 || this.seconds == 2 || this.seconds == 1) {
			for(final Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
				player.sendMessage(this.plugin.text(player, plugin.getLang().getString("starting").replace("<formatted>", TimeUtil.getInstance().formated(seconds)).replace("<seconds>", TimeUtil.getInstance().getSeconds(seconds, false))));
			}
		} else if(this.seconds < 1) {
			cancel();
			for(final Player player : Bukkit.getOnlinePlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 10F, 10F);
				if(player.getVehicle() != null) {
					player.getVehicle().remove();
				}
				plugin.getPlayerManager().getData(player.getUniqueId()).addPlayed();
				player.sendMessage(this.plugin.text(player, plugin.getLang().getString("game_started")));
			}
			plugin.getTaskManager().getGameTask().start();
			plugin.getTaskManager().getBorderTask().start();
		}
	}
	
	public void start() {
		this.running = true;
		this.plugin.getGameManager().setGameState(GameState.STARTING);
		for(final Player player : Bukkit.getOnlinePlayers()) {
			mountPlayer(player);
		}
		this.runTaskTimer(this.plugin, 20L, 20L);
	}
	
	private void mountPlayer(final Player player) {
		final Horse horse = (Horse) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
		horse.setTarget(player);
		horse.setAdult();
		horse.setTamed(true);
		((LivingEntity)horse).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1), true);
		((LivingEntity)horse).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 10000000, false));
		((LivingEntity)horse).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 10000000, false));
		horse.setVelocity(new Vector(0, 0, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				horse.setPassenger(player);
				
			}
		}.runTaskLater(this.plugin, 30L);
	}
		
	
	public boolean isRunning() {
		return this.running;
	}
	
	public int getSeconds() {
		return this.seconds;
	}

}
