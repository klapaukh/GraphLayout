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
	}
	
	public void moveRegained(){
		this.moveLost = false;
	}
}
