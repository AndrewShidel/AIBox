package NeuralNetwork;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

// Idea: connections should be strengthened every time a synapse fires
public abstract class NeuralNetwork {
	private int inputSize;
	private int hiddenSize;
	private int outputSize;
	private double score;
	
	public abstract byte[] onInputRequested();
	public abstract float onOutput(byte[] output);
	public abstract void onLearningFinished();
	
	public void initialize(int inputSize, int hiddenSize, int outputSize) {
		this.inputSize = inputSize;
		this.hiddenSize = hiddenSize;
		this.outputSize = outputSize;
	}
	public void initialize(InputStream input) {
		Scanner scanner = new Scanner(input);
		String line = scanner.nextLine();
		int count = 0;
		while (line != null) {
			if (count == 0) { 
				String[] parts = line.split(" ");
				inputSize = Integer.parseInt(parts[0]);
				hiddenSize = Integer.parseInt(parts[1]);
				outputSize = Integer.parseInt(parts[2]);
			}else{
				
			}
			line = scanner.nextLine();
		}
	}

	public void writeState(OutputStream output) {
		
	}

	public double getScore() {
		return score;
	}
	
}
