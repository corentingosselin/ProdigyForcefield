package fr.cocoraid.prodigyforcefield.database;

import fr.cocoraid.prodigyforcefield.ProdigyForcefield;
import org.apache.commons.io.FileUtils;
import org.bukkit.Sound;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cocoraid on 13/02/2017.
 */
public class LocalDatabase {

    private List<UUID> toggled = new ArrayList<>();
    private File file;


    public LocalDatabase() {
        file = getFile();

        try {

            List<String> list = FileUtils.readLines(file, "utf-8");
            list.forEach(s -> {
                toggled.add(UUID.fromString(s));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveDatabase() {
        try{
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            toggled.forEach(uuid -> {
                writer.println(uuid.toString());
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setToggled(UUID uuid) {
      toggled.add(uuid);
    }


    public void removeToggled(UUID uuid) {
        toggled.remove(uuid);
    }



    public File getFile() {
        File file = new File(ProdigyForcefield.getInstance().getDataFolder(), "database" + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public List<UUID> getToggled() {
        return toggled;
    }
}
