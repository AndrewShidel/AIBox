import java.util.ArrayList;

public class Tick {
  private ArrayList<TickEvent> events;
  private int ticks = 0;
  public Tick(){
    events = new ArrayList<TickEvent>();
  }
  public void tick(){
    ++ticks;
    TickEvent event;
    for (int i=0; i<events.size(); i++){
      event = events.get(i);
      if (event.nextRun>=ticks){
        event.run();
        event.nextRun += event.frequency;
      }
    }
  }
  public void addHandler(TickEvent event){
    event.nextRun = event.frequency;
    events.add(event);
  }
  public abstract class TickEvent {
    public int frequency=0;
    public int nextRun=0;
    public abstract void run();
  }
}
