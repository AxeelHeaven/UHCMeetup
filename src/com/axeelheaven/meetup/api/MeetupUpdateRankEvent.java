package com.axeelheaven.meetup.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MeetupUpdateRankEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private boolean cancelled;
	private Player player;
	private String lastRank;
	private String newRank;
	
	public MeetupUpdateRankEvent(final Player player, final String lastRank, final String newRank){
		this.cancelled = false;
		this.player = player;
		this.lastRank = lastRank;
		this.newRank = newRank;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public String getLastRank() {
		return this.lastRank;
	}
	
	public String getNewRank() {
		return this.newRank;
	}
	
	public HandlerList getHandlers(){
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}
}
