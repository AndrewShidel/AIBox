import java.util.ArrayList;
import java.util.Random;

public class Neuron {
	protected int enforcementCounter;
	protected ArrayList<Neuron> connections;
	private String id;
	private static Random rand = new Random();
	private int lastFireTime;
	private int ACTION_POTENTIAL = 3;
	private int energy = 0;
	private Boolean isEnd = false;

	public Neuron(String id){
		connections = new ArrayList<Neuron>();
		this.id = id;
		this.lastFireTime = -1*ACTION_POTENTIAL;
		System.out.println("Id: "+id);
		Pipe.netSize++;
	}
	public void charge(int enforcement, int depth){
		energy++;
		int time = Tick.ticks;
		if (energy < ACTION_POTENTIAL){
			//System.out.println("AP!!! "  + (time-lastFireTime) + "  Res: " + ((time - lastFireTime) < ACTION_POTENTIAL));
			return;
		}
		//System.out.println("NOT!!!");
		lastFireTime = time;
		if (depth>1024) {
			//System.out.println("Depth!!!");
			return;
		}
		energy = 0;
		Tick.tick();
		enforcementCounter += enforcement;
		if (enforcementCounter<0){
			shakeItUp();
			enforcementCounter = 0;
		}
		if (isEnd){
			//Then this is an end neuron
			queueBit();
		}
		for (int i=0; i<connections.size(); i++){
			connections.get(i).charge(enforcement, ++depth);
		}
	}
	public void initChildren(int size, Neuron parent, Integer ratio){
		if (Pipe.netSize%ratio==0){
			isEnd=true;
			Pipe.write(id, false);
		}
		while (size>0){
			Neuron child = new Neuron(id+size);
			child.initChildren(--size, this, ratio);
			connections.add(child);
		}
	}
	public void shakeItUp(){
		//ACTION_POTENTIAL = randInt(20, 200);
		if (connections.size()>0 && randCondition()){
			connections.remove(randInt(0, connections.size()-1));
			if (connections.size()==0) Pipe.endSize++;
		}else{
			Neuron mate = findNeuron(0);
			mate.connect(this);
			connections.add(mate);
			if (connections.size() == 1) Pipe.endSize--;
		}
	}
	public Neuron findNeuron(int depth){
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
		Pipe.write(id, true);
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
