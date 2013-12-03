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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class GraceDotParser {

	Set<Atom> objects;
	Map<String, List<Relation>> relations; // Map name -> neighbors

	public GraceDotParser(File file, String image) throws FileNotFoundException {
		objects = new HashSet<Atom>();
		relations = new TreeMap<String, List<Relation>>();
		Scanner scan = new Scanner(file);

		boolean started = false;
		while (scan.hasNext()) {
			String line = scan.nextLine();
			if (line.isEmpty())
				continue;
			if ((!started && line.contains("{")) || (started && line.trim().equals("}"))) {
				continue;
			}
			String[] parts = line.split("\\s*-[>-]\\s*");
			if (parts.length != 2) {
				continue;
			}

			Atom n1 = new Atom(image, parts[0].trim());
			Atom n2 = new Atom(image, parts[1].trim().replaceAll(";$", ""));
			if (!objects.contains(n1)) {
				objects.add(n1);
			}
			if (!objects.contains(n2)) {
				objects.add(n2);
			}

			List<Relation> l = relations.get(n1.name);
			if (l == null) {
				l = new ArrayList<Relation>();
				relations.put(n1.name, l);
			}
			Relation r = new Relation("dd", n2.name);
			if(!l.contains(r)){
				l.add(r);
			}
		}
		scan.close();

	}

	public List<Atom> getObjects() {
		return new ArrayList<Atom>(objects);
	}

	public Map<String, List<Relation>> getRelations() {
		return relations;
	}
}
