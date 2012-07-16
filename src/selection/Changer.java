package selection;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import nz.ac.vuw.ecs.moveme.PSMoveClient;

public class Changer {
	private final JFrame frame;
	private final PSMoveClient client;

	public Changer(JFrame frame, PSMoveClient client) {
		this.frame = frame;
		this.client = client;
	}

	public void next() {
		if (GUI.count < GUI.guis.size() - 1) {
			frame.getContentPane().removeAll();
			frame.getContentPane().add(GUI.guis.get(++GUI.count), BorderLayout.CENTER);
			client.registerListener(GUI.guis.get(GUI.count));
			frame.validate();
			frame.repaint();
		}
	}
}
