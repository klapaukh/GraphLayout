package selection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import nz.ac.vuw.ecs.moveme.PSMoveClient;
import nz.ac.vuw.ecs.moveme.UpdateListener;

public class QuestionPane extends JComponent implements UpdateListener,
		MouseListener {

	private static final long serialVersionUID = 17834150123L;
	private static final int textHeight = 60;
	private static final int partHeight = 1080;
	private int partWidth = 640;
	private final String[] questions;
	private final int[] answers;
	private final GUI g1, g2;
	private final Changer changer;

	public QuestionPane(Changer changer,GUI g1, GUI g2) {
		this.questions = new String[] { "Which graph layout was better?",
				"Which did you perform faster on?" };
		this.answers = new int[questions.length];
		for(int i = 0 ; i < answers.length;i++){
			answers[i]= -1;
		}
		this.changer = changer;
		this.g1 = g1;
		this.g2 = g2;
	}

	public void paint(Graphics g) {
		partWidth = this.getWidth() / questions.length;

		int graphHeight = partHeight - textHeight;
		// TODO: Make the graph the right aspect ratio?

		g.setColor(Color.black);

		// Draw the questions
		for (int i = 0; i < questions.length; i++) {
			g.drawString(questions[i], i * partWidth, 40);
		}

		// Draw the graphs
		for (int i = 0; i < questions.length; i++) {
			g1.drawAt(g, i * partWidth, textHeight, partWidth, graphHeight);
			g2.drawAt(g, i * partWidth, textHeight + graphHeight, partWidth,
					graphHeight);
		}

		// Draw Frames
		g.drawLine(0, textHeight, getWidth(), textHeight);
		g.drawLine(0, textHeight + graphHeight, getWidth(), textHeight
				+ graphHeight);
		for (int i = 0; i < questions.length; i++) {
			g.drawRect(i * partWidth, 0, partWidth, partHeight);
		}
	}

	private void select(int x, int y) {
		if (y < textHeight) {
			return;
		}
		int graph;
		if (y > textHeight && y < textHeight + partHeight) {
			// Graph 1 selected
			graph = 1;
		} else {
			// Graph 2 selected
			graph = 2;
		}

		for (int i = 0; i < questions.length; i++) {
			if (x <= (i+1)*partWidth) {
				//this is the question that has been answered
				answers[i] = graph;
				checkFinished();
				return;
			}
		}
	}
	
	private void checkFinished(){
		boolean finished = true;
		for(int i = 0 ; i < answers.length;i++){
			finished = finished && answers[i] != -1;
		}
		if(finished){
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
		if (trigger > 100) {
			this.select((int) x, (int) y);
		}
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
