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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class SpriteLibrary {

	private Map<String,Sprite> sprites;
	private String dataFile;

	public SpriteLibrary() throws IOException {
		this("sprites.data");
	}

	public SpriteLibrary(String dataFile) throws IOException {
		sprites = new TreeMap<String,Sprite>();

		this.dataFile = dataFile;
		File file = new File(dataFile);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("Data file does not exist, and could not be created");
			}
		}
		loadData();
	}

	private void loadData() throws IOException {
		File f = new File(this.dataFile);
		try {
			Scanner scan = new Scanner(f);
			while(scan.hasNextLine()){
				String name = scan.next().trim();
//				System.out.println("Adding sprite " + name);
				sprites.put(name,new Sprite(name,scan.nextLine().trim()));
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IOException("Data file could not be found to read in sprite database");
		}
	}

	public Sprite getSprite(String name){
		return sprites.get(name);
	}

	public Set<String> getNames(){
		return sprites.keySet();
	}

}
