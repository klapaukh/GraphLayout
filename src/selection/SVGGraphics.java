package selection;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import javax.swing.JFrame;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class SVGGraphics extends Graphics2D {
	private double alpha = 1;
	public JFrame frame;
	private String color = "rgb(0,0,0)";
	private StringBuilder s = new StringBuilder();

	public SVGGraphics(int width, int height, JFrame frame) {
		s.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n");
		s.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"\n");
		s.append("\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n");
		s.append("<svg xmlns=\"http://www.w3.org/2000/svg\"\n");
		s.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\"\n");
		s.append("width=\"");
		s.append(1920);
		s.append("\"px\" height=\"");
		s.append(1080);
		s.append("\"px\"\n");
		s.append("viewBox=\"0 0 ");
		s.append(width);
		s.append(' ');
		s.append(height);
		s.append("\"\n");
		s.append("zoomAndPan=\"disable\" >\n");
		this.frame=frame;
	}

	@Override
	public void draw(Shape s) {
		throw new NotImplementedException();
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		throw new NotImplementedException();
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		throw new NotImplementedException();
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		throw new NotImplementedException();
	}

	@Override
	public void drawString(String str, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, float x, float y) {
		throw new NotImplementedException();
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		throw new NotImplementedException();
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		throw new NotImplementedException();
	}

	@Override
	public void fill(Shape s) {
		throw new NotImplementedException();
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		throw new NotImplementedException();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		throw new NotImplementedException();
	}

	@Override
	public void setComposite(Composite comp) {
		throw new NotImplementedException();
	}

	@Override
	public void setPaint(Paint paint) {
		throw new NotImplementedException();
	}

	@Override
	public void setStroke(Stroke s) {
		throw new NotImplementedException();
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		throw new NotImplementedException();
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		throw new NotImplementedException();
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		throw new NotImplementedException();
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		throw new NotImplementedException();
	}

	@Override
	public RenderingHints getRenderingHints() {
		throw new NotImplementedException();
	}

	@Override
	public void translate(int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public void translate(double tx, double ty) {
		throw new NotImplementedException();
	}

	@Override
	public void rotate(double theta) {
		throw new NotImplementedException();
	}

	@Override
	public void rotate(double theta, double x, double y) {
		throw new NotImplementedException();
	}

	@Override
	public void scale(double sx, double sy) {
		throw new NotImplementedException();
	}

	@Override
	public void shear(double shx, double shy) {
		throw new NotImplementedException();
	}

	@Override
	public void transform(AffineTransform Tx) {
		throw new NotImplementedException();
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		throw new NotImplementedException();
	}

	@Override
	public AffineTransform getTransform() {
		throw new NotImplementedException();
	}

	@Override
	public Paint getPaint() {
		throw new NotImplementedException();
	}

	@Override
	public Composite getComposite() {
		throw new NotImplementedException();
	}

	@Override
	public void setBackground(Color color) {
		throw new NotImplementedException();
	}

	@Override
	public Color getBackground() {
		throw new NotImplementedException();
	}

	@Override
	public Stroke getStroke() {
		throw new NotImplementedException();
	}

	@Override
	public void clip(Shape s) {
		throw new NotImplementedException();
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		throw new NotImplementedException();
	}

	@Override
	public Graphics create() {
		throw new NotImplementedException();
	}

	@Override
	public Color getColor() {
		throw new NotImplementedException();
	}

	@Override
	public void setColor(Color c) {
		this.color = "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
		this.alpha = c.getAlpha()/255.0;
	}

	@Override
	public void setPaintMode() {
		throw new NotImplementedException();
	}

	@Override
	public void setXORMode(Color c1) {
		throw new NotImplementedException();
	}

	@Override
	public Font getFont() {
		throw new NotImplementedException();
	}

	@Override
	public void setFont(Font font) {
		throw new NotImplementedException();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return frame.getGraphics().getFontMetrics(f);
	}

	@Override
	public Rectangle getClipBounds() {
		throw new NotImplementedException();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		throw new NotImplementedException();
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		throw new NotImplementedException();
	}

	@Override
	public Shape getClip() {
		throw new NotImplementedException();
	}

	@Override
	public void setClip(Shape clip) {
		throw new NotImplementedException();
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		throw new NotImplementedException();
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		s.append("<line ");
		s.append("x1=\"");
		s.append(x1);
		s.append("\" y1=\"");
		s.append(y1);
		s.append("\" xx2=\"");
		s.append(x2);
		s.append("\" y2=\"");
		s.append(y2);
		s.append("\" stroke=\"");
		s.append(color);
		s.append("\" />\n");

	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		throw new NotImplementedException();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		throw new NotImplementedException();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		throw new NotImplementedException();
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		throw new NotImplementedException();
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		throw new NotImplementedException();
	}

	@Override
	public void dispose() {
		throw new NotImplementedException();
	}

}
