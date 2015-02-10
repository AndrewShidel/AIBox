package NeuralNetwork;
import java.util.ArrayList;
import java.util.Random;


public class Environment {
	protected static ArrayList<Neuron> sources;
	static int enforcment = 0;

	static int random1 = randInt(1, 128);
	static int random2 = randInt(1, 128);
	static TimerHandler timer = new TimerHandler();
	static int size = 16; // 16 for 2 8-bit numbers

	static Pipe pipe = new Pipe();

	public static void main(String[] args) throws InterruptedException{
		sources = new ArrayList<Neuron>();
		initNetwork(size, pipe, timer);
		System.out.println("Net: " + Pipe.netSize + "  End: " + Pipe.endSize);

		Tick.TickEvent event = new Tick.TickEvent(){
			@Override
			public void run(){
				try{
					readNetwork();
				}catch(InterruptedException e){}
			}
		};
		event.frequency = 50;
		//Tick.addHandler(event);
		runNetwork();
	}
	private static void runNetwork() throws InterruptedException{
		while(true){
			if (enforcment==1){
				enforcment = 0;
				random1 = randInt(1, 128);
				random2 = randInt(1, 128);
			}
			for (int i=0; i<size; i++){
				if (i < size/2){
					if (getBit(random1, i)==1)
						sources.get(i).charge(enforcment, 0);
				}else{
					if (getBit(random2, i-size/2)==1)
						sources.get(i).charge(enforcment, 0);
				}
			}
			readNetwork();
		}
	}
	private static void readNetwork() throws InterruptedException{
		enforcment = -1;
		Integer[] result = pipe.read(1);
		for (int i=0;i<result.length; i++){
			System.out.println("Result: " + result[i]);
			System.out.println("Net: " + Pipe.netSize + "  End: " + Pipe.endSize);
			if(result[i]==random1+random2){
				enforcment = 1;
				System.out.println(random1+"+"+random2+"="+(random1+random2));
			}
		}
	}
	private static void initNetwork(Integer size, Pipe pipe, TimerHandler timer){
		int ratio = (int)(Math.pow(2,Environment.size)/9);
		while(size>0){
			Neuron child = new Neuron(""+size);
			child.initChildren(--size, null,  ratio);
			sources.add(child);
		}
	}
	private static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	private static int getBit(int n, int k) {
	    return (n >> k) & 1;
	}
}
