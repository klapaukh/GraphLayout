package selection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import representation.Node;
import representation.SpriteLibrary;

public class GUI extends JComponent implements MouseInputListener {

	private static final long serialVersionUID = 2173693118914351514L;
	private static final int INITIAL_CAPACITY = 100;
	private JFrame frame;
	private List<Node> nodes;
	private double[][] points;
	private boolean selecting, deselecting;
	private Node selected;
	public List<Integer> start, end;
	public List<String> label;
	private SpriteLibrary sprites;
	private int[] pointsDrawX, pointsDrawY;
	private int size;
	private List<Node> selectedThisRound;

	public GUI() throws IOException {
		nodes = new ArrayList<Node>();
		selectedThisRound = new ArrayList<Node>();
		points = new double[INITIAL_CAPACITY][2];
		pointsDrawY = new int[INITIAL_CAPACITY];
		pointsDrawX = new int[INITIAL_CAPACITY];
		start = new ArrayList<Integer>();
		end = new ArrayList<Integer>();
		label = new ArrayList<String>();
		this.sprites = new SpriteLibrary();
		size = 0;

		selecting = false;
		deselecting = false;
		selected = null;
		frame = new JFrame("Graph Renderer");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(screenSize);
		frame.setUndecorated(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(this, BorderLayout.CENTER);

		loadGraph("examplef.txt");

		frame.setVisible(true);
		repaint();
	}

	public void loadGraph(String filename) {
		// generate some nodes
		nodes.clear();
		start.clear();
		end.clear();
		label.clear();
		size = 0;
		selected = null;
		selecting = false;

		try {
			Scanner scan = new Scanner(new File(filename));

			while (scan.hasNextLine()) {
				String node = scan.nextLine().trim();
				if (!node.isEmpty()) {
					String[] props = node.split(",");
					String label = props[0];
					String type = props[1];
					int x = Integer.parseInt(props[2]);
					int y = Integer.parseInt(props[3]);
					int width = Integer.parseInt(props[4]);
					int height = Integer.parseInt(props[5]);
					nodes.add(new Node(x, y, width, height, label, type, sprites, 1, 1));

					for (int i = 6; i < props.length; i += 2) {
						int end = Integer.parseInt(props[i]);
						if (nodes.size() - 1 <= end) {
							start.add(nodes.size() - 1);
							this.end.add(end);
							this.label.add(props[i + 1]);
						}
					}

				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Generating Random Graph");
			int numNodes = (int) (Math.random() * 30 + 10);
			for (int i = 0; i < numNodes; i++) {
				nodes.add(new Node((int) (Math.random() * 800), (int) (Math.random() * 800), (int) (Math.random() * 40 + 10),
						(int) (Math.random() * 40 + 10), randString(5), randType(), sprites, 1, 1));
			}

			int numEdges = (int) (Math.random() * numNodes / 2 + numNodes / 2);
			for (int i = 0; i < numEdges; i++) {
				start.add((int) (Math.random() * nodes.size()));
				end.add((int) (Math.random() * nodes.size()));
				if (Math.random() > 0.5) {
					label.add("");
				} else {
					label.add(randString(5));
				}

			}
		}
	}

	public String randType() {
		Set<String> names = sprites.getNames();
		int get = (int) (Math.random() * names.size());
		for (String s : names) {
			if (get-- == 0) {
				return s;
			}
		}
		return names.iterator().next();
	}

	public static String randString(int x) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < x; i++) {
			s.append((char) ((char) (Math.random() * 26) + 'a'));
		}
		return s.toString();
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		for (int i = 0; i < start.size(); i++) {
			Node s = nodes.get(start.get(i));
			Node e = nodes.get(end.get(i));
			String l = label.get(i);
			s.drawArc((Graphics2D) g, e, l, null);
		}

		for (Node n : nodes) {
			n.draw(g);
		}
		if ((selecting || deselecting) && size >= 3) {
			if (selecting) {
				g.setColor(new Color(255, 0, 255, 50));
			} else {
				g.setColor(new Color(255, 255, 0, 50));
			}
			g.fillPolygon(pointsDrawX, pointsDrawY, size);
			if (selecting) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.blue);
			}
			g.drawPolygon(pointsDrawX, pointsDrawY, size);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (selecting || deselecting) {
			if (e.getButton() == MouseEvent.BUTTON2) {
				selectedThisRound.get(selectedThisRound.size()-1).toggleSelected();
			} else {
				if (size > 0)
					size--;
				for (Node n : nodes) {
					boolean thisRound = selectedThisRound.contains(n);
					if (!thisRound && deselecting == n.selected() && n.inside(points, size)) {
						if (!selectedThisRound.contains(n)) {
							n.setSelected(selecting);
							selectedThisRound.add(n);
						}
					} else if (thisRound && !n.inside(points, size)) {
						n.setSelected(deselecting);
						selectedThisRound.remove(n);
					}
				}
			}
			this.repaint();
			return;
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			selecting = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			
			deselecting = true;
		} else {
			return;
		}
		ensureCapacity();
		points[size][0] = e.getX();
		points[size][1] = e.getY();
		pointsDrawX[size] = e.getX();
		pointsDrawY[size] = e.getY();
		size++;

		this.repaint();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if ((selecting && e.getButton() == MouseEvent.BUTTON1) || (deselecting && e.getButton() == MouseEvent.BUTTON3)) {
			for (Node n : nodes) {
				boolean thisRound = selectedThisRound.contains(n);
				if (!thisRound && deselecting == n.selected() && n.inside(points, size)) {
					if (!selectedThisRound.contains(n)) {
						n.setSelected(selecting);
					}
				} else if (thisRound && !n.inside(points, size)) {
					n.setSelected(deselecting);
					selectedThisRound.remove(n);
				}
			}

			size = 0;
			selecting = false;
			deselecting = false;
			selected = null;
			selectedThisRound.clear();
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selecting || deselecting) {
			ensureCapacity();
			points[size][0] = e.getX();
			points[size][1] = e.getY();
			pointsDrawX[size] = e.getX();
			pointsDrawY[size] = e.getY();
			size++;

			for (Node n : nodes) {
				boolean thisRound = selectedThisRound.contains(n);
				if (!thisRound && deselecting == n.selected() && n.inside(points, size)) {
					if (!selectedThisRound.contains(n)) {
						n.setSelected(selecting);
						selectedThisRound.add(n);
					}
				} else if (thisRound && !n.inside(points, size)) {
					n.setSelected(deselecting);
					selectedThisRound.remove(n);
				}
			}
		} else if (selected != null) {
			selected.setPosition(e.getX(), e.getY());
		}
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	public void ensureCapacity() {
		if (size >= pointsDrawX.length) {
			int[] tx = pointsDrawX;
			int[] ty = pointsDrawY;
			double[][] tp = points;
			pointsDrawX = new int[pointsDrawX.length * 2];
			pointsDrawY = new int[pointsDrawY.length * 2];
			points = new double[points.length * 2][2];
			for (int i = 0; i < size; i++) {
				pointsDrawX[i] = tx[i];
				pointsDrawY[i] = ty[i];
				points[i][0] = tp[i][0];
				points[i][1] = tp[i][1];
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new GUI();

	}

}
