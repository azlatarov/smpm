package custom.utils;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;



public final class jvuXmlJAVAX {
	
	private jvuXmlJAVAX() {}
	
	public static void XMLTransform (InputStream xml, InputStream xsl, OutputStream result) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(xsl));
			transformer.transform(new StreamSource(xml),new StreamResult(result));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
