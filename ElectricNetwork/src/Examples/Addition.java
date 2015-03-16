package Examples;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Addition {
	public static void main (String[] args) {
		AdditionNetwork network = new AdditionNetwork();
		try {
			Network.NetworkPrinter.printDracula(network, new PrintWriter("/home/andrew/Downloads/strathausen-dracula-a6a5fa7/js/additionNetwork.html"));
			Network.NetworkPrinter.printSpringy(network, new PrintWriter("/home/andrew/Downloads/dhotson-springy-559a400/additionNetwork.html"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
