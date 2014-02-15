package app;

import processing.core.PApplet;
import toxi.geom.Polygon2D;
import toxi.geom.PolygonClipper2D;
import toxi.geom.SutherlandHodgemanClipper;
import toxi.geom.Vec2D;
import toxi.geom.mesh2d.Voronoi;
import toxi.physics2d.VerletParticle2D;

import java.util.ArrayList;
import java.util.List;

import static util.Color.*;

public class VSys {
	private App p5;
	private Settings X;
	private PolygonClipper2D clipper;
	private Voronoi voronoi;
	private ArrayList<VerletParticle2D> sites;
	private ArrayList<Polygon2D> cells;

	public VSys(App p5) {
		this.p5 = p5;
		this.X = App.CONF;
		PSys psys = App.PSYS;
		this.sites = psys.getPhysics().particles;
		this.clipper = new SutherlandHodgemanClipper(psys.getBounds().copy().scale(1.5f));
		this.voronoi = new Voronoi();
		this.cells = new ArrayList<>();
	}

	public void draw() {
		p5.noFill(); p5.noStroke();
		if (X.showVoronoi) {
			if (X.UPDATE_VORONOI) {
				voronoi = new Voronoi();
				voronoi.addPoints(sites);
				cells = new ArrayList<>();
				for (Polygon2D poly : voronoi.getRegions()) {
					poly = clipper.clipPolygon(poly);
					for (Vec2D v : this.sites) { if (poly.containsPoint(v)) { cells.add(poly); } }
				}
			} if (X.showBezier) {
				p5.noFill(); p5.stroke(VOR_CELLS);
				for (Polygon2D poly : cells) {
					List<Vec2D> v = poly.vertices;
					int j = v.size();
					p5.beginShape();
					p5.vertex((v.get(j - 1).x + v.get(0).x) / 2, (v.get(j - 1).y + v.get(0).y) / 2);
					for (int i = 0; i < j; i++) { p5.bezierVertex(v.get(i).x, v.get(i).y, v.get(i).x, v.get(i).y, (v.get((i + 1) % j).x + v.get(i).x) / 2, (v.get((i + 1) % j).y + v.get(i).y) / 2); }
					p5.endShape(PApplet.CLOSE);
				}
			} if (X.showPolygons) {
				p5.stroke(VOR_VOIDS); p5.noFill();
				for (Polygon2D poly : cells) { App.GFX.polygon2D(poly); }
			} if (X.showVerts) {
				p5.noFill(); p5.stroke(VOR_VERTS);
				for (Polygon2D poly : cells) { for (Vec2D vec : poly.vertices) { App.GFX.circle(vec, 2); } }
			} if (X.showInfo) {
				p5.fill(VOR_TXT); p5.noStroke();
				for (Polygon2D poly : cells) { p5.text(poly.getNumVertices() + "." + cells.indexOf(poly), poly.getCentroid().x, poly.getCentroid().y); }
			}
		}
	}
}