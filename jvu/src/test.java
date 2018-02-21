import java.io.ByteArrayInputStream;

import custom.tools.jvuDiacriticsRemover;
import custom.utils.jvuDataStructures;
import custom.utils.jvuIO;
import custom.utils.jvuString;


public class test {
	public static void main(String[] args) throws Exception {
		Object[] o = null;
		
		o = jvuDataStructures.clean(new Object[] {"s1", "s2", "s3"}, new Object[] {"s2"});
		//print(o);
		o = jvuDataStructures.clean(new Object[] {"s1", "s2",""," ", "s3", ""}, new Object[] {"", " "});
		//print(o);
		o = jvuDataStructures.clean(new Object[] {new Integer(1), new Integer(2), new Integer(3)}, new Object[] {new Integer(2)});
		//print(o);
		o = jvuDataStructures.clean(new Object[] {new Double(1.11), new Double(2.22), new Double(3.33)}, new Object[] {new Double(2.22)});
		//print(o);
		
		System.out.println(jvuString.rmvAll("asfjasflj1234729729rwefkjAAABdBADBASsdflswu902r92r2f89h", "\\d"));
		System.out.println(jvuString.rmvAll("asfjasflj1234729729rwefkjAAABdBADBASsdflswu902r92r2f89h", "[a-z]"));
		System.out.println(jvuString.rmvAll("asfjasflj1234729729rwefkjAAABdBADBASsdflswu902r92r2f89h", "a-z"));
		System.out.println(jvuString.rmvAll("asfjasflj1234729729rwefkj\r\nAAABdBADBAS\r\nsdflswu902r92r2f89h", "\r\n"));
		
		System.out.println(jvuString.surround("Test string", "'"));
		System.out.println(jvuString.surround("Test string", "\""));
		System.out.println(jvuString.surround("Test string", "()"));
		System.out.println(jvuString.surround("Test string", "{}"));
		System.out.println(jvuString.surround("Test string", "[]"));
		System.out.println(jvuString.surround("Test string", "*"));
		System.out.println(jvuString.surround("Test string", "(()"));
		System.out.println(jvuString.trim("'Test ''''string'", "'"));
		System.out.println(jvuString.trim("\"Test \"string\"", "\""));//???
		System.out.println(jvuString.trim("(Test )(string)", "()"));
		System.out.println(jvuString.trim("{Test }{string}", "{}"));
		System.out.println(jvuString.trim("[Test ][string]", "[]"));
		System.out.println(jvuString.trim("*Test **string*", "*"));
		System.out.println(jvuString.trim("(Test )(string)", "(()"));
		
		char cr = 0x0A; String CR = "\r";
		char lf = 0x0D; String LF = "\n";
		(new StringBuffer()).toString();
		new String(new StringBuffer());
		
		byte[] b = "sfd;asf\nsfasfsadf\nasfasdfsaf\nafaf".getBytes();
		boolean lineCountMethodComparisson = jvuIO.getLineCount(new ByteArrayInputStream(b)) == jvuIO.getLineCount_V2(new ByteArrayInputStream(b));
		System.out.println(" Comparison between 2 methods to do line count on a fail was: "+((lineCountMethodComparisson)?"successful":"UNsuccessful"));
        System.out.println(jvuDiacriticsRemover.removeAll("èéàçsqjgm"));
        
        //@jvuFileSystemTraverser.jar
        FileSystemTraverser.main(new String[] {"D:\\A\\ë÷n","D:\\out.txt","true","D:\\A_ë÷n_backup"});
        //or
        Traversar ft = new Traversar("D:\\A\\ôií","D:\\out_ôií.txt",true,"D:\\A_ôií_backup");
        ft.traverse();
	}
	
	private static void print(Object[] in) {
		for(int i = 0; i < in.length; i++){
			System.out.println(in[i]);
		}
	}
}
