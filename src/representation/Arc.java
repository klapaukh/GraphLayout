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

package representation;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Arc {
	public final Node left;
	public final Node right;
	public final Node label;
	public final Node edgeCenter;
	public final String name;

	public Arc(Node left, Node right, String name, double labelCharge,  double mass, SpriteLibrary l){
		this.left = left;
		this.right = right;
		this.name = name;
		label = new Node((left.x() + right.x())/2, (left.y() + right.y())/2,10,10,"label","label",l, labelCharge, mass );

		edgeCenter = new Node((left.x() + right.x())/2, (left.y() + right.y())/2,1,1,"label","label",l, labelCharge, mass );
	}

	public void drawArc(Graphics2D g){
		left.drawArc(g, right, name, label);

		edgeCenter.setPosition((left.x() + right.x())/2, (left.y() + right.y())/2);
		edgeCenter.finaliseMove();
	}

	public void updateArc(FontMetrics m){
		left.updateArc(m, right, name, label);

		edgeCenter.setPosition((left.x() + right.x())/2, (left.y() + right.y())/2);
		edgeCenter.finaliseMove();
	}

	public Node other(Node n){
		if(left.equals(n)){
			return right;
		}else if(right.equals(n)){
			return left;
		}else {
			return null;
		}
	}

	public double length(){
		double dx = (left.x() + left.width()/2) - (right.x() + right.width()/2);
		double dy = (left.y() + left.height()/2) - (right.y() + right.height()/2);
		double dist = Math.sqrt(dy*dy+dx*dx);
		return dist;

	}

	public boolean contains(Node n){
		return this.left.equals(n) || this.right.equals(n);
	}

}
