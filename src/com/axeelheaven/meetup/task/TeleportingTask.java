package com.axeelheaven.meetup.task;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.enums.GameState;
import com.axeelheaven.meetup.util.TimeUtil;

public class TeleportingTask extends BukkitRunnable {

	private boolean running = false;
	private Main plugin;
	private int seconds;
	
	public TeleportingTask(final Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		this.seconds--;
		if(this.seconds == 60 || this.seconds == 50 || this.seconds == 40 || this.seconds == 30 || this.seconds == 20 ||
				this.seconds == 10 || this.seconds == 5 || this.seconds == 4 || this.seconds == 3 || this.seconds == 2 || this.seconds == 1) {
			for(final Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
				player.sendMessage(this.plugin.text(player, plugin.getLang().getString("teleporting").replace("<formatted>", TimeUtil.getInstance().formated(seconds)).replace("<seconds>", TimeUtil.getInstance().getSeconds(seconds, false))));
			}
		} else if(this.seconds < 1) {
			this.cancel();
			this.plugin.getScenarioManager().setSelectedScenario();
			for(final Player player : Bukkit.getOnlinePlayers()) {
				this.plugin.getGameManager().setLocation(player);
				this.plugin.getKitsManager().getRandom().setupContents(player);
				player.getInventory().setHeldItemSlot(0);
				player.sendMessage(this.plugin.text(player, plugin.getLang().getString("selected_scenario").replace("<scenario>", this.plugin.getScenarioManager().getSelectedScenario())));
			}
			this.plugin.getTaskManager().getStartingTask().start();
		}
	}
	
	public void start(final int seconds) {
		this.plugin.getGameManager().setGameState(GameState.WAITTING);
		this.seconds = seconds;
		this.running = true;
		this.runTaskTimer(this.plugin, 20L, 20L);
	}
	
	public void cancel() {
		this.running = false;
		Bukkit.getScheduler().cancelTask(this.getTaskId());
		this.plugin.getTaskManager().setTeleportingTask(new TeleportingTask(this.plugin));
	}
		
	
	public boolean isRunning() {
		return this.running;
	}
	
	public int getSeconds() {
		return this.seconds;
	}

}
