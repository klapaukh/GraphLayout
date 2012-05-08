package selection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	private List<double[]> points;
	private boolean selecting;
	private Node selected;
	public List<Integer> start, end;
	public List<String> label;
	private SpriteLibrary sprites;
	private int[] pointsDrawX,pointsDrawY;
	private int size;
	

	public GUI() throws IOException {
		nodes = new ArrayList<Node>();
		points = new ArrayList<double[]>();
		pointsDrawY = new int[INITIAL_CAPACITY];
		pointsDrawX = new int[INITIAL_CAPACITY];
		start = new ArrayList<Integer>();
		end = new ArrayList<Integer>();
		label = new ArrayList<String>();
		this.sprites = new SpriteLibrary();
		size = 0;

		selecting = false;
		selected = null;
		frame = new JFrame("Graph Renderer");
		frame.setSize(800, 800);
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
		points.clear();
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
					nodes.add(new Node(x, y, width, height, label, type, sprites,1,1));

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
						(int) (Math.random() * 40 + 10), randString(5), randType(), sprites,1,1));
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

	Node temp = null;
	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		for (int i = 0; i < start.size(); i++) {
			Node s = nodes.get(start.get(i));
			Node e = nodes.get(end.get(i));
			String l = label.get(i);
			s.drawArc((Graphics2D) g, e, l, temp);
		}

		for (Node n : nodes) {
			n.draw(g);
		}
		if (selecting) {
				g.setColor(new Color(255,0,255,50));
				g.fillPolygon(pointsDrawX,pointsDrawY, points.size());
				g.setColor(Color.red);
				g.drawPolygon(pointsDrawX,pointsDrawY, points.size());
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		boolean on = false;
		for (Node n : nodes) {
			if (n.in(e.getX(), e.getY())) {
				on = true;
				selected = n;
				break;
			}

		}
		if (!on) {
			selecting = true;
			points.add(new double[] { e.getX(), e.getY() });
			ensureCapacity();
			pointsDrawX[size] = e.getX();
			pointsDrawY[size] = e.getY();
			size++;
		} else {

		}

		this.repaint();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (selecting) {
			for (Node n : nodes) {
				if(n.inside(points.toArray(new double[points.size()][2]))){
					n.toggleSelected();
				}
			}

			points.clear();
			size = 0;
		}
		selecting = false;
		selected = null;
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selecting) {
			points.add(new double[] { e.getX(), e.getY() });
			ensureCapacity();
			pointsDrawX[size]=e.getX();
			pointsDrawY[size]=e.getY();
			size++;
		} else if (selected != null) {
			selected.setPosition(e.getX(), e.getY());
		}
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
	
	public void ensureCapacity(){
		if(size >= pointsDrawX.length){
			int[] tx = pointsDrawX;
			int[] ty = pointsDrawY;
			pointsDrawX = new int[pointsDrawX.length*2];
			pointsDrawY = new int[pointsDrawY.length*2];
			for(int i = 0 ; i < size;i++){
				pointsDrawX[i] = tx[i];
				pointsDrawY[i] = ty[i];
			}
		}
	}
	public static void main(String args[]) throws IOException {
		new GUI();

	}

}
