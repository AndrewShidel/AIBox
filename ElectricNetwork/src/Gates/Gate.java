package Gates;

public abstract class Gate {
	public boolean term1;
	public boolean term2;
	public abstract boolean isOpen();
	public void reset() {
		term1 = term2 = false;
	}
}
