import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import org.bukkit.configuration.file.FileConfiguration;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;

// Must have a plugin manifest and extends IRC plugin for it to be a valid plugin
// the name is the name of your plugin
@PluginManifest(name = "Simple Logger")
public class SimpleLogger extends IRCPlugin {

    private FileHandler fh = null;
    private String file = this.getCacheDirectory() + File.separator;

    @Override
    public void onFinish() {
        // Logs that it has successfully stopped.
        IRC.log("Simple auto responder has stopped.");
        // stops the logger
        fh.close();
    }

    @Override
    public boolean onStart() {
        // Gets the config
        final FileConfiguration config = this.getConfig();

        // The settings file to load
        final File SETTINGS_FILE = new File(this.getCacheDirectory()
                + File.separator + "Config.yml");

        // Checks if the file exists.
        final boolean exists = SETTINGS_FILE.exists();

        if (exists) {
            try {
                // Loads the settings file if it exists
                config.load(SETTINGS_FILE);

                // set the variables
                file = config.getString("SIMPLE_LOGGER.SAVE_PATH", file);
                new File(file).mkdir();
            } catch (final Exception e) {
                IRC.debug(e);
            }
        } else {
            // set the configs
            config.set("SIMPLE_LOGGER.SAVE_PATH", file);

            // save the configs
            this.saveConfig(config, SETTINGS_FILE);
        }
        final String date = new SimpleDateFormat("EEE, MMM d, ''yy, hmm a")
                .format(new Date()).toString();
        final String f = file + File.separator + date + ".log";
        try {
            // Sets up the file handler
            fh = new FileHandler(f, true);

            // Sets the formatting type
            fh.setFormatter(new SimpleFormatter());
        } catch (final SecurityException e) {
            // debugs the error using monsterirc's debug method
            IRC.debug(e);
        } catch (final IOException e) {
            IRC.debug(e);
        }

        // Adds the file handler
        IRC.getLogger().addHandler(fh);
        IRC.log("Simple Logger has started successfully.");
        return true;

    }
}
