package fr.cocoraid.prodigyforcefield;

import fr.cocoraid.prodigyforcefield.database.LocalDatabase;
import fr.cocoraid.prodigyforcefield.database.ProdigyForcefieldConfig;
import fr.cocoraid.prodigyforcefield.utils.CC;
import fr.cocoraid.prodigyforcefield.utils.UtilMath;
import fr.cocoraid.prodigyforcefield.worldguard.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by cocoraid on 19/08/2017.
 */
public class ProdigyForcefield  extends JavaPlugin implements Listener {


    private static ProdigyForcefield instance;
    private ProdigyForcefieldConfig config;
    private WorldGuardHook worldguard;
    private LocalDatabase database;
    private Map<Entity, Integer> spam = new HashMap<>();


    @Override
    public void onEnable() {
        this.instance = this;

        try {
            config = new ProdigyForcefieldConfig(new File("plugins/ProdigyForcefield", "prodigyforcefield.yml"));
            config.load();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        database = new LocalDatabase();

        ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();

        c.sendMessage(" ");
        c.sendMessage(" ");
        c.sendMessage("§2" + "The Prodigy is the man who knows how to eject someone...");
        c.sendMessage(" ");
        c.sendMessage(" ");
        c.sendMessage("§2" + "Optional Depencies: ");
        c.sendMessage("§a" + "    - WorldGuard: " +  (getServer().getPluginManager().getPlugin("WorldGuard")!=null ? "§a✔" : "§4✘"));
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            this.worldguard = new WorldGuardHook();
            worldguard.setupConfiguration();
        }


        new BukkitRunnable() {
            @Override
            public void run() {

                database.getToggled().removeIf(uuid -> {
                    Player p = Bukkit.getPlayer(uuid);
                    if(p != null) {
                        if (!p.isOnline()) return false;
                        if (!isForcefieldAllowed(p.getLocation()) && !p.hasPermission("prodigyforcefield.bypassregion")) {
                            p.sendMessage(CC.colored(config.toggleRemoved));
                            p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 0);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                });

                database.getToggled().forEach(uuid -> {
                    Player p = Bukkit.getPlayer(uuid);
                    if(p != null) {
                        if(!p.isOnline()) return;
                        Location location = p.getLocation();
                        if(config.mob) {
                            p.getNearbyEntities(config.radius, config.radius, config.radius).stream().filter(en ->!(en instanceof Player) && en instanceof LivingEntity && !en.hasMetadata("NPC")).forEach(en -> {
                                UtilMath.bumpEntity(en,p.getLocation(),config.power,config.ypower);
                                if (config.particle) {
                                    for (int i = 0; i < config.particleNumber; i++) {
                                        Vector vector = UtilMath.getRandomVector().multiply(config.radius);
                                        vector.setY(Math.abs(vector.getY()));
                                        location.add(vector);
                                        p.getWorld().spawnParticle(Particle.valueOf(config.particleType), location, 1, 0, 0, 0, 0F);
                                        location.subtract(vector);
                                    }
                                }

                            });
                        }

                        UtilMath.getClosestPlayersFromLocation(p.getLocation(),config.radius).stream().filter(cur -> !cur.hasMetadata("NPC") &&!cur.equals(p) && !cur.hasPermission("prodigyforcefield.bypass")).forEach(cur -> {

                            UtilMath.bumpEntity(cur,p.getLocation(),config.power,config.ypower);

                            if(!spam.containsKey(cur)) {

                                if(config.particle) {
                                    for (int i = 0; i < config.particleNumber; i++) {
                                        Vector vector = UtilMath.getRandomVector().multiply(config.radius);
                                        vector.setY(Math.abs(vector.getY()));
                                        location.add(vector);
                                        p.getWorld().spawnParticle(Particle.valueOf(config.particleType), location, 1, 0, 0, 0, 0F);
                                        location.subtract(vector);
                                    }
                                }

                                if(config.sound)
                                    cur.playSound(cur.getLocation(), config.soundType, config.volume, config.pitch);
                                if(!config.denyMessage.equalsIgnoreCase("none"))
                                    cur.sendMessage(CC.colored(config.denyMessage));
                                if(!config.denyTitle.equalsIgnoreCase("none"))
                                    cur.sendTitle("", CC.colored(config.denyTitle));
                            }
                            spam.put(cur,20);
                        });
                    }
                });

                spam.keySet().removeIf(id -> {
                    if(spam.get(id) <= 0)
                        return true;
                    else {
                        spam.put(id, spam.get(id) - 1);
                        return false;
                    }
                });

            }
        }.runTaskTimer(this, 1, 0);

    }


    @Override
    public void onDisable() {
        database.saveDatabase();
        if(worldguard != null)
            worldguard.saveConfiguration();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (command.getName().equalsIgnoreCase("prodigyforcefield") || command.getName().equalsIgnoreCase("pf") ) {
                if (args.length == 0) {
                    if(p.hasPermission("prodigyforcefield.toggle")) {
                        if(database.getToggled().contains(p.getUniqueId())) {
                            p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 2);
                            database.removeToggled(p.getUniqueId());
                            p.sendMessage(config.toggleOff.replace("&","§"));
                        } else {
                            if(!isForcefieldAllowed(p.getLocation()) && !p.hasPermission("prodigyforcefield.bypassregion")) {
                                p.sendMessage(CC.colored(config.toggleDeny));
                                return false;
                            }
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0);
                            database.setToggled(p.getUniqueId());
                            p.sendMessage(config.toggleOn.replace("&", "§"));
                        }
                    }else {
                        p.sendMessage(config.permission.replace("&","§"));
                    }
                } else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("reload")) {
                        if(p.hasPermission("prodigyforcefield.reload")) {
                            try {
                                config.load();
                                p.sendMessage("§aConfiguration file are reloaded ! :D");
                            } catch (InvalidConfigurationException e) {
                                e.printStackTrace();
                            }
                        }else {
                            p.sendMessage(config.permission.replace("&","§"));
                        }
                    }else if(args[0].equalsIgnoreCase("help")) {
                        if(p.hasPermission("prodigyforcefield.help")) {
                            p.sendMessage("§6§lProdigy§b§lForcefield");
                            p.sendMessage("§aReload the config file: §c/pf reload");
                            p.sendMessage("§aToggle mode: §c/pf toggle");
                            p.sendMessage("§aToggle mode: §c/pf");
                            p.sendMessage("§aAdd blacklisted region: §c/pf add <region>");
                            p.sendMessage("§aAdd blacklisted region: §c/pf remove <region>");
                        }else {
                            p.sendMessage(config.permission.replace("&","§"));
                        }
                    } else if(args[0].equalsIgnoreCase("toggle")) {
                        if(p.hasPermission("prodigyforcefield.toggle")) {
                            if(database.getToggled().contains(p.getUniqueId())) {
                                p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 2);
                                database.removeToggled(p.getUniqueId());
                                p.sendMessage(config.toggleOff.replace("&","§"));
                            } else {
                                if(!isForcefieldAllowed(p.getLocation()) && !p.hasPermission("prodigyforcefield.bypassregion")) {
                                    p.sendMessage(CC.colored(config.toggleDeny));
                                    return false;
                                }
                                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0);
                                database.setToggled(p.getUniqueId());
                                p.sendMessage(config.toggleOn.replace("&", "§"));
                            }
                        } else {
                            p.sendMessage(config.permission.replace("&","§"));
                        }

                    } else if(Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) {
                        Player cur = Bukkit.getPlayer(args[0]);
                        if(p.hasPermission("prodigyforcefield.toggle.other")) {
                            if(database.getToggled().contains(cur.getUniqueId())) {
                                cur.playSound(cur.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 2);
                                p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 2);
                                database.removeToggled(cur.getUniqueId());
                                cur.sendMessage(config.toggleOff.replace("&","§"));
                                p.sendMessage(config.toggleOff.replace("&","§"));
                            } else {
                                if(!isForcefieldAllowed(cur.getLocation()) && !cur.hasPermission("prodigyforcefield.bypassregion")) {
                                    cur.sendMessage(CC.colored(config.toggleDeny));
                                    p.sendMessage(CC.colored(config.toggleOthersDeny));
                                    return false;
                                }
                                cur.playSound(cur.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0);
                                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0);
                                database.setToggled(cur.getUniqueId());
                                cur.sendMessage(config.toggleOn.replace("&", "§"));
                                p.sendMessage(config.toggleOn.replace("&", "§"));

                            }
                        } else {
                            p.sendMessage(config.permission.replace("&","§"));
                        }
                    }

                    else {
                        p.sendMessage("§c/pf : To show help menu");
                    }

                }else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("add")) {
                        if(p.hasPermission("prodigyforcefield.blacklistregion")) {
                            if (worldguard != null) {
                                worldguard.addBlacklistedRegion(args[1]);
                                p.sendMessage("§aThis region has been added to the blacklist");
                                p.playSound(p.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                            } else {
                                p.sendMessage("§cError: Worldguard is not installed on your server !");
                            }
                        } else {
                            p.sendMessage(config.permission.replace("&","§"));
                        }
                    } else if(args[0].equalsIgnoreCase("remove")) {
                        if(p.hasPermission("prodigyforcefield.blacklistregion")) {
                            if (worldguard != null) {
                                worldguard.removeBlacklistedRegion(args[1]);
                                p.sendMessage("§cThis region has been removed to the blacklist");
                                p.playSound(p.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                            } else {
                                p.sendMessage("§cError: Worldguard is not installed on your server !");
                            }
                        } else {
                            p.sendMessage(config.permission.replace("&","§"));
                        }
                    } else {
                        p.sendMessage("§c/pf : To show help menu");
                    }
                }

                else {
                    p.sendMessage("§c/pf : To show help menu");
                }
            }
        }
        return false;

    }

    public static ProdigyForcefield getInstance() {
        return instance;
    }

    public boolean isForcefieldAllowed(Location l) {
        return worldguard == null ? true : worldguard.isForcefieldAllowed(l);
    }


    public ProdigyForcefieldConfig getForcefieldConfig() {
        return config;
    }

    public LocalDatabase getProdigyMentionDatabase() {
        return database;
    }

}