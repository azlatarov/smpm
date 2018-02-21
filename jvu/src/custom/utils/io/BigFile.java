package custom.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Iterator;

import custom.utils.jvuNetworks;


public class BigFile implements Iterable<String> {
    private BufferedReader _reader;
    
	public static void main(String[] args) throws Exception {
		BigFile file = new BigFile("C:\\Users\\User\\Desktop\\dumpfiles.txt");
		for (String line : file) {
			System.out.println(line);
			InputStream is = jvuNetworks.getFtpIS("appserver", "52WgAsMt65", "192.168.1.173","/../../usr/local/BIS/data/dt/as2dump/2012/11/23/SCAN.BERNARD.ARVAL/"+line);
			FileOutputStream fos = new FileOutputStream(new File("c:\\Users\\patrice\\Desktop\\DUMPFILES\\"+line));
			jvuIO.in2out(is, fos);
			is.close();
			fos.close();
		}
	}
    
    public BigFile(String fPath) throws Exception {
    	File f = new File(fPath);
    	if(!f.exists())
    		f.createNewFile();
    	_reader = new BufferedReader(new FileReader(f));
    }
 
    public void Close() {
		try { _reader.close(); }
		catch (Exception ex) { }
    }
 
    public Iterator<String> iterator() {
    	return new FileIterator();
    }
 
    private class FileIterator implements Iterator<String> {
		private String _currentLine;
	 
		public boolean hasNext() {
		    try {
		    	_currentLine = _reader.readLine();
		    } catch (Exception ex) {
				_currentLine = null;
				ex.printStackTrace();
		    }
		    
		    return _currentLine != null;
		}
		
		public String next() {
		    return _currentLine;
		}
		
		public void remove() { }
    }
}