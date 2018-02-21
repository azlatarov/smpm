package custom.utils;

import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import com.oreilly.servlet.multipart.FilePart;


public final class jvuNetworks {
	
	private jvuNetworks() {}
	
	/**
	 * Connect to server over FTP. Do not forget to logout after done working with client!
	 * @author	Atanas P. Zlatarov
	 * @jar		commons-net-2.0.jar
	 * @api 	http://commons.apache.org/net/api/index.html?org/apache/commons/net/ftp/FTPClient.html
	 */
	public FTPClient doFTPConnect(String ip, String usr, String pwd) throws IOException {
		FTPClient client = new FTPClient();
		client.connect(ip);
		return (client.login(usr, pwd)) ? client : null;
	}
	
	/**
	 * List files in a server directory.
	 * @author	Atanas P. Zlatarov
	 * @jar		commons-net-2.0.jar
	 * @api 	http://commons.apache.org/net/api/index.html?org/apache/commons/net/ftp/FTPClient.html
	 */
	public void listFilesInFTPDir(FTPClient client, String path) throws IOException {
		String separator = "/";
		for(FTPFile f : client.listFiles(path)) {
			String fName = f.getName();
			fName = fName.substring(fName.lastIndexOf(separator) + separator.length());
			System.out.println(fName+" : "+jvuDate.formatDate(f.getTimestamp().getTime(), "dd MMM yyyy HH:mm:ss"));
		}
	}
	
	/**
	 * Get InputStream using java.net.URL object.
	 */
	public static InputStream getFtpIS (String usr, String pwd, String ip, String fPath) throws MalformedURLException, IOException {
		return buildFtpURL(usr, pwd, ip, fPath).openStream();
	}

	/**
	 * Get OutputStream using java.net.URL object.
	 */
	public static OutputStream getFtpOS (String usr, String pwd, String ip, String fPath) throws MalformedURLException, IOException {
		return buildFtpURL(usr, pwd, ip, fPath).openConnection().getOutputStream();
	}
	
	private static URL buildFtpURL (String usr, String pwd, String ip, String fPath) throws MalformedURLException {
		return new URL("ftp://"+usr+":"+pwd+"@"+ip+fPath);
	}
	
	/**
	 * Connect to HTTP client using basic authentication. Use when username/pass cannot be included in the url.
	 * @jar		commons-httpclient.jar
	 * @api 	http://hc.apache.org/httpclient-legacy/apidocs/index.html?org/apache/commons/httpclient/HttpClient.html
	 */
	public HttpClient httpClient (String uri, String usr, String pass) {
		HttpClient client = new HttpClient();
		AuthScope authScope = new AuthScope(uri, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
		Credentials credentials = new UsernamePasswordCredentials(usr, pass);
		client.getState().setCredentials(authScope, credentials);
		return client;
	}
	
	/**
	 * Execute HTTP client GET request.
	 * @jar		commons-httpclient.jar
	 * @api 	http://hc.apache.org/httpclient-legacy/apidocs/index.html?org/apache/commons/httpclient/HttpClient.html
	 */
	public String doGetRequest(HttpClient client, String url) throws Exception {
		String result;
		
		GetMethod get = new GetMethod(url);
		get.setDoAuthentication(true);
		
		try {
			result = executeRequest(client, get);
		} finally {
			get.releaseConnection();
		}
		
		return result;
	}
	
	/**
	 * Execute HTTP client POST request.
	 * @jar		commons-httpclient.jar
	 * @api 	http://hc.apache.org/httpclient-legacy/apidocs/index.html?org/apache/commons/httpclient/HttpClient.html
	 */
	public String doPostRequest(HttpClient client, String url, Map<String, Object> parameters) throws Exception {
		String result;
		
		PostMethod post = new PostMethod(url);
		post.setDoAuthentication(true);
		post.addParameter("userLoginName", "testeur");//TODO use parameters here
		
		try {
			result = executeRequest(client, post);
		} finally {
			post.releaseConnection();
		}
		
		return result;
	}
	
	/**
	 * General execute request method used by GET and POST execute methods above.
	 * @jar		commons-httpclient.jar
	 * @api 	http://hc.apache.org/httpclient-legacy/apidocs/index.html?org/apache/commons/httpclient/HttpClient.html
	 */
	private String executeRequest (HttpClient client, HttpMethod method) throws Exception {
		try {
			int status = client.executeMethod(method);
			if(status == 404)
				throw new Exception("Returned status 404. Check if server is running.");
			
			return method.getResponseBodyAsString();
		} catch (HttpException he) {
			throw new Exception("HttpException encountered");
		} catch (IOException ioe) {
			throw new Exception("IOException encountered");
		} catch (Exception e) {
			throw new Exception("Exception encountered");
		}
	}
	
	public void doFtpAppend (String ip, String usrName, String pwd, String fPath, InputStream dataToAdd) throws IOException {
		FTPClient client2 = new FTPClient();
		client2.connect(ip);
		client2.login(usrName, pwd);
		client2.appendFile(fPath, dataToAdd);
	}
	
	/**
	 * Read multipart request and fill up a Hashtable with <name, bytes> pairs. The Param part will have bytes=null, while File part bytes=fileBytes.
	 * @param	request
	 * @return
	 * @throws	IOException
	 * @author	Atanas P. Zlatarov
	 * @jar 	servlet-api.jar
	 * @jar 	servlet-cos.jar
	 * @api 	http://www.servlets.com/cos/javadoc/index.html?com/oreilly/servlet/MultipartRequest.html
	 */
	final static int FILE_LENGTH = 128 * 1024 * 1024;
	public Hashtable<String, byte[]> extract_MultipartHTTPRequest_Data (HttpServletRequest req) throws IOException {
		Hashtable<String, byte[]> result = new Hashtable<String, byte[]>();
		Part part;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		
		try {
			MultipartParser reqParser = new MultipartParser(req, FILE_LENGTH);
			while ((part = reqParser.readNextPart()) != null) {
				if (part.isFile())
					jvuIO.in2out(is=((FilePart) part).getInputStream(), baos=new ByteArrayOutputStream());
				result.put(part.getName(), (part.isFile()) ? baos.toByteArray() : null);
			}
		} finally {
			jvuIO.closeResource(is);
			jvuIO.closeResource(baos);
		}
		
		return result;
	}
	
	/**
	 * TODO ::: This method does not work if request has different number of param lines; very bad implementation!!!
	 * Retrieve FileName, ContentType, and file itself.
	 * Sometimes when using B2B solutions, the HttpServletRequest is available in the form of an InputStream. Since a HttpServletRequest object cannot be build
	 * from the InputStream, the data has to be parsed as a string.
	 * @param inStream
	 * @param outStream
	 * @param inParams
	 * @param outParams
	 * @param logStream
	 * @param comment
	 * @throws Exception
	 */
    public static void extract_MultipartHTTPRequest_StringData (InputStream inStream, OutputStream outStream, HashMap inParams, HashMap outParams, OutputStream logStream, String comment) throws Exception {
		try {
			Vector vFileBytes = new Vector();
			int contentLength = inStream.available();
			byte[] bytes = new byte[contentLength];
			byte[] tempByte = new byte[1];
			int paramLineCount = 4;
			int byteCount = 0;

			while (inStream.read(tempByte) > -1) {
				bytes[byteCount] = tempByte[0];
				byteCount++;
			}
			String data = new String(bytes, "ISO-8859-1");
			String boundary = data.substring(0, data.indexOf('\n') - 1);
			String[] elements = data.split(boundary + "\n");
			int i = 0;
			if (elements[i].length() > 0) {
				String[] descval = elements[i].split("\n");
				// take the first line of this element and split it by ";"
				String[] disp = descval[1].split(";");
				// if there's a filename, it's a file
				if (disp.length > 2) {
					String longFileName = disp[2].substring(disp[2].indexOf('"') + 1, disp[2].length() - 2).trim();
					outParams.put("FILENAME", longFileName);
					outParams.put("CONTENTTYPE", descval[2].substring(descval[2].indexOf(' ') + 1, descval[2].length() - 1));

					int pos = 0;
					int lineCount = 0;
					while (lineCount != paramLineCount) {
						if ((char) bytes[pos] == '\n')
							lineCount++;
						pos++;
					}
					byte[] fileBytes = new byte[bytes.length - boundary.length() - 4 - pos];
					int fileByteCount = 0;
					// grab all the bytes from the current position all the way to right befor the last boundary
					for (int k = pos; k < (bytes.length - boundary.length() - 4); k++)
						vFileBytes.add(new Byte(bytes[k]));
					
					// convert the Vector to a byte array
					fileBytes = new byte[vFileBytes.size()];
					for (int j = 0; j < fileBytes.length; j++) {
						Byte temp = (Byte) vFileBytes.get(j);
						fileBytes[j] = temp.byteValue();
					}
					outStream.write(fileBytes);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /*		CALL a servlet (from another servlet)
     * 		
     * 		URL url = new URL("http://192.168.0.117:18080/someWar/someServlet");
    		URLConnection urlConn = url.openConnection();
    		urlConn.setDoInput(true);
    		urlConn.setDoOutput(true);
    		urlConn.setUseCaches(false);
    		urlConn.setRequestProperty("Content-Type", "text/xml");
    		DataOutputStream upload = new DataOutputStream(urlConn.getOutputStream());
    		upload.writeBytes(data);
    		upload.flush();
    		upload.close();
    		
    		BufferedReader output = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
    		PrintWriter servletOutput = servletResponse.getWriter();        
    		servletOutput.print("<html><body><h1>This is the result: </h1><p />");
    		String line = null;
    		while (null != (line = output.readLine())){
    		    servletOutput.println(line);
    		}
    		output.close();
    		servletOutput.print("</body></html>");
    		servletOutput.close();
    */	
    
    /*
    	HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("URL_HERE");
		post.setParameter("Content-Type", "TYPE_HERE");
		post.setRequestEntity(new StringRequestEntity("INPUT_CONTENT_HERE"));
		
		String wsResult = null;
		try {
			wsResult = executeRequest(client, post);
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			post.releaseConnection();
		}
    */
}
