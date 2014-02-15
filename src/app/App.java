package app;

import controlP5.*;
import core.graph.GEdit;
import core.graph.Graph;
import core.metaball.Metaball;
import org.philhosoft.p8g.svg.P8gGraphicsSVG;
import processing.core.PApplet;
import processing.core.PFont;
import toxi.geom.Vec2D;
import toxi.processing.ToxiclibsSupport;
import util.Color;

import java.text.DecimalFormat;

//import util.xml.XMLflowgraph;

public class App extends PApplet {
	public static final int WIDTH = 1600, HEIGHT = 1000;
	public static final DecimalFormat DF3 = new DecimalFormat("#.###");
	public static String xmlFilePath = "./data/flowgraph_test_lg.xml";
	public static boolean RECORDING = false;
	public static boolean isShiftDown;
	public static float ZOOM = 1;
	public static float world_scale = 10;
	public static ToxiclibsSupport GFX;
	public static Settings CONF;
	public static PSys PSYS;
	public static Graph GRAPH;
	public static GEdit GEDIT;
	public static VSys VSYS;
	public ControlP5 CP5;
	public Knob radiusSlider, colorSlider, capacitySlider;
	public Group properties, generator, config;
	public Textfield nameTextfield;
	public Accordion accordion;
	public PFont pfont, bfont;
	public static void main(String[] args) {
		PApplet.main(new String[]{("app.App")});
		System.out.println(System.getProperty("user.dir"));
	}
	public static void __rebelReload() {System.out.println(); System.out.println("__rebelReload");}
	public void setup() {
		pfont = createFont("SourceCodePro", 10);
		bfont = createFont("SourceCodePro", 14);
		GFX = new ToxiclibsSupport(this);
		CONF = new Settings();
		CP5 = new ControlP5(this);
		PSYS = new PSys(this);
		GRAPH = new Graph();
		GEDIT = new GEdit(this);
		VSYS = new VSys(this);
		size(WIDTH, HEIGHT, P2D);
		frameRate(60);
		smooth(8);
		colorMode(HSB, 100, 100, 100);
		background(Color.BG);
		ellipseMode(RADIUS);
		textAlign(LEFT);
		textFont(pfont, 10);
		strokeWeight(1);
		noStroke();
		noFill();
		initGUI();
		initGUI_styles();
	}
	public void draw() {
		background(Color.BG); noFill(); noStroke();
		PSYS.draw();
		VSYS.draw();
		GEDIT.draw();
		if (RECORDING) { RECORDING = false; endRecord(); System.out.println("SVG EXPORTED SUCCESSFULLY"); }
		CP5.draw();
	}

	public Vec2D mousePos() {return new Vec2D(mouseX, mouseY);}
	public void mouseMoved() {
		GEDIT.mouseMoved(mousePos());
	}
	public void mousePressed() {
		if (mouseButton == RIGHT) { GEDIT.mousePressed(mousePos()); toggleObjProperties(); }
	}

	public void mouseDragged() {
		if (mouseButton == RIGHT) GEDIT.mouseDragged(mousePos());
	}
	public void mouseReleased() {
		if (mouseButton == RIGHT) GEDIT.mouseReleased(mousePos());
	}
	public void keyPressed() {
		if (key == CODED && keyCode == SHIFT) { isShiftDown = true; }
		switch (key) {
			case '1': CONF.showPolygons = !CONF.showPolygons; break;
			case '2': CONF.showBezier = !CONF.showBezier; break;
			case '3': CONF.showVerts = !CONF.showVerts; break;
			case '4': CONF.showInfo = !CONF.showInfo; break;
			case 'c': CONF.doClip = !CONF.doClip; break;
//			case 'w': GRAPH.testWrite(10); break;
//			case 'r':  GRAPH.testRead(); break;
			case 'a': GEDIT.addNodeAtCursor(nameTextfield.getStringValue(), radiusSlider.getValue(), mousePos()); toggleObjProperties(); break;
			case 'f': GEDIT.addEdgeToSelection(); break;
			case 'x': GEDIT.removeActiveNode(); break;

			case 'q': GEDIT.createBranch(capacitySlider.getValue()); break;
			case 'l': GEDIT.freezeNode(); break;
		}
	}
	public void keyReleased() { if (key == CODED && keyCode == SHIFT) { isShiftDown = false; } }

	private void toggleObjProperties() {
		if (GEDIT.hasActiveNode()) {
			radiusSlider.setValue(GEDIT.getActiveNode().getSize());
			colorSlider.setValue(GEDIT.getActiveNode().getColor());
			capacitySlider.setValue(GEDIT.getActiveNode().getOccupancy());
			nameTextfield.setValue(GEDIT.getActiveNode().getName());
			radiusSlider.show();
			colorSlider.show();
			capacitySlider.show();
			nameTextfield.show();
			accordion.open(2);
		} else {
			radiusSlider.hide();
			colorSlider.hide();
			capacitySlider.hide();
			nameTextfield.hide();
			accordion.close(2);
		}
	}
	private void initGUI() {
		CP5.enableShortcuts();
		CP5.setAutoDraw(false);
		CP5.setFont(pfont, 10);
		CP5.setAutoSpacing(4, 8);
		CP5.setColorBackground(Color.CP5_BG).setColorForeground(Color.CP5_FG).setColorActive(Color.CP5_ACT);
		CP5.setColorCaptionLabel(Color.CP5_CAP).setColorValueLabel(Color.CP5_VAL);
		config = CP5.addGroup("VERLET PHYSICS SETTINGS").setBackgroundHeight(260).setBarHeight(32).setWidth(220);
		CP5.begin(10, 15);
		CP5.addSlider("world_scale").setRange(1, 20).setDecimalPrecision(0).linebreak();
		CP5.addSlider("verlet_drag").setValue(PSYS.getDrag()).setRange(0.1f, 1).setDecimalPrecision(2).linebreak();
		CP5.addSlider("particle_scale").setValue(CONF.particleScale).setRange(0.5f, 2).setDecimalPrecision(1).linebreak();
		CP5.addSlider("spring_scale").setValue(CONF.springScale).setRange(0.5f, 2).setDecimalPrecision(1).linebreak();
		CP5.addSlider("behavior_scale").setValue(CONF.behaviorScale).setRange(0, 7).setDecimalPrecision(1).linebreak();
		CP5.addSlider("particle_strength").setValue(CONF.particleWeight).setRange(0.1f, 5).setDecimalPrecision(1).linebreak();
		CP5.addSlider("behavior_strength").setValue(CONF.behaviorStrength).setRange(-5f, 0).setDecimalPrecision(2).linebreak();
		CP5.addSlider("spring_strength").setValue(CONF.springStrength).setRange(0.001f, 0.05f).setDecimalPrecision(3).linebreak();
		CP5.addSlider("mindist_strength").setValue(CONF.mindistStrength).setRange(0.001f, 0.05f).setDecimalPrecision(2).linebreak();
		CP5.end();
		generator = CP5.addGroup("RECURSIVE GRAPH GENERATOR").setBackgroundHeight(140).setBarHeight(32).setWidth(220);
		CP5.begin(10, 10);
		CP5.addNumberbox("ITER_A").setPosition(10, 14).linebreak();
		CP5.addNumberbox("ITER_B").setPosition(10, 38).linebreak();
		CP5.addNumberbox("ITER_C").setPosition(10, 62).linebreak();
		CP5.addNumberbox("ITER_D").setPosition(10, 86).linebreak();
		CP5.addNumberbox("ITER_E").setPosition(10, 110).linebreak();
		CP5.end();
		MultiList mainMenu = CP5.addMultiList("myList", 90, 0, 130, 24); MultiListButton file;
		file = mainMenu.add("File", 1); file.setWidth(130); file.setHeight(20);
		file.add("file_quit", 11).setCaptionLabel("Quit");
		file.add("file_open", 12).setCaptionLabel("Open XML");
		file.add("file_save", 13).setCaptionLabel("Save XML");
		file.add("file_print", 14).setCaptionLabel("Print SVG");
		file.add("file_loadDef", 15).setCaptionLabel("Load Defaults");
		file.add("file_saveDef", 16).setCaptionLabel("Save Defaults");
		MultiListButton view; view = mainMenu.add("View", 2); view.setWidth(130); view.setHeight(20);
		view.add("view_physInfo", 26).setCaptionLabel("Info");
		view.add("view_outliner", 25).setCaptionLabel("Outliner");
		view.add("view_voronoi", 24).setCaptionLabel("Voronoi");
		view.add("view_nodes", 21).setCaptionLabel("Nodes");
		view.add("view_edges", 25).setCaptionLabel("Edges");
		view.add("view_particles", 22).setCaptionLabel("Particles");
		view.add("view_springs", 23).setCaptionLabel("Springs");
		view.add("view_minDist", 23).setCaptionLabel("MinDist");
		view.add("view_weights", 23).setCaptionLabel("Weights");
		view.add("view_behaviors", 23).setCaptionLabel("Behaviors");
		//		view.add("view_vorInfo", 27).setCaptionLabel("Show Vor Info");
//		view.add("view_nodesColor", 28).setCaptionLabel("Nodes Color");
		MultiListButton run; run = mainMenu.add("Run", 3); run.setWidth(130); run.setHeight(20);
		run.add("run_physics", 31).setCaptionLabel("Run Physics");
		run.add("run_voronoi", 32).setCaptionLabel("Run Voronoi");
		run.add("run_updateVals", 33).setCaptionLabel("Update Values");
		run.add("run_flowgraph", 33).setCaptionLabel("Update FlowGraph");
		MultiListButton edit; edit = mainMenu.add("Edit", 4); edit.setWidth(130); edit.setHeight(20);
		edit.add("edit_addMinDist", 41).setCaptionLabel("Add MinDist");
		edit.add("edit_rebuildMinD", 42).setCaptionLabel("Rebuild MinDist");
		edit.add("edit_clearMinD", 43).setCaptionLabel("Clear MinDist");
		edit.add("edit_clearPhys", 44).setCaptionLabel("Reset Physics");
		edit.add("edit_clearAll", 45).setCaptionLabel("Clear");
		CP5.begin(0, 0);
		properties = CP5.addGroup("OBJECT_PROPERTIES").setBackgroundHeight(200).setBarHeight(32).setWidth(220);
		radiusSlider = CP5.addKnob("setSize").setCaptionLabel("Size").setRange(0, 500).setPosition(10, 30).setDecimalPrecision(1);
		radiusSlider.setValue(50);
		radiusSlider.addListener(new radiusSliderListener());
		radiusSlider.hide();
		colorSlider = CP5.addKnob("setColor").setCaptionLabel("Color").setRange(0, 100).setPosition(80, 30).setDecimalPrecision(0);
		colorSlider.setValue(50);
		colorSlider.addListener(new colorSliderListener());
		colorSlider.hide();
		capacitySlider = CP5.addKnob("setCapacity").setCaptionLabel("Capacity").setRange(1, 200).setPosition(150, 30).setDecimalPrecision(0);
		capacitySlider.setValue(1);
		capacitySlider.addListener(new capacitySliderListener());
		capacitySlider.hide();
		nameTextfield = CP5.addTextfield("setName").setCaptionLabel("Unique Datablock ID Name").setPosition(40, 140);
		nameTextfield.setValue("untitled");
		nameTextfield.addListener(new nameTextfieldListener());
		nameTextfield.hide();
		CP5.end();
		accordion = CP5.addAccordion("acc").setPosition(0, 92).setWidth(220).setCollapseMode(Accordion.MULTI);
		accordion.addItem(config).addItem(generator).addItem(properties);
		accordion.open(0, 1);
	}
	private void initGUI_styles() {
		for (Button b : CP5.getAll(Button.class)) {
			b.setSize(130, 22);
			b.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER).setFont(pfont);
		} for (Numberbox n : CP5.getAll(Numberbox.class)) {
			n.setSize(200, 16);
			n.setRange(0, 10);
			n.setDirection(Controller.HORIZONTAL);
			n.setMultiplier(0.05f);
			n.setDecimalPrecision(0);
			n.getCaptionLabel().align(ControlP5.RIGHT, ControlP5.CENTER);
			n.getValueLabel().align(ControlP5.LEFT, ControlP5.CENTER);
			n.setGroup(generator);
		} for (Knob k : CP5.getAll(Knob.class)) {
			k.setRadius(30);
			k.setDragDirection(Knob.HORIZONTAL);
			k.setGroup(properties);
		} for (Textfield t : CP5.getAll(Textfield.class)) {
			t.setSize(140, 22);
			t.setAutoClear(false);
			t.getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE).getStyle().setPaddingTop(4);
			t.setGroup(properties);
		} for (Slider s : CP5.getAll(Slider.class)) {
			s.setSize(170, 16);
			s.showTickMarks(false);
			s.setHandleSize(12);
			s.setSliderMode(Slider.FLEXIBLE);
			s.getValueLabel().align(ControlP5.RIGHT_OUTSIDE, ControlP5.CENTER).getStyle().setPaddingLeft(4);
			s.getCaptionLabel().align(ControlP5.RIGHT, ControlP5.CENTER).getStyle().setPaddingRight(4);
			s.setGroup(config);
		} for (Group g : CP5.getAll(Group.class)) {
			g.setBackgroundColor(Color.CP5_GRP);
			g.getCaptionLabel().align(ControlP5.LEFT, ControlP5.CENTER).getStyle().setPaddingLeft(4);
		}
	}
	public void controlEvent(ControlEvent theEvent) {
		if (!theEvent.isGroup()) {
			println("controlEvent: " + theEvent.getController().getName());
			switch (theEvent.getController().getName()) {
				case "file_quit": System.out.println("[quit]"); exit(); break;
				case "file_open": GRAPH.rebuild();break;
				case "file_save": GRAPH.build();/*XMLtool.marshal(); */ break;
				case "file_print": beginRecord(P8gGraphicsSVG.SVG, "./out/svg/print-###.svg"); RECORDING = true; break;
//				case "file_loadDef": CP5.loadProperties((xmlFilePath + "defaults.ser")); break;
//				case "file_saveDef": CP5.saveProperties((xmlFilePath + "defaults.ser")); break;
				case "view_nodes": CONF.showNodes = !CONF.showNodes; break;
//				case "view_nodesColor": FSys.showNodesCol = !FSys.showNodesCol; break;
				case "view_edges": CONF.showEdges = !CONF.showEdges; break;
				case "view_outliner": CONF.showOutliner = !CONF.showOutliner; break;
				case "view_particles": CONF.showParticles = !CONF.showParticles; break;
				case "view_springs": CONF.showSprings = !CONF.showSprings; break;
				case "view_minDist": CONF.showMinDist = !CONF.showMinDist; break;
				case "view_weights": CONF.showWeights = !CONF.showWeights; break;
				case "view_behaviors": CONF.showBehaviors = !CONF.showBehaviors; break;
				case "view_physInfo": CONF.showInfo = !CONF.showInfo; break;
				case "view_voronoi": CONF.showVoronoi = !CONF.showVoronoi; break;
//				case "view_vorInfo": CONF.showVorInfo = !CONF.showVorInfo; break;
				case "run_physics": CONF.isUpdating = !CONF.isUpdating; break;
				case "run_flowgraph": GEDIT.isUpdating = !GEDIT.isUpdating; break;
				case "run_voronoi": CONF.UPDATE_VORONOI = !CONF.UPDATE_VORONOI; break;
				case "edit_addMinDist": PSYS.addMinDist(); break;
				case "edit_rebuildMinD": PSYS.clearMinDist(); PSYS.addMinDist(); break;
				case "edit_clearMinD": PSYS.clearMinDist(); break;
				case "edit_clearPhys": PSYS.reset(); break;
				case "edit_clearAll": PSYS = new PSys(this); GRAPH.reset(); break;
				case "world_scale": world_scale = theEvent.getController().getValue(); break;
				case "verlet_drag": CONF.PHYS_DRAG = (theEvent.getController().getValue()); break;
				case "particle_scale": CONF.particleScale = theEvent.getController().getValue(); break;
				case "mindist_strength": CONF.mindistStrength = theEvent.getController().getValue(); break;
				case "particle_strength": CONF.particleWeight = theEvent.getController().getValue(); break;
				case "behavior_scale": CONF.behaviorScale = theEvent.getController().getValue(); break;
				case "behavior_strength": CONF.behaviorStrength = theEvent.getController().getValue(); break;
				case "spring_scale": CONF.springScale = theEvent.getController().getValue(); break;
				case "spring_strength": CONF.springStrength = theEvent.getController().getValue(); break;
			}
		}
	}

	static class nameTextfieldListener implements ControlListener {
		String name;
		public void controlEvent(ControlEvent e) {
			name = e.getController().getStringValue();
			GEDIT.setName(name);
		/*	if (e.getController().isMousePressed()) {				for (GNode n : GEDIT.getSelectedNodes()) { n.setName(name); }			}*/
		}
	}

	static class colorSliderListener implements ControlListener {
		float color;
		public void controlEvent(ControlEvent e) {
			color = e.getController().getValue();
			GEDIT.setColor(color);
		/*	if (e.getController().isMousePressed()) {				for (GNode n : GEDIT.getSelectedNodes()) { n.setColor(color); }			}*/
		}
	}

	static class capacitySliderListener implements ControlListener {
		float capacity;
		public void controlEvent(ControlEvent e) {
			capacity = e.getController().getValue();
			GEDIT.setOccupancy(capacity);
	/*		if (e.getController().isMousePressed()) {				for (GNode n : GEDIT.getSelectedNodes()) { n.setOccupancy(capacity); }			}*/
		}
	}

	static class radiusSliderListener implements ControlListener {
		float size;
		public void controlEvent(ControlEvent e) {
			size = e.getController().getValue();
			GEDIT.setSize(size);
		}
	}
}
//		translate(-((ZOOM * width) - width) / 2, -((ZOOM * height) - height) / 2);		scale(ZOOM);
