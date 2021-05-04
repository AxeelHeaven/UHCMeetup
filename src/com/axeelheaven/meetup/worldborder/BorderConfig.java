package com.axeelheaven.meetup.worldborder;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.util.FileConfig;

public class BorderConfig
{
	
	public static boolean taskStart = false;
	public BorderConfig(final Main pluginMain){
		plugin = pluginMain;
	    wbLog = plugin.getLogger();
	    
	    StartBorderTimer();
	    
	    borders.clear();
	    knockBack = 2.0D;
	}
	
	private static Plugin plugin = JavaPlugin.getProvidingPlugin(BorderConfig.class);
  private static Logger wbLog = null;
  public static volatile DecimalFormat coord = new DecimalFormat("0.0");
  private static int borderTask = -1;
  public static volatile WorldFillTask fillTask = null;
  private static Runtime rt = Runtime.getRuntime();
private static Map<String, BorderData> borders = Collections.synchronizedMap(new LinkedHashMap<String, BorderData>());
  private static double knockBack;
  private static boolean portalRedirection = true;
  private static int fillAutosaveFrequency = 30;
  private static int fillMemoryTolerance = 500;
  
  public static long Now()
  {
    return System.currentTimeMillis();
  }
  
  public static void setBorder(String world, BorderData border, boolean logIt)
  {
    borders.put(world, border);
    if (logIt) {
      log("Border set. " + BorderDescription(world));
    }
  }
  
  public static void setBorder(String world, BorderData border)
  {
    setBorder(world, border, true);
  }
  
  public static void setBorder(String world, int radiusX, int radiusZ, double x, double z, Boolean shapeRound)
  {
    BorderData old = Border(world);
    boolean oldWrap = (old != null) && (old.getWrapping());
    setBorder(world, new BorderData(x, z, radiusX, radiusZ, shapeRound, oldWrap), true);
  }
  
  public static void setBorder(String world, int radiusX, int radiusZ, double x, double z)
  {
    BorderData old = Border(world);
    Boolean oldShape = old == null ? null : old.getShape();
    boolean oldWrap = (old != null) && (old.getWrapping());
    setBorder(world, new BorderData(x, z, radiusX, radiusZ, oldShape, oldWrap), true);
  }
  
  public static void setBorder(String world, int radius, double x, double z, Boolean shapeRound)
  {
    setBorder(world, new BorderData(x, z, radius, radius, shapeRound), true);
  }
  
  public static void setBorder(String world, int radius, double x, double z)
  {
    setBorder(world, radius, radius, x, z);
  }
  
  public static void setBorderCorners(String world, double x1, double z1, double x2, double z2, Boolean shapeRound, boolean wrap)
  {
    double radiusX = Math.abs(x1 - x2) / 2.0D;
    double radiusZ = Math.abs(z1 - z2) / 2.0D;
    double x = (x1 < x2 ? x1 : x2) + radiusX;
    double z = (z1 < z2 ? z1 : z2) + radiusZ;
    setBorder(world, new BorderData(x, z, (int)Math.round(radiusX), (int)Math.round(radiusZ), shapeRound, wrap), true);
  }
  
  public static void setBorderCorners(String world, double x1, double z1, double x2, double z2, Boolean shapeRound)
  {
    setBorderCorners(world, x1, z1, x2, z2, shapeRound, false);
  }
  
  public static void setBorderCorners(String world, double x1, double z1, double x2, double z2)
  {
    BorderData old = Border(world);
    Boolean oldShape = old == null ? null : old.getShape();
    boolean oldWrap = (old != null) && (old.getWrapping());
    setBorderCorners(world, x1, z1, x2, z2, oldShape, oldWrap);
  }
  
  public static void removeBorder(String world)
  {
    borders.remove(world);
    log("Removed border for world \"" + world + "\".");
  }
  
  public static void removeAllBorders()
  {
    borders.clear();
    log("Removed all borders for all worlds.");
  }
  
  public static String BorderDescription(String world)
  {
    BorderData border = (BorderData)borders.get(world);
    if (border == null) {
      return "No border was found for the world \"" + world + "\".";
    }
    return "World \"" + world + "\" has border " + border.toString();
  }
  
  public static Set<String> BorderDescriptions()
  {
    Set<String> output = new HashSet<String>();
    for (String worldName : borders.keySet()) {
      output.add(BorderDescription(worldName));
    }
    return output;
  }
  
  public static BorderData Border(String world)
  {
    return (BorderData)borders.get(world);
  }
  
  public static Map<String, BorderData> getBorders()
  {
    return new LinkedHashMap<String, BorderData>(borders);
  }
  
  public static String ShapeName(Boolean round)
  {
    if (round == null) {
      return "default";
    }
    return round.booleanValue() ? "elliptic/round" : "rectangular/square";
  }
  
  public static void setPortalRedirection(boolean enable)
  {
    portalRedirection = enable;
    log("Portal redirection " + (enable ? "enabled" : "disabled") + ".");
  }
  
  public static boolean portalRedirection()
  {
    return portalRedirection;
  }
  
  public static void setKnockBack(double numBlocks)
  {
    knockBack = numBlocks;
    log("Knockback set to " + knockBack + " blocks inside the border.");
  }
  
  public static double KnockBack()
  {
    return knockBack;
  }
  
  public static void setFillAutosaveFrequency(int seconds)
  {
    fillAutosaveFrequency = seconds;
    if (fillAutosaveFrequency == 0) {
      log("World autosave frequency during Fill process set to 0, disabling it. Note that much progress can be lost this way if there is a bug or crash in the world generation process from Bukkit or any world generation plugin you use.");
    } else {
      log("World autosave frequency during Fill process set to " + fillAutosaveFrequency + " seconds (rounded to a multiple of 5). New chunks generated by the Fill process will be forcibly saved to disk this often to prevent loss of progress due to bugs or crashes in the world generation process.");
    }
  }
  
  public static int FillAutosaveFrequency()
  {
    return fillAutosaveFrequency;
  }
  
  public static boolean isBorderTimerRunning()
  {
    if (borderTask == -1) {
      return false;
    }
    return true;
  }
  
  public static void StartBorderTimer()
  {
    StopBorderTimer(false);
    
    int tick = 4;
    borderTask = new BorderCheckTask().runTaskTimer(plugin, 5L, tick).getTaskId();
    if (borderTask == -1) {
      logWarn("Failed to start timed border-checking task! This will prevent the plugin from working. Try restarting Bukkit.");
    }
    logConfig("Border-checking timed task started.");
  }
  
  public static void StopBorderTimer()
  {
    StopBorderTimer(true);
  }
  
  public static void StopBorderTimer(boolean logIt)
  {
    if (borderTask == -1) {
      return;
    }
    plugin.getServer().getScheduler().cancelTask(borderTask);
    borderTask = -1;
    if (logIt) {
      logConfig("Border-checking timed task stopped.");
    }
  }
  
  public static void StopFillTask()
  {
    if ((fillTask != null) && (fillTask.valid())) {
      fillTask.cancel();
    }
  }
  
  public static void RestoreFillTask(String world, int fillDistance, int chunksPerRun, int tickFrequency, int x, int z, int length, int total) {}
  
  public static void StopTrimTask() {}
  
  public static int AvailableMemory()
  {
    return (int)((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576L);
  }
  
  public static boolean AvailableMemoryTooLow()
  {
    return AvailableMemory() < fillMemoryTolerance;
  }
  
  public static boolean HasPermission(Player player, String request)
  {
    return HasPermission(player, request, true);
  }
  
  public static boolean HasPermission(Player player, String request, boolean notify)
  {
    if (player == null) {
      return true;
    }
    if (player.hasPermission("worldborder." + request)) {
      return true;
    }
    if (notify) {
      player.sendMessage("You do not have sufficient permissions.");
    }
    return false;
  }
  
  public static String replaceAmpColors(String message)
  {
    return ChatColor.translateAlternateColorCodes('&', message);
  }
  
  public static String stripAmpColors(String message)
  {
    return message.replaceAll("(?i)&([a-fk-or0-9])", "");
  }
  
  public static void log(Level lvl, String text)
  {
    wbLog.log(lvl, text);
  }
  
  public static void log(String text)
  {
    log(Level.INFO, text);
  }
  
  public static void logWarn(String text)
  {
    log(Level.WARNING, text);
  }
  
  public static void logConfig(String text)
  {
    log(Level.INFO, "[CONFIG] " + text);
  }
}
