package custom.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public final class jvuXmlW3C {
	
	private jvuXmlW3C() {}
	
	/**
	 * Converts a byte array to org.w3c.dom.Document.
	 * @param	xmlBytes
	 * @return
	 * @throws	Exception
	 */
    public static Document bytes2doc (byte[] xmlBytes) throws Exception {
    	Document xml;
    	try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			xml = builder.parse(new ByteArrayInputStream(xmlBytes));
    	} catch (SAXException saxe) {
    		throw new Exception("SAXException encountered while in method Utils.bytesToXml()");
    	} catch (ParserConfigurationException pce) {
    		throw new Exception("ParserConfigurationException encountered while in method Utils.bytesToXml()");
    	} catch (IOException ioe) {
    		throw new Exception("IOException encountered while in method Utils.bytesToXml()");
    	}
    	
    	return xml;
	}
    
    /**
     * Converts a org.w3c.dom.Document to byte array.
     * @param	node
     * @return
     * @throws	Exception
     */
    public static byte[] doc2bytes (Node node) throws Exception  {
    	byte [] xmlBytes;
    	
		try {
			Source source = new DOMSource(node);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Result result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			xmlBytes = out.toByteArray();
		} catch (TransformerConfigurationException e) {
			throw new Exception("TransformerConfigurationException encountered while in method Utils.doc2bytes()");
		} catch (TransformerException e) {
			throw new Exception("TransformerException encountered while in method Utils.doc2bytes()");
		}
		
		return xmlBytes;
	}
    

}
