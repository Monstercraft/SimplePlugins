import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

// Must have a plugin manifest and extends IRC plugin for it to be a valid plugin
// the name is the name of your plugin
@PluginManifest(name = "Simple Auto Responder")
public class SimpleAutoResponder extends IRCPlugin implements IRCListener,
        Listener {

    private FileConfiguration config;
    private List<String> input;
    private List<String> output;

    public boolean LineContainsWord(final String line, final String Word) {
        try {
            return Pattern.compile("\\b" + Word + "\\b").matcher(line).find();
        } catch (final IllegalStateException e) {
            return false;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void onAction(final IRCChannel arg0, final String arg1,
            final String arg2) {
    }

    @Override
    public void onConnect(final IRCServer arg0) {
    }

    @Override
    public void onDisconnect(final IRCServer arg0) {
    }

    @Override
    public void onFinish() {
        // Logs that it has successfully stopped.
        IRC.log("Simple auto responder has stopped.");
    }

    @Override
    public void onJoin(final IRCChannel channel, final String user,
            final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKick(final IRCChannel channel, final String kicker,
            final String user, final String reason) {
    }

    @Override
    public void onMessage(final IRCChannel c, final String sender,
            final String msg) {
        // Check if the input strings
        for (final String s : input) {
            // Get the indes of the string in the input
            final int index = input.indexOf(s);
            // Check if the input contains the string
            if (this.LineContainsWord(msg, s)) {
                // check for the output message
                if (output.get(index) != null) {
                    // send the output message
                    IRC.sendMessageToChannel(c, output.get(index));
                } else {
                    // The output message was null, something in the config was
                    // wrong.
                    IRC.log("Invalid configuration file for SimpleAutoResponder");
                }
            }
        }
    }

    @Override
    public void onMode(final IRCChannel arg0, final String arg1,
            final String arg2, final String arg3) {
    }

    @Override
    public void onNickChange(final IRCChannel channel, final String oldNick,
            final String newNick) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPart(final IRCChannel arg0, final String arg1) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        // Check if the input strings
        for (final String s : input) {
            // Get the indes of the string in the input
            final int index = input.indexOf(s);
            // Check if the input contains the string
            if (this.LineContainsWord(event.getMessage(), s)) {
                // check for the output message
                if (output.get(index) != null) {
                    // send the output message
                    event.getPlayer().sendMessage(output.get(index));
                } else {
                    // The output message was null, something in the config was
                    // wrong.
                    IRC.log("Invalid configuration file for SimpleAutoResponder");
                }
            }
        }
    }

    @Override
    public void onPrivateMessage(final String to, final String from,
            final String msg) {
    }

    @Override
    public void onQuit(final IRCChannel channel, final String arg1) {
    }

    @Override
    public boolean onStart() {
        // Gets the file configuration.
        config = this.getConfig();

        // Creats an empty array list for us to save the settings to.
        output = new ArrayList<String>();
        input = new ArrayList<String>();

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
                input = config.getStringList("AUTO_RESPONDER.MESSAGE");
                output = config.getStringList("AUTO_RESPONDER.RESPONSE");
            } catch (final Exception e) {
                IRC.debug(e);
            }
        } else {
            // add samples so people know how to correctly modify it
            input.add("hi");
            output.add("Hello.");

            // set the configs
            config.set("AUTO_RESPONDER.MESSAGE", input);
            config.set("AUTO_RESPONDER.RESPONSE", output);

            // save the configs
            this.saveConfig(config, SETTINGS_FILE);
        }

        // Logs in the console that we have started up successfully
        IRC.log("Simple auto responder started.");
        this.registerBukkitListener(this);
        return true;// True for the plugin to start, otherwise false to just
                    // kill the plugin
    }

}
