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
					e.printStackTrace();
				}
			}

			frame.validate();
			frame.repaint();
		}
	}
}
