package com.axeelheaven.meetup.task;

import org.bukkit.*;

import org.bukkit.entity.*;

import com.axeelheaven.meetup.Main;

import java.util.*;
import org.bukkit.block.*;

public class GlassRunnable {
	
	private Map<UUID, Set<Location>> Locations;
	private Main plugin;
	private int color = 14;
	private long task = 1;
    
    public GlassRunnable(final Main plugin) {
        this.Locations = new HashMap<UUID, Set<Location>>();
        this.Locations.clear();
        this.plugin = plugin;
        this.color = plugin.getConfig().getInt("border_settings.glassborder");
        this.task = plugin.getConfig().getInt("border_settings.glassborder_task");
    }
    
    private int closest(final int n, final int... array) {
        int n2 = array[0];
        for (int i = 0; i < array.length; ++i) {
            if (Math.abs(n - array[i]) < Math.abs(n - n2)) {
                n2 = array[i];
            }
        }
        return n2;
    }
    
    private void update(final Player player) {
        final HashSet<Location> set = new HashSet<Location>();
        final int closest = this.closest(player.getLocation().getBlockX(), -this.plugin.getGameManager().getBorder()-1, this.plugin.getGameManager().getBorder());
        final int closest2 = this.closest(player.getLocation().getBlockZ(), -this.plugin.getGameManager().getBorder()-1, this.plugin.getGameManager().getBorder());
        final boolean b = Math.abs(player.getLocation().getX() - closest) < 10.5;
        final boolean b2 = Math.abs(player.getLocation().getZ() - closest2) < 10.5;
        if (!b && !b2) {
            this.removeGlass(player);
            return;
        }
        if (b) {
            for (int i = -6; i < 7; ++i) {
                for (int j = -7; j < 8; ++j) {
                    final Location location = new Location(player.getLocation().getWorld(), (double)Double.valueOf(closest), (double)Double.valueOf(player.getLocation().getBlockY() + i), (double)Double.valueOf(player.getLocation().getBlockZ() + j));
                    if (!set.contains(location) && !location.getBlock().getType().isOccluding()) {
                        set.add(location);
                    }
                }
            }
        }
        if (b2) {
            for (int k = -6; k < 7; ++k) {
                for (int l = -7; l < 8; ++l) {
                    final Location location2 = new Location(player.getLocation().getWorld(), (double)Double.valueOf(player.getLocation().getBlockX() + l), (double)Double.valueOf(player.getLocation().getBlockY() + k), (double)Double.valueOf(closest2));
                    if (!set.contains(location2) && !location2.getBlock().getType().isOccluding()) {
                        set.add(location2);
                    }
                }
            }
        }
        this.render(player, set);
    }
    
    @Deprecated
    private void render(final Player player, final Set<Location> set) {
        if (this.Locations.containsKey(player.getUniqueId())) {
            this.Locations.get(player.getUniqueId()).addAll(set);
            for (final Location location : this.Locations.get(player.getUniqueId())) {
                if (!set.contains(location)) {
                    final Block block = location.getBlock();
                    player.sendBlockChange(location, block.getTypeId(), block.getData());
                }
            }
            final Iterator<Location> iterator2 = set.iterator();
            while (iterator2.hasNext()) {
                player.sendBlockChange((Location)iterator2.next(), 95, (byte)this.color);
            }
        }
        else {
            final Iterator<Location> iterator3 = set.iterator();
            while (iterator3.hasNext()) {
                player.sendBlockChange((Location)iterator3.next(), 95, (byte)this.color);
            }
        }
        this.Locations.put(player.getUniqueId(), set);
    }
    
    private void removeGlass(final Player player) {
        if (this.Locations.containsKey(player.getUniqueId())) {
            for (final Location location : this.Locations.get(player.getUniqueId())) {
                final Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            this.Locations.remove(player.getUniqueId());
        }
    }
    
    public void start() {
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable(){
			@Override
			public void run(){
	            for(Player player : Bukkit.getOnlinePlayers()) {
	            	if(player.getLocation().getWorld().getName().equals("Meetup")) {
	                    update(player);
	            	}
	            }
			}
		}, 20L, this.task);
    }
}
