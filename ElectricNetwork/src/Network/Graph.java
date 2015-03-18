package Network;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import Gates.*;

public abstract class Graph {
	public ArrayList<ArrayList<Connection>> network;
	public ArrayList<Gate> nodes;
	private ArrayList<Integer> queue, preQueue;
	private HashMap<Integer, Integer> enforcementList; // Maps a node index to a counter.
	private byte[] outputArray;
	private int size;
	public int bucketSize = 30;
	public float modularity = (float) 0.05;
	public float outputConnectivity = (float) 0.05;
	
	public int inputSize, hiddenSize, outputSize;
	
	public abstract byte[] onInputRequested();
	public abstract byte[] onOutput(byte[] output);
	public abstract void onLearningFinished();
	
	private static Random rand;
	private static final double normDistMax = 0.39894228; // ~= 1/sqrt(2*pi)

	
	public void initialize(int inputSize, int hiddenSize, int outputSize, int maxConnections) {
		size = inputSize + hiddenSize + outputSize;
		this.inputSize = inputSize;
		this.hiddenSize = hiddenSize;
		this.outputSize = outputSize;
		
		rand = new Random(System.currentTimeMillis());
		
		// Network is the list of Connections. Dimension 1 is the node itself,
		// while dimension two is its connection.  Each Connection holds an index
		// for the nodes list and a terminalID (0=input1, 1=input2, -1=output)
		network = new ArrayList<ArrayList<Connection>>();
		nodes = new ArrayList<Gate>();
		queue = new ArrayList<Integer>();
		preQueue = new ArrayList<Integer>();
		enforcementList = new HashMap<Integer, Integer>();
		
		for (int i=0; i<size; i++) {
			ArrayList<Connection> row = new ArrayList<Connection>();
			//row.add(new Connection(i));
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
			if (numConnections == 0) {
				numConnections = 1;
			}
			for (int j=0; j < numConnections; j++) {
				Connection connection = getRandomConnection(i);
				network.get(i).add(connection);
			}
		}
	}
	
	
	public void startLearning() {
		outputArray = new byte[outputSize / 8];
		int count = 0;
		while (true) {
			count++;
			byte[] input = onInputRequested();
			for (int i=0; i<input.length*8 && i<inputSize; i++) {
				if (isSet(input, i)) {
					ArrayList<Connection> connections = network.get(i);
					int j = 0;
					for (Connection connection : connections) {
						Gate gate = nodes.get(connection.index);
						int terminalID = connection.terminalID;
						if (terminalID == 0){
							gate.setTerm1(true);
							gate.lastConnection = i;
							gate.depth = nodes.get(i).depth+1;
						}else if (terminalID == 1){
							gate.setTerm2(true);
							gate.lastConnection = i;
							gate.depth = nodes.get(i).depth+1;
						}
						preQueue.add(connection.index);
						j++;
					}
				}
			}
			int depth = 0;
			while (!preQueue.isEmpty() && depth < size) {
				for (Integer index : preQueue) {
					if (nodes.get(index).isOpen()) {
						queue.add(index);
					}
				}
				preQueue.clear();
				for (Integer index : queue) {
					ArrayList<Connection> connections = network.get(index);
					int j = 0;
					for (Connection connection : connections) {
						Gate gate = nodes.get(connection.index);
						int terminalID = connection.terminalID;
						if (terminalID == 0){
							gate.setTerm1(true);
							gate.lastConnection = index;
							gate.depth = nodes.get(index).depth+1;
						}else if (terminalID == 1){
							gate.setTerm2(true);
							gate.lastConnection = index;
							gate.depth = nodes.get(index).depth+1;
						}
						if (!preQueue.contains(connection.index)) {
							preQueue.add(connection.index);
						}
						j++;
					}
				}
				queue.clear();
				depth++;
			}
			int bitNum = 0;
			for (int i=nodes.size() - outputSize; i<nodes.size(); i++) {
				if (((Output)nodes.get(i)).isSet()) {
					setBit(bitNum);
				}
				bitNum++;
			}
			byte[] error = onOutput(outputArray);
			
			for (int i=0; i<error.length*8; i++) {
				if (isSet(error, i)) {
					// TODO: Backpropegate negative
					backpropegate(inputSize + hiddenSize + i, -1);
				} else {
					// TODO: Backpropegate positive
					backpropegate(inputSize + hiddenSize + i, 1);
				}
			}
			
			try {
				NetworkPrinter.printSpringy(this, new PrintWriter("/home/andrew/Downloads/dhotson-springy-559a400/additionNetwork.html"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (int i=0; i<nodes.size(); i++) {
				nodes.get(i).reset();
			}
		}
	}
	
	private void backpropegate(int start, float error) {
		Gate to = nodes.get(start);
		int maxDepth = to.depth;
		
		int count = 0;
		while (to.depth > 0 && count < maxDepth) {
			count++;
			
			int from = to.lastConnection;
			if (from == -1){
				return;
			}
			int innerIndex = 0;
			Connection connection = null;
			for (Connection con : network.get(from)) {
				if (con.index == start){
					connection = con;
					break;
				}
				innerIndex++;
			}
			if (connection == null) {
				start = from;
				to = nodes.get(start);
				continue;
			}
			if (error > 0) {
				Integer enforceCount = enforcementList.get(connection.conIndex);
				if (enforceCount==null) {enforceCount=0;}
				enforcementList.put(connection.conIndex, enforceCount+1);
			} else if (error < 0) {
				Integer enforceCount = enforcementList.get(connection.conIndex);
				if (enforceCount == null || enforceCount-- <= 0) {
					if (rand.nextDouble()*normDistMax < normDist(((double)count/maxDepth)*4 - 2)) {
						network.get(from).remove(innerIndex);
						
						int randIndex = rand.nextInt(inputSize+hiddenSize);
						int tryCount = 0;
						while(network.get(randIndex).contains(connection) && tryCount < 10){
							randIndex = rand.nextInt(inputSize+hiddenSize);
							tryCount++;
						}
						if (tryCount < 10) {
							network.get(randIndex).add(connection);
						}
					}
				}
				if (enforceCount != null && enforceCount <= 0) {
					enforcementList.remove(connection.conIndex);
				}
			}
			start = from;
			to = nodes.get(start);
		}
		
	}
	
	/**
	 * Normal distribution curve assuming mu=0 and sigma=1.
	 */
	private double normDist(double x) {
		return 1/(Math.sqrt(2 * Math.PI) * Math.sqrt(Math.exp(Math.pow(x,2))));
	}
	
	private double normDist(double mu, double sigma, double x) {
		return Math.exp(-1*Math.pow((x-mu),2)/(2*Math.pow(sigma, 2)))/(Math.sqrt(2*Math.PI)*sigma);
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
	
	private Connection getRandomConnection(int fromIndex) {
		int numBuckets = size/bucketSize;
		int bucket = fromIndex%numBuckets;
		int toIndex = Integer.MAX_VALUE;
		float randNum = rand.nextFloat();
		if (randNum < modularity) {
			do{
				toIndex = rand.nextInt(numBuckets) + bucketSize*rand.nextInt(2);
			}while(toIndex%numBuckets == bucket);
		} else if(randNum < modularity + outputConnectivity){
			toIndex = inputSize + hiddenSize + rand.nextInt(outputSize) - 1;
		} else {
			toIndex = numBuckets*rand.nextInt(bucketSize) + bucket;
		}
		if (toIndex < inputSize || toIndex > size || toIndex == fromIndex) {
			return getRandomConnection(fromIndex);
		}
		return new Connection(toIndex, (byte)(rand.nextBoolean()==true?0:1));
	}
	public static class Connection {
		public static int totalConnections = 0;
		public int conIndex;
		public int index;
		public byte terminalID = -1; // -1 = output terminal
		public Point lastConnected = null; // null = No one has connected to this terminal
		public Connection(int index) {
			this.index = index;
			terminalID = 1;
			conIndex = totalConnections;
			totalConnections++;
		}
		public Connection(int index, byte terminalID) {
			this.index = index;
			this.terminalID = terminalID;
			conIndex = totalConnections;
			totalConnections++;
		}
	}
	public static class Point {
		public int x; // From
		public int y; // To
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
