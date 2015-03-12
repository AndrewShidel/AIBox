package Gates;

public class Transistor extends Gate{
	
	@Override
	public boolean isOpen() {
		return term1 && term2;
	}
	
	@Override
	public void reset() {
		term2 = false;
	}
}
