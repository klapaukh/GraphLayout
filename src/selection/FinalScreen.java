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
		// TODO Auto-generated method stub
	}

	@Override
	public void noController() {
		// TODO Auto-generated method stub

	}

}
