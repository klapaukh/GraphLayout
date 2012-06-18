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
