package Gates;

public class And extends Gate{

	@Override
	public boolean isOpen() {
		return term1 && term2;
	}

}
