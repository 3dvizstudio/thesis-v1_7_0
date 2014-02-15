package core.graph;

import app.App;
import app.Settings;
import processing.core.PApplet;
import toxi.geom.Circle;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;

import java.util.ArrayList;

import static util.Color.*;

/**
 * Created on 2/13/14.
 */
public class GEdit {
	public boolean isUpdating;
	private GNode activeNode;
	private GNode hoveredNode;
	private ArrayList<GNode> selectedNodes = new ArrayList<>();
	private ArrayList<GNode> lockedNodes = new ArrayList<>();
	private App p5;
	private Settings X;
	private Graph graph;
	public GEdit(App p5) {this.p5 = p5; this.graph = App.GRAPH; this.X = App.CONF;}

	public void draw() {
		p5.noFill(); p5.noStroke();

		for (GNode n : graph.nodes) { n.update(X.particleWeight, X.behaviorScale, X.behaviorStrength); if (X.showNodes) { n.draw(p5);} }
		if (X.showNodes) {
			if (hasActiveNode()) {
				p5.stroke(ACTIVE);
				p5.ellipse(activeNode.getX(), activeNode.getY(), activeNode.getRadius() + 10, activeNode.getRadius() + 10);
			} for (GNode n : selectedNodes) {
				p5.stroke(SELECTED);
				p5.ellipse(n.getX(), n.getY(), n.getRadius() + 3, n.getRadius() + 3);
				n.drawTag(p5);
			} if (hoveredNode != null) {
				hoveredNode.drawTagID(p5); hoveredNode.drawTag(p5);
			}
		}
		for (GEdge e : graph.edges) {e.update(X.springStrength, X.springScale); if (X.showEdges) { e.draw(p5); } }
		if (X.showOutliner) { drawOutliner(); }
	}
	private void drawOutliner() {
		p5.noFill(); p5.noStroke();
		float totalSize = 0;
		int xx = App.WIDTH - 120;

		for (GNode n : graph.nodes) {
			int id = 50 + (graph.nodes.indexOf(n) * 12);
			totalSize += n.getSize();
			if (graph.nodes.indexOf(n) % 2 == 0) { p5.fill(0xff383838); } else {p5.fill(0xff333333);}
			p5.rect(xx, id, 120, 11);
			p5.fill(0xff1d1d1d);
			p5.rect(xx, id, 4, 11);
			p5.fill(n.getColor(), 100, 100);
			p5.rect(xx + 1, id, 2, 11);
			if (n == activeNode) p5.fill(ACTIVE);
			else if (selectedNodes.contains(n)) p5.fill(SELECTED);
			else p5.fill(0xff999999);
			p5.textAlign(PApplet.LEFT);
			p5.text(n.getName(), xx + 10, id + 10);
			p5.textAlign(PApplet.RIGHT);
			p5.text(n.getId(), xx - 10, id + 10);
			p5.text((int) n.getSize(), xx + 100, id + 10);
		}

		p5.noStroke();
		p5.fill(0xffffffff);
		p5.text("NAME", 10, -2);
		p5.textAlign(PApplet.RIGHT);
		p5.text("AREA", 100, -2);
		p5.fill(ACTIVE);
		p5.textFont(p5.bfont, 14);
		p5.text("Total Area", xx, 0);
		p5.text(App.DF3.format(totalSize) + " sq.m", xx + 100, 30);
		p5.textFont(p5.pfont, 10);
		p5.text("nodes" + graph.nodes.size(), xx - 200, 30);
		p5.text("edges" + graph.edges.size(), xx - 200, 40);
		p5.text("mapNodes" + Graph.getMap().getNodes().size(), xx - 200, 50);
		p5.text("mapEdges" + Graph.getMap().getEdges().size(), xx - 200, 60);

//		p5.popMatrix();
	}
	private void selectNodeNearPosition(Vec2D mousePos) {
		if (!App.isShiftDown) clearSelection();
		else deselectNode();
		for (GNode n : graph.nodes) {
			Circle c = new Circle(n.getX(), n.getY(), 20);
			if (c.containsPoint(mousePos)) {
				setActiveNode(n);
				break;
			} else deselectNode();
		}
	}
	/*	public void removeActiveNode() {

			if (hasActiveNode()) {
				System.out.println("hasActive" + hasActiveNode());
				GNode n = activeNode;
				System.out.println("index: " + graph.nodes.indexOf(n) + ", id: " + n.getId());

	//			ArrayList<GNode> relatives = graph.getEdgeIndex().get(n.getId());//			if (relatives != null) {
				for (GEdge e : graph.edges) {
					System.out.println("checking edge" + graph.edges.indexOf(e));
					if (e.a() == n) {
						System.out.println("found match" + graph.edges.indexOf(e));
						System.out.println("[ " + e.a().getId() + "]["+ e.b().getId() + "][");

	//					graph.edges.remove(e); graph.build();
								*//**//**//*break;*//*
				}//				if (e.b() == n) { graph.removeEdge(e); break; }
			}
			System.out.println("--------------------------");
*//*			for (GEdge e : graph.edges) {
				if (e.a() == n) {System.out.println("found edge" + graph.edges.indexOf(e)); break; }
			}*//*
//			graph.removeNode(n); graph.build();
//			activeNode = null;
		}
	}	*/
	public void removeActiveNode() {
		System.out.println();
		System.out.println("___________________________________________________________________________________________________");
		for (GNode n : Graph.getMap().getNodes()) {
			System.out.print("[" + n.getId() + "]");
		}
		for (GEdge n : Graph.getMap().getEdges()) {
			System.out.println("[" + Graph.getMap().getEdges().indexOf(n) + "] " + n.getFrom() + "=>" + n.getTo());
		}

		if (hasActiveNode()) {
			GNode n = activeNode;
			System.out.println("Active Node: [" + graph.nodes.indexOf(n) + "], id: " + n.getId());
			ArrayList<GNode> relatives = graph.getEdgeIndex().get(n.getId());

			System.out.println("checking relatives...");
			if (relatives == null) { System.out.println("no relatives"); } else {
				System.out.println("********************************** FOUND RELATIVES: ");
				for (GNode g : relatives) { System.out.println("[" + g.getId() + "]"); }
			}
			System.out.println("checking edges...");
			ArrayList<GEdge> rels = new ArrayList<>();
			for (GEdge e : graph.edges) {
				if (e.a() == n) {
					rels.add(e);
					System.out.println("a[" + e.a().getId() + "] ==> b[" + e.b().getId() + "] <= MATCH A [" + graph.edges.indexOf(e) + "]");
				} if (e.b() == n) {
					rels.add(e);
					System.out.println("a[" + e.a().getId() + "] ==> b[" + e.b().getId() + "] <= MATCH B [" + graph.edges.indexOf(e) + "]");
				}/* else {
					System.out.println("[" + graph.edges.indexOf(e) + " a[" + e.a().getId() + "] b[" + e.b().getId() + "]");
				}*/
			}
			System.out.println("_________________________________");
			System.out.println("| rels : " + rels.size());
			System.out.println("| edges: " + graph.edges.size());
			System.out.println("| nodes: " + graph.nodes.size());
			System.out.println("---------------------------------");
		}
	}
	private void deselectNode() {
		releaseNode();
		activeNode = null;
	}
	private void releaseNode() {
		if (hasActiveNode()) { if (!lockedNodes.contains(activeNode)) activeNode.getParticle2D().unlock(); }
	}
	private void clearSelection() {
		selectedNodes.clear();
	}
	private void moveActiveNode(Vec2D mousePos) {
		if (hasActiveNode()) {
			activeNode.getParticle2D().lock();
			activeNode.getParticle2D().set(mousePos);
		}
	}
	private void highlightNodeNearPosition(Vec2D mousePos) {
		hoveredNode = null;

		for (GNode n : graph.nodes) {
			Circle c = new Circle(mousePos, 20);
			if (c.containsPoint(n.getParticle2D())) {
				hoveredNode = n;
				break;
			}
		}
	}
	private void setActiveNode(GNode n) {
		this.activeNode = n;
		selectedNodes.add(n);
	}
	public void createBranch(float num) {
		if (hasActiveNode()) {
			GNode pNode = getActiveNode();
			String name = pNode.getName();
			int col = pNode.getColor();
			Vec2D pPos = pNode.getParticle2D();
			float size = pNode.getSize() / (num + 1);
			pNode.setSize(size);
			for (int i = 1; i <= num; i++) {
				Vec2D pos = Vec2D.fromTheta(i * PApplet.TWO_PI / num).scaleSelf(size).addSelf(pPos);
				GNode n = newNode(name + i, size, pos, col);
				graph.addNode(n);
				GEdge e = newEdge(pNode, n);
				graph.addEdge(e);
			}
		}
	}

	public void addNodeAtCursor(String name, float size, Vec2D pos) {
		GNode n = newNode(name, size, pos);
		graph.addNode(n);
	}

	public void addEdgeToSelection() {
		if (getSelectedNodes().size() >= 2) {
			GNode na = selectedNodes.get(0);
			GNode nb = selectedNodes.get(1);
			GEdge e = newEdge(na, nb);
			graph.addEdge(e);
//			clearSelection(); selectedNodes.add(na); selectedNodes.add(nb);
		}
	}

	private GEdge newEdge(GNode from, GNode to) {
		GEdge e = new GEdge();
		e.setFrom(from.getId());
		e.setTo(to.getId());
		e.setSpring2D(new VerletSpring2D(e.a().getParticle2D(), e.b().getParticle2D(), e.len() * X.springScale, X.springStrength));
//		App.PSYS.addSpring(e.getSpring2D());
		return e;
	}
	private GNode newNode(String name, float size, Vec2D pos) {
		GNode n = newNode(name, size, pos, 100);
		return n;
	}
	private GNode newNode(String name, float size, Vec2D pos, int color) {
		GNode n = new GNode();
		n.setId(graph.nodes.size());
		n.setName(name);
		n.setSize(size);
		n.setX(pos.x);
		n.setY(pos.y);
		n.setColor(color);
		n.setParticle2D(new VerletParticle2D(pos));
		n.setBehavior2D(new AttractionBehavior2D(n.getParticle2D(), n.getRadius(), -1));
		return n;
	}
	public boolean hasActiveNode() {return activeNode != null; }
	public GNode getActiveNode() { return activeNode; }
	public ArrayList<GNode> getSelectedNodes() { return selectedNodes; }
	public void mousePressed(Vec2D mousePos) { selectNodeNearPosition(mousePos); }
	public void mouseDragged(Vec2D mousePos) { if (hasActiveNode()) moveActiveNode(mousePos); }
	public void mouseReleased(Vec2D mousePos) {releaseNode(); }
	public void mouseMoved(Vec2D mousePos) {highlightNodeNearPosition(mousePos);}
	public void freezeNode() {
		if (hasActiveNode()) {
			if (lockedNodes.contains(activeNode)) {
				lockedNodes.remove(activeNode); activeNode.getParticle2D().unlock();
			} else { activeNode.getParticle2D().lock(); lockedNodes.add(activeNode); }
		}
	}
	public void setName(String name) { if (hasActiveNode()) activeNode.setName(name); }
	public void setOccupancy(float occupancy) { if (hasActiveNode()) activeNode.setOccupancy((int) occupancy); }
	public void setColor(float color) { if (hasActiveNode()) activeNode.setColor((int) color); }
	public void setSize(float size) { if (hasActiveNode()) activeNode.setSize(size); }
}
		/*GNode n = new GNode();
		n.setId(graph.nodes.size());
		n.setName($name);
		n.setSize($size);
		n.setX($pos.x);
		n.setY($pos.y);
		VerletParticle2D v = new VerletParticle2D($pos);
		AttractionBehavior2D a = new AttractionBehavior2D(v, n.getRadius(), -1);
		n.setParticle2D(v);
		n.setBehavior2D(a);*/
//		graph.addNode(n);

