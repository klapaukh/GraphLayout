package representation;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

public class Node {

	private double[][] points;
	private double[][] nextPoints;
	private boolean selected;
	private int width, height, imWidth;
	private double x, y;
	private Image im;
	public final String label, type;
	private static final int HORIZONTAL_PADDING = 10;
	private double charge, dx, dy;
	private final double mass;
	private boolean mobile;
	private double forcex, forcey;
	private int degree;

	private double nextx, nexty;

	public Node(double x, double y, String label, String type, SpriteLibrary l, double charge, double mass) {
		this.degree = 0;
		this.charge = charge;
		this.mass = mass;
		selected = false;
		mobile = true;
		this.x = x;
		this.y = y;
		this.label = label;
		this.type = type;
		Sprite s = l.getSprite(type);
		if (s != null) {
			im = s.getSprite();
			this.width = im.getWidth(null);
			this.height = im.getHeight(null);
			this.imWidth = width;
			if (this.height < 0 || this.width < 0) {
				throw new RuntimeException("Node size not loading");
			}
		} else {
			throw new IllegalArgumentException("Cannot initialize default node size without an image");
		}

		points = new double[][] { { x, y }, { x + width, y }, { x + width, y + height }, { x, y + height } };
		nextPoints = new double[][] { { x, y }, { x + width, y }, { x + width, y + height }, { x, y + height } };

	}

	public Node(double x, double y, int width, int height, String label, String type, SpriteLibrary l, double charge, double mass) {
		this.charge = charge;
		this.mass = mass;
		selected = false;
		mobile = true;
		this.x = x;
		this.y = y;
		this.width = width;
		this.imWidth = width;
		this.height = height;
		this.label = label;
		this.type = type;
		Sprite s = l.getSprite(type);
		if (s != null) {
			im = s.getSprite();
		}

		points = new double[][] { { x, y }, { x + width, y }, { x + width, y + height }, { x, y + height } };
		nextPoints = new double[][] { { x, y }, { x + width, y }, { x + width, y + height }, { x, y + height } };
	}

	public int degree() {
		return degree;
	}

	public void addEdge() {
		degree++;
	}

	public void removeEdge() {
		degree--;
	}

	public double x() {
		return x;
	}

	public double nextx() {
		return nextx;
	}

	public double y() {
		return y;
	}

	public double nexty() {
		return nexty;
	}

	public int imWidth() {
		return imWidth;
	}

	public void resetForces() {
		forcex = forcey = 0;
	}

	public void addForce(double x, double y) {
		forcex += x;
		forcey += y;
	}

	public void addForce(Vector v) {
		forcex += v.x;
		forcey += v.y;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public double charge() {
		return charge;
	}

	public void toggleSelected() {
		this.selected = !this.selected;
	}

	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public boolean selected(){
		return selected;
	}
	
	public void toggleMovement() {
		mobile = !mobile;
	}

	public double getCharge() {
		return charge;
	}

	public boolean in(int x, int y) {
		return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
	}

	public boolean inside(double[][] polygon, int size) {
		return Intersection.intersect(this.points, this.points.length, polygon, size);
	}

	public boolean overlapps(Node o) {
		return !((this.x + this.width < o.x) || // I am to the left of him
				(this.x > o.x + o.width) || // I am to the right
				(this.y + this.height < o.y) || // I am above
		(this.y > o.y + o.height)); // I am below

	}

	public int overlapArea(Node o) {
		int x1 = (int) Math.max(this.x, o.x);
		int x2 = (int) Math.min(this.x + this.width, o.x + o.width);
		int y1 = (int) Math.max(this.y, o.y);
		int y2 = (int) Math.min(this.y + this.height, o.y + o.height);

		int width = Math.abs(x1 - x2);
		int height = Math.abs(y1 - y2);

		return width * height;
	}

	public boolean equals(Object o) {
		if (o instanceof Node) {
			return this.label.equals(((Node) o).label);
		}
		return false;
	}

	public void collided(Node n, double coefficientOfRestitution) {
		Vector v1 = VectorFactory.newVector(dx, dy);
		// System.out.print(name + " " + v1 + " to ");
		Vector v2 = VectorFactory.newVector(n.dx, n.dy);
		Vector a = VectorFactory.newVector(n.x + n.width / 2 - x - width / 2, n.y + n.height / 2 - y - height / 2);
		a.multiply(-1);
		Vector projv1 = VectorFactory.newVector(a).multiply(v1.dot(a) / Math.pow(a.length(), 2));
		Vector orthv1 = v1.minus(projv1);
		Vector projv2 = VectorFactory.newVector(a).multiply(v2.dot(a) / Math.pow(a.length(), 2));

		Vector collision = projv1.multiply(this.mass - n.mass * coefficientOfRestitution)
				.add(projv2.multiply(n.mass * (1 + coefficientOfRestitution))).multiply(1.0 / (this.mass + n.mass));
		Vector speed = collision.add(orthv1);
		dx = speed.x;
		dy = speed.y;

		VectorFactory.delete(v1);
		VectorFactory.delete(v2);
		VectorFactory.delete(a);
		VectorFactory.delete(projv1);
		VectorFactory.delete(projv2);
		// System.out.println(speed);
	}

	public void setPosition(double newx, double newy) {
		this.nextx = newx;
		this.nexty = newy;

		nextPoints[0][0] = nextx;
		nextPoints[0][1] = nexty;

		nextPoints[1][0] = nextx + width;
		nextPoints[1][1] = nexty;

		nextPoints[2][0] = nextx + width;
		nextPoints[2][1] = nexty + height;

		nextPoints[3][0] = nextx;
		nextPoints[3][1] = nexty + height;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

	public void setVelocity(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * Return the kinectic energy of the current Node
	 * 
	 * @return
	 */
	public double kineticEnergy() {
		if (!mobile)
			return 0;
		double velocity = Math.hypot(dx, dy); // Velocity Vector
		return 0.5 * mass * velocity * velocity;
	}

	public void setMobile(boolean b) {
		this.mobile = b;
	}

	public void updateVelocity(double dampening, double timestep) {
		// f = ma => a = f / m

		// System.out.print(dx + "," + dy+ " -> ");
		dy *= dampening * timestep;
		dx *= dampening * timestep;

		dx += forcex / mass;
		dy += forcey / mass;
		// System.out.println(dx + "," + dy);

		double jumpAway = 5 * Math.max(this.width, this.height);
		if (Double.isNaN(dx) || Double.isInfinite(dx)) {
			dx = Math.random() * jumpAway - jumpAway / 2;
			System.out.println("X Force is NAN");
		}

		if (Double.isNaN(dy) || Double.isInfinite(dy)) {
			dy = Math.random() * jumpAway - jumpAway / 2;
			System.out.println("Y Force is NAN");
		}

	}

	/**
	 * Preliminary Move
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 * @param timeStep
	 */
	public double move(int screenWidth, int screenHeight, double timeStep) {
		if (!mobile)
			return timeStep;

		this.nextx = (x + dx * timeStep);
		this.nexty = (y + dy * timeStep);

		nextPoints[0][0] = nextx;
		nextPoints[0][1] = nexty;

		nextPoints[1][0] = nextx + width;
		nextPoints[1][1] = nexty;

		nextPoints[2][0] = nextx + width;
		nextPoints[2][1] = nexty + height;

		nextPoints[3][0] = nextx;
		nextPoints[3][1] = nexty + height;

		double collTime = timeStep * 2;

		if (nextx < 0) {
			collTime = Math.min(-x / dx - epsilon(timeStep), collTime);
		} else if (nextx > screenWidth - width) {
			collTime = Math.min((screenWidth - x) / dx - epsilon(timeStep), collTime);
		}
		if (nexty < 0) {
			collTime = Math.min(-y / dy - epsilon(timeStep), collTime);
		} else if (nexty > screenHeight - height) {
			collTime = Math.min((screenHeight - y) / dy - epsilon(timeStep), collTime);
		}
		return Math.max(0, collTime);
	}

	public double fullCollided(Node n, double timestep) {
		double time = timestep;
		double nextTime;
		for (int i = 0; i < this.points.length; i++) {
			for (int j = 0; j < n.points.length; j++) {
				nextTime = Intersection.intersectWhileMoving(this.points[i][0], this.points[i][1], this.points[(i + 1) % this.points.length][0],
						this.points[(i + 1) % this.points.length][1], this.nextPoints[i][0], this.nextPoints[i][1], n.points[j][0], n.points[j][1],
						n.nextPoints[j][0], n.nextPoints[j][1], timestep);
				time = Math.min(time, nextTime);
				nextTime = Intersection.intersectWhileMoving(n.points[j][0], n.points[j][1], n.points[(j + 1) % n.points.length][0], n.points[(j + 1)
						% n.points.length][1], n.nextPoints[j][0], n.nextPoints[j][1], this.points[i][0], this.points[i][1], this.nextPoints[i][0],
						this.nextPoints[i][1], timestep);
				time = Math.min(time, nextTime);
			}
		}
		if (Double.compare(timestep, time) != 0) {
			if (Double.compare(time, 0) == 0) {
				System.out.println("CLASH");
			}
			double epsilon = epsilon(timestep);
			if (Double.compare(epsilon, time) < 0) {
				time -= epsilon(timestep);
			}
		}
		return time;
	}

	private double epsilon(double timestep) {
		// e_x = dx* e_t ==> e_t = e_x/dx
		if (Double.compare(Math.max(Math.abs(dx), Math.abs(dy)) * timestep, 1) < 0) {
			return 0;
		}
		double epsilon = 2.0 / Math.max(Math.abs(dx), Math.abs(dy));
		return epsilon;
	}

	public void collideWall(int screenWidth, int screenHeight, boolean fullCollisions) {
		double bounceFactor = -1.0;

		if (fullCollisions) {
			double tx0 = -(double) x / dx;
			double txW = (double) (screenWidth - width - x) / dx;

			double ty0 = -(double) y / dy;
			double tyH = (double) (screenHeight - height - y) / dy;

			double tx = Math.min(tx0, txW);
			double ty = Math.min(ty0, tyH);

			int side = Double.compare(tx, ty);
			if (side < 0) {
				dx *= bounceFactor;
			} else if (side > 0) {
				dy *= bounceFactor;
			} else {
				dy *= bounceFactor;
				dx *= bounceFactor;
			}
		} else {
			int padding = 2;
			if (nextx <= 0) {
				nextx = padding;
				dx *= bounceFactor;
			} else if (nextx >= screenWidth - width) {
				nextx = screenWidth - width - padding;
				dx *= bounceFactor;
			}
			if (nexty <= 0) {
				nexty = padding;
				dy *= bounceFactor;
			} else if (nexty >= screenHeight - height) {
				nexty = screenHeight - height - padding;
				dy *= bounceFactor;
			}
		}

		nextPoints[0][0] = nextx;
		nextPoints[0][1] = nexty;

		nextPoints[1][0] = nextx + width;
		nextPoints[1][1] = nexty;

		nextPoints[2][0] = nextx + width;
		nextPoints[2][1] = nexty + height;

		nextPoints[3][0] = nextx;
		nextPoints[3][1] = nexty + height;
	}

	/**
	 * Update x and y positions
	 */
	public void finaliseMove() {
		this.x = this.nextx;
		this.y = this.nexty;

		double[][] temp = points;
		this.points = nextPoints;
		this.nextPoints = temp;
	}

	public void draw(Graphics g) {
		FontMetrics m = g.getFontMetrics();
		Rectangle2D met = m.getStringBounds(label, g);
		int textheight = (int) Math.ceil(met.getHeight());
		int textWidth = (int) Math.ceil(met.getWidth());
		if (textWidth + HORIZONTAL_PADDING > this.width) {
			this.width = textWidth + HORIZONTAL_PADDING;
		}

		int boxx = (int) x + (width / 2) - (textWidth / 2) - HORIZONTAL_PADDING / 2;
		int boxy = (int) y + height - textheight;
		int boxWidth = textWidth + HORIZONTAL_PADDING;
		int boxHeight = textheight;
		
		
		int imx = (int) x + width / 2 - imWidth / 2;
		int imy = (int) y;
		int imheight = height - boxHeight;
		
		g.drawRect(imx, imy, imWidth, height);
		if (im != null) {
			g.drawImage(im, imx, imy, imWidth, height, null);
			if(selected){
				g.setColor(Color.green);
				int [] xpoints = {imx, imx + imWidth/4, imx+imWidth, imx + imWidth/4};
				int[] ypoints =  {imy + 1* imheight/2, imy+imheight, imy, imy + 3*imheight/4};
				g.fillPolygon(xpoints, ypoints, 4);
				g.setColor(Color.BLACK);
				g.drawPolygon(xpoints, ypoints, 4);
			}
		}


		if (selected) {
			g.setColor(Color.yellow);
		} else if (!mobile) {
			g.setColor(Color.pink);
		} else {
			g.setColor(Color.cyan);
		}

		g.fillRect(boxx, boxy, boxWidth, boxHeight);

		g.setColor(Color.BLACK);

		g.drawRect(boxx, boxy, boxWidth, boxHeight);

		g.drawString(label, (int) x + (width / 2) - (textWidth / 2), (int) y + height - m.getMaxDescent());

		double vecSize = Math.sqrt(forcex * forcex + forcey * forcey);
		int vecx = (int) ((int) (150 * forcex / vecSize) + x + width / 2);
		int vecy = (int) ((int) (150 * forcey / vecSize) + y + height / 2);

		g.drawLine((int) (x + width / 2), (int) (y + height / 2), vecx, vecy);

		// g.fillRect(x, y, width, height);
		// g.setColor(Color.pink);
		// g.drawRect(x, y, width, height);
	}

	public void updateArc(FontMetrics m, Node target, String name, Node l) {
		drawArc(null, m, target, name, l);
	}

	public void drawArc(Graphics2D o, Node n, String name, Node l) {
		drawArc(o, o.getFontMetrics(), n, name, l);
	}

	public void drawArc(Graphics2D o, FontMetrics m, Node n, String name, Node label) {
		int bubble = 20;
		int h = 70;

		if (n == this) { // self arc

			if (o != null) {
				o.setColor(Color.BLUE);
				o.drawArc((int) x, (int) y - h / 2, width, h, 180, -180);
			}
			int mx = (int) (this.x + width / 2), my = (int) (this.y - h / 2 - bubble);
			int lx1 = 0, ly1 = 0, labelWidth = 0, labelHeight = 0;

			if (o != null) {
				o.setColor(Color.BLACK);
			}
			if (name != null && !name.isEmpty()) {
				Rectangle2D r = m.getStringBounds(name, o);
				labelHeight = (int) r.getHeight();
				labelWidth = (int) r.getWidth();
				int sx = (int) (mx - r.getWidth() / 2);
				lx1 = sx;
				ly1 = (int) (my - r.getHeight());

				if (o != null) {
					o.drawString(name, sx, my);
					// o.drawRect(lx1, ly1, labelWidth, labelHeight);
					// o.drawRect((int)this.x,(int)this.y, this.width,this.height);
				}

				label.x = lx1;
				label.y = ly1;
				label.width = labelWidth;
				label.height = labelHeight;
			}

		} else {
			int x1 = (int) x + width / 2;
			int y1 = (int) y + height / 2;
			int x2 = (int) n.x + n.width / 2;
			int y2 = (int) n.y + n.height / 2;

			double theta = Math.atan2((y1 - y2), (x1 - x2));
			double alpha = Math.atan((double) n.width / n.height);
			double beta = Math.atan((double) n.height / n.width);

			int dir;
			if (theta >= 2 * alpha + beta || theta < -(2 * alpha + beta)) {
				// Left
				dir = 0;
			} else if (theta >= beta && theta < 2 * alpha + beta) {
				// Bottom
				dir = 1;
			} else if (theta >= -beta && theta < beta) {
				// RIGHT
				dir = 2;
			} else if (theta >= -(2 * alpha + beta) && theta < -beta) {
				// TOP
				dir = 3;
			} else {
				System.out.println("Error");
				dir = 4;
			}

			if (o != null) {
				o.setColor(Color.BLUE);
			}

			if (o != null) {
				o.setColor(Color.BLACK);
			}
			switch (dir) {
			case 0: // LEFT
			{
				int mx = (int) ((x2 + x1) / 2), my = (int) ((y1 + y2) / 2 - bubble);
				int lx1 = 0, ly1 = 0, labelWidth = 0, labelHeight = 0;
				String s = name;
				if (!s.isEmpty()) {
					Rectangle2D r = m.getStringBounds(s, o);
					labelWidth = (int) r.getWidth();
					labelHeight = (int) r.getHeight();
					lx1 = (int) (mx - r.getWidth() / 2);
					ly1 = my - labelHeight;
					if (o != null) {
						o.drawString(s, lx1, my);
					}
					// if (o != null) {
					// o.drawRect(lx1, ly1, labelWidth, labelHeight);
					// }
					if (label != null) {
						label.x = lx1;
						label.y = ly1;
						label.width = labelWidth;
						label.height = labelHeight;
					}
				}
			}
				break;
			case 1: // BOTTOM
			{
				int mx = (int) ((x2 + x1) / 2 - bubble), my = (int) ((y1 + y2) / 2);
				int lx1 = 0, ly1 = 0, labelWidth = 0, labelHeight = 0;
				if (!name.isEmpty()) {
					Rectangle2D r = m.getStringBounds(name, o);
					labelWidth = (int) r.getWidth();
					labelHeight = (int) r.getHeight();
					lx1 = mx - labelWidth;
					my += labelHeight / 2;
					ly1 = my - labelHeight;
					if (o != null) {
						o.drawString(name, lx1, my);
					}
					// if (o != null) {
					// o.drawRect(lx1, ly1, labelWidth, labelHeight);
					// }
					if (label != null) {
						label.x = lx1;
						label.y = ly1;
						label.width = labelWidth;
						label.height = labelHeight;
					}
				}
			}
				break;
			case 2: // RIGHT
			{
				int mx = (int) ((x2 + x1) / 2), my = (int) ((y1 + y2) / 2 + bubble);
				int lx1 = Integer.MAX_VALUE, ly1 = my, labelWidth = 0, labelHeight = 0;
				if (!name.isEmpty()) {
					Rectangle2D r = m.getStringBounds(name, o);
					labelWidth = (int) r.getWidth();
					labelHeight = (int) r.getHeight();
					my += labelHeight + m.getMaxDescent();
					lx1 = (int) (mx - r.getWidth() / 2);
					ly1 = my - labelHeight;
					if (o != null) {
						o.drawString(name, lx1, my);
					}
					if (label != null) {
						label.x = lx1;
						label.y = ly1;
						label.width = labelWidth;
						label.height = labelHeight;
					}
				}
			}
				break;
			case 3: // TOP
			{
				int lx1 = (int) ((x2 + x1) / 2 + bubble), my = (int) ((y1 + y2) / 2);
				int ly1 = my, labelWidth = 0, labelHeight = 0;
				if (!name.isEmpty()) {
					Rectangle2D r = m.getStringBounds(name, o);
					labelWidth = (int) r.getWidth();
					labelHeight = (int) r.getHeight();
					my += labelHeight / 2;
					ly1 = my - labelHeight;
					if (o != null) {
						o.drawString(name, lx1, my);
					}
					if (label != null) {
						label.x = lx1;
						label.y = ly1;
						label.width = labelWidth;
						label.height = labelHeight;
					}
				}
			}
				break;
			default:
				System.out.println("Case Error");
			}

			if (o != null) {
				o.setColor(Color.BLUE);
				o.drawLine((int) (this.x + this.width / 2.0), (int) (this.y + this.height / 2.0), (int) (n.x + n.width / 2.0),
						(int) (n.y + n.height / 2.0));
			}
		}

	}

}
