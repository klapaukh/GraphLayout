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


import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.IOException;

public class Sprite implements Comparable<Sprite> {

	public final String name;
	private Image[] im;
	private final String[] imgSrc;

	// Only support PNG, JPG and GIF
	public Sprite(String name, String imgSrc) throws IOException {
		if (name == null || name.equals("")) {
			throw new IOException("Name cannot be null or \"\"");
		}
		this.name = name;

		if (imgSrc.equals("")) {
			throw new IllegalArgumentException("Image path must be given for all sprites in database");
		}
		this.imgSrc = imgSrc.split("\\s+");

	}

	public Image getSprite() {
		if (im == null) {
			im = new Image[imgSrc.length];
		}
		int idx;
		if (im.length == 1) {
			idx = 0;
		} else {
			idx = (int) (Math.random() * im.length);
		}
		if (im[idx] == null) {
			im[idx] = Toolkit.getDefaultToolkit().createImage(imgSrc[idx]);
			MediaTracker m = new MediaTracker(new Component(){
				private static final long serialVersionUID = -7484809444025767961L;});
			m.addImage(im[idx],1);
			try {
				m.waitForAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return im[idx];
	}

	@Override
	public int compareTo(Sprite o) {
		if (o instanceof Sprite) {
			return name.compareTo(((Sprite) o).name);
		}
		return 1;
	}

	public boolean equals(Object o) {
		return name.equals(o);
	}

}
