package Gates;

public class Or extends Gate{

	@Override
	public boolean isOpen() {
		return term1 || term2;
	}
	
}
