package NeuralNetwork;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class TimerHandler {
	private HashMap<String, TimerHandlerTask> tasks;
	private Timer timer;
	private Boolean running = false;
	public TimerHandler(){
		tasks = new HashMap<String, TimerHandler.TimerHandlerTask>();
		timer = new Timer();
		timer.scheduleAtFixedRate(new RunTasks(), 500, 500);
	}
	public void addTask(String id, TimerHandlerTask task){
		if (!running) tasks.put(id, task);
	}
	public void removeTask(String id){
		if (!running) tasks.remove(id);
	}
	public interface TimerHandlerTask{
		public void task();
	}
	private class RunTasks extends TimerTask{
		@Override
		public void run() {
			if (!running){
				running = true;
				for (String key: tasks.keySet()){
					tasks.get(key).task();
				}
				running = false;
			}
		}
	}
}
