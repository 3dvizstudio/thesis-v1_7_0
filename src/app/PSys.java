package app;

import core.graph.GEdge;
import core.graph.GNode;
import core.graph.Graph;
import processing.core.PApplet;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletMinDistanceSpring2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static util.Color.*;

public class PSys {
	private VerletPhysics2D physics;
	private final Rect bounds;
	private List<VerletSpring2D> springs;
	private List<VerletSpring2D> minDistSprings;
	private List<AttractionBehavior2D> behaviors;
	private List<VerletParticle2D> particles;
	private HashMap<String, String> info;
	private Settings X;
	private App p5;

	public PSys(App p5) {
		this.p5 = p5;
		this.X = App.CONF;
		physics = new VerletPhysics2D();
		bounds = new Rect(10, 10, p5.width - 20, p5.height - 20);
		physics.setWorldBounds(bounds);
		physics.setDrag(X.PHYS_DRAG);
		springs = new ArrayList<>();
		behaviors = new ArrayList<>();
		particles = new ArrayList<>();
		minDistSprings = new ArrayList<>();
		info = new HashMap<>();
	}
	public void draw() {
		p5.noFill(); p5.noStroke();
		if (X.isUpdating) update();
		p5.stroke(0, 0, 50);
		if (X.showSprings) { for (VerletSpring2D s : springs) { p5.line(s.a.x, s.a.y, s.b.x, s.b.y); } }
		if (X.showMinDist) { for (VerletSpring2D s : minDistSprings) { p5.line(s.a.x, s.a.y, s.b.x, s.b.y); } }
		p5.stroke(GREY); p5.fill(GREY_DK);
		if (X.showParticles) { for (VerletParticle2D a : physics.particles) { p5.ellipse(a.x, a.y, 6, 6); } }
		if (X.showWeights) { for (VerletParticle2D a : physics.particles) { p5.ellipse(a.x, a.y, a.getWeight(), a.getWeight()); } }
		p5.stroke(0xff343434); p5.noFill();
		if (X.showBehaviors) { for (AttractionBehavior2D a : behaviors) { Vec2D vb = a.getAttractor(); p5.ellipse(vb.x, vb.y, a.getRadius(), a.getRadius()); } }
		p5.fill(0xff666666);
		if (X.showInfo) {
			p5.pushMatrix(); p5.translate(300, 30);
			for (String key : info.keySet()) {
				p5.translate(0, 10); p5.textAlign(PApplet.LEFT); p5.text(key, -50, 0);
				p5.textAlign(PApplet.RIGHT); p5.text(String.valueOf(info.get(key)), 80, 0);
			} p5.popMatrix();
		}
	}
	private void update() {
		physics.update();
		physics.setDrag(X.PHYS_DRAG);
		for (VerletSpring2D s : springs) { s.setStrength(X.springStrength); }
		for (VerletSpring2D s : minDistSprings) {s.setStrength(X.mindistStrength);}
		for (VerletParticle2D n : particles) { n.setWeight(X.particleWeight); }
		for (AttractionBehavior2D b : behaviors) {b.setStrength(X.behaviorStrength);}
		info.put("PSYS.springs : ", String.valueOf(physics.springs.size()));
		info.put("PSYS.particles phys: ", String.valueOf(physics.particles.size()));
		info.put("PSYS.behaviors phys: ", String.valueOf(physics.behaviors.size()));
		info.put("phys.springs : ", String.valueOf(springs.size()));
		info.put("phys.particles : ", String.valueOf(particles.size()));
		info.put("phys.behaviors : ", String.valueOf(behaviors.size()));
		info.put("w.iter : ", App.DF3.format(physics.getNumIterations()));
		info.put("w.drag : ", App.DF3.format(physics.getDrag()));
		info.put("x.b scale : ", App.DF3.format(X.behaviorScale));
		info.put("x.p scale : ", App.DF3.format(X.particleScale));
		info.put("x.s scale : ", App.DF3.format(X.springScale));
	}

	public void addMinDist() {
		Graph g = App.GRAPH;
		for (GNode na : g.nodes) {
			VerletParticle2D va = na.getParticle2D();
			for (GNode nb : g.nodes) {
				VerletParticle2D vb = nb.getParticle2D();
				if ((na != nb) && (physics.getSpring(na.getParticle2D(), nb.getParticle2D()) == null)) {
					float len = (na.getRadius() + nb.getRadius());
					VerletSpring2D s = new VerletMinDistanceSpring2D(va, vb, len, .01f);
					minDistSprings.add(s);
					physics.addSpring(s);
				}
			}
		}
	}
	public void addParticle(GNode n) {
		particles.add(n.getParticle2D());
		physics.addParticle(n.getParticle2D());
		behaviors.add(n.getBehavior2D());
		physics.addBehavior(n.getBehavior2D());
	}
	public void removeParticle(GNode n) {
		particles.remove(n.getParticle2D());
		physics.removeParticle(n.getParticle2D());
		behaviors.remove(n.getBehavior2D());
		physics.removeBehavior(n.getBehavior2D());
	}
	public void addSpring(GEdge e) {
		springs.add(e.getSpring2D());
		physics.addSpring(e.getSpring2D());
	}
	public void removeSpring(GEdge e) {
		springs.remove(e.getSpring2D());
		physics.removeSpring(e.getSpring2D());
	}

	public void clearMinDist() { for (VerletSpring2D s : minDistSprings) { physics.springs.remove(s); } minDistSprings.clear();}
	public void reset() {springs.clear(); minDistSprings.clear(); particles.clear(); behaviors.clear(); physics.clear();}

	public VerletPhysics2D getPhysics() { return physics; }
	public Rect getBounds() { return bounds; }
	public float getDrag() {return physics.getDrag();}
}



/*	public void addSpring(GNode na, GNode nb) {
		float l = na.getRadius() + nb.getRadius() + 5;
		VerletSpring2D s = new VerletSpring2D(na.getParticle2D(), nb.getParticle2D(), l, 0.01f);
		physics.addSpring(s);
		springs.add(s);
	}*/

//		VerletSpring2D md = physics.getSpring(e.getSpring2D().a, e.getSpring2D().b);
//		if (md != null) { physics.removeSpring(md); minDistSprings.remove(md); }
/*	public void updateSpring(GEdge e) {
		VerletParticle2D a = e.a().getParticle2D();
		VerletParticle2D b = e.b().getParticle2D();
		VerletSpring2D s = physics.getSpring(a, b);
		s.setRestLength(e.len() * X.springScale);
		s.setStrength(X.springStrength);
	}*/
/*
	public void updateParticle(GNode n) {
		float radius = (float) ((Math.sqrt(n.getSize() / Math.PI)) * X.particleScale * App.world_scale) + X.particlePadding;
		VerletParticle2D v = n.getParticle2D();
		n.setX(v.x); n.setY(v.y); n.setRadius(radius);
		v.setWeight(X.particleWeight);
		AttractionBehavior2D b = n.getBehavior2D();
		b.setRadius(n.getRadius() * X.behaviorScale);
		b.setStrength(X.behaviorStrength);
	}
*/
//	public void setDrag(float drag) { physics.setDrag(drag);}
//	public void clearSprings() { physics.springs.clear(); }
/*	public void addSpring(GEdge r) {
		VerletParticle2D a = r.a().getParticle2D();
		VerletParticle2D b = r.b().getParticle2D();
		float l = r.len();
		VerletSpring2D md = physics.getSpring(a, b);
		if (md != null) { physics.removeSpring(md); minDistSprings.remove(md); }
//		VerletSpring2D s = new VerletSpring2D(a, b, l, X.springStrength);
		springs.add(s);
		physics.addSpring(s);
	}*/