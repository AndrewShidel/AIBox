import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Pipe {
	private List<Boolean> buffer;
	private Boolean running = false;
	private int total = 0; //Remove this.
	public Pipe(){
		buffer = Collections.synchronizedList(new ArrayList<Boolean>());
	}
	public void write(int bit){
		total += bit;
		buffer.add(bit==1?true:false);
	}
	public char[] read(int size) throws InterruptedException{
		while (buffer.size()<size*8){
			Thread.sleep(50);
		}
		/*char[] c =  new char[]{(char)total};
		total = 0;
		clear();
		return c;*/
		char[] res = new char[]{bitsToChar(size*8)};
		clear();
		return res;
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
