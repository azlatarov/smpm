package custom.utils;

import java.util.Collection;
import java.util.Iterator;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;

/**
 * @jar svnkit.jar
 * @web http://svnkit.com/index.html
 * @api http://jackrabbit.apache.org/api/2.2/ -- Apache Jackrabbit API
 * @api http://www.day.com/maven/jsr170/javadocs/jcr-1.0/index.html -- Content Repository for Java API
 */

public final class jvuRepository {
	
	private jvuRepository() {}
	
	public static void main (String [] str) {
		ISVNAuthenticationManager authManager;

		try {
			DAVRepositoryFactory.setup(); // http or https
			//SVNRepositoryFactoryImpl.setup(); // svn or svn+xxx (svn+ssh)
			//FSRepositoryFactory.setup(); // file 
			
			// Init. repository to work with
			String url = "https://bprocess-scm:1443/svn/BPSPROD";
			String user = "azlatarov";
			String pass = "bps0987";
			SVNRepository repository = null;
			
			try {
				repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url), null);
			} catch (SVNException svne) {
				svne.printStackTrace();
			}
			
			// Init. authentication manager based on protocol of use
			authManager = new BasicAuthenticationManager(user, pass); // use only user-pass
			//authManager = SVNWCUtil.createDefaultAuthenticationManager("login", "pass" ...); 
			repository.setAuthenticationManager(authManager);
			
			
            SVNNodeKind nodeKind = repository.checkPath("", -1);
            if (nodeKind == SVNNodeKind.NONE) {
                System.err.println("There is no entry at '" + url + "'.");
                System.exit(1);
            } else if (nodeKind == SVNNodeKind.FILE) {
                System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
                System.exit(1);
            }
            
            System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
            System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));
            System.out.println("");
 
            //listEntries(repository, "");
            
            
            //byte [] oldData = "some text".getBytes();
    		byte [] newData = "some updated text".getBytes();
            
            
            // ADD NEW FILE          
            ISVNEditor editor = repository.getCommitEditor("some comment here", null);
            editor.openRoot(-1);
    		editor.openDir("exploitation", -1);
    		editor.openDir("exploitation/oldMappings", -1);
    		editor.addFile("exploitation/oldMappings/blablabla.txt", null, -1);
    		
    		editor.applyTextDelta("exploitation/oldMappings/blablabla.txt", null); // baseChecksum is null - there's no file yet
    		    		
    		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator( );
    		deltaGenerator.sendDelta("exploitation/oldMappings/blablabla.txt", newData, newData.length, editor);
    		    		
    		editor.closeFile("exploitation/oldMappings/blablabla.txt", null);
    		editor.closeDir(); // close 'oldMappings' directory
    		editor.closeDir(); // close 'exploitation' directory
    		
    		
    		// UPDATE EXISTING FILE
    		
    		
    		//SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator( );
    		//String checksum = deltaGenerator.sendDelta("/exploitation/oldMappings/blablabla.txt", new ByteArrayInputStream(oldData), 0, new ByteArrayInputStream( newData ), editor , true);
    		
    		
    		
    		
    		editor.closeDir(); // close root directory
    		editor.closeEdit();
            
    		
    		
    		
    		
    		
    		
            long latestRevision = -1;
            latestRevision = repository.getLatestRevision();
            System.out.println("");
	        System.out.println("---------------------------------------------");
	        System.out.println("Repository latest revision: " + latestRevision);
	        System.exit(0);
			
		} catch (SVNException svne) {
            SVNErrorMessage err = svne.getErrorMessage();
            System.err.println(err.getFullMessage());
            System.exit(1);
            
		}
		
	}
	
    public static void listEntries(SVNRepository repository, String path) throws SVNException {
		
		Collection entries = repository.getDir(path, -1, null, (Collection) null);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
		    SVNDirEntry entry = (SVNDirEntry) iterator.next();
		    System.out.println("/" + (path.equals("") ? "" : path + "/")
		            + entry.getName() + " (author: '" + entry.getAuthor()
		            + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
		    /*
		     * Checking up if the entry is a directory.
		     */
		    if (entry.getKind() == SVNNodeKind.DIR) {
		        listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
		    }
		    
		}
		
	}

}
