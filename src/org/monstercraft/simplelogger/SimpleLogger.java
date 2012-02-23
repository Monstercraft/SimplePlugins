package org.monstercraft.simplelogger;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import org.bukkit.configuration.file.FileConfiguration;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;

//Must have a plugin manifest and extends IRC plugin for it to be a valid plugin
//the name is the name of your plugin
@PluginManifest(name = "Simple Logger")
public class SimpleLogger extends IRCPlugin {

	private FileHandler fh = null;
	private String file = getCacheDirectory() + File.separator
			+ "MonsterIRC.log";

	@Override
	public void onFinish() {
		// Logs that it has successfully stopped.
		log("Simple auto responder has stopped.");
		// stops the logger
		fh.close();
	}

	@Override
	public boolean onStart() {
		// Gets the config
		FileConfiguration config = getConfig();

		// The settings file to load
		File SETTINGS_FILE = new File(getCacheDirectory() + File.separator
				+ "Config.yml");

		// Checks if the file exists.
		boolean exists = SETTINGS_FILE.exists();

		if (exists) {
			try {
				// Loads the settings file if it exists
				config.load(SETTINGS_FILE);

				// set the variables
				file = config.getString("SIMPLE_LOGGER.SAVE_FILE", file);
				if (!new File(file).exists()) {
					new File(file).mkdirs();
				}
			} catch (Exception e) {
				debug(e);
			}
		} else {
			// set the configs
			config.set("SIMPLE_LOGGER.SAVE_FILE", file);

			// save the configs
			saveConfig(config, SETTINGS_FILE);
		}

		try {
			// Sets up the file handler
			fh = new FileHandler(file, true);
			fh.setFormatter(new SimpleFormatter());
		} catch (SecurityException e) {
			debug(e);
		} catch (IOException e) {
			debug(e);
		}
		getLogger().addHandler(fh);
		log("Simple Logger has started successfully.");
		return true;

	}
}
