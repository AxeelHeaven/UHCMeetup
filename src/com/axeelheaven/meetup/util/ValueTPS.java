package com.axeelheaven.meetup.util;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ValueTPS {
	
	private static ValueTPS instance;
	private String packageName;
	private String version;
	private Class<?> clazz = null;
	private Object si = null;
	private Field tpsField = null;
	private DecimalFormat decimal;
	private final Plugin PLUGIN = JavaPlugin.getProvidingPlugin(ValueTPS.class);
    
    public ValueTPS() {
	    this.packageName = PLUGIN.getServer().getClass().getPackage().getName();
	    this.version = this.packageName.substring(this.packageName.lastIndexOf('.') + 1);
	    try{
	    	this.clazz = Class.forName("net.minecraft.server." + this.version + "." + "MinecraftServer");
	    	this.si = this.clazz.getMethod("getServer", new Class[0]).invoke(null, new Object[0]);
	    	this.tpsField = this.si.getClass().getField("recentTps");
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
	    //TPS FORMAT: 20.20
	    this.decimal = new DecimalFormat("#.##");
    }
    
    public double[] getRecentTPS() {
	    try{
	    	return (double[])this.tpsField.get(this.si);
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
		return null;
    }
    
    public String getRecentTPS(final int task) {
    	return this.decimal.format(this.getRecentTPS()[task]);
    }
    
    public static ValueTPS getInstance() {
    	if(instance == null) {
    		instance = new ValueTPS();
    	}
    	return instance;
    }
	
}
