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
	
