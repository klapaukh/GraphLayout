package generation;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import javax.swing.JComponent;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import representation.Arc;
import representation.Node;
import representation.SpriteLibrary;
import representation.Vector;
import representation.VectorFactory;

public class Graph {

	List<Node> nodes;
	List<Arc> edges;
	public long forceMode;
	public int numLabels, numNodes;
	public final static long HOOKES_LAW = 0x1;
	public static final long CHARGED_WALLS = 0x2;
	public static final long COLLISIONS = 0x4;
	public static final long COULOMBS_LAW = 0x8;
	public static final long CHARGED_LABELS = 0x10;
	public static final long CHARGED_EDGE_CENTERS = 0x20;
	public static final long FULL_COLLISIONS = 0x40;
	public static final long HOOKES_LOG_LAW = 0x80;
	public static final long DEGREE_BASED_CHARGE = 0x100;
	public static final long WRAP_AROUND_CHARGES = 0x200;
	public double dampening = 0.9;
	public double epsilon = 3, epsilonClose = 50;
	public int iterMax = 10000;
	public double epsilonStep = 0.05;
	public double timestep = 0.01; // timestep in seconds
	public int defaultWidth = 80, defaultHeight = 80, defaultMass = 2;
	public double defaultCharge = 3, defaultLabelCharge = 3;
	public double totalVertexCharge = 10, totalLabelCharge = 1;
	public double wallCharge = 10000;// 0.03;
	public double k = 0.2;
	public double c1 = -60;// , c2 = 0;
	public double ke = 50000;
	public double coefficientOfRestitution = 0.9;
	private Random r;
	public Semaphore lock;
	public int graphHeight, graphWidth;
	private SpriteLibrary sprites;

	public Graph(JComponent parent, long forceMode) {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Arc>();
		this.forceMode = forceMode;
		lock = new Semaphore(1);
		r = new Random();

		graphWidth = 1920;
		graphHeight = 1080;

		if (parent != null) {
			parent.setMinimumSize(new Dimension(graphWidth, graphHeight));
			parent.setMaximumSize(new Dimension(graphWidth, graphHeight));
			parent.setPreferredSize(new Dimension(graphWidth, graphHeight));
			parent.setSize(graphWidth, graphHeight);
		}
		this.sprites = null;
		try {
			this.sprites = new SpriteLibrary();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Graph(JComponent parent) {
		this(parent, 0x11111111);
	}

	public Graph() {
		this(null, 0x11111111);
	}

	public Graph(long forceMode) {
		this(null, forceMode);
	}

	public void countLabels() {
		numLabels = 0;
		for (Arc a : edges) {
			numLabels += a.name == null ? 0 : 1;
		}
	}

	public int getHeight() {
		return graphHeight;
	}

	public int getWidth() {
		return graphWidth;
	}

	public void countNodes() {
		numNodes = nodes.size();

	}

	public double getTotalEnergy() {
		double energy = 0;
		for (Node n : nodes) {
			energy += n.kineticEnergy();
		}
		return energy;
	}

	public int getTotalDrawingArea() {
		int area = 0;
		for (int i = 0; i < graphWidth; i++) {
			for (int j = 0; j < graphHeight; j++) {
				area += numDrawn(i, j);
			}
		}
		return area;
	}

	public int calculateAreaDrawn() {
		int area = 0;
		for (int i = 0; i < graphWidth; i++) {
			for (int j = 0; j < graphHeight; j++) {
				if (drawn(i, j)) {
					area += 1;
				}
			}
		}
		return area;
	}

	public double getTotalEdgeLength() {
		double dist = 0;
		for (Arc a : edges) {
			dist += a.length();
		}
		return dist;
	}
	
	public double getMeanEdgeLength() {
		double dist = 0;
		for (Arc a : edges) {
			dist += a.length();
		}
		return dist/edges.size();
	}
	
	
	public double getVarianceEdgeLength() {
		double mean = 0;
		double ex2 = 0;
		for (Arc a : edges) {
			mean += a.length();
			ex2 += a.length() * a.length();
		}
		mean /= edges.size();
		ex2 /= edges.size();
		
		return ex2-(mean*mean);
	}

	public double layoutWidth() {
		double x0 = nodes.get(0).x();
		double x1 = x0 + nodes.get(0).width();

		for (Node n : nodes) {
			if (n.x() < x0) {
				x0 = n.x();
			}
			if (n.x() + n.width() > x1) {
				x1 = n.x() + n.width();
			}
		}
		return x1 - x0;
	}

	public double layoutHeight() {
		double y0 = nodes.get(0).y();
		double y1 = y0 + nodes.get(0).height();

		for (Node n : nodes) {
			if (n.y() < y0) {
				y0 = n.y();
			}
			if (n.y() + n.height() > y1) {
				y1 = n.y() + n.height();
			}
		}
		return y1 - y0;
	}

	private int numDrawn(int x, int y) {
		int num = 0;
		for (Node n : nodes) {
			if (n.in(x, y)) {
				num += 1;
			}
		}
		for (Arc a : edges) {
			if (a.label.in(x, y)) {
				num += 1;
			}
		}
		return num;
	}

	public void draw(Graphics2D c) {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (Arc a : edges) {
			a.drawArc(c);
		}
		for (Node n : nodes) {
			n.draw(c);
		}
		lock.release();
	}

	private boolean drawn(int x, int y) {
		for (Node n : nodes) {
			if (n.in(x, y)) {
				return true;
			}
		}
		for (Arc a : edges) {
			if (a.label.in(x, y)) {
				return true;
			}
		}
		return false;
	}

	public void add(Atom a) {
		if (!has(a.name)) {
			int idx = a.type.lastIndexOf('/');
			String type = a.type;
			if (idx != -1) {
				type = a.type.substring(idx + 1);
			}
			nodes.add(new Node(1, 1, defaultWidth, defaultHeight, a.name, type, sprites, defaultCharge, defaultMass));
		}
	}

	public boolean has(String a) {
		for (Node n : nodes) {
			if (n.label.equals(a)) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		nodes.clear();
		edges.clear();
	}

	public void toggleForceMode(long force) {
		forceMode ^= force;
		if ((force & HOOKES_LAW) != 0) {
			forceMode = forceMode & ~(HOOKES_LOG_LAW);
		}
		if ((force & HOOKES_LOG_LAW) != 0) {
			forceMode = forceMode & ~HOOKES_LAW;
		}
	}

	public void addLink(String o1, String o2, String rel) {
		Node n1 = null, n2 = null;

		for (Node n : nodes) {
			if (n.label.equals(o1)) {
				n1 = n;
			}
			if (n.label.equals(o2)) {
				n2 = n;
			}
		}

		if (n1 == null) {
			n1 = new Node(1, 1, defaultWidth, defaultHeight, o1, "Unknown", sprites, defaultCharge, defaultMass);
			nodes.add(n1);
		}

		if (n2 == null) {
			n2 = new Node(1, 1, defaultWidth, defaultHeight, o2, "Unknown", sprites, defaultCharge, defaultMass);
			nodes.add(n2);
		}

		n1.addEdge();
		n2.addEdge();
		edges.add(new Arc(n1, n2, rel, defaultLabelCharge, defaultMass, sprites));

	}

	/**
	 * Initialises all the nodes. Sets their velocities to 0, and randomly places them on the screen such that they are non overlapping and not next
	 * to a wall.
	 */
	public void init() {
		numNodes = nodes.size();
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			n.setVelocity(0, 0);// set up initial node velocities to (0,0)

			boolean overlapping = true;
			// Node must not overlap with any other nodes on creation
			while (overlapping) {
				// Node must not be adjacent to a wall when it is created
				n.setPosition(r.nextInt(graphWidth - 10 - n.width()) + 10, r.nextInt(graphHeight - 10 - n.height()) + 10);
				n.finaliseMove();
				overlapping = false;
				// Check if it overlaps anything
				for (int j = 0; j < i; j++) {
					overlapping |= n.overlapps(nodes.get(j));
					if (overlapping) {
						// break out if it finds something to save on time
						break;
					}
				}
			}
			n.setCharge(defaultCharge);
		}
	}

	/**
	 * Get a node at this x,y coordinate
	 * 
	 * @param x
	 *            The x coordinate to check for nodes at
	 * @param y
	 *            The y coordinate to check for nodes at
	 * @return A node that overlaps this point, or null if there are none
	 */
	public Node getNode(int x, int y) {
		for (Node n : nodes) {
			if (n.in(x, y)) {
				return n;
			}
		}
		return null;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Node a : nodes) {
			s.append(a.label + " ");
		}

		return s.toString();
	}

	private Vector coulombRepulsion(Node n1, Node n2, Vector v) {
		// F = ke * (q1*q2)/r^2
		double r = Math.max(1,
				Math.hypot((n1.x() + n1.width() / 2.0) - (n2.x() + n2.width() / 2.0), (n1.y() + n1.height() / 2.0) - (n2.y() + n2.height() / 2.0)));

		double d = 1;
		if ((forceMode & DEGREE_BASED_CHARGE) != 0) {
			d = Math.max(1, (n1.degree() * n2.degree()) / 4.0); // Average node has 2 neighbors
		}
		double f = (d * ke * n1.charge() * n2.charge()) / (r * r);

		double theta = Math.atan2((n1.y() + n1.height() / 2.0) - (n2.y() + n2.height() / 2.0), (n1.x() + n1.width() / 2.0)
				- (n2.x() + n2.width() / 2.0));
		if (theta < 0) {
			theta = 2 * Math.PI + theta;
		}
		// System.out.println(f + " " + theta);

		v.set(f * Math.cos(theta), f * Math.sin(theta));
		// System.out.println(v);
		return v;
	}

	private Vector backwardCoulombRepulsion(Node n1, Node n2, Vector v) {
		// F = ke * (q1*q2)/r^2
		double dx = graphWidth - (n1.x() + n1.width() / 2.0) - (n2.x() + n2.width() / 2.0);
		double dy = graphHeight - (n1.y() + n1.height() / 2.0) - (n2.y() + n2.height() / 2.0);

		double r = Math.max(1, Math.hypot(dx, dy));

		double d = 1;
		if ((forceMode & DEGREE_BASED_CHARGE) != 0) {
			d = Math.max(1, (n1.degree() * n2.degree()) / 4.0); // Average node has 2 neighbors
		}
		double f = (d * ke * n1.charge() * n2.charge()) / (r * r);

		double theta = Math.atan2((n1.y() + n1.height() / 2.0) - (n2.y() + n2.height() / 2.0), (n1.x() + n1.width() / 2.0)
				- (n2.x() + n2.width() / 2.0));
		if (theta < 0) {
			theta = 2 * Math.PI + theta;
		}
		// System.out.println(f + " " + theta);

		// Actually the angle is reversed :O
		theta += Math.PI;

		v.set(f * Math.cos(theta), f * Math.sin(theta));
		// System.out.println(v);
		return v;
	}

	private Vector hookeAttraction(Node n1, Node n2, Vector v) {
		// F = -k * distance
		double naturalLength = Math.max(n1.width(), n1.height()) + Math.max(n2.width(), n2.height());
		double centerDistance = Math.hypot((n1.x() + n1.width() / 2.0) - (n2.x() + n2.width() / 2.0),
				(n1.y() + n1.height() / 2.0) - (n2.y() + n2.height() / 2.0));
		double f = -k * (centerDistance - naturalLength);
		double theta = Math.atan2((n1.y() + n1.height() / 2.0) - (n2.y() + n2.height() / 2.0), (n1.x() + n1.width() / 2.0)
				- (n2.x() + n2.width() / 2.0));
		if (theta < 0) {
			theta = 2 * Math.PI + theta;
		}

		return v.set(f * Math.cos(theta), f * Math.sin(theta));

	}

	private Vector hookeLogAttraction(Node n1, Node n2, Vector v) {
		// F = -k * distance
		double naturalLength = Math.max(n1.width(), n1.height()) + Math.max(n2.width(), n2.height());
		double centerDistance = Math
				.max(Math.hypot((n1.x() + n1.width() / 2.0) - (n2.x() + n2.width() / 2.0), (n1.y() + n1.height() / 2.0)
						- (n2.y() + n2.height() / 2.0)), 1);

		// Eade's log attraction
		double f = c1 * Math.log(centerDistance / naturalLength);

		if (Double.isNaN(f) || Double.isInfinite(f)) {
			System.out.println("hookeLogAttraction is NaN or Infinite");
		}

		double theta = Math.atan2((n1.y() + n1.height() / 2.0) - (n2.y() + n2.height() / 2.0), (n1.x() + n1.width() / 2.0)
				- (n2.x() + n2.width() / 2.0));
		if (theta < 0) {
			theta = 2 * Math.PI + theta;
		}

		return v.set(f * Math.cos(theta), f * Math.sin(theta));

	}

	private Vector chargedWalls(Node n, Vector v, Vector v2) {
		int nx = (int) (n.x() + n.width() / 2), ny = (int) (n.y() + n.height() / 2);
		v.set(0, 0);
		v.add(wall(n.charge(), wallCharge, getHeight(), nx, ny, v2)); // LHS wall
		v.add(wall(n.charge(), wallCharge, getHeight(), nx - getWidth(), ny, v2)); // RHS wall
		v.addUpsideDown(wall(n.charge(), wallCharge, getWidth(), ny, nx, v2)); // top wall
		v.addUpsideDown(wall(n.charge(), wallCharge, getWidth(), ny - getHeight(), nx, v2)); // bottom wall

		return v;
	}

	private Vector wall(double q, double wq, double L, double x, double y, Vector v) {
		double xConst = y / (x * Math.sqrt(x * x + y * y)) + (L - y) / (x * Math.sqrt(x * x + (y - L) * (y - L)));
		double yConst = 1 / (Math.sqrt(x * x + (y - L) * (y - L))) - 1 / (Math.sqrt(x * x + y * y));
		v.set(xConst, yConst);
		return v.multiply(q * wq);
	}

	public double oneIterWikiAlgorith() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		double total_kinetic_energy = 0; // running sum of total kinetic energy over all particles
		Vector solution = VectorFactory.newVector(0, 0);
		Vector solution2 = VectorFactory.newVector(0, 0);

		for (Node n : nodes) {
			n.resetForces();
		}

		// Coulombs law
		if ((forceMode & COULOMBS_LAW) != 0) {
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = i + 1; j < nodes.size(); j++) {
					Node n = nodes.get(i);
					Node n2 = nodes.get(j);
					coulombRepulsion(n, n2, solution);
					n.addForce(solution);
					n2.addForce(solution.multiply(-1));
				}
			}
		}

		// Repulisive labels
		if ((forceMode & CHARGED_LABELS) != 0) {
			//Node v label
			for (Node n : nodes) {
				for (Arc a : edges) {
					if (!a.contains(n)) { //don't repel your own nodes
						coulombRepulsion(n, a.label, solution);
						n.addForce(solution);
						solution.multiply(-1);
						a.left.addForce(solution);
						a.right.addForce(solution);
					}
				}
			}
			
			//Label v Label
			for(int i =0 ; i < edges.size(); i++){
				for( int j = i+1; j < edges.size();j++){  //Repel ALL other labels
					Arc a1 = edges.get(i);
					Arc a2 = edges.get(j);
					
					coulombRepulsion(a1.label, a2.label, solution); 
					
					a1.left.addForce(solution);
					a2.right.addForce(solution);
					solution.multiply(-1);
					a2.left.addForce(solution);
					a2.right.addForce(solution);
				}
			}
		}

		
		// Repulisive labels
		if ((forceMode & CHARGED_EDGE_CENTERS) != 0) {
			//Node v label
			for (Node n : nodes) {
				for (Arc a : edges) {
					if (!a.contains(n)) { //don't repel your own nodes
						coulombRepulsion(n, a.edgeCenter, solution);
						n.addForce(solution);
						solution.multiply(-1);
						a.left.addForce(solution);
						a.right.addForce(solution);
					}
				}
			}
			
			//Label v Label
			for(int i =0 ; i < edges.size(); i++){
				for( int j = i+1; j < edges.size();j++){  //Repel ALL other labels
					Arc a1 = edges.get(i);
					Arc a2 = edges.get(j);
					
					coulombRepulsion(a1.edgeCenter, a2.edgeCenter, solution);
					
					a1.left.addForce(solution);
					a2.right.addForce(solution);
					solution.multiply(-1);
					a2.left.addForce(solution);
					a2.right.addForce(solution);
				}
			}
		} 
		// Hookes Law
		if ((forceMode & HOOKES_LAW) != 0 || (forceMode & HOOKES_LOG_LAW) != 0) {
			for (Node n : nodes) {
				for (Arc a : edges) {
					Node n2 = a.other(n);
					if (n2 != null && !n2.equals(n)) {
						if ((forceMode & HOOKES_LAW) != 0) {
							hookeAttraction(n, n2, solution);
						} else {
							hookeLogAttraction(n, n2, solution);
						}
						n.addForce(solution);
						n2.addForce(solution.multiply(-1));
					}
				}
			}
		}

		if ((forceMode & CHARGED_WALLS) != 0) {
			for (Node n : nodes) {
				n.addForce(chargedWalls(n, solution, solution2)); // Charged walls
			}
		}

		// Coulombs law
		if ((forceMode & WRAP_AROUND_CHARGES) != 0) {
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = i + 1; j < nodes.size(); j++) {
					Node n = nodes.get(i);
					Node n2 = nodes.get(j);
					backwardCoulombRepulsion(n, n2, solution);
					n.addForce(solution);
					n2.addForce(solution.multiply(-1));
				}
			}
		}

		for (Node n : nodes) {
			n.updateVelocity(dampening, timestep);
		}

		if ((forceMode & FULL_COLLISIONS) != 0) {

			double firstWallHit = timestep * 1.2;
			List<Node> wallHit = new ArrayList<Node>();
			for (Node n : nodes) {
				double d = n.move(graphWidth, graphHeight, timestep);
				if (Double.compare(d, firstWallHit) < 0) {
					firstWallHit = d;
					wallHit.clear();
					wallHit.add(n);
				} else if (Double.compare(d, firstWallHit) == 0) {
					wallHit.add(n);
				}
			}

			double firstObjectHit = timestep * 1.2;
			List<Node> objHit = new ArrayList<Node>();
			if ((forceMode & FULL_COLLISIONS) != 0) {
				for (int i = 0; i < nodes.size(); i++) {
					for (int j = i + 1; j < nodes.size(); j++) {
						double d = nodes.get(i).fullCollided(nodes.get(j), timestep);
						if (Double.compare(d, firstObjectHit) < 0) {
							firstObjectHit = d;
							objHit.clear();
							objHit.add(nodes.get(i));
							objHit.add(nodes.get(j));
						} else if (Double.compare(d, firstObjectHit) == 0) {
							objHit.add(nodes.get(i));
							objHit.add(nodes.get(j));

						}
					}
				}
			}

			if (Double.compare(firstWallHit, timestep) >= 0 && Double.compare(firstObjectHit, timestep) >= 0) {
				for (Node n : nodes) {
					n.finaliseMove();
				}
			} else {
				int first = Double.compare(firstWallHit, firstObjectHit);

				if (first < 0) {
					// only hit at wall
					if (Double.compare(firstWallHit, 0) <= 0) {
						System.out.println("ILLEGAL WALL " + String.format("%.3f", firstWallHit));
					}
					for (Node n : nodes) {
						n.move(graphWidth, graphHeight, firstWallHit);
					}

					for (Node n : wallHit) {
						n.collideWall(graphWidth, graphHeight, true);
					}

					for (Node n : nodes) {
						n.finaliseMove();
					}

				} else if (first > 0) {
					// Only hit objects
					if (Double.compare(firstObjectHit, 0) <= 0) {
						System.out.println("ILLEGAL OBJECT " + String.format("%.3f", firstObjectHit));
						firstObjectHit = 0.001 * timestep;
					}
					for (Node n : nodes) {
						n.move(graphWidth, graphHeight, firstObjectHit);
						n.finaliseMove();
					}

					for (int i = 0; i < objHit.size(); i += 2) {
						objHit.get(i).collided(objHit.get(i + 1), coefficientOfRestitution);
						objHit.get(i + 1).collided(objHit.get(i), coefficientOfRestitution);
					}
				} else {
					// both happened
					if (Double.compare(firstWallHit, 0) <= 0) {
						System.out.println("ILLEGAL BOTH " + String.format("%.3f", firstObjectHit));
					}
					for (Node n : nodes) {
						n.move(graphWidth, graphHeight, firstObjectHit);
					}

					for (Node n : wallHit) {
						n.collideWall(graphWidth, graphHeight, true);
					}

					for (Node n : nodes) {
						n.finaliseMove();
					}

					for (int i = 0; i < objHit.size(); i += 2) {
						objHit.get(i).collided(objHit.get(i + 1), coefficientOfRestitution);
						objHit.get(i + 1).collided(objHit.get(i), coefficientOfRestitution);
					}
				}
			}
		} else {
			for (Node n : nodes) {
				n.move(graphWidth, graphHeight, timestep);
				n.collideWall(graphWidth, graphHeight, false);
				n.finaliseMove();
			}
		}
		if ((forceMode & COLLISIONS) != 0) {
			//Node v Node
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = i + 1; j < nodes.size(); j++) {
					if (nodes.get(i).overlapps(nodes.get(j))) {// Nodes have hit
						// System.out.println("Collided");
						nodes.get(i).collided(nodes.get(j), coefficientOfRestitution);
						nodes.get(j).collided(nodes.get(i), coefficientOfRestitution);
					}
				}// next node
			}
			
			//Node V label
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = 0; j < edges.size(); j++) {
					Node n1 = nodes.get(i);
					Arc a1 = edges.get(j);
					if (n1.overlapps(a1.label)) {// Nodes have hit
						// System.out.println("Collided");
						n1.collided(a1.left, coefficientOfRestitution);
						n1.collided(a1.right, coefficientOfRestitution);
						a1.left.collided(n1, coefficientOfRestitution);
						a1.right.collided(n1, coefficientOfRestitution);
						
					}
				}// next node
			}
			
			for (int i = 0; i < edges.size(); i++) {
				for (int j = i + 1; j < edges.size(); j++) {
					Arc a1 = edges.get(i);
					Arc a2 = edges.get(j);
					if (a1.label.overlapps(a2.label)) {// Nodes have hit
						// System.out.println("Collided");
						a1.left.collided(a2.left, coefficientOfRestitution);
						a1.left.collided(a2.right, coefficientOfRestitution);
						a1.right.collided(a2.left, coefficientOfRestitution);
						a1.right.collided(a2.right, coefficientOfRestitution);
						
						a2.left.collided(a1.left, coefficientOfRestitution);
						a2.left.collided(a1.right, coefficientOfRestitution);
						a2.right.collided(a1.left, coefficientOfRestitution);
						a2.right.collided(a1.right, coefficientOfRestitution);
					}
				}// next node
			}
		}

		for (Node n : nodes) {
			total_kinetic_energy = total_kinetic_energy + n.kineticEnergy();
		}

		VectorFactory.delete(solution);
		VectorFactory.delete(solution2);

		lock.release();
		return Double.isNaN(total_kinetic_energy) ? Double.MAX_VALUE : total_kinetic_energy;

	}

	public void addBlackHole(int x, int y) {
		Node h = new Node(x, y, 10, 10, "BlackHole", "PhysicalAnomaly", sprites, -defaultCharge, Double.MAX_VALUE);
		h.toggleMovement();
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		nodes.add(h);
		lock.release();

	}

	public void setSize(int width, int height) {
		this.graphWidth = width;
		this.graphHeight = height;
	}

	public int[] countOverlaps() {
		int[] results = new int[4];

		// count Node Node overlaps
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i + 1; j < nodes.size(); j++) {
				results[0] += nodes.get(i).overlapps(nodes.get(j)) ? 1 : 0;
			}
		}

		// count label label overlapps
		for (int i = 0; i < edges.size(); i++) {
			for (int j = i + 1; j < edges.size(); j++) {
				results[1] += edges.get(i).label.overlapps(edges.get(j).label) ? 1 : 0;
			}
		}

		// count node label overlapps
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				results[2] += nodes.get(i).overlapps(edges.get(j).label) ? 1 : 0;
			}
		}

		results[3] = 0;
		// count Edge crossings
		for (int i = 0; i < edges.size(); i++) {
			for (int j = i + 1; j < edges.size(); j++) {
				Arc e1 = edges.get(i);
				Arc e2 = edges.get(j);
				Node k1 = e1.left;
				Node k2 = e1.right;

				Node l1 = e2.left;
				Node l2 = e2.right;

				double gradk = (k1.y() - k2.y()) / (double) (k1.x() - k2.x());
				double gradl = (l1.y() - l2.y()) / (double) (l1.x() - l2.x());

				double k1y = k1.y() + k1.height() / 2.0;
				double l1y = l1.y() + l1.height() / 2.0;
				double k1x = k1.x() + k1.width() / 2.0;
				double l1x = l1.x() + l1.width() / 2.0;
				double k2y = k2.y() + k2.height() / 2.0;
				double l2y = l2.y() + l2.height() / 2.0;
				double l2x = l2.x() + l2.width() / 2.0;

				if (Double.compare(gradk, gradl) == 0 || (Double.isInfinite(gradk) && Double.isInfinite(gradl))) {
					// parallel case
					if (Double.isInfinite(gradk)) {
						// vertical
						double ky1 = Math.min(k1y, k2y);
						double ly1 = Math.min(l1y, l2y);
						double ky2 = Math.max(k1y, k2y);
						double ly2 = Math.max(l1y, l2y);

						if (ky2 < ly1 || ly2 < ky1) {
							results[3] += 0;
						} else {
							results[3] += 1;
						}

					} else {
						double kx1 = Math.min(k1x, l2x);
						double lx1 = Math.min(l1x, l2x);
						double kx2 = Math.max(k1x, l2x);
						double lx2 = Math.max(l1x, l2x);

						if (kx2 < lx1 || lx2 < kx1) {
							results[3] += 0;
						} else {
							results[3] += 1;
						}
					}
				} else {
					// not parallel
					double a = l2.x() - l1.x();
					double b = k1.x() - k2.x();
					double c = l2.y() - l1.y();
					double d = k1.y() - k2.y();
					double e = k1.x() - l1.x();
					double f = k1.y() - l1.y();

					double detInv = a * d - b * c;
					double s = (d * e - b * f) / detInv;
					double t = (-c * e + a * f) / detInv;

					if (s > 0 && s < 1 && t > 0 && t < 1) {
						results[3] += 1;
					}

				}
			}
		}
		return results;

	}

	public void readGraphML(String fileName) {
		readGraphML(fileName, null);
	}

	public void readGraphML(String fileName, String node) {
		GraphMLXMLParser def = new GraphMLXMLParser(node);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File(fileName), def);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		buildGraph(def.getObjects(), def.getRelations());
	}

	public void readGraceDot(String fileName, String image) {
		try {
			GraceDotParser def = new GraceDotParser(new File(fileName),image);
			buildGraph(def.getObjects(), def.getRelations());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void buildGraph(List<Atom> atoms, Map<String, List<Relation>> connections) {

		try {
			lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		clear();
		for (Atom a : atoms) {
			add(a);
		}
		for (Map.Entry<String, List<Relation>> m : connections.entrySet()) {
			String name = m.getKey();
			List<Relation> l = m.getValue();

			for (Relation r : l) {
				addLink(name, r.element, r.relName);
			}
		}

		countNodes();
		countLabels();

		init();
		lock.release();
	}

	public int simulate(FontMetrics metrics) {
		init();
		double kineticEnergy = Double.MAX_VALUE;
		int i = 0;
		while (kineticEnergy > epsilon && i < iterMax) {
			kineticEnergy = oneIterWikiAlgorith();
			for (Arc a : edges) {
				a.updateArc(metrics);
			}
			i++;
		}
		return i;
	}

	public int cmx() {
		int numEdges = 0;
		for (Node n : nodes) {
			numEdges += n.degree();
		}

		int call = (numEdges * (numEdges - 1)) / 2;
		int cimp = 0;

		for (Node n : nodes) {
			int deg = n.degree();
			cimp += deg * (deg - 1);
		}

		cimp /= 2;

		int cmx = call - cimp;
		return cmx;
	}

	public int numberComponents() {
		int total = 0;

		List<Node> visited = new ArrayList<Node>();
		Stack<Node> toVisit = new Stack<Node>();

		while (visited.size() < nodes.size()) {
			Node start = null;
			for (int i = 0; i < nodes.size(); i++) {
				if (!visited.contains(nodes.get(i))) {
					start = nodes.get(i);
					break;
				}
			}
			if (start == null) {
				throw new RuntimeException("numberComponents is buggy. Start node was null");
			}
			toVisit.push(start);
			while (!toVisit.isEmpty()) {
				Node here = toVisit.pop();
				if (visited.contains(here)) {
					continue;
				}
				visited.add(here);
				for (Arc a : edges) {
					Node n = a.other(here);
					if (n != null && !visited.contains(n) && !toVisit.contains(n)) {
						toVisit.push(n);
					}
				}
			}
			total++;
		}

		return total;
	}

	public int[] getDegrees() {
		int[] degrees = new int[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			degrees[i] = nodes.get(i).degree();
		}
		return degrees;
	}

	public boolean isPlanar() {
		try {
			Process pickgraph = Runtime.getRuntime().exec("./pickgraph");
			OutputStream out = pickgraph.getOutputStream();
			Scanner scan = new Scanner(pickgraph.getInputStream());
			out.write((this.toDaveFormat()).getBytes());
			out.flush();
			String sol = scan.next();

			out.close();
			scan.close();
			pickgraph.destroy();

			if (sol.trim().equalsIgnoreCase("true")) {
				return true;
			}

			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public String toDaveFormat() {
		StringBuilder s = new StringBuilder();
		for (Arc a : edges) {
			int i = nodes.indexOf(a.left);
			int n = nodes.indexOf(a.right);
			s.append(i).append("--").append(n).append(',');
		}
		return s.replace(s.length() - 1, s.length(), "\n").toString();
	}

	public void remove(Node n) {
		try {
			lock.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < edges.size(); i++) {
			Node nn = edges.get(i).other(n);
			if (nn != null) {
				nn.removeEdge();
				if (!edges.get(i).contains(n)) {
					System.out.println("RAwr");
				}
				edges.remove(i--);
			}
		}
		this.nodes.remove(n);
		lock.release();

	}

	public void printRender(PrintStream out) {
		for (Node n : nodes) {
			out.print(n.label + ",");
			out.print(n.type + ",");
			out.print(((int) n.x()) + ",");
			out.print(((int) n.y()) + ",");
			out.print(n.imWidth() + ",");
			out.print(n.height());
			for (Arc a : edges) {
				Node nn = a.other(n);
				if (nn != null) {
					int pos = nodes.indexOf(nn);
					String name = a.name;
					out.print("," + pos + "," + name);
				}
			}
			out.println();
		}
	}

	public double angleDeviation() {
		int total = 0;
		int count = 0; 
		for(int i= 0; i < edges.size(); i++){
			for(int j=i+1;j<edges.size(); j++){
				Arc a1 =edges.get(i);
				Arc a2 =edges.get(j);

				if(a1.left == a1.right || a2.left == a2.right){
					//Ignore self loops;
					continue;
				}
				Node corner =null, n1=null, n2=null;
				if(a1.contains(a2.left)){
					corner = a2.left;
					n1 = a2.right;
					n2 = a1.other(a2.left);
				}else if(a1.contains(a2.right)){
					corner = a2.right;
					n1 = a2.left;
					n2 = a1.other(a2.right);
				}
				
				if(corner == null){
					continue;
				}
				
				double cx = corner.x() + corner.width()/2;
				double cy = corner.y() + corner.height()/2;
				
				double n1x = n1.x() + n1.width()/2;
				double n1y = n1.y() + n1.height()/2;
				
				double n2x = n2.x() + n2.width()/2;
				double n2y = n2.y() + n2.height()/2;
				
				double B = Math.sqrt(Math.pow(cx-n1x,2) + Math.pow(cy-n1y,2));
				double C = Math.sqrt(Math.pow(cx-n2x,2) + Math.pow(cy-n2y,2));
				double A = Math.sqrt(Math.pow(n1x-n2x,2) + Math.pow(n1y-n2y,2));
				
				double cosa = ((C*C)+(B*B) - (A*A))/(2*B*C);
				double a = Math.acos(cosa);
				
				double error = a- (360.0/corner.degree());
				
				total += error * error;
				count++;
				
			}
		}
		if(count != 0){
			total /= count;
		}
		return Math.sqrt(total);
	}
}
