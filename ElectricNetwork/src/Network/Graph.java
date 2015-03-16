package Network;

import java.util.ArrayList;
import java.util.Random;

import Gates.*;

public abstract class Graph {
	public ArrayList<ArrayList<Connection>> network;
	public ArrayList<Gate> nodes;
	private ArrayList<Integer> queue, preQueue;
	private byte[] outputArray;
	private int size;
	public int bucketSize = 10;
	
	private int inputSize, hiddenSize, outputSize;
	
	public abstract byte[] onInputRequested();
	public abstract byte[] onOutput(byte[] output);
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
		preQueue = new ArrayList<Integer>();
		
		Random rand = new Random();
		for (int i=0; i<size; i++) {
			ArrayList<Connection> row = new ArrayList<Connection>();
			row.add(new Connection(i));
			network.add(row);
			
			Gate gate = null;
			if (i < inputSize) {
				gate = new Input();
			} else if (i < inputSize + hiddenSize) {
				int type = (int)(rand.nextFloat()*4);
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
				case 3:
					gate = new Transistor();
				}
			} else {
				gate = new Output();
			}
			nodes.add(gate);
		}
		
		for (int i=0; i<inputSize+hiddenSize; i++) {
			int numConnections = rand.nextInt(maxConnections);
			if (numConnections == 0 && i < inputSize) {
				numConnections = 1;
			}
			for (int j=0; j < numConnections; j++) {
				Connection connection = getRandomConnection(i, rand);
				network.get(i).add(connection);
			}
		}
	}
	
	
	public void startLearning() {
		outputArray = new byte[outputSize / 8];
		while (true) {
			byte[] input = onInputRequested();
			for (int i=0; i<input.length*8 && i<inputSize; i++) {
				if (isSet(input, i)) {
					ArrayList<Connection> connections = network.get(i);
					int j = 0;
					for (Connection connection : connections) {
						Gate gate = nodes.get(connection.index);
						int terminalID = connection.terminalID;
						if (terminalID == 0){
							gate.term1 = true;
							connection.lastConnected = new Point(i, j);
						}else if (terminalID == 1){
							gate.term2 = true;
							connection.lastConnected = new Point(i, j);
						}
						preQueue.add(connection.index);
						j++;
					}
				}
			}
			while (!preQueue.isEmpty()) {
				for (Integer index : preQueue) {
					if (nodes.get(index).isOpen()) {
						queue.add(index);
					}
				}
				for (Integer index : queue) {
					ArrayList<Connection> connections = network.get(index);
					int j = 0;
					for (Connection connection : connections) {
						Gate gate = nodes.get(connection.index);
						int terminalID = connection.terminalID;
						if (terminalID == 0){
							gate.term1 = true;
							connection.lastConnected = new Point(index, j);
						}else if (terminalID == 1){
							gate.term2 = true;
							connection.lastConnected = new Point(index, j);
						}
						preQueue.add(connection.index);
						j++;
					}
				}
			}
			int bitNum = 0;
			for (int i=nodes.size() - outputSize - 1; i<nodes.size(); i++) {
				if (((Output)nodes.get(i)).isSet()) {
					setBit(bitNum);
				}
				bitNum++;
			}
			byte[] error = onOutput(outputArray);
			
			for (int i=0; i<error.length*8; i++) {
				if (isSet(error, i)) {
					// TODO: Backpropegate negative
					
				} else {
					// TODO: Backpropegate positive
				}
			}
			
			for (int i=0; i<nodes.size(); i++) {
				nodes.get(i).reset();
			}
		}
	}
	
	private boolean isSet(byte[] arr, int bit) {
	    int index = bit / 8;  // Get the index of the array for the byte with this bit
	    int bitPosition = bit % 8;  // Position of this bit in a byte

	    return (arr[index] >> bitPosition & 1) == 1;
	}
	
	private void setBit(int index) {
		int arrIndex = index/8;
		byte byteIndex = (byte) (index%8);
		outputArray[arrIndex] |= 1 << byteIndex;
	}
	
	private Connection getRandomConnection(int fromIndex, Random rand) {
		if (fromIndex > inputSize+hiddenSize) 
			return null;
		int bucket = fromIndex/bucketSize;
		int offset = size - bucket*bucketSize < bucketSize ? bucketSize - (size - bucket*bucketSize) : 0;
		int toIndex;
		if (/*(bucket+1)*bucketSize - fromIndex < 2 && */rand.nextFloat() < .1) {
			toIndex = (((int)(rand.nextFloat()*(size/bucketSize)))) * bucketSize + rand.nextInt((bucketSize-offset));
		} else {
			toIndex = bucket*bucketSize+rand.nextInt((bucketSize-offset));
			if (toIndex > size)
				return null;
		}
		if (toIndex < inputSize) {
			return getRandomConnection(fromIndex, rand);
		}
		Connection connection = new Connection(toIndex, (byte)(rand.nextBoolean()==true?0:1));
		
		return connection;
	}
	public static class Connection {
		public int index;
		public byte terminalID = -1; // -1 = output terminal
		public Point lastConnected = null; // null = No one has connected to this terminal
		public Connection(int index) {
			this.index = index;
		}
		public Connection(int index, byte terminalID) {
			this.index = index;
			this.terminalID = terminalID;
		}
	}
	public static class Point {
		public int x;
		public int y;
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
