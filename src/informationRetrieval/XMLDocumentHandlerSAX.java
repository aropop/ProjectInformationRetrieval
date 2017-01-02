package informationRetrieval;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDocumentHandlerSAX extends DefaultHandler {
	/** A buffer for each XML element */
	private StringBuffer elementBuffer = new StringBuffer();

	private Document mDocument;
	private ArrayList<String> XMLPath;
	private ArrayList<String> terms;

	// constructor
	public XMLDocumentHandlerSAX(File xmlFile)
		throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		// use validating parser
		spf.setValidating(true);
		// make the parser name space aware turn
		//spf.setNamespaceAware(true);


		SAXParser parser = spf.newSAXParser();
		parser.parse(xmlFile, this);
	}

	// call at document start
	public void startDocument() throws SAXException {
		mDocument = new Document();
	}

	// call at element start
	public void startElement(
		String namespaceURI,
		String localName,
		String qualifiedName,
		Attributes attrs)
		throws SAXException {
		String eName = localName;
		if ("".equals(eName)) {
			eName = qualifiedName; // namespaceAware = false
		}
		// list the attribute(s)
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name
                if ("".equals(aName)) { aName = attrs.getQName(i); }
                // perform application specific action on attribute(s)
                // for now just dump out attribute name and value
								 System.out.println("attr " + aName+"="+attrs.getValue(i));
            }
        }
		elementBuffer.setLength(0);
	}

	// call when cdata found
	public void characters(char[] text, int start, int length)
		throws SAXException {
		elementBuffer.append(text, start, length);
	}

	// call at element end
	public void endElement(
		String namespaceURI,
		String simpleName,
		String qualifiedName) {

		String eName = simpleName;
		if ("".equals(eName)) {
			eName = qualifiedName; // namespaceAware = false
		}
		System.out.println(
			"endElement eName: "
				+ eName
				+ "\teltBuff:  "
				+ elementBuffer.toString());
		//mDocument.add(Field(eName, elementBuffer.toString(), new FieldType()));


	}

	public Document getDocument() {
		return mDocument;
	}

	public static void main(String[] args) {
		File tst = new File("tst.xml");
		try {
			XMLDocumentHandlerSAX handler = new XMLDocumentHandlerSAX(tst);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 }
