package com.axeelheaven.meetup.task;

import com.axeelheaven.meetup.Main;

public class TaskManager {
	
	private TeleportingTask teleportingTask;
	private StartingTask startingTask;
	private GameTask gameTask;
	private BorderTask borderTask;
	private RestartingTask  restartingTask;
	
	public TaskManager(final Main plugin) {
		this.teleportingTask = new TeleportingTask(plugin);
		this.startingTask = new StartingTask(plugin);
		this.gameTask = new GameTask(plugin);
		this.borderTask = new BorderTask(plugin);
		this.restartingTask = new RestartingTask(plugin);
	}
	
	
	public RestartingTask getRestartingTask() {
		return this.restartingTask;
	}

	public BorderTask getBorderTask() {
		return this.borderTask;
	}
	
	public GameTask getGameTask() {
		return this.gameTask;
	}
	
	public TeleportingTask getTeleportingTask() {
		return this.teleportingTask;
	}
	
	public void setTeleportingTask(final TeleportingTask teleportingTask) {
		this.teleportingTask = teleportingTask;
	}
	
	public StartingTask getStartingTask() {
		return this.startingTask;
	}
	
	
}
