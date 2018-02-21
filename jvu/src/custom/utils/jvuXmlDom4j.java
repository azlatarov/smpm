package custom.utils;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Visitor;
import org.dom4j.VisitorSupport;
import org.dom4j.tree.DefaultElement;

/**
 * @jar dom4j-full.jar
 * @jar dom4j.jar		(alternative to dom4j-full.jar; half the size)
 */
public final class jvuXmlDom4j {
	
	private jvuXmlDom4j() {}
    
	public static String removeAllNamespaces_RegEx (Document document) {
		return document.asXML()
				.replaceAll("(<\\?[^<]*\\?>)?", "") /* remove preamble */
				.replaceAll("xmlns.*?(\"|\').*?(\"|\')", "") /* remove xmlns declaration */
				.replaceAll("xsi.*?(\"|\').*?(\"|\')", "") /* remove xsi declaration */
				.replaceAll("(<)(\\w+:)(.*?>)", "$1$3") /* remove opening tag prefix */
				.replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
	}
	
	public static Document removeAllNamespaces_Visitor (Document document) {
		document.accept(new NamespaceCleaner());
		return document;
	}
	
	public static Document changeNamespace_Visitor (Document document, Namespace oldNS, Namespace newNS) {
		Visitor visitor = new NamespaceChanger(oldNS, newNS);
	    document.accept(visitor);
	    return document;
	}
	
	/*
	 * NOT WORKING START
	 */
	public static void removeAllNamespaces(Document doc) {
		Element root = doc.getRootElement();
	        if (root.getNamespace() != Namespace.NO_NAMESPACE) {            
	                removeNamespaces(root.content());
	        }
	    }

	    public static void unfixNamespaces(Document doc, Namespace original) {
	        Element root = doc.getRootElement();
	        if (original != null) {
	            setNamespaces(root.content(), original);
	        }
	    }

	    public static void setNamespace(Element elem, Namespace ns) {

	        elem.setQName(QName.get(elem.getName(), ns, elem.getQualifiedName()));
	    }

	    /**
	     *Recursively removes the namespace of the element and all its
	    children: sets to Namespace.NO_NAMESPACE
	     */
	    public static void removeNamespaces(Element elem) {
	        setNamespaces(elem, Namespace.NO_NAMESPACE);
	    }

	    /**
	     *Recursively removes the namespace of the list and all its
	    children: sets to Namespace.NO_NAMESPACE
	     */
	    public static void removeNamespaces(List l) {
	        setNamespaces(l, Namespace.NO_NAMESPACE);
	    }

	    /**
	     *Recursively sets the namespace of the element and all its children.
	     */
	    public static void setNamespaces(Element elem, Namespace ns) {
	        setNamespace(elem, ns);
	        setNamespaces(elem.content(), ns);
	    }

	    /**
	     * Recursively sets the namespace of the List and all children if the current namespace is match
	     */
	    public static void setNamespaces(List l, Namespace ns) {
	        Node n = null;
	        for (int i = 0; i < l.size(); i++) {
	            n = (Node) l.get(i);

	            if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
	                ((Attribute) n).setNamespace(ns);
	            }
	            if (n.getNodeType() == Node.ELEMENT_NODE) {
	                setNamespaces((Element) n, ns);
	            }            
	        }
	    }
	/*
	 * NOT WORKING END
	 */
	
}

final class NamespaceCleaner extends VisitorSupport {
    
	public void visit(Document document) {
		((DefaultElement) document.getRootElement()).setNamespace(Namespace.NO_NAMESPACE);
        document.getRootElement().additionalNamespaces().clear();
    }
    
    public void visit(Namespace namespace) {
        namespace.detach();
    }
    
    public void visit(Attribute node) {
    	if (node.toString().contains("xmlns") || node.toString().contains("xsi:"))
    		node.detach();
    }

    public void visit(Element node) {
        if (node instanceof DefaultElement)
        	((DefaultElement) node).setNamespace(Namespace.NO_NAMESPACE);
    }
}

final class NamespaceChanger extends VisitorSupport {
	private Namespace from;
	private Namespace to;

	public NamespaceChanger(Namespace from, Namespace to) {
		this.from = from;
		this.to = to;
	}
	
	public void visit(Element node) {
		Namespace ns = node.getNamespace();

		// reads root NSs and applies prefix everywhere
		if(ns.getURI().equals(from.getURI())) {
			String nodeName = node.getName();
			
			if("save".equalsIgnoreCase(nodeName))
				node.setQName(new QName(nodeName, to));
		}
	}
}

