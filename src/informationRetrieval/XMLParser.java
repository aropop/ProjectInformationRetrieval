package informationRetrieval ;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


public class XMLParser {
	
	private ArrayList<String> paths;

    public XMLParser(String filePath){
      File xmlFile = new File(filePath);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder;
      
      paths = new ArrayList<String>();

      try {
          dBuilder = dbFactory.newDocumentBuilder();
          Document doc = dBuilder.parse(xmlFile);
          doc.getDocumentElement().normalize();
          Element root = doc.getDocumentElement();
          NodeList nodeList = root.getChildNodes();
          //now XML is loaded as Document in memory, lets convert it to Object List
          for (int i = 0; i < nodeList.getLength(); i++) {
        	  if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                  Element element = (Element) nodeList.item(i);
                  nodeTraverser(element, root.getTagName());
                }
          }
          //lets print Employee list information
      } catch (SAXException | ParserConfigurationException | IOException e1) {
          e1.printStackTrace();
      }
    }

    private void nodeTraverser(Element el, String path){
      String newPath = path + "/" + el.getTagName();
      
      if(el.hasChildNodes()){
        NodeList nodeList = el.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++){
          if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) nodeList.item(i);
            nodeTraverser(element, newPath);
          } else if(nodeList.item(i).getNodeType() == Node.TEXT_NODE){
        	Text txt = (Text) nodeList.item(i);
        	if(!txt.getTextContent().trim().equals("")){
        		String[] txtContents = txt.getTextContent().split(" ");
            	for(int j = 0; j < txtContents.length; j++){
            		String cont = txtContents[j].replaceAll("\\s","");
            		cont = cont.replaceAll("[^a-zA-Z]", "").toLowerCase();
            		if(!cont.equals("")){
            			paths.add(newPath + "#" + cont);
            		}
            	}
        	}
          }
        }
      }
    }
    
    public int getAmountOfPaths(){
    	return paths.size();
    }
    
    public String getTerm(int pos){
    	String path = paths.get(pos);
    	return path.split("#")[1];
    }
    
    public String getContext(int pos){
    	String path = paths.get(pos);
    	String[] tmp = path.split("#");
    	return tmp[0] + "/" + tmp[1];
    }
      
    public static void main(String[] args) {
        String filePath = "/home/wovari/information_retrieval/pages/932/509932.xml";
    	XMLParser xmlparse = new XMLParser(filePath);
    	
    	System.out.println("SIZE: " + xmlparse.getAmountOfPaths());
    	for(int i = 0; i < xmlparse.getAmountOfPaths(); i++){
    		System.out.println(i);
    		System.out.println(xmlparse.getTerm(i));
    		System.out.println(xmlparse.getContext(i));
    	}
    }
}
