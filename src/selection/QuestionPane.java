package selection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.IOException;

public class QuestionPane extends MoveComponent implements MouseListener {

	private static final long serialVersionUID = 17834150123L;
	private static final int textHeight = 60;
	private static final int partHeight = 1080;
	private int graphHeight = 10;
	private int partWidth = 640;
	private final String[] questions;
	private final int[] answers;
	private final GUI g1, g2;
	private final Changer changer;
	private final BufferedWriter out;
	private int mouseX, mouseY;

	public QuestionPane(Changer changer, GUI g1, GUI g2, BufferedWriter out) {
		this.questions = new String[] { "Which were more accurate on?",
				"Which did you perform faster on?", "Which did you prefer?" };
		this.answers = new int[questions.length];
		for (int i = 0; i < answers.length; i++) {
			answers[i] = -1;
		}
		this.changer = changer;
		this.g1 = g1;
		this.g2 = g2;
		this.mouseX = 0;
		this.mouseY = 0;
		this.addMouseListener(this);
		this.out = out;
		this.filename = "QSPANE";
	}

	public void paint(Graphics g) {
		partWidth = 1920 / questions.length;

		graphHeight = (partHeight - textHeight) / 2;
		// TODO: Make the graph the right aspect ratio?
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), textHeight);
		g.setColor(Color.black);

		// Draw the questions
		for (int i = 0; i < questions.length; i++) {
			g.drawString(questions[i], i * partWidth, 40);
		}

		// Draw the graphs
		Color shade = new Color(1.0f, 1.0f, 0f, 0.5f);
		for (int i = 0; i < questions.length; i++) {
			g1.drawAt(g, i * partWidth, textHeight, partWidth, graphHeight);
			g2.drawAt(g, i * partWidth, textHeight + graphHeight, partWidth,
					graphHeight);
			if (answers[i] != -1) {
				g.setColor(shade);
				g.fillRect(i * partWidth, textHeight
						+ (answers[i] == 1 ? 0 : graphHeight), partWidth,
						graphHeight);
			}
		}
		g.setColor(Color.black);

		// Draw Frames
		g.drawLine(0, textHeight, 1920, textHeight);
		g.drawLine(0, textHeight + graphHeight, 1920, textHeight + graphHeight);
		for (int i = 0; i < questions.length; i++) {
			g.drawRect(i * partWidth, 0, partWidth, partHeight);
		}

		g.setColor(Color.GREEN);
		g.fillOval(mouseX - 10, mouseY - 10, 20, 20);
	}

	private void select(int x, int y) {
		if (y < textHeight) {
			return;
		}
		int graph;
		if (y > textHeight && y < textHeight + graphHeight) {
			// Graph 1 selected
			graph = 1;
		} else {
			// Graph 2 selected
			graph = 2;
		}

		for (int i = 0; i < questions.length; i++) {
			if (x <= (i + 1) * partWidth) {
				// this is the question that has been answered
				answers[i] = graph;
				checkFinished();
				this.repaint();
				return;
			}
		}
	}

	private void checkFinished() {
		boolean finished = true;
		for (int i = 0; i < answers.length; i++) {
			finished = finished && answers[i] != -1;
		}
		if (finished) {
			try {
				synchronized (out) {
					out.write("Questions,");
					for (int i = 0; i < answers.length; i++) {
						out.write(answers[i]);
					}
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.changer.next();
		}
	}

	@Override
	public void positionUpdate(int buttonsPushed, int buttonsHeld,
			int buttonsReleased, int trigger) {
		// Can't do anything with only this information
	}

	@Override
	public void positionUpdate(float x, float y, int buttonsPushed,
			int buttonsHeld, int buttonsReleased, int trigger) {
		int normX = (int) (getWidth() * (x + 0.5));
		int normY = (int) -(getHeight() * (y - 0.5));
		mouseX = normX;
		mouseY = normY;
		if (trigger > 100) {
			this.select(mouseX, mouseY);
		}
		this.repaint();
	}

	@Override
	public void noController() {
		// Do nothing - at this point you are stuck with just using the mouse
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			this.select(arg0.getX(), arg0.getY());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}
