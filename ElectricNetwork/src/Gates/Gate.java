package Gates;

public abstract class Gate {
	protected boolean term1 = false;
	protected boolean term2 = false;
	
	public int lastConnection = -1;
	public int depth = 0;
	
	public abstract boolean isOpen();
	public void setTerm1(boolean val){
		term1 = val;
	}
	public void setTerm2(boolean val){
		term2 = val;
	}
	public void reset() {
		term1 = term2 = false;
		lastConnection = -1;
		depth = 0;
	}
}
