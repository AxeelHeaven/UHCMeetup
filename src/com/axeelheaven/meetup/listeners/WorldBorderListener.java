package com.axeelheaven.meetup.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.axeelheaven.meetup.Main;

public class WorldBorderListener implements Listener{

	private static Main plugin;
	
	public WorldBorderListener(final Main main) {
		plugin = main;
	}
	
	@EventHandler
	public void PlayerMove(final PlayerMoveEvent event) {
		update(event.getPlayer());
	}
	
	public static void update(final Player p) {
		int i = plugin.getGameManager().getBorder() - 1;
		if (p.getLocation().getWorld().getName().equals("Meetup")) {
			final World world = p.getLocation().getWorld();
			if (p.getLocation().getBlockX() > i){
				p.teleport(new Location(world, i - 2, p.getLocation().getBlockY(), p.getLocation().getBlockZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
		    	int y = world.getHighestBlockYAt(p.getLocation()) + 1;
		    	Location loc = new Location(world, p.getLocation().getX(), y, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
		    	p.teleport(loc); 
		    	p.setFallDistance(0.0F); 
		    }
		    if (p.getLocation().getBlockZ() > i){
		    	p.teleport(new Location(world, p.getLocation().getBlockX(), p.getLocation().getBlockY(), i - 2, p.getLocation().getYaw(), p.getLocation().getPitch()));
		      	int y = world.getHighestBlockYAt(p.getLocation()) + 1;
		      	Location loc = new Location(world, p.getLocation().getX(), y, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
		    	p.teleport(loc); 
		    	p.setFallDistance(0.0F); 
		    }
		    if (p.getLocation().getBlockX() < -(i + 1)){
		    	p.teleport(new Location(world, -i + 2, p.getLocation().getBlockY(), p.getLocation().getBlockZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
		    	int y = world.getHighestBlockYAt(p.getLocation()) + 1;
		    	Location loc = new Location(world, p.getLocation().getX(), y, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
		    	p.teleport(loc); 
		    	p.setFallDistance(0.0F); 
		    }
		    if (p.getLocation().getBlockZ() < -(i + 1)){
		    	p.teleport(new Location(world, p.getLocation().getBlockX(), p.getLocation().getBlockY(), -i + 2, p.getLocation().getYaw(), p.getLocation().getPitch()));
		    	int y = world.getHighestBlockYAt(p.getLocation()) + 1;
		    	Location loc = new Location(world, p.getLocation().getX(), y, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
		    	p.teleport(loc);
		    	p.setFallDistance(0.0F); 
		    }
	    }
	}

}
