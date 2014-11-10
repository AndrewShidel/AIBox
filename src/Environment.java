import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;


public class Environment {
	protected static ArrayList<Neuron> sources;
	public static void main(String[] args) throws InterruptedException{
		sources = new ArrayList<Neuron>();
		TimerHandler timer = new TimerHandler();
		int size = 16; // 16 for 2 8-bit numbers

		Pipe pipe = new Pipe();
		initNetwork(size, pipe, timer); 
		
		int enforcment = 0;
		
		int random1 = randInt(1, 128);
		int random2 = randInt(1, 128);
		while(true){
			for (int i=0; i<size; i++){
				if (i < size/2){
					if (getBit(random1, i)==1)
						sources.get(i).charge(null, enforcment, 0);
				}else{
					if (getBit(random2, i-size/2)==1)
						sources.get(i).charge(null, enforcment, 0);
				}
			}
			int result = (int) pipe.read(1)[0];
			System.out.println(result);
			if (enforcment==1){
				enforcment = 0;
				random1 = randInt(1, 128);
				random2 = randInt(1, 128);
			}else if(result==random1+random2){
				enforcment = 1;
				System.out.println(random1+"+"+random2+"="+(random1+random2));
			}else{
				enforcment = -1;
			}
		}
	}
	private static void initNetwork(int size, Pipe pipe, TimerHandler timer){
		while(size>0){
			Neuron child = new Neuron(pipe, timer, ""+size);
			child.initChildren(size);
			sources.add(child);
			--size;
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
