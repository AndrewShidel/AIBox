package Gates;

public class Output extends Gate{
	@Override
	public boolean isOpen() {
		return false;
	}
	public boolean isSet() {
		return term1 || term2;
	}
}
