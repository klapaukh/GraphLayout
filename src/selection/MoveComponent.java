package selection;

import javax.swing.JComponent;

import nz.ac.vuw.ecs.moveme.UpdateListener;

public abstract class MoveComponent extends JComponent implements UpdateListener{

	protected String filename;
	private static final long serialVersionUID = 3728612264508579669L;
	public String toSVG(){
		throw new RuntimeException("BUG!!");
	}
}
