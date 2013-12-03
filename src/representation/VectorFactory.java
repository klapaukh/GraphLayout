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

public class VectorFactory {

	private VectorFactory() {

	}

	public static void delete(Vector v) {

	}

	public static Vector newVector(Vector v) {
		return newVector(v.x, v.y);
	}

	public static Vector newVector(double x, double y) {
				return new Vector(x, y);
	}

}
