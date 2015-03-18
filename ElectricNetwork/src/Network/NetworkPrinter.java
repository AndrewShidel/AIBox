package Network;

import java.io.PrintWriter;
import java.util.ArrayList;

public class NetworkPrinter {
	public static void printDracula(Graph graph, PrintWriter output) {
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
	public static void printSpringy(Graph graph, PrintWriter output) {
		String deps =   "<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js'></script>"
						+ "<script src='springy.js'></script>"
						+ "<script src='springyui.js'></script>";
		deps += "<style>\n#container{\nwidth:100%;\nheight:100%;\n}\n</style>";
		output.write("<html><head>" + deps + "<script>\n");
		output.write("var graph = new Springy.Graph();");
		
		String names = "";
		int numBuckets = graph.nodes.size()/graph.bucketSize;
		
		for (int i=0; i<graph.nodes.size(); i++) {
			int bucket = i%numBuckets;
			//String color = rainbow((int)(((double)bucket/numBuckets) * 240));
			String color = rainbow(bucket, numBuckets);
			names += "var node" + i + " = graph.newNode({label: '"+i+"', color: '"+color+"'});\n";
		}
		output.write("\n\n" + names + "\n");
		
		for (int i=0; i<graph.network.size(); i++) {
			ArrayList<Graph.Connection> connections = graph.network.get(i);
			for (Graph.Connection connection : connections) {
				String color;
				if (i < graph.inputSize) {
					color = "'#000000'";
				}else if (connection.index > graph.inputSize + graph.hiddenSize - 1) {
					color = "'#0000FF'";
				}else{
					color = "'#00FF00'";
				}
				output.write("graph.newEdge(node"+i+", node"+connection.index+", {color: "+color+"});\n");
			}
		}
		output.write("\njQuery(function(){jQuery('#container').attr('width', window.innerWidth);jQuery('#container').attr('height', window.innerHeight); window.springy=jQuery('#container').springy({graph:graph,nodeSelected:function(e){console.log('Node selected: '+JSON.stringify(e.data))}})});");

		output.write("\n</script></head><body><canvas id='container'/></body></html>");
		output.close();
	}

	private static String rainbow(int length, int maxLength)
	{
	    double i = (length * 255 / maxLength);
	    int r = (int) Math.round(Math.sin(0.024 * i + 0) * 127 + 128);
	    int g = (int) Math.round(Math.sin(0.024 * i + 2) * 127 + 128);
	    int b = (int) Math.round(Math.sin(0.024 * i + 4) * 127 + 128);
	    return "rgb(" + r + "," + g + "," + b + ")";
	}
}
