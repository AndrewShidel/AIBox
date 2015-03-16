package Examples;

import java.nio.ByteBuffer;
import java.util.Random;

import Network.Graph;

public class AdditionNetwork extends Graph{
	private Random rand = new Random(System.currentTimeMillis());
	private short correctAnswer;
	public AdditionNetwork() {
		//this.initialize(16, 128, 16, 10);
		this.initialize(8, 64, 8, 4);
	}
	@Override
	public byte[] onInputRequested() {
		byte[] data = new byte[2];
		data[0] = (byte)(rand.nextInt(256)-128);
		data[1] = (byte)(rand.nextInt(256)-128);
		correctAnswer = (short) (data[0] + data[1]);
		return data;
	}

	@Override
	public byte[] onOutput(byte[] output) {
		short diff = (short) ((output[0]^correctAnswer) | (output[1]^(correctAnswer<<8)));
		return ByteBuffer.allocate(2).putShort(diff).array();
		/*int outputInt = ((output[0] & 0xff) << 8) | (output[1] & 0xff);
		if (outputInt == correctAnswer) {
			return 1;
		} else {
			return -1;
		}*/
	}

	@Override
	public void onLearningFinished() {
		// TODO Auto-generated method stub
		
	}
}
