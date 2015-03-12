package Network;

import java.util.ArrayList;
import java.util.Random;

import Gates.*;
import Gates.Input;

public abstract class Graph {
	public ArrayList<ArrayList<Connection>> network;
	public ArrayList<Gate> nodes;
	private ArrayList<Integer> queue;
	private int size;
	private static final int bucketSize = 50;
	
	private int inputSize, hiddenSize, outputSize;
	
	public abstract byte[] onInputRequested();
	public abstract float onOutput(byte[] output);
	public abstract void onLearningFinished();

	
	public void initialize(int inputSize, int hiddenSize, int outputSize, int maxConnections) {
		size = inputSize + hiddenSize + outputSize;
		this.inputSize = inputSize;
		this.hiddenSize = hiddenSize;
		this.outputSize = outputSize;
		
		// Network is the list of Connections. Dimension 1 is the node itself,
		// while dimension two is its connection.  Each Connection holds an index
		// for the nodes list and a terminalID (0=input1, 1=input2, -1=output)
		network = new ArrayList<ArrayList<Connection>>();
		nodes = new ArrayList<Gate>();
		queue = new ArrayList<Integer>();
		
		Random rand = new Random();
		for (int i=0; i<size; i++) {
			ArrayList<Connection> row = new ArrayList<Connection>();
			row.add(new Connection(i));
			network.add(row);
			
			Gate gate = null;
			if (i < inputSize) {
				gate = new Input();
			} else if (i < inputSize + hiddenSize) {
				int type = (int)(rand.nextFloat()*3);
				switch (type) {
				case 0:
					gate = new And();
					break;
				case 1:
					gate = new Or();
					break;
				case 2:
					gate = new Not();
					break;
				}
			} else {
				gate = new Output();
			}
			nodes.add(gate);
		}
		
		for (int i=0; i<inputSize+hiddenSize; i++) {
			for (int j=0; j < rand.nextInt(maxConnections); j++) {
				Connection connection = getRandomConnection(i, rand);
				network.get(i).add(connection);
			}
		}
		startLearning();
	}
	
	private void startLearning() {
		byte[] input = onInputRequested();
		for (int i=0; i<input.length*8 && i<inputSize; i++) {
			if (isSet(input, i)) {
				ArrayList<Connection> connections = network.get(i);
				for (Connection connection : connections) {
					Gate gate = nodes.get(connection.index);
					int terminalID = connection.terminalID;
					if (terminalID == 0){
						gate.term1 = true;
					}else if (terminalID == 1){
						gate.term2 = true;
					}
					queue.add(connection.index);
				}
			}
		}
	}
	
	private boolean isSet(byte[] arr, int bit) {
	    int index = bit / 8;  // Get the index of the array for the byte with this bit
	    int bitPosition = bit % 8;  // Position of this bit in a byte

	    return (arr[index] >> bitPosition & 1) == 1;
	}
	
	private Connection getRandomConnection(int fromIndex, Random rand) {
		int bucket = fromIndex/bucketSize;
		int toIndex;
		if ((bucket+1)*bucketSize - fromIndex < 2 && rand.nextFloat() < .2) {
			toIndex = ((int)(rand.nextFloat()*(size/bucketSize)))+1;
		} else {
			toIndex = bucket*bucketSize+rand.nextInt((bucketSize+1));
		}
		Connection connection = new Connection(toIndex, (byte)(rand.nextBoolean()==true?0:1));
		
		return connection;
	}
	private class Connection {
		public int index;
		public byte terminalID = -1; // -1 = output terminal
		public Connection(int index) {
			this.index = index;
		}
		public Connection(int index, byte terminalID) {
			this.index = index;
			this.terminalID = terminalID;
		}
	}
}
