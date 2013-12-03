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

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import nz.ac.vuw.ecs.moveme.MoveLostListener;
import nz.ac.vuw.ecs.moveme.UpdateListener;

public abstract class MoveComponent extends JComponent implements UpdateListener, MoveLostListener{


	protected volatile boolean moveLost;
	protected String filename;
	private static final long serialVersionUID = 3728612264508579669L;
	protected Image moveNotThere;

	public MoveComponent(){
		moveLost = false;
		try {
			moveNotThere = ImageIO.read(new File("images/moveNotThere.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toSVG(){
		throw new RuntimeException("BUG!!");
	}

	public void moveLost(){
		this.moveLost = true;
		this.repaint();
	}

	public void moveRegained(){
		this.moveLost = false;
		this.repaint();
	}
}
