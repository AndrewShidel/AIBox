import java.util.ArrayList;
import java.util.Random;

public class Neuron {
	protected int enforcementCounter;
	protected ArrayList<Neuron> connections;
	private Pipe pipe;
	private int bit = -1;
	private TimerHandler timer;
	private String id;
    private static Random rand = new Random();
    private long lastFireTime;
    private int ACTION_POTENTIAL = 10;
	public Neuron(Pipe pipe, TimerHandler timer, String id){
		this.pipe = pipe;
		connections = new ArrayList<Neuron>();
		this.timer = timer;
		this.id = id;
		this.lastFireTime = System.currentTimeMillis()-ACTION_POTENTIAL;
		System.out.println("Id: "+id);
	}
	public void charge(Neuron source, int enforcement, int depth){
		long time = System.currentTimeMillis();
		if (time - lastFireTime < ACTION_POTENTIAL){
			return;
		}
		lastFireTime = time;
		if (depth>80) return;
		enforcementCounter += enforcement;
		if (enforcement<0){
			shakeItUp();
			enforcementCounter=1;
		}
		if (connections.size() == 0){
			//Then this is an end neuron
			queueBit();
		}else{
			for (int i=0; i<connections.size(); i++){
				connections.get(i).charge(this, enforcement, ++depth);
			}
		}
	}
	public void initChildren(int size){
		for (int i=0; i<size; ++i){
			Neuron child = new Neuron(pipe, timer, id+size);
			child.initChildren(--size);
			connections.add(child);
		}
	}
	public void shakeItUp(){
		ACTION_POTENTIAL = randInt(20, 200);
		if (connections.size()>0 && randCondition()){
			connections.remove(randInt(0, connections.size()-1));
			if (connections.size()==0){
				bit = -1;
			}
		}else{
			Neuron mate = findNeuron(0);
			mate.connect(this);
			connections.add(mate);
			if (timer!=null){
				timer.removeTask(id);
			}
		}
	}
	private Neuron findNeuron(int depth){
		if (connections.size() == 0){
			return this;
		}else if ((depth>0 && randCondition())){
			return connections.get(randInt(0, connections.size()-1));
		}else{
			return connections.get(randInt(0, connections.size()-1)).findNeuron(depth+1);
		}
	}
	public void connect(Neuron connection){
		//connections.add(connection);
	}
	private void queueBit(){
		if (bit==-1){
			// Set the timer
			timer.addTask(id, new TimerHandler.TimerHandlerTask() {
				public void task() {
					write();
				}
			});
		}
		bit = 1;
	}
	private void write(){
		pipe.write(bit);
		bit = 0;
	}
	private static Boolean randCondition() {
	    return rand.nextBoolean();
	}
	private static int randInt(int min, int max) {
		try{
			return rand.nextInt((max - min) + 1) + min;
		}catch (StackOverflowError e){
			e.printStackTrace();
		}
	    return 0;
	}
}
