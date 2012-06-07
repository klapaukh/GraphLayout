package generation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import representation.Node;

public class ForceDirectedLayout extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {

	private static final long serialVersionUID = -4643904229279719099L;
	private Graph g;

	private boolean mouseDown;

	public ForceDirectedLayout(long forceMode) {
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		g = new Graph(this, forceMode);
		g.setSize(this.getWidth(), this.getHeight());
		this.mouseDown = false;
	}

	public ForceDirectedLayout() {
		this(0x11111111);
	}

	public void readXML(String fileName) {

		AlloyXMLParser def = new AlloyXMLParser();
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

	public void readGraphML(String fileName) {
		GraphMLXMLParser def = new GraphMLXMLParser();
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
			g.lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		g.clear();

		for (Atom a : atoms) {
			g.add(a);
		}

		for (Map.Entry<String, List<Relation>> m : connections.entrySet()) {
			String name = m.getKey();
			List<Relation> l = m.getValue();

			for (Relation r : l) {
				g.addLink(name, r.element, r.relName);
			}
		}

		g.countNodes();
		g.countLabels();

		g.init();
		g.lock.release();
	}

	public void animateWikiAlgorithm() {
		new Thread() {
			public void run() {
				animate(0);
			}
		}.start();
	}

	private void animate(int i) {

		double kineticEnergy = Double.MAX_VALUE;
		while ((kineticEnergy > g.epsilon && i < g.iterMax)) {
			kineticEnergy = g.oneIterWikiAlgorith();
			i++;
			repaint();

			try {
				Thread.sleep((mouseDown || kineticEnergy < g.epsilonClose) ? 10 : 1);
			} catch (Throwable e) {
			}
		}
		System.out.println("Done: " + i + " steps with " + kineticEnergy + "J with "
				+ (g.countOverlaps()[0] + g.countOverlaps()[1] + g.countOverlaps()[2]) + " overlaps");
	}

	public String getGraphString() {
		return g.toString();
	} // replace

	public void paint(Graphics o) {
		Graphics2D c = (Graphics2D) o;
		c.setColor(Color.WHITE);
		c.fillRect(0, 0, getWidth(), getHeight());

		if (g == null) {
			c.drawRect(10, 10, 100, 100);
			return;
		}

		g.draw(c);
	}

	/**
	 * A undirected general graph
	 * 
	 * @author roma
	 * 
	 */

	public static String randString(int x, Random r) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < x; i++) {
			s.append((char) (r.nextInt(26) + 'a'));
		}
		return s.toString();
	}

	Point orig = null;
	Node n = null;

	@Override
	public void mousePressed(MouseEvent e) {
		mouseDown = true;
		if (e.getButton() == MouseEvent.BUTTON1) {// Left
			if (g == null) {
				return;
			}
			n = g.getNode(e.getX(), e.getY());
			if (n == null) {
				return;
			}
			n.setMobile(false);
			orig = new Point((int) n.x(), (int) n.y());
		} else if (e.getButton() == MouseEvent.BUTTON3) {// Right

			n = g.getNode(e.getX(), e.getY());
			if (n != null) {
				System.out.println("Removing Node " + n.label);
				g.remove(n);
			}
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			// g.toggleForceMoce();
			JPopupMenu pop = new JPopupMenu();
			JMenuItem menuItem = new JMenuItem("Hooke's Law");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.HOOKES_LAW);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			menuItem = new JMenuItem("Hooke's Log Law");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.HOOKES_LOG_LAW);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			menuItem = new JMenuItem("Charged Walls");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.CHARGED_WALLS);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			menuItem = new JMenuItem("Wrap Around Charges");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.WRAP_AROUND_CHARGES);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			menuItem = new JMenuItem("Collisions");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.COLLISIONS);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			menuItem = new JMenuItem("Coulomb's Law");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.COULOMBS_LAW);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			// menuItem = new JMenuItem("Solid Charges");
			// menuItem.addActionListener(new ActionListener() {
			// public void actionPerformed(ActionEvent e) {
			// g.toggleForceMode(Graph.SOLID_COULOMBS_LAW);
			// printForceMode(g.forceMode);
			// }
			// });
			// pop.add(menuItem);

			// menuItem = new JMenuItem("Full Collisions");
			// menuItem.addActionListener(new ActionListener() {
			// public void actionPerformed(ActionEvent e) {
			// g.toggleForceMode(Graph.FULL_COLLISIONS);
			// printForceMode(g.forceMode);
			// }
			// });
			// pop.add(menuItem);

			menuItem = new JMenuItem("Degree Based Charge");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.DEGREE_BASED_CHARGE);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			menuItem = new JMenuItem("Charged Labels");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					g.toggleForceMode(Graph.CHARGED_LABELS);
					printForceMode(g.forceMode);
				}
			});
			pop.add(menuItem);

			pop.show(this, e.getX(), e.getY());

		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDown = false;
		if (orig != null) {
			orig = null;
			int x = Math.max(0, e.getX());
			x = Math.min(getWidth(), x);
			int y = Math.min(getHeight(), e.getY());
			y = Math.max(0, y);
			n.setPosition(x, y);
			n.finaliseMove();
			n.setMobile(true);
			repaint();
		}
		n = null;

	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (n != null) {
			int x = Math.max(1, e.getX());
			x = Math.min(getWidth() - n.width(), x);
			int y = Math.min(getHeight() - n.height(), e.getY());
			y = Math.max(1, y);
			n.setPosition(x, y);
			n.finaliseMove();
			repaint();
		}
	}

	private int[] getOverlaps() {
		return g.countOverlaps();
	}

	public static void createRandomGraph(ForceDirectedLayout l) {
		// Generate Data
		Random r = new Random();
		String types[] = { "Dog", "Cat", "Person", "Rose", "Flower", "Tree", "Bridge", "Car", "MushroomCloud", "Missile", "Bird", "Tank", "Fish",
				"Rock", "Water", "Torch", "X" };
		int v = r.nextInt(10) + 5;
		List<Atom> atoms = new ArrayList<Atom>();
		Map<String, List<Relation>> connections = new TreeMap<String, List<Relation>>();

		for (int i = 0; i < v; i++) {
			atoms.add(new Atom(types[(int) (Math.random() * types.length)], randString(15, r)));
		}

		int maxe = r.nextInt(atoms.size()) + 4;
		List<Relation> c = new ArrayList<Relation>();
		for (int i = 0; i < maxe; i++) {
			Atom a = atoms.get(r.nextInt(atoms.size()));
			int idx1 = r.nextInt(atoms.size());
			c.add(new Relation(randString(3, r), atoms.get(idx1).name));
			connections.put(a.name, c);
		}

		l.buildGraph(atoms, connections);
	}

	public static void printForceMode(long mode) {
		System.out.println("Forces Active\n----------");
		if ((mode & Graph.HOOKES_LAW) != 0) {
			System.out.println("Hooke's Law");
		}
		if ((mode & Graph.CHARGED_WALLS) != 0) {
			System.out.println("Charged Walls");
		}
		if ((mode & Graph.COLLISIONS) != 0) {
			System.out.println("Collisions");
		}
		if ((mode & Graph.COULOMBS_LAW) != 0) {
			System.out.println("Coulomb's Law");
		}
		if ((mode & Graph.CHARGED_LABELS) != 0) {
			System.out.println("Charged Labels");
		}
		// if ((mode & Graph.SOLID_COULOMBS_LAW) != 0) {
		// System.out.println("Solid Charges Law");
		// }
		if ((mode & Graph.FULL_COLLISIONS) != 0) {
			System.out.println("Full Collisions");
		}
		if ((mode & Graph.HOOKES_LOG_LAW) != 0) {
			System.out.println("Logarithmic Hooke's Law");
		}
		if ((mode & Graph.WRAP_AROUND_CHARGES) != 0) {
			System.out.println("Wrap Around Charges");
		}
		if ((mode & Graph.DEGREE_BASED_CHARGE) != 0) {
			System.out.println("Degree Based Charge");
		}
	}

	private static String toAnalysisString(long forceMode) {
		StringBuilder s = new StringBuilder();

		if ((forceMode & Graph.COULOMBS_LAW) != 0) {
			// s.append('H');
		}
		if ((forceMode & Graph.HOOKES_LAW) != 0) {
			s.append('H');
		}
		if ((forceMode & Graph.HOOKES_LOG_LAW) != 0) {
			s.append('L');
		}
		if ((forceMode & Graph.CHARGED_WALLS) != 0) {
			s.append('W');
		}
		if ((forceMode & Graph.CHARGED_LABELS) != 0) {
			s.append('E');
		}
		if ((forceMode & Graph.DEGREE_BASED_CHARGE) != 0) {
			s.append('D');
		}
		if ((forceMode & Graph.COLLISIONS) != 0) {
			s.append('C');
		}
		if ((forceMode & Graph.FULL_COLLISIONS) != 0) {
			s.append('F');
		}
		if ((forceMode & Graph.WRAP_AROUND_CHARGES) != 0) {
			s.append('A');
		}
		return s.toString();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		g.setSize(this.getWidth(), this.getHeight());
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
		g.setSize(this.getWidth(), this.getHeight());
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	public static void main(String args[]) throws FileNotFoundException {

		boolean nonGraphical = args.length != 0;

		long forceMode = Graph.COULOMBS_LAW;

		if (!nonGraphical) {
			JFrame f = new JFrame("Testing Graph Layout Algorithms");
			final ForceDirectedLayout l;
			final JScrollPane s;
			f.getContentPane().add(s = new JScrollPane(l = new ForceDirectedLayout(forceMode)));
			s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			// Make a random graph
			// createRandomGraph(l);
			// Read data from data set
			// File dir = new File("rome");
			// String[] files = dir.list();

			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(1920, 1080);
			f.setVisible(true);

			l.g.iterMax = 10000;
			l.g.forceMode = Graph.HOOKES_LAW | Graph.COULOMBS_LAW | Graph.CHARGED_LABELS | Graph.CHARGED_WALLS;

			// l.animateWikiAlgorithm();
			long start = System.currentTimeMillis();
			l.readGraceDot("./" + "4480.dot", "Abb");
			// createRandomGraph(l);
			l.animate(0);
			int[] overlaps = l.getOverlaps();
			long end = System.currentTimeMillis();
			long t1 = (end - start) / 1000;
			System.out.println("Ran in " + t1 + " s with (" + overlaps[0] + ", " + overlaps[1] + ", " + overlaps[2] + " ,"
					+ (overlaps[0] + overlaps[1] + overlaps[2]) + ") overlaps");
			start = System.currentTimeMillis();

			System.out.println(String.format("%.3f", l.g.angleDeviation()));
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int over[] = l.getOverlaps();
				// f.setTitle("Overlap (" + over[0] + ", " + over[1] + ", " + over[2] + ", " + (over[0] + over[1] + over[2])
				// + ") (nodes, labels, mixed, total)");
				f.setTitle(over[3] + " Crossings");
			}
		} else {
			// test sets from http://www.graphdrawing.org/data.html

			if (args.length != 8) {
				System.err.println("Usage: java ForceDirectedLayout prefix dataSet file nodeImage maxIterations energyCutOff baseForce extraforces");
				System.err.println("Base Forces");
				System.err.println("0: Hooke's Law");
				System.err.println("1: Log Law");

				System.err.println("\nForces:");
				System.err.println("1: Coulomb's Law");
				System.err.println("2: Charged Walls");
				System.err.println("4: Charged Labels");
				System.err.println("8: Collisions");
				System.err.println("16: Degree Based Charge");
				System.err.println("32: Wrap Around Charges");

				System.exit(-1);
			}

			// get arguments
			String prefix = args[0];
			String folder = args[1];
			String file = args[2];
			String nodeImage = args[3];
			int maxIter = Integer.parseInt(args[4]);
			int cutOfEnergy = Integer.parseInt(args[5]);
			int baseforces = Integer.parseInt(args[6]);
			int extraforces = Integer.parseInt(args[7]);

			forceMode = 0;

			if (baseforces == 0) {
				forceMode |= Graph.HOOKES_LAW;
			} else if (baseforces == 1) {
				forceMode |= Graph.HOOKES_LOG_LAW;
			}
			if ((extraforces & 0x1) != 0) {
				forceMode |= Graph.COULOMBS_LAW;
			}
			if ((extraforces & 0x2) != 0) {
				forceMode |= Graph.CHARGED_WALLS;
			}
			if ((extraforces & 0x4) != 0) {
				forceMode |= Graph.CHARGED_LABELS;
			}
			if ((extraforces & 0x8) != 0) {
				forceMode |= Graph.COLLISIONS;
			}
			if ((extraforces & 0x10) != 0) {
				forceMode |= Graph.DEGREE_BASED_CHARGE;
			}
			if ((extraforces & 0x20) != 0) {
				forceMode |= Graph.WRAP_AROUND_CHARGES;
			}

			Graph g = new Graph(forceMode);
			g.iterMax = maxIter;
			g.epsilon = cutOfEnergy;

			if (file.endsWith("graphml")) {
				g.readGraphML(prefix + folder + File.separator + file, nodeImage);
			}else if(file.endsWith("dot")){
				g.readGraceDot(prefix + folder + File.separator + file, nodeImage);
			}

			Font font = new Font("Arial", Font.PLAIN, 12);
			FontMetrics metrics = new FontMetrics(font) {
				private static final long serialVersionUID = 3950683061494423627L;
			};

			long start = System.currentTimeMillis();
			int steps = g.simulate(metrics);
			long end = System.currentTimeMillis();

			g.countLabels();
			PrintStream image = new PrintStream(new File(file + "." + g.forceMode + ".rend"));
			PrintStream data = new PrintStream(new File(file + "." + g.forceMode + ".dat"));

			g.printRender(image);

			data.println("\n====RESULTS====");

			List<String> headings = new ArrayList<String>();
			List<String> values = new ArrayList<String>();

			headings.add("Data Set");
			values.add(folder);

			headings.add("File");
			values.add(file);

			int numNodes = g.numNodes;
			headings.add("#Nodes");
			values.add("" + numNodes);

			int numLabels = g.numLabels;
			headings.add("#Edges");
			values.add("" + numLabels);

			int nodeHeight = g.nodes.size() > 0 ? g.nodes.get(0).height() : 0;
			headings.add("Node Height");
			values.add("" + nodeHeight);

			int nodeWidth = g.nodes.size() > 0 ? g.nodes.get(0).width() : 0;
			headings.add("Node Width");
			values.add("" + nodeWidth);

			int overlaps[] = g.countOverlaps();
			int nodeNode = overlaps[0];
			int labelLabel = overlaps[1];
			int nodeLabel = overlaps[2];
			int edgeCrossings = overlaps[3];
			headings.add("Node Node Overlaps");
			values.add("" + nodeNode);
			headings.add(" Node Label Overlaps");
			values.add("" + nodeLabel);
			headings.add("Label Label Overlaps");
			values.add("" + labelLabel);
			headings.add("Edge Crossings");
			values.add("" + edgeCrossings);

			headings.add("Iterations");
			values.add("" + steps);

			headings.add("Max Iterations");
			values.add("" + maxIter);

			headings.add("Base Force");
			values.add(((forceMode & Graph.HOOKES_LAW) == 0) ? "Log" : "Hookes");

			headings.add("Forces");
			values.add(toAnalysisString(forceMode));

			double finalEnergy = g.getTotalEnergy();
			headings.add("Final Energy");
			values.add(String.format("%.3f", finalEnergy));

			headings.add("Cut of Energy");
			values.add(String.format("%d", cutOfEnergy));

			int totalAreaNeeded = g.getTotalDrawingArea();
			headings.add("Total Area Needed");
			values.add("" + totalAreaNeeded);

			int areaDrawn = g.calculateAreaDrawn();
			headings.add("Area Drawn");
			values.add("" + areaDrawn);

			int canvasWidth = g.graphWidth;
			headings.add("Canvas Width");
			values.add("" + canvasWidth);

			int canvasHeight = g.graphHeight;
			headings.add("Canvas Height");
			values.add("" + canvasHeight);

			long time = end - start;
			headings.add("time");
			values.add("" + time);

			headings.add("Total Edge Lengths");
			values.add(String.format("%.3f", g.getTotalEdgeLength()));

			headings.add("Mean Edge Length");
			values.add(String.format("%.3f", g.getMeanEdgeLength()));

			headings.add("Variance of Edge Length");
			values.add(String.format("%.3f", g.getVarianceEdgeLength()));

			headings.add("Layout Width");
			values.add(String.format("%.3f", g.layoutWidth()));

			headings.add("Layout Height");
			values.add(String.format("%.3f", g.layoutHeight()));
			
			headings.add("Average Deviation From Ideal Arc Seperation");
			values.add(String.format("%.3f", g.angleDeviation()));

			for (int i = 0; i < headings.size(); i++) {
				data.print(headings.get(i));
				data.print((i == headings.size() - 1) ? "\n" : ",");
			}
			for (int i = 0; i < values.size(); i++) {
				data.print(values.get(i));
				data.print((i == values.size() - 1) ? "\n" : ",");
			}

		}

	}

}
