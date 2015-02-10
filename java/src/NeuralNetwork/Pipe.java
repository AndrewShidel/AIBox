package NeuralNetwork;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Pipe {
	private List<Boolean> buffer;
	private static int total = 0;
	public static int netSize = 0;
	public static int endSize = 0;
	public List<Integer> values;
	private static HashMap<String, Boolean> states = new HashMap<String, Boolean>();;
	public Pipe(){
		buffer = Collections.synchronizedList(new ArrayList<Boolean>());
		values = Collections.synchronizedList(new ArrayList<Integer>());
		final int frequency = 1000;
		Tick.TickEvent event= new Tick.TickEvent(){
			@Override
			public void run(){
				//System.out.println("Writes: " + total);
				if (total>0){
					values.add((Integer) frequency/total);
					total=0;
				}
			}
		};
		event.frequency = frequency;
		Tick.addHandler(event);
	}
	public void write(int bit){
		total += bit;
		buffer.add(bit==1?true:false);
	}
	public static void write(){
		total++;
	}
	public static void write(String id, Boolean bit){
		states.put(id, bit);
	}
	public Integer[] read(int size) throws InterruptedException{
		int value = 0;
		int count = 1;
		for (String key: states.keySet()){
			value+=(states.get(key)?1:0)*count;
			count*=2;
		}
		return new Integer[]{value};
		/*Integer[] vals = values.toArray(new Integer[values.size()]);
		if (vals.length>0)
			values.clear();
		return vals;*/
		/*while (buffer.size()<size*8){
			Thread.sleep(50);
		}*/
		/*char[] c =  new char[]{(char)total};
		total = 0;
		clear();
		return c;*/
		/*char[] res = new char[]{bitsToChar(size*8)};
		clear();
		return res;*/
	}
	public void clear(){
		buffer.clear();
	}
	private char bitsToChar(int size){
		int n = 0;
		for (int i = 0; i < size; i++) {
			n = (n << 1) + (buffer.get(i) ? 1 : 0);
		}
		return (char)n;
	}
}
