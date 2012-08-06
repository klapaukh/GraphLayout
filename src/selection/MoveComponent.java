package selection;

import javax.swing.JComponent;

import nz.ac.vuw.ecs.moveme.MoveLostListener;
import nz.ac.vuw.ecs.moveme.UpdateListener;

public abstract class MoveComponent extends JComponent implements UpdateListener, MoveLostListener{
	
	
	protected volatile boolean moveLost;
	protected String filename;
	private static final long serialVersionUID = 3728612264508579669L;
	
	public MoveComponent(){
		moveLost = false;
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
