package com.axeelheaven.meetup.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil{
  
	public static Location Center(Location paramLocation){
		return new Location(paramLocation.getWorld(), getRelativeCoord(paramLocation.getBlockX()), getRelativeCoord(paramLocation.getBlockY()), getRelativeCoord(paramLocation.getBlockZ()));
	}
  
	private static double getRelativeCoord(int paramInt){
		double d = paramInt;
		d = d < 0.0D ? d + 0.5D : d + 0.5D;
		return d;
	}
  
	public static String getString(Location paramLocation, boolean paramBoolean){
		if (paramBoolean == true) {
			return paramLocation.getWorld().getName() + ";" + Center(paramLocation).getX() + ";" + paramLocation.getY() + ";" + Center(paramLocation).getZ() + ";" + 0 + ";" + paramLocation.getYaw();
		}
		return paramLocation.getWorld().getName() + ";" + paramLocation.getX() + ";" + paramLocation.getY() + ";" + paramLocation.getZ() + ";" + paramLocation.getPitch() + ";" + paramLocation.getYaw();
	}
  
	public static Location getLocation(String paramString){
		String[] arrayOfString = paramString.split(";");
		Location localLocation = new Location(Bukkit.getWorld(arrayOfString[0]), Double.parseDouble(arrayOfString[1]), Double.parseDouble(arrayOfString[2]), Double.parseDouble(arrayOfString[3]), Float.parseFloat(arrayOfString[5]), Float.parseFloat("0"));
		return localLocation;
	}
}
