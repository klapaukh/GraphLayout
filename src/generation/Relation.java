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

package generation;
	public class Relation{

		public final String relName;
		public final String element;

		public Relation(String relName, String element){
			this.relName = relName;
			this.element = element;
		}

		public boolean equals(Object o){
			if(o instanceof Relation){
				return relName.equals(((Relation) o).relName) && element.equals(((Relation) o).element);
			}
			return false;
		}

		public String toString(){
			return element + ":" + relName;
		}
	}

