package com.axeelheaven.meetup.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.enums.GameState;

public class GameTask extends BukkitRunnable {

	private boolean running = false;
	private Main plugin;
	private int seconds;
	
	public GameTask(final Main plugin) {
		this.plugin = plugin;
		this.seconds = 0;
	}
	
	@Override
	public void run() {
		this.seconds++;
	}
	
	public void start() {
		this.plugin.getGameManager().setGameState(GameState.INGAME);
		this.running = true;
		this.runTaskTimer(this.plugin, 20L, 20L);
	}
		
	
	public boolean isRunning() {
		return this.running;
	}
	
	public int getSeconds() {
		return this.seconds;
	}

}
