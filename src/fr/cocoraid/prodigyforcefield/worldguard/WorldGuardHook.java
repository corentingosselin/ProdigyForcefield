package fr.cocoraid.prodigyforcefield.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.cocoraid.prodigyforcefield.ProdigyForcefield;
import fr.cocoraid.prodigyforcefield.database.ProdigyForcefieldConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by cocoraid on 20/08/2017.
 */
public class WorldGuardHook
{

    private ProdigyForcefieldConfig config =  ProdigyForcefield.getInstance().getForcefieldConfig();
    private WorldGuardPlugin hook;
    private Set<String> regionBlacklist;

    public WorldGuardHook() {
        this.hook = null;
        this.hook = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        this.regionBlacklist = new HashSet<String>();
    }


    public boolean isForcefieldAllowed(final Location loc) {
        final RegionManager manager = this.hook.getRegionManager(loc.getWorld());
        final Iterator<String> iter = this.regionBlacklist.iterator();
        while (iter.hasNext()) {
            final ProtectedRegion region = manager.getRegion(iter.next());
            if (region != null && region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                return false;
            }
        }
        return true;
    }

    public void setupConfiguration() {
        this.regionBlacklist.addAll(config.blacklisted);
    }


    public void saveConfiguration() {
        try {
            config.blacklisted.clear();
            config.blacklisted.addAll(this.regionBlacklist);
            config.save();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    public void addBlacklistedRegion(final String region) {
        try {
            this.regionBlacklist.add(region);
            config.blacklisted.add(region);
            config.save();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void removeBlacklistedRegion(final String region) {
        try {
            this.regionBlacklist.remove(region);
            config.blacklisted.remove(region);
            config.save();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getBlacklistedRegions() {
        return this.regionBlacklist;
    }
}
