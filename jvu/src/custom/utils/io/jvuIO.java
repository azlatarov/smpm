package custom.utils.io;

import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
//import java.util.jar.JarInputStream;
//import java.util.zip.ZipOutputStream;
//import java.util.zip.GZIPOutputStream;


public final class jvuIO {
	
	private jvuIO () {}
	
	// ISs:
	// new FileInputStream("filePath")
	// new ByteArrayInputStream(byte[])
	// new JarInputStream(java.io.ByteArrayInputStream)
	
	// READERs:
	// new InputStreamReader(java.io.InputStream)
	// new BufferedReader(java.io.Reader)
	// new LineNumberReader(java.io.Reader)
	
	// OSs:
	// new ByteArrayOutputStream()
	// new ZipOutputStream(java.io.ByteArrayOutputStream)
	// new TarArchiveOutputStream(java.io.ByteArrayOutputStream) -- commons-compress-1.1.jar
	// new GZIPOutputStream(java.io.ByteArrayOutputStream)
	
	public static InputStreamReader getIS (InputStream is, String encoding) {
		if (is == null)
			return null;
		if (encoding == null || "".equals(encoding))
			return new InputStreamReader(is);
		
		return new InputStreamReader(is, Charset.forName(encoding).newDecoder());
	}
	
	/**
     * Reads data from inputStream and writes it to outputStream. The write process is buffered. 
     * @return	number of bytes read
     * @throws 	CustomUtilsException
	 * @author 	Atanas P. Zlatarov
     */
    public static int in2out (InputStream in, OutputStream out) throws IOException {
    	if (in == null || out == null)
    		return 0;
    	
		int i;
		int offset = 0;
		byte[] b = new byte[2048];
		while ((i = in.read(b)) > 0) {
			out.write(b, 0, i);
			offset += i;
		}
		
		return offset;
    }
    
	public static byte[] reader2bytes (Reader reader) throws IOException {
		if (reader == null)
			return null;
		
		int ch;
		final StringBuffer buf = new StringBuffer();
		while ((ch=reader.read()) > -1)
			buf.append((char)ch);
		
		return buf.toString().getBytes();
	}
	
	public static File bytes2file (byte[] fBytes, String fPath) throws FileNotFoundException, IOException {
		if (fPath == null || "".equals(fPath))
			return null;
		if (fBytes == null || fBytes.length == 0)
			return new File(fPath);
		
		FileOutputStream fos = null;
		try {
    		File f = new File(fPath);
    		fos = new FileOutputStream(f);
    		fos.write(fBytes);
    		fos.flush();
    		fos.close();
    		
    		return f;
		} finally {
		    closeResource(fos);
		}
	}
    
    public static int getLineCount (InputStream is) throws IOException {
    	if(is == null)
    		return -1;
    	
	    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		while (lnr.readLine() != null);
		return lnr.getLineNumber();
	}
    
    public static int getLineCount_V2 (InputStream is) throws IOException {
    	if(is == null)
    		return -1;
    	
    	int cnt = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while (br.readLine() != null)
            cnt++;
        return cnt;
    }
    
    public static byte[] file2bytes (String fPath) throws FileNotFoundException, IOException {
    	return is2bytes(new FileInputStream(fPath));
    }
    
    public static byte[] file2bytes (File f) throws FileNotFoundException, IOException {
    	return is2bytes(new FileInputStream(f));
    }
    
    public static byte[] is2bytes (InputStream is) throws IOException {
    	if(is == null)
    		return null;
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		in2out(is, baos);
		return baos.toByteArray();
    }
    
    public static boolean isEDIFACT (byte[] data) {
    	return ( startsWith(data, "UNA") || startsWith(data, "UNB") );
    }
    
    public static boolean isPDF (byte[] data) {
    	return startsWith(data, "%PDF");
    }
    
    public static boolean isZip (byte[] data) {
        //TODO add check for buggy windows files (xslx, docx, etc.)!
        //TODO add check for jar files!
    	return startsWith(data, "PK");
    }
    
    public static boolean isXml (byte[] data) {
        return startsWith(data, "<?xml");
    }
    
    //TODO add checks for other types: rar, jar, edicom, equal-line-length, ...
    
    private static boolean startsWith (byte[] data, String chars) {
    	if(data == null || data.length < chars.length())
    		return false;
    	
    	int i = 0;
    	byte[] firstData = new byte[chars.length()];//TODO change execution i.e. 1 char can be 16-bit
    	while (i < chars.length())
    		firstData[i] = data[i++];
    	
    	return (chars.equals(new String(firstData)));
    }
    
	public static void closeResource (InputStream is) throws IOException {
        if(is != null)
        	is.close();
    }
    
    public static void closeResource (OutputStream os) throws IOException {
        if(os != null)
            os.close();
    }
}

class MyPrintStream extends PrintStream {
	
	public MyPrintStream(OutputStream out) {
		super(out);
	}

	StringBuffer buf = new StringBuffer("");
	public void println(String x) {
		buf.append(x);
	}
	
	public String getStreamData() {
		return buf.toString();
	}
}
