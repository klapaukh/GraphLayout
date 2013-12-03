/*
 * Force Direct Graph Layout Tool
 *
 * Copyright (C) 2013  Roman Klapaukh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package selection;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.IOException;

import nz.ac.vuw.ecs.moveme.UpdateListener;

public class QuestionPane extends MoveComponent implements MouseListener {

	private static final long		serialVersionUID	= 17834150123L;
	private static int				textHeight			= 60;
	private static int				partHeight			= 1080;
	private int						graphHeight			= 10;
	private int						graphWidth			= 960, textWidth;
	private final String[]			questions;
	private final int[]				answers;
	private final GUI				g1, g2;
	private final Changer			changer;
	private final BufferedWriter	out;
	private int						mouseX, mouseY;
	private Font					font;

	public QuestionPane(Changer changer, GUI g1, GUI g2, BufferedWriter out) {
		this.questions = new String[] { "Which were you more accurate on?", "Which did you perform faster on?", "Which did you prefer?" };
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
		this.font = new Font("serif", Font.PLAIN, 40);
	}

	public void paint(Graphics g) {
		graphWidth = getWidth() / 2;
		textWidth = getWidth() / 3;
		partHeight = getHeight();
		graphHeight = partHeight / 2;
		textHeight = graphHeight / questions.length;

		// Draw a background
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw the graphs
		g1.drawAt(g, 0, 0, graphWidth, graphHeight);
		g2.drawAt(g, graphWidth, 0, graphWidth, graphHeight);

		g.setColor(Color.black);
		g.setFont(font);
		// Draw the questions
		for (int i = 0; i < questions.length; i++) {
			FontMetrics m = g.getFontMetrics();
			Rectangle2D rect = m.getStringBounds(questions[i], g);

			g.drawString(questions[i], (int) (getWidth() / 2 - rect.getWidth() / 2),
					(int) (graphHeight + i * textHeight + textHeight / 2 - rect.getHeight() / 2));
		}

		Color shade = Color.green.brighter();
		for (int i = 0; i < questions.length; i++) {

			if (answers[i] != -1) {
				g.setColor(shade);
				if (answers[i] == 1 || answers[i] == 3) {
					g.fillRect(0, i * textHeight + graphHeight, textWidth, textHeight);
				}
				if (answers[i] == 2 || answers[i] == 3) {
					g.fillRect(2 * textWidth, i * textHeight + graphHeight, textWidth, textHeight);
				}
			}
		}

		g.setColor(Color.gray);
		// Draw Frames
		g.drawLine(graphWidth, 0, graphWidth, graphHeight);
		g.drawLine(textWidth, graphHeight, textWidth, getHeight());
		g.drawLine(2 * textWidth, graphHeight, 2 * textWidth, getHeight());
		for (int i = 0; i < questions.length; i++) {
			int y = graphHeight + i * textHeight;
			g.drawRect(0, y, getWidth(), y);
		}

		g.setColor(Color.GREEN);
		g.fillOval(mouseX - 10, mouseY - 10, 20, 20);
		g.setColor(Color.BLACK);
		g.drawOval(mouseX - 10, mouseY - 10, 20, 20);

		if(moveLost){
			g.drawImage(moveNotThere, 10, 10, null);
		}
	}

	private void select(int x, int y) {
		if (y < graphHeight) {
			// Missed the questions
			return;
		}
		int graph;
		if (x < textWidth) {
			// Graph 1 selected
			graph = 1;
		} else if (x > 2 * textWidth) {
			// Graph 2 selected
			graph = 2;
		} else {
			// Not selecting anything
			return;
		}

		for (int i = 0; i < questions.length; i++) {
			if (y <= (i + 1) * textHeight + graphHeight) {
				// this is the question that has been answered
				if (answers[i] == -1) {
					answers[i] = graph;
				} else if (answers[i] == graph) {
					answers[i] = -1;
				} else if (answers[i] == 3) {
					answers[i] -= graph;
				} else {
					answers[i] = 3;
				}
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
					out.write("Questions");
					out.write(",");
					out.write(g1.filename);
					out.write(",");
					out.write(g2.filename);
					for (int i = 0; i < answers.length; i++) {
						out.write(',');
						out.write(""+answers[i]);
					}
					out.write('\n');
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.changer.next();
		}
	}

	@Override
	public void positionUpdate(int buttonsPushed, int buttonsHeld, int buttonsReleased, int trigger) {
		// Can't do anything with only this information
	}

	private int	lastTrigger	= 0;

	@Override
	public void positionUpdate(float x, float y, int buttonsPushed, int buttonsHeld, int buttonsReleased, int trigger) {
		int normX = (int) (getWidth() * (x + 0.5));
		int normY = (int) -(getHeight() * (y - 0.5));
		mouseX = normX;
		mouseY = normY;
		if (trigger > 100 && lastTrigger < 100) {
			this.select(mouseX, mouseY);
		}
		if ((buttonsReleased & UpdateListener.ButtonCross) != 0) {
			checkFinished();
		}
		lastTrigger = trigger;
		this.repaint();
	}

	@Override
	public void noController() {
		// Do nothing - at this point you are stuck with just using the mouse
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
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
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			this.select(arg0.getX(), arg0.getY());
		}
	}

}
