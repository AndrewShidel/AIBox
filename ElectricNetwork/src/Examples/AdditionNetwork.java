package Examples;

import Network.Graph;

public class AdditionNetwork extends Graph{
	public AdditionNetwork() {
		this.initialize(16, 128, 8, 10);
	}
	@Override
	public byte[] onInputRequested() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float onOutput(byte[] output) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onLearningFinished() {
		// TODO Auto-generated method stub
		
	}
}
