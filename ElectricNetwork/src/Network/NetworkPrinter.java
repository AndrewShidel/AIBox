package Network;

import java.io.PrintWriter;
import java.util.ArrayList;

public class NetworkPrinter {
	public static void print(Graph graph, PrintWriter output) {
		String deps =     "<script type='text/javascript' src='raphael-min.js'></script>"
						+ "<script type='text/javascript' src='dracula_graffle.js'></script>"
						+ "<script type='text/javascript' src='jquery-1.4.2.min.js'></script>"
						+ "<script type='text/javascript' src='dracula_graph.js'></script>";
		deps += "<style>\n#container{\nwidth:100%;\nheight:100%;\n}\n</style>";
		output.write("<html><head>" + deps + "<script>\n");
		output.write("window.onload=function(){\nvar g = new Graph();");
		for (int i=0; i<graph.network.size(); i++) {
			ArrayList<Graph.Connection> connections = graph.network.get(i);
			for (Graph.Connection connection : connections) {
				output.write("g.addEdge('"+connection.index+"', '"+i+"');");
			}
		}
		output.write("var layouter = new Graph.Layout.Spring(g);");
		output.write("layouter.layout();");
		output.write("var renderer = new Graph.Renderer.Raphael('container', g, window.innerWidth, window.innerHeight);");
		output.write("renderer.draw();\n}");
		output.write("\n</script></head><body><div id='container'></div></body></html>");
		output.close();
	}
}
