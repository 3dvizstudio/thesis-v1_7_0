package core.graph;

import app.App;
import app.Settings;
import core.graph.xml.XMLmap;
import core.graph.xml.XMLtool;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 2/13/14.
 */
public class Graph {
	private static XMLmap Map;
	private static HashMap<Integer, GNode> nodeIndex = new HashMap<>();
	private static HashMap<Integer, ArrayList<GNode>> edgeIndex = new HashMap<>();
	public ArrayList<GNode> nodes;
	public ArrayList<GEdge> edges;

	public Graph() {
		Map = new XMLmap();
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
//		XMLtool.unmarshal();
	}

	public void build() {
		Map = new XMLmap();
		Map.setNodes(nodes);
		Map.setEdges(edges);
		edgeIndex = new HashMap<>();
		nodeIndex = new HashMap<>();
		for (GNode n : nodes) { nodeIndex.put(n.getId(), n); }
		for (GEdge e : edges) {
			ArrayList<GNode> nlist = edgeIndex.get(e.getFrom());
			if (nlist == null) {
				nlist = new ArrayList<>();
				edgeIndex.put(e.getFrom(), nlist);
			}
			nlist.add(nodeIndex.get(e.getTo()));
			System.out.println();
		} XMLtool.marshal();
	}

	public void rebuild() {
		XMLtool.unmarshal();
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		App.PSYS.reset();
		edgeIndex = new HashMap<>();
		nodeIndex = new HashMap<>();
		Settings X = App.CONF;
		for (GNode n : Map.getNodes()) {
			n.setParticle2D(new VerletParticle2D(n.getX(), n.getY()));
			n.setBehavior2D(new AttractionBehavior2D(n.getParticle2D(), n.getRadius(), -1));
			n.update(X.particleWeight, X.behaviorScale, X.behaviorStrength);
			nodes.add(n);
			nodeIndex.put(n.getId(), n);
			App.PSYS.addParticle(n);
		} for (GEdge e : Map.getEdges()) {
			e.setSpring2D(new VerletSpring2D(e.a().getParticle2D(), e.b().getParticle2D(), e.len(), 0.001f));
			e.update(X.springStrength, X.springScale);
			edges.add(e);
			App.PSYS.addSpring(e);
			ArrayList<GNode> nlist = edgeIndex.get(e.getFrom());
			if (nlist == null) { nlist = new ArrayList<>(); edgeIndex.put(e.getFrom(), nlist); }
			nlist.add(nodeIndex.get(e.getTo()));
		}
		build();
	}

	public void addNode(GNode n) {
		nodes.add(n);
		App.PSYS.addParticle(n);
		build();
	}
	public void addEdge(GEdge e) {
		edges.add(e);
		App.PSYS.addSpring(e);
		build();
	}
	public void removeNode(GNode n) {
		nodes.remove(n);
		nodeIndex.remove(n.getId());
		App.PSYS.removeParticle(n);

	}
	public void removeEdge(GEdge e) {
		edges.remove(e);
		edgeIndex.remove(e.getFrom());
		App.PSYS.removeSpring(e);

	}
	public void addNodes(ArrayList<GNode> nodes) { for (GNode n : nodes) { addNode(n); }}
	public void addEdges(ArrayList<GEdge> edges) { for (GEdge e : edges) {addEdge(e);} }

	public static XMLmap getMap() { return Map; }
	public static void setMap(XMLmap Map) { Graph.Map = Map; }
	public HashMap<Integer, ArrayList<GNode>> getEdgeIndex() { return edgeIndex; }
	public HashMap<Integer, GNode> getNodeIndex() { return nodeIndex; }
	public void setNodes(ArrayList<GNode> nodes) { this.nodes = nodes; }
	public void setEdges(ArrayList<GEdge> edges) { this.edges = edges; }
	public static GNode getNode(int id) {return nodeIndex.get(id);}
	public void reset(){
		nodes.clear();edges.clear(); build();
	}
}
