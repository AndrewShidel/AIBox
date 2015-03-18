package Gates;

public class Output extends Gate{
	public int numConnections = 0;
	@Override
	public boolean isOpen() {
		return false;
	}
	public boolean isSet() {
		return term1 || term2;
	}
}
