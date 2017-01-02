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
import org.xml.sax.SAXException;


public class XMLParser {

    public XMLParser(String filePath, Document index){
      File xmlFile = new File(filePath);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder;

      try {
          dBuilder = dbFactory.newDocumentBuilder();
          Document doc = dBuilder.parse(xmlFile);
          doc.getDocumentElement().normalize();
          Element root = doc.getDocumentElement();
          NodeList nodeList = root.getChildNodes();
          //now XML is loaded as Document in memory, lets convert it to Object List
          List<Employee> empList = new ArrayList<Employee>();
          for (int i = 0; i < nodeList.getLength(); i++) {
              empList.add(getEmployee(nodeList.item(i)));
          }
          //lets print Employee list information
          for (Employee emp : empList) {
              System.out.println(emp.toString());
          }
      } catch (SAXException | ParserConfigurationException | IOException e1) {
          e1.printStackTrace();
      }
    }

    private nodeTraverser(Element el, String path){
      String newPath = path + el.getTagName();
      if(el.hasChildNodes()){
        NodeList nodeList = el.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++){
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) nodeList.item(i);
            nodeTraverser(element, newPath);
          }
        }
      } else {
        addToIndex(Element el, String newPath);
      }
    }
    private addToIndex(Element el, String path){
      doc.add(new Field(el.getTextContent(), path, fieldType));
    }

    public static void main(String[] args) {
        String filePath = "employee.xml";

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Employee");
            //now XML is loaded as Document in memory, lets convert it to Object List
            List<Employee> empList = new ArrayList<Employee>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                empList.add(getEmployee(nodeList.item(i)));
            }
            //lets print Employee list information
            for (Employee emp : empList) {
                System.out.println(emp.toString());
            }
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }

    }


    private static Employee getEmployee(Node node) {
        //XMLReaderDOM domReader = new XMLReaderDOM();
        Employee emp = new Employee();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            emp.setName(getTagValue("name", element));
            emp.setAge(Integer.parseInt(getTagValue("age", element)));
            emp.setGender(getTagValue("gender", element));
            emp.setRole(getTagValue("role", element));
        }

        return emp;
    }


    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

}
