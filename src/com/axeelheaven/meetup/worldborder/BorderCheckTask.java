package com.axeelheaven.meetup.worldborder;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BorderCheckTask
extends BukkitRunnable
{
public void run()
{
  if (BorderConfig.KnockBack() == 0.0D) {
    return;
  }
  @SuppressWarnings("deprecation")
Collection<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
  for (Player player : players) {
    checkPlayer(player, null, false, true);
  }
}

private static Set<String> handlingPlayers = Collections.synchronizedSet(new LinkedHashSet<String>());

public static Location checkPlayer(Player player, Location targetLoc, boolean returnLocationOnly, boolean notify)
{
  if ((player == null) || (!player.isOnline())) {
    return null;
  }
  Location loc = targetLoc == null ? player.getLocation().clone() : targetLoc;
  if (loc == null) {
    return null;
  }
  World world = loc.getWorld();
  if (world == null) {
    return null;
  }
  BorderData border = BorderConfig.Border(world.getName());
  if (border == null) {
    return null;
  }
  if (border.insideBorder(loc.getX(), loc.getZ(), false)) {
    return null;
  }
  if (handlingPlayers.contains(player.getName().toLowerCase())) {
    return null;
  }
  handlingPlayers.add(player.getName().toLowerCase());
  
  Location newLoc = newLocation(player, loc, border, notify);
  boolean handlingVehicle = false;
  if (player.isInsideVehicle())
  {
    Entity ride = player.getVehicle();
    player.leaveVehicle();
    if (ride != null)
    {
      double vertOffset = (ride instanceof LivingEntity) ? 0.0D : ride.getLocation().getY() - loc.getY();
      Location rideLoc = newLoc.clone();
      rideLoc.setY(newLoc.getY() + vertOffset);
      
      ride.setVelocity(new Vector(0, 0, 0));
      ride.teleport(rideLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
  }
  if (player.getPassenger() != null)
  {
    Entity rider = player.getPassenger();
    player.eject();
    rider.teleport(newLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    player.sendMessage("Your passenger has been ejected.");
  }
  if (!returnLocationOnly) {
    player.teleport(newLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
  }
  if (!handlingVehicle) {
    handlingPlayers.remove(player.getName().toLowerCase());
  }
  if (returnLocationOnly) {
    return newLoc;
  }
  return null;
}

public static Location checkPlayer(Player player, Location targetLoc, boolean returnLocationOnly)
{
  return checkPlayer(player, targetLoc, returnLocationOnly, true);
}

private static Location newLocation(Player player, Location loc, BorderData border, boolean notify)
{
  Location newLoc = border.correctedPosition(loc, false, player.isFlying());
  if (newLoc == null) {
    newLoc = new Location(loc.getWorld(), 0.0D, loc.getWorld().getHighestBlockYAt(0, 0) + 0.5D, 0.0D);
  }
  if (notify) {
    player.sendMessage("You can not leave the limits of the world.");
  }
  return newLoc;
}
}
