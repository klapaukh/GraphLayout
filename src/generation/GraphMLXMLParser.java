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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class GraphMLXMLParser extends DefaultHandler {
	StringBuffer textBuffer;
	String type = "Person";
	List<Atom> objects;
	Map<String, List<Relation>> relations; // Map name -> neighbors


	public GraphMLXMLParser(){
		this(null);
	}

	public GraphMLXMLParser(String node){
		if(node != null){
			this.type = node;
		}
		objects = new ArrayList<Atom>();
		relations = new TreeMap<String, List<Relation>>();
	}

	public void startDocument() {
	}

	public void endDocument() {
	}

	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) {
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware

		if(eName.equalsIgnoreCase("node")){
			//open sig scope
			String name = attrs.getValue("id");
			objects.add(new Atom(type,name));
//			System.out.println("Node " + name);
		}else if(eName.equalsIgnoreCase("edge")){
			//open field scope
			String name = attrs.getValue("id");
			String source = attrs.getValue("source");
			String target = attrs.getValue("target");

			List<Relation> l = relations.get(source);
			if(l ==null){
				l = new ArrayList<Relation>();
				relations.put(source,l);
			}
			l.add(new Relation(name, target));
//			System.out.println("Edge " + name + " from " + source + " to " + target);
		}
	}

	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) {	}

	public void characters(char buf[], int offset, int len)
	{
	  String s = new String(buf, offset, len);
	  if (textBuffer == null) {
	    textBuffer = new StringBuffer(s);
	  } else {
	    textBuffer.append(s);
	  }
	}


	public List<Atom> getObjects(){
		return objects;
	}

	public Map<String, List<Relation>> getRelations(){
		return relations;
	}


}
