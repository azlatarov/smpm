package custom.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;


public final class jvuArchiving {
	
	private jvuArchiving () {}
	
	/**
	 * Zip a single file.
	 * @param	fileName - name will be given to file inside the zip
	 * @param	fileBytes
	 * @author 	Atanas P. Zlatarov
	 */
	public static byte[] zipFile (String fName, byte[] fBytes) throws CustomUtilsException {
		Map<String, byte[]> fileToZip = new HashMap<String, byte[]> ();
		fileToZip.put(fName, fBytes);
		return zipFilesTogether(fileToZip);
	}
	
	/**
	 * Zip multiple files 1 by 1 i.e. separately.
	 * Zip name = [original file name] + ".zip". If original file name is missing, one will be generated.
	 * @param	list of files - Map<String, byte[]>
	 * @return	list of zip files - Map<String, byte[]>
	 * @author 	Atanas P. Zlatarov
	 */
	public static Map<String, byte[]> zipFilesSeparately (Map<String, byte[]> inFiles) throws CustomUtilsException {
		if (inFiles == null || inFiles.size() == 0) return inFiles;
		
		Map<String, byte[]> zippedFiles = new HashMap<String, byte[]> ();
		for (String fName : inFiles.keySet()) {
			if (fName==null || "".equals(fName)) fName = autogenFileName();
			
			zippedFiles.put(fName+".zip", zipFile(fName, inFiles.get(fName)));
		}
		
		return zippedFiles;
	}
	
	/**
     * Zip multiple files in a single file. If original file name is missing, one will be generated.
     * No CRC32 check-sum is calculated.
     * @param	files to zip - Map<String, byte[]>
	 * @author 	Atanas P. Zlatarov
	 */
	public static byte[] zipFilesTogether (Map<String, byte[]> inFiles) throws CustomUtilsException {
		if (inFiles == null || inFiles.isEmpty()) return null;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ZipOutputStream zout = new ZipOutputStream(out);
	    String fName = null;
		byte[] fBytes = null;
		
	    Entry<String, byte[]> e = null;
	    Iterator<Entry<String, byte[]>> it = inFiles.entrySet().iterator();
	    try {
		    while (it.hasNext()) {// TODO : JSE 1.5 map iteration
		        e = it.next();
		        
		        fName = e.getKey();
		        if (fName == null || "".equals(fName)) fName = autogenFileName();
		        
		        fBytes = e.getValue();
				if (fBytes == null || fBytes.length == 0) continue;
				
				zout.putNextEntry(new ZipEntry(fName));
				zout.write(fBytes);
				zout.closeEntry();
			}
			zout.finish();
			zout.close();
		}  catch (IOException ioe) {
			throw new CustomUtilsException("Exception during method execution.", ioe);
		} finally {
			try {
				zout.close();
			} catch (IOException ioe2) {
				throw new CustomUtilsException("Exception while releasing method resources.", ioe2);
			}
		}
		
		return out.toByteArray();
	}
	
	/**
     * Tar multiple files in a single file. If original file name is missing, one will be generated.
     * @param	files to tar - Map<String, byte[]>
	 * @author 	Atanas P. Zlatarov
	 * @jar		commons-compress-1.1.jar (download from my web site)
	 */
	public static byte[] tarFilesTogether (Map<String, byte[]> inFiles) throws CustomUtilsException {
		if (inFiles == null || inFiles.isEmpty()) return null;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TarArchiveOutputStream taos = new TarArchiveOutputStream(out);
		String fName = null;
		byte[] fBytes = null;
		
		Entry<String, byte[]> e = null;
		Iterator<Entry<String, byte[]>> it = inFiles.entrySet().iterator();
		try {
		    while (it.hasNext()) {// TODO : JSE 1.5 map iteration
		        e = it.next();
		        
		        fName = e.getKey();
		        if (fName == null || "".equals(fName)) fName = autogenFileName();
		        
		        fBytes = e.getValue();
		        if (fBytes == null || fBytes.length == 0) continue;
				
				TarArchiveEntry tarEntry = new TarArchiveEntry(fName);
				tarEntry.setSize(fBytes.length);
				
				taos.putArchiveEntry(tarEntry);
				taos.write(fBytes, 0, fBytes.length);
				taos.closeArchiveEntry();
			}
		    taos.finish();
		    taos.close(); // !!!IMPORTANT: close immediately after flush, or during untar will have 'unexpected EOF' problem, and some files will not be found!!!
		}  catch (IOException ioe) {
			throw new CustomUtilsException("Exception during method execution.", ioe);
		} finally {
			try {
				taos.close();
			} catch (IOException ioe2) {
				throw new CustomUtilsException("Exception while releasing method resources.", ioe2);
			}
		}
		
		return out.toByteArray();
	}
	
	/**
	 * GZIP a file.
	 * @param	file bytes - byte[]
	 * @author	Atanas P. Zlatarov
	 */
	public static byte[] gzipFile (byte[] fileBytes) throws CustomUtilsException {
		if (fileBytes == null || fileBytes.length == 0) return fileBytes;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = null;
		try {
			gzos = new GZIPOutputStream(baos);
			// TODO: why no fName specified?
			gzos.write(fileBytes, 0, fileBytes.length);
			gzos.finish();
			gzos.close();
		}  catch (IOException ioe) {
			throw new CustomUtilsException("Exception during method execution.", ioe);
		} finally {
			try {
				gzos.close();
			} catch (IOException ioe2) {
				throw new CustomUtilsException("Exception while releasing method resources.", ioe2);
			}
		}
		
		return baos.toByteArray();
	}
	
	/**
	 * Unzip a file.
	 * @param	
	 * @author 	Atanas P. Zlatarov
	 */
	public static Map<String, byte[]> unzipFile (byte[] zipBytes) throws CustomUtilsException {
		if (zipBytes == null || zipBytes.length == 0) return null;
		
		Map<String, byte[]> out = new Hashtable<String, byte[]>();
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes));
		
		ByteArrayOutputStream baos = null;
		try {
			ZipEntry e = zis.getNextEntry();
			while (e != null) {//while ( (e = zis.getNextEntry()) != null ) { TODO
				baos = new ByteArrayOutputStream();
				jvuIO.in2out(zis, baos);
				baos.flush();
				
				out.put(e.getName(), baos.toByteArray());
				
				e = zis.getNextEntry();
			}
		} catch (IOException ioe) {
			throw new CustomUtilsException("Exception during method execution.", ioe);
		} finally {
			try {
				zis.close();
			} catch (IOException ioe2) {
				throw new CustomUtilsException("Exception while releasing method resources.", ioe2);
			}
		}
				
		return out;
	}
	
	public static String autogenFileName () {
		return "autogen_"+Long.toString(System.currentTimeMillis())+"_"+(Math.random()*1000);
	}
	
	/**
	 * Return true if the provided byte array is a JAR file with at least one entry in it.
	 * @throws IOException 
	 * @throws CustomUtilsException 
	 */
	public static boolean checkJarValidity (byte[] jarFile) throws CustomUtilsException {
		boolean isJar = false;
		JarInputStream jis = null;
		try {
			if (jarFile == null || jarFile.length == 0) return isJar;
			
			jis = new JarInputStream(new ByteArrayInputStream(jarFile));		
			if (jis.getNextJarEntry() != null) isJar = true;
		} catch (IOException ioe) {
			throw new CustomUtilsException("Exception during method execution.", ioe);
		} finally {
			try {
				jis.close();
			} catch (IOException ioe2) {
				throw new CustomUtilsException("Exception while releasing method resources.", ioe2);
			}
		}
		
		return isJar;
	}
	
	
	
}
