package fr.cocoraid.prodigyforcefield.database;

import fr.cocoraid.prodigyforcefield.utils.Config;
import org.bukkit.Sound;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cocoraid on 19/08/2017.
 */
public class ProdigyForcefieldConfig extends Config {

    static String comment[] = {"/*Welcome to ProdigyForcefield configuration*/", " To disable a message/title just set to 'none'", " You can get the list of particle type right here: ", "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html "};


    @ConfigOptions(name = "permission.message")
    public String permission = "&cYou do not have the permission";

    @ConfigOptions(name = "forcefield.deny.message")
    public String denyMessage = "&cYou can not get closer to this player";
    @ConfigOptions(name = "forcefield.deny.title")
    public String denyTitle = "&cYou can not get closer to this player";
    @ConfigOptions(name = "forcefield.radius")
    public double radius = 3;

    @ConfigOptions(name = "forcefield.power")
    public double power = 1.5;
    @ConfigOptions(name = "forcefield.ypower")
    public double ypower = 1;

    @ConfigOptions(name = "forcefield.sound.enable")
    public boolean sound = true;
    @ConfigOptions(name = "forcefield.sound.type")
    public Sound soundType = Sound.ENTITY_ELDER_GUARDIAN_CURSE;
    @ConfigOptions(name = "forcefield.sound.volume")
    public float volume = 1;
    @ConfigOptions(name = "forcefield.sound.pitch")
    public float pitch = 2;


    @ConfigOptions(name = "forcefield.particle.enable")
    public boolean particle = true;
    @ConfigOptions(name = "forcefield.mob.enable")
    public boolean mob = true;
    @ConfigOptions(name = "forcefield.particle.type")
    public String particleType = "CRIT_MAGIC";
    @ConfigOptions(name = "forcefield.particle.number")
    public int particleNumber = 100;

    @ConfigOptions(name = "toggle.message.on")
    public String toggleOn = "&aForcefield activated !";
    @ConfigOptions(name = "toggle.message.off")
    public String toggleOff = "&cForcefield is now disabled";
    @ConfigOptions(name = "toggle.message.deny")
    public String toggleDeny = "&cYou cannot activate your forcefield in this area...";
    @ConfigOptions(name = "toggle.message.denyOthers")
    public String toggleOthersDeny = "&cYou cannot activate the forcefield  for this player, he is in a wrong area...";
    @ConfigOptions(name = "toggle.message.removed")
    public String toggleRemoved = "&cYour forcefield has been disabled, you cannot use it in this area...";

    @ConfigOptions(name = "blacklisted.regions")
    public List<String> blacklisted = new ArrayList<>(Arrays.asList("blacklistedRegion1","blacklistedRegion2"));

    public ProdigyForcefieldConfig(final File file) {
        super(file, Arrays.asList(comment));
    }
}
