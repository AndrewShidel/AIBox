package Gates;

/**
 * This class defines a modified not gate. It is modified in the sense that it has two input
 * terminals; one to set the gate, and one to carry a charge.
 */
public class Not extends Gate{
	@Override
	public boolean isOpen() {
		return !term1 && term2;
	}
}
