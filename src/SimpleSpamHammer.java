import java.util.HashMap;
import java.util.Map;

import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

// Must have a plugin manifest and extends IRC plugin for it to be a valid plugin
// the name is the name of your plugin
@PluginManifest(name = "Simple Spam Hammer")
public class SimpleSpamHammer extends IRCPlugin implements IRCListener {

    /**
     * Timer
     */
    public class Timer {

        private long end;
        private final long start;
        private final long period;

        /**
         * Instantiates a new Timer with a given time period in milliseconds.
         *
         * @param period
         *            Time period in milliseconds.
         */
        public Timer(final long period) {
            this.period = period;
            start = System.currentTimeMillis();
            end = start + period;
        }

        /**
         * Returns the number of milliseconds elapsed since the start time.
         *
         * @return The elapsed time in milliseconds.
         */
        public long getElapsed() {
            return (System.currentTimeMillis() - start);
        }

        /**
         * Returns the number of milliseconds remaining until the timer is up.
         *
         * @return The remaining time in milliseconds.
         */
        public long getRemaining() {
            if (this.isRunning()) {
                return (end - System.currentTimeMillis());
            }
            return 0;
        }

        /**
         * Returns <tt>true</tt> if this timer's time period has not yet elapsed.
         *
         * @return <tt>true</tt> if the time period has not yet passed.
         */
        public boolean isRunning() {
            return (System.currentTimeMillis() < end);
        }

        /**
         * Restarts this timer using its period.
         */
        public void reset() {
            end = System.currentTimeMillis() + period;
        }

        /**
         * Sets the end time of this timer to a given number of milliseconds from the time it is called. This does not edit the period of the timer
         * (so will not affect operation after reset).
         *
         * @param ms
         *            The number of milliseconds before the timer should stop running.
         * @return The new end time.
         */
        public long setEndIn(final long ms) {
            end = System.currentTimeMillis() + ms;
            return end;
        }
    }

    private final Map<String, Integer> kickAmount = new HashMap<String, Integer>();
    private final Map<String, Timer> times = new HashMap<String, Timer>();
    private final Map<String, String> channels = new HashMap<String, String>();

    private final Object timeLock = new Object();

    private final Runnable kickTimer = new Runnable() {

        @Override
        public void run() {
            while (true) {
                synchronized (timeLock) {
                    for (final String sender : times.keySet()) {
                        if (times.get(sender).getRemaining() == 0) {
                            times.remove(sender);
                            kickAmount.remove(sender);
                            IRC.mode(channels.get(sender), sender, "-q");
                            IRC.sendMessageToChannel(channels.get(sender),
                                    sender + " you have been unmuted!");
                            channels.remove(sender);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onAction(final IRCChannel channel, final String sender,
            final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnect(final IRCServer server) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDisconnect(final IRCServer server) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onJoin(final IRCChannel channel, final String user,
            final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKick(final IRCChannel channel, final String kicker,
            final String user, final String reason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMessage(final IRCChannel channel, final String sender,
            final String message) {
        synchronized (timeLock) {
            if (kickAmount.containsKey(sender)) {
                kickAmount.put(sender, kickAmount.get(sender) + 1);
                if (kickAmount.get(sender) == 5) {
                    IRC.sendMessageToChannel(
                            channel,
                            sender
                                    + " be quiet for the next 60 seconds or you will be muted!");
                    times.put(sender, new Timer(60000));
                    channels.put(sender, channel.getChannel());
                }
                if (kickAmount.get(sender) == 10) {
                    IRC.mode(channel.getChannel(), sender, "+q");
                    IRC.sendMessageToChannel(channel, sender
                            + " you have been muted for the next 60 seconds!");
                }
            } else {
                kickAmount.put(sender, 0);
            }
        }
    }

    @Override
    public void onMode(final IRCChannel channel, final String sender,
            final String user, final String mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNickChange(final IRCChannel channel, final String oldNick,
            final String newNick) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPart(final IRCChannel channel, final String user) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPrivateMessage(final String to, final String from,
            final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onQuit(final IRCChannel channel, final String user) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onStart() {
        final Thread t = new Thread(kickTimer);
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(false);
        t.start();
        IRC.log("Simple Kicker has started successfully.");
        return true;
    }
}
