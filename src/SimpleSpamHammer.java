import java.util.HashMap;
import java.util.Map;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

//Must have a plugin manifest and extends IRC plugin for it to be a valid plugin
//the name is the name of your plugin
@PluginManifest(name = "Simple Spam Hammer")
public class SimpleSpamHammer extends IRCPlugin implements IRCListener {

	private Map<String, Integer> kickAmount = new HashMap<String, Integer>();
	private Map<String, Timer> times = new HashMap<String, Timer>();
	private Map<String, String> channels = new HashMap<String, String>();
	private Object timeLock = new Object();

	@Override
	public void onFinish() {
	}

	@Override
	public boolean onStart() {
		Thread t = new Thread(kickTimer);
		t.setPriority(Thread.MAX_PRIORITY);
		t.setDaemon(false);
		t.start();
		log("Simple Kicker has started successfully.");
		return true;
	}

	private Runnable kickTimer = new Runnable() {

		@Override
		public void run() {
			while (true) {
				synchronized (timeLock) {
					for (String sender : times.keySet()) {
						if (times.get(sender).getRemaining() == 0) {
							times.remove(sender);
							kickAmount.remove(sender);
							mode(channels.get(sender), sender, "-q");
							sendMessageToChannel(channels.get(sender), sender
									+ " you have been unmuted!");
							channels.remove(sender);
						}
					}
				}
			}
		}
	};

	@Override
	public void onMessage(IRCChannel channel, String sender, String message) {
		synchronized (timeLock) {
			if (kickAmount.containsKey(sender)) {
				kickAmount.put(sender, kickAmount.get(sender) + 1);
				if (kickAmount.get(sender) == 5) {
					sendMessageToChannel(
							channel,
							sender
									+ " be quiet for the next 60 seconds or you will be muted!");
					times.put(sender, new Timer(60000));
					channels.put(sender, channel.getChannel());
				}
				if (kickAmount.get(sender) == 10) {
					mode(channel.getChannel(), sender, "+q");
					sendMessageToChannel(channel, sender
							+ " you have been muted for the next 60 seconds!");
				}
			} else {
				kickAmount.put(sender, 0);
			}
		}
	}

	@Override
	public void onPrivateMessage(String to, String from, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnect(IRCServer server) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(IRCServer server) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKick(IRCChannel channel, String kicker, String user,
			String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAction(IRCChannel channel, String sender, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMode(IRCChannel channel, String sender, String user,
			String mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPart(IRCChannel channel, String user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onQuit(IRCChannel channel, String user) {
		// TODO Auto-generated method stub

	}

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
		public Timer(long period) {
			this.period = period;
			this.start = System.currentTimeMillis();
			this.end = start + period;
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
			if (isRunning()) {
				return (end - System.currentTimeMillis());
			}
			return 0;
		}

		/**
		 * Returns <tt>true</tt> if this timer's time period has not yet
		 * elapsed.
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
			this.end = System.currentTimeMillis() + period;
		}

		/**
		 * Sets the end time of this timer to a given number of milliseconds
		 * from the time it is called. This does not edit the period of the
		 * timer (so will not affect operation after reset).
		 * 
		 * @param ms
		 *            The number of milliseconds before the timer should stop
		 *            running.
		 * @return The new end time.
		 */
		public long setEndIn(long ms) {
			this.end = System.currentTimeMillis() + ms;
			return this.end;
		}
	}

    @Override
    public void onJoin(IRCChannel channel, String user, String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onNickChange(IRCChannel channel, String oldNick, String newNick) {
        // TODO Auto-generated method stub
        
    }
}
