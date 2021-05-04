package com.axeelheaven.meetup.worldborder;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WBListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e){
		if (BorderConfig.KnockBack() == 0.0){
			return;
		}

		Location newLoc = BorderCheckTask.checkPlayer(e.getPlayer(), e.getTo(), true, true);
		if (newLoc != null){
			e.setCancelled(true);
			e.setTo(newLoc);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event){
		if (BorderConfig.KnockBack() == 0.0 || !BorderConfig.portalRedirection()){
			return;
		}
		Location newLoc = BorderCheckTask.checkPlayer(event.getPlayer(), event.getTo(), true, false);
		if (newLoc != null){
			event.setTo(newLoc);
		}
	}
	
}
