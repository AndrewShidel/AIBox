import java.util.ArrayList;

public class Tick {
	private static ArrayList<TickEvent> events = new ArrayList<TickEvent>();
	public static int ticks = 0;

	public Tick() {
		// events = new ArrayList<TickEvent>();
	}

	public static void tick() {
		++ticks;
		TickEvent event;
		for (int i = 0; i < events.size(); i++) {
			event = events.get(i);
			if (ticks >= event.nextRun) {
				//System.out.println("Running event: " + ticks);
				event.run();
				event.nextRun += event.frequency;
			}
		}
	}

	public static void addHandler(TickEvent event) {
		event.nextRun = event.frequency;
		events.add(event);
	}

	public static abstract class TickEvent {
		public int frequency = 0;
		public int nextRun;

		public abstract void run();
	}
}
