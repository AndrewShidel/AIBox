package examples;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import NeuralNetwork.NeuralNetwork;

public class StabilityControl{
	public static void main(String[] args) {
		StabilityControl outer = new StabilityControl();
		StabilityNetwork network = outer.new StabilityNetwork();
		network.initialize(8, 128, 1);
	}
	private class StabilityNetwork extends NeuralNetwork {
		private float balance = 55;
		private static final byte idealPoint = 64;
		private static final double tippingAmount = 0.1;
		private static final byte maxValue = (byte)128;
		
		@Override
		public void initialize(int inputSize, int hiddenSize, int outputSize) {
			super.initialize(inputSize, hiddenSize, outputSize);
			while (true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				balance -= .05;
			}
		}
		
		@Override
		public byte[] onInputRequested() {
			return new byte[] {(byte)balance};
		}

		@Override
		public float onOutput(byte[] output) {
			if (output[0]==1){
				balance += tippingAmount;
			}
			float error = Math.abs(balance-idealPoint);
			return error<1?(1-error)*maxValue:-1*error;
		}

		@Override
		public void onLearningFinished() {
			try {
				writeState(new FileOutputStream("./output.net"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

