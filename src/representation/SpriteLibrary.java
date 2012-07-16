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
