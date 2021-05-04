package com.axeelheaven.meetup.util;

import com.axeelheaven.meetup.Main;

public class TimeUtil {
	
	private Main plugin;
	private static TimeUtil timeUtil;

    private String sec;
    private String secs;
    private String min;
    private String mins;
	
	public TimeUtil(Main main) {
		plugin = main;
	    
	    sec = plugin.getLang().getString("time_format.second");
	    secs = plugin.getLang().getString("time_format.seconds");
	    min = plugin.getLang().getString("time_format.minute");
	    mins = plugin.getLang().getString("time_format.minutes");
	    timeUtil = this;
	}
	
	public static TimeUtil getInstance() {
		return timeUtil;
	}

	public String getSeconds(int seconds, boolean showEmptyHours){
		int hours = seconds / 3600;
	    seconds -= hours * 3600;
	    int minutes = seconds / 60;
	    seconds -= minutes * 60;
	    
	    StringBuilder builder = new StringBuilder();
	    if (hours > 0){
	    	if (hours < 10) {
	    		builder.append('0');
	    	}
	    	builder.append(hours);
	    	builder.append(':');
	    }else if (showEmptyHours){
	    	builder.append("00:");
	    }
	    if ((minutes < 10) && (hours != -1)) {
	    	builder.append('0');
	    }
	    builder.append(minutes);
	    builder.append(':');
	    if (seconds < 10) {
	    	builder.append('0');
	    }
	    builder.append(seconds);
	    
	    return builder.toString();
	}
	
	public String formated(final int integer){
	    int m = integer * 60;
	    
	    String s = "";
	    if (m > 2) {
	      s = mins;
	    }
	    if (integer == 60) {
	      s = min;
	    }
	    if (integer < 60) {
	      s = secs;
	    }
	    if (integer == 1) {
	      s = sec;
	    }
	    return s;
	  }
	
}
