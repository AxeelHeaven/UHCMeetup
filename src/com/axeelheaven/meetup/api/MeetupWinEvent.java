package com.axeelheaven.meetup.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.axeelheaven.meetup.storage.SPlayer;

public class MeetupWinEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private Player player;
	private SPlayer data;
	private boolean cancelled;
	
	public MeetupWinEvent(final Player player, final SPlayer data){
		this.player = player;
		this.data = data;
		this.cancelled = false;
	}
	
	public SPlayer getData(){
		return this.data;
	}
	
	public Player getPlayer(){
		return this.player;
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
