package selection;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.JFrame;

import nz.ac.vuw.ecs.moveme.PSMoveClient;

public class Changer {
	private final JFrame frame;
	private final PSMoveClient client;
	private final BufferedWriter out;

	public Changer(JFrame frame, PSMoveClient client, BufferedWriter out) {
		this.frame = frame;
		this.client = client;
		this.out = out;
	}

	public void next() {
		if (GUI.count < GUI.guis.size() - 1) {
			frame.getContentPane().removeAll();
			GUI.count++;
			client.registerListener(GUI.guis.get(GUI.count));
			client.setMoveLostListener(GUI.guis.get(GUI.count));
			frame.getContentPane().add(GUI.guis.get(GUI.count),
					BorderLayout.CENTER);

			synchronized (out) {
				try {
					out.write("Changer,");
					out.write(""+ System.currentTimeMillis());
					out.write(',');
					out.write(GUI.guis.get(GUI.count -1).filename);
					out.write(',');
					out.write(GUI.guis.get(GUI.count).filename);
					out.write('\n');
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

			frame.validate();
			frame.repaint();
		}
	}
}
