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
import java.awt.geom.Rectangle2D;

public class FinalScreen extends MoveComponent {

	private static final long serialVersionUID = 759336901491281162L;
	private Font font;
	private final String message = "Thank you for participating";


	public FinalScreen(){
		this.font = new Font("serif",Font.PLAIN,40);
		this.filename = "Final Screen";
	}

	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0,0,1920,1080);

		g.setColor(Color.black);
		g.setFont(font);

		FontMetrics m = g.getFontMetrics();
		Rectangle2D rect = m.getStringBounds(message, g);


		g.drawString(message, (int) (getWidth()/2 - rect.getWidth()/2), (int) (getHeight()/2 - rect.getHeight()/2));
	}
	@Override
	public void positionUpdate(int buttonsPushed, int buttonsHeld,
			int buttonsReleased, int trigger) {

	}

	@Override
	public void positionUpdate(float x, float y, int buttonsPushed,
			int buttonsHeld, int buttonsReleased, int trigger) {
	}

	@Override
	public void noController() {

	}

}
