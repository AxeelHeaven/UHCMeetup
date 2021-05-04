package com.axeelheaven.meetup.commands;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.util.Util;
import com.axeelheaven.meetup.util.ValueTPS;

public class UptimeCommand implements CommandExecutor {

	private Main plugin;
	private DecimalFormat decimal;
	
	public UptimeCommand(final Main plugin) {
		this.plugin = plugin;
	    this.decimal = new DecimalFormat("#.##");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ping")) {
			if(sender instanceof Player) {
				final Player player = (Player) sender;
				player.sendMessage(plugin.getLang().getString("ping_command").replace("<ping>", String.valueOf(Util.get().getPing((Player) sender))));
			}
			 return  true;
		}
		
		if(cmd.getName().equalsIgnoreCase("uptime")) {
			if(!sender.hasPermission("hmeetup.uptime")){
				sender.sendMessage(plugin.text(plugin.getLang().getString("no_permissions")));
				return true;
			}
			
			this.message(sender, "");
			double tps = ValueTPS.getInstance().getRecentTPS()[0];
		    String tpsString = decimal.format(tps);
		    if(tps > 20) {
		    	tpsString = "*20";
		    }
		    this.message(sender, " &7- &fCurrent TPS: &a" + tpsString);
		    double performance = (tps * 100) / 20.0;
		    if(performance > 100) {
		    	performance = 100;
		    }
		    this.message(sender, " &7- &fServer Performance: &a" + decimal.format(performance) + "%");
		    this.message(sender, " &7- &fServer Uptime: &a" + formatTime(ManagementFactory.getRuntimeMXBean().getUptime()));
		    this.message(sender, " &7- &fMax Memory: &a" + formatSize(Runtime.getRuntime().maxMemory()));
		    this.message(sender, " &7- &fTotal Memory: &a" + formatSize(Runtime.getRuntime().totalMemory()));
		    this.message(sender, " &7- &fFree Memory: &a" + formatSize(Runtime.getRuntime().freeMemory()));
		    this.message(sender, "&7 - &fWorlds: ");
		    List<World> worlds = Bukkit.getWorlds();
		    for (World w : worlds){
		    	String worldType;
		    	switch (w.getEnvironment()){
		    	case NETHER: 
		    		worldType = "Nether";
		    		break;
		    	case THE_END: 
		    		worldType = "The End";
				default:
					worldType = "Default World";
					break;
		    	}
		    	int tileEntities = 0;
		    	try{
		    		for (org.bukkit.Chunk chunk : w.getLoadedChunks()) {
		    			tileEntities += chunk.getTileEntities().length;
		    		}
		    	}catch (ClassCastException ex){}
		    	this.message(sender, "   &7- &f" + w.getName() + "&7(" + worldType + "): &f" + w.getLoadedChunks().length + " chunks, " + w.getEntities().size() + " entidades, " + tileEntities + " titles.");
		    }
		    this.message(sender, "");
		    return true;
		}

	    
		
		return false;
	}

    private void message(final CommandSender sender, final String string) {
    	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
	}

	private String formatTime(final long l) {
        if (l < 1000)
            return l + " milliseconds";

        long sec = l/1000;
        long min = sec/60;

        if (min < 60)
            return min + " minutes";

        long hours = min/60;
        if (hours < 24)
            return hours + "hours";

        return (hours/24) + " days";
    }

    private String formatSize(final long v) {
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

}
