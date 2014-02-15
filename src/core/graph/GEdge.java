package core.graph;

import app.App;
import processing.core.PApplet;
import toxi.physics2d.VerletSpring2D;

import javax.xml.bind.annotation.*;

/**
 * Created on 2/13/14.
 */
@XmlRootElement(name = "rel")
@XmlAccessorType(XmlAccessType.FIELD)
public class GEdge {

	@XmlAttribute
	private int from;
	@XmlAttribute
	private int to;
	@XmlTransient
	private VerletSpring2D spring2D;
	@XmlTransient
	private float length;

	public void draw(PApplet pg) {
		pg.stroke(0xff666666); pg.noFill();
		pg.line(a().getX(), a().getY(), b().getX(), b().getY());
	}
	public void update(float strength, float scale) {
		VerletSpring2D s = getSpring2D();
		s.setStrength(strength);
		s.setRestLength(len() * scale);
	}
	public GNode a() { return Graph.getNode(from); }
	public GNode b() { return Graph.getNode(to); }

	public float len() { return a().getRadius() + b().getRadius(); }
	public void setTo(int to) {this.to = to;}
	public void setFrom(int from) {this.from = from;}
	public int getFrom() { return from; }
	public int getTo() { return to; }
	public VerletSpring2D getSpring2D() { return spring2D; }
	public void setSpring2D(VerletSpring2D spring2D) { this.spring2D = spring2D; }
}
