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

public class AlloyXMLParser extends DefaultHandler {
	StringBuffer textBuffer;

	List<Atom> objects;
	Map<String, List<Relation>> relations; // Map name -> neighbors

	String field;
	String sig;
	String first;

	public AlloyXMLParser(){
		objects = new ArrayList<Atom>();
		relations = new TreeMap<String, List<Relation>>();

		first = sig = field = null;

	}

	public void startDocument() {
		//Do nothing
	}

	public void endDocument() {
		//do nothing
	}

	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) {
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware

		if(eName.equalsIgnoreCase("sig")){
			//open sig scope
			sig = attrs.getValue("label");
//			System.out.println("Sig " + sig);
		}else if(eName.equalsIgnoreCase("field")){
			//open field scope
			field = attrs.getValue("label");
//			System.out.println("Field " + field);
		}else if(eName.equalsIgnoreCase("atom")){
			String name = attrs.getValue("label");
			if(sig != null){
				objects.add(new Atom(sig,name));
//				System.out.println("\t"+ name);
			}else if(field != null){
				if(first == null){
					first = name;
				}else{
					List<Relation> l = relations.get(first);
					if(l ==null){
						l = new ArrayList<Relation>();
						relations.put(first,l);
					}
					l.add(new Relation(field, name));
//					System.out.println("\t" + first + " -> " + name);
					first = null;
				}
			}
		}
//		if (attrs != null) {
//			for (int i = 0; i < attrs.getLength(); i++) {
//				String aName = attrs.getLocalName(i); // Attr name
//				if ("".equals(aName))
//					aName = attrs.getQName(i);
//				emit(" ");
//				emit(aName + "=\"" + attrs.getValue(i) + "\"");
//			}
//		}
	}

	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) {
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware
		if(eName.equalsIgnoreCase("sig")){
			//close sig scope
			sig = null;
		}else if(eName.equalsIgnoreCase("field")){
			//close field scope
			field = null;
			first = null;
		}
	}

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
