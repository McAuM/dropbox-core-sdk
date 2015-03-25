package com.dropbox.core.dropbox_core_sdk;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxPath;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.util.IOUtil;

public class App 
{
	public static void main(String[] args)
            throws IOException
        {
            int code = _main(args);
            System.exit(code);
        }
	public static int _main(String[] args) 
	        throws IOException 
	    {
		 
		String mode = "";
		String argAuthFile = "";
		String path1 = "";
		String path2 = "";
		DbxAuthInfo authInfo;
		DbxClient dbxClient = null;
		DbxEntry meta;
		
        if (args.length == 1 || args.length == 0){
        	if(args.length == 1){
        	String in1 = args[0];
        	}
        	//if(in1.equals("help")){
        		System.out.println("------- Dropbox Interface -------");
        		System.out.println("	  ___  ___ ___ ");
        		System.out.println("	 |   \\| _ )_ _|");
        		System.out.println("	 | |) | _ \\| | ");
        		System.out.println("	 |___/|___/___|");
        		System.out.println("Usage :");
        		System.out.println(" -command <need> [option] {detail}");
        		System.out.println(" -help		[-e] [-c] {show more information -e Example,-c Credit}");
        		System.out.println(" -account	<AuthFile> {show account information}");
        		System.out.println(" -account2	<AuthFile> {show account information and space}");
        		System.out.println(" -space		<AuthFile> {show space information}");
        		System.out.println(" -spaceper	<AuthFile> {show space in percent}");
        		System.out.println(" -authorize	<KeyFileIn> <AuthFileOut> {authorize Dropbox account}");
        		System.out.println(" -listing	<AuthFile> <path1> {show all file/folder in path1}");
        		System.out.println(" -delete	<AuthFile> <path1> {delete file/folder in path1}");
        		System.out.println(" -newfolder	<AuthFile> <path1> {create folder in path1}");
        		System.out.println(" -metadata	<AuthFile> <path1> {show information of file or folder}");
        		System.out.println(" -upload	<AuthFile> <path1> <path2> {upload file from path1 to path2}");
        		System.out.println(" -download	<AuthFile> <path1> <path2> {download file from path2 to path1}");
        		System.out.println(" -copy		<AuthFile> <path1> <path2> {copy file/folder from path1 to path2}");
        		System.out.println(" -move		<AuthFile> <path1> <path2> {move file/folder from path1 to path2}");
        	//}
        	return 0;
        }
        
        // read and verify autorize file
        if ((args.length >= 2)&&(!args[0].equals("help"))&&(!args[0].equals("authorize"))){
        	mode = args[0];
        	argAuthFile = args[1]; 
        	// Read auth info file.
            try {
                authInfo = DbxAuthInfo.Reader.readFromFile(argAuthFile);
            }
            catch (JsonReader.FileLoadException ex) {
                System.out.println("Error loading <auth-file>: " + ex.getMessage());
                return 1;
            }
            
            // Create a DbxClient, which is what you use to make API calls.
            String userLocale = Locale.getDefault().toString();
            DbxRequestConfig requestConfig = new DbxRequestConfig("pcs-cpebox", userLocale);
            dbxClient = new DbxClient(requestConfig, authInfo.accessToken, authInfo.host);
        }
        
        if ((args.length == 2)&&(!args[0].equals("help"))&&(!args[0].equals("authorize"))){
        	mode = args[0];
        	argAuthFile = args[1];
        	if(mode.equals("account")){
        		DbxAccountInfo dbxAccountInfo;
                try {
                    dbxAccountInfo = dbxClient.getAccountInfo();
                }
                catch (DbxException ex) {
                    System.out.println("Error in getAccountInfo(): " + ex.getMessage());
                    //ex.printStackTrace();
                    return 1;
                }
                //System.out.print("User's account info: " + dbxAccountInfo.toStringMultiline());
                System.out.println("user account info:");
                System.out.println("user id = "+dbxAccountInfo.userId);
                System.out.println("display name = "+dbxAccountInfo.displayName);
                System.out.println("country = "+dbxAccountInfo.country);
                System.out.println("referral link = "+dbxAccountInfo.referralLink);
                return 0;
        	}
        	
        	if(mode.equals("account2")){
        		DbxAccountInfo dbxAccountInfo;
                try {
                    dbxAccountInfo = dbxClient.getAccountInfo();
                }
                catch (DbxException ex) {
                    System.out.println("Error in getAccountInfo(): " + ex.getMessage());
                    //ex.printStackTrace();
                    return 1;
                }
                //System.out.print("User's account info: " + dbxAccountInfo.toStringMultiline());
                System.out.println("User account info:");
                System.out.println("user id = "+dbxAccountInfo.userId);
                System.out.println("display name = "+dbxAccountInfo.displayName);
                System.out.println("country = "+dbxAccountInfo.country);
                System.out.println("referral link = "+dbxAccountInfo.referralLink);
                double total = spacereadable(dbxAccountInfo.quota.total,"gb");
                double used = spacereadable(dbxAccountInfo.quota.normal+dbxAccountInfo.quota.shared,"gb");
                double free = total-used;
                System.out.println("");
                System.out.println("User space:");
                System.out.println("total = "+total+" gb");
                System.out.println("used = "+used+" gb");
                System.out.println("free = "+free+" gb");
                return 0;
        	}
        	
        	if(mode.equals("space")){
        		DbxAccountInfo dbxAccountInfo;
                try {
                    dbxAccountInfo = dbxClient.getAccountInfo();
                }
                catch (DbxException ex) {
                    //System.out.println("Error in getSpaceInfo(): " + ex.getMessage());
                    //ex.printStackTrace();
                    return 1;
                }
                System.out.println("space of : "+dbxAccountInfo.displayName);
                double totalmb = spacereadable(dbxAccountInfo.quota.total,"mb");
                double total = spacereadable(dbxAccountInfo.quota.total,"gb");
                double usedmb = spacereadable(dbxAccountInfo.quota.normal+dbxAccountInfo.quota.shared,"mb");
                double used = spacereadable(dbxAccountInfo.quota.normal+dbxAccountInfo.quota.shared,"gb");
                double freemb = totalmb-usedmb;
                double free = total-used;
                
                System.out.println("total = "+(int)total+" gb or "+(int)totalmb+" mb");
                System.out.println("used = "+(int)used+" gb or "+(int)usedmb+" mb");
                System.out.println("free = "+(int)free+" gb or "+(int)freemb+" mb");
                return 0;
        	}
        	
        	if(mode.equals("spaceper")){
        		DbxAccountInfo dbxAccountInfo;
                try {
                    dbxAccountInfo = dbxClient.getAccountInfo();
                }
                catch (DbxException ex) {
                    System.out.println("Error in getSpaceInfo(): " + ex.getMessage());
                    //ex.printStackTrace();
                    return 1;
                }
              
                double total = spacereadable(dbxAccountInfo.quota.total,"gb");
                double used = spacereadable(dbxAccountInfo.quota.normal+dbxAccountInfo.quota.shared,"gb");
                double free = total-used;
                double percents = used/total*100;
                int percentsint= (int)percents;
                System.out.println(percentsint);
                return 0;
        	}
        	
        	if(mode.equals("spaceuse")){ //return space use percent
        		DbxAccountInfo dbxAccountInfo;
                try {
                    dbxAccountInfo = dbxClient.getAccountInfo();
                }
                catch (DbxException ex) {
                    System.out.println("Error in getSpaceInfo(): " + ex.getMessage());
                    //ex.printStackTrace();
                    return 1;
                }               
                
                double total = spacereadable(dbxAccountInfo.quota.total,"mb");
                double used = spacereadable(dbxAccountInfo.quota.normal+dbxAccountInfo.quota.shared,"mb");
                double free = (used / total) *100;
                int per = (int) free ;
                System.out.println(per);

                return 0;
        	}
        }
        if ((args.length == 2)&&(args[0].equals("help"))){
	        if ((args.length == 2)&&(args[0].equals("help"))&&((args[1].equals("-e"))||(args[1].equals("-c")))){
	        	if(args[1].equals("-e")){
	        		System.out.println("exmaple ");
	        		System.out.println("local path ex. \"C:\\Program Files\"");
	        		System.out.println("dropbox path start with \"/\" ex. /home/music");
	        		
	        	}else{
	        		System.out.println("private cloud storage - cpebox");
	        		System.out.println("connect dropbox to archive");
	        	}
	        	return 0;
	        }else{
	        	System.out.println("please use command help for more information");
	        	return 1;
	        }
        }
	

        if (args.length == 3){

        	if(args[0].equals("authorize")){
       		String argAppInfoFile = args[1];
       	    String argAuthFileOutput = args[2];
       	    Boolean checka=false;
	       	     if(checka){
	       	    	 System.out.println("permission denied");
	       	     }else{
	       	    	System.out.println("in else");
	       	        // Read app info file (contains app key and app secret)
	       	        DbxAppInfo appInfo;
	       	        try {
	       	            appInfo = DbxAppInfo.Reader.readFromFile(argAppInfoFile);
	       	            //pattern
	       	            //{
	       	            //"key": "xxxxxxxxxxxxx",
	       	            //"secret": "yyyyyyyyyyyyyy"
	       	            //}
	       	        }
	       	        catch (JsonReader.FileLoadException ex) {
	       	            System.out.println("Error reading <app-info-file>: " + ex.getMessage());
	       	            System.exit(1); return 1;
	       	        }
	
	       	        // Run through Dropbox API authorization process
	       	        String userLocale = Locale.getDefault().toString();
	       	        DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize", userLocale);
	       	        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);
	
	       	        String authorizeUrl = webAuth.start();
	       	        System.out.println("1. Go to " + authorizeUrl);
	       	        System.out.println("2. Click \"Allow\" (you might have to log in first).");
	       	        System.out.println("3. Copy the authorization code.");
	       	        System.out.print("Enter the authorization code here: ");
	
	       	        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
	       	        if (code == null) {
	       	            System.exit(1); 
	       	            return 1;
	       	        }
	       	        code = code.trim();
	
	       	        DbxAuthFinish authFinish;
	       	        try {
	       	            authFinish = webAuth.finish(code);
	       	        }
	       	        catch (DbxException ex) {
	       	            System.out.println("Error in DbxWebAuth.start: " + ex.getMessage());
	       	            System.exit(1); 
	       	            return 1;
	       	        }
	
	       	        System.out.println("Authorization complete.");
	       	        System.out.println("- User ID: " + authFinish.userId);
	       	        System.out.println("- Access Token: " + authFinish.accessToken);
	
	       	        // Save auth information to output file.
	       	        DbxAuthInfo authInfoI = new DbxAuthInfo(authFinish.accessToken, appInfo.host);
	       	        try {
	       	            DbxAuthInfo.Writer.writeToFile(authInfoI, argAuthFileOutput);
	       	            System.out.println("Saved authorization information to \"" + argAuthFileOutput + "\".");
	       	        }
	       	        catch (IOException ex) {
	       	            System.out.println("Error saving to <auth-file-out>: " + ex.getMessage());
	       	            System.out.println("Dumping to stderr instead:");
	       	            DbxAuthInfo.Writer.writeToStream(authInfoI, System.out);
	       	            System.exit(1); 
	       	            return 1;
	       	        }
	       	     }
        	}
        	
        	if(!args[0].equals("authorize")){
	        	mode = args[0];
	        	argAuthFile = args[1]; 
	        	path1 = args[2];
	        	
	            String pathError = DbxPath.findError(path1);
	            if (pathError != null) {
	                System.out.println("Invalid <dropbox-path>: " + pathError);
	                return 1;
	            }
        	}
            
        	if(mode.equals("listing")){
        		try {
					meta = dbxClient.getMetadata(path1);
				} catch (DbxException ex) {
        			System.out.println("Error : " + ex.getMessage());
        			return 1;
				}
        		if(meta==null){
        			System.out.println("not found file or folder in "+path1);
        		}else{				
	        		DbxEntry.WithChildren listing;
	        		try {
	        			listing = dbxClient.getMetadataWithChildren(path1);
	        			System.out.println("Files in the root path:");
	        			for (DbxEntry child : listing.children) {
	        			    System.out.println("	" + child.name + ": " );
	        			    //System.out.println("	" + child.name + ": " + child.path+" ");
	        			}
	        		} catch (DbxException ex) {
	        			System.out.println("Error in listing(): " + ex.getMessage());
	        			return 1;
	        		}
        		}
        		System.out.println("listing complete");
        		return 0;
        	}
        	
        	if(mode.equals("delete")){
        		try {
					dbxClient.delete(path1);
				} catch (DbxException ex) {
        			System.out.println("Error in delete(): " + ex.getMessage());
        			return 1;
				}
        		System.out.println("delete "+path1+" complete");
        		return 0;
        	}
        	
        	if(mode.equals("newfolder")){
        		DbxEntry.Folder creater1;
        		try {
					creater1 = dbxClient.createFolder(path1);
				} catch (DbxException ex) {
        			System.out.println("Error in listing(): " + ex.getMessage());
        			//ex.printStackTrace();
        			return 1;
				}
        		System.out.println("create folder "+creater1.path+" complete ");
        		System.out.print(creater1.toStringMultiline());
        		return 0;
        	}
        	
        	if(mode.equals("metadata")){
        		try {
					meta = dbxClient.getMetadata(path1);
				} catch (DbxException ex) {
        			System.out.println("Error in listing(): " + ex.getMessage());
        			//ex.printStackTrace();
        			return 1;
				}
        		if(meta==null){
        			System.out.println("not found file or folder in "+path1);
        		}else{
        			System.out.print("metadata : "+meta.toStringMultiline());
        		}
        		return 0;
        	}
        }
        
        if (args.length == 4){
        	//System.out.println("in2==4");
        	mode = args[0];
        	argAuthFile = args[1]; 
        	path1 = args[2];
        	path2 = args[3];
        	
            //check dropbox path
            String pathError = DbxPath.findError(path2);
            if (pathError != null) {
                System.out.println("Invalid <dropbox-path>: " + pathError);
                return 1;
            }
            
        	if(mode.equals("upload")){
                // Make the API call to upload the file.
                DbxEntry.File metadata;
                try {
                    InputStream in = new FileInputStream(path1);
                    try {
                        metadata = dbxClient.uploadFile(path2, DbxWriteMode.force(), -1, in);
                    } catch (DbxException ex) {
                        System.out.println("Error uploading : " + ex.getMessage());
                        return 1;
                    } finally {
                        IOUtil.closeInput(in);
                    }
                }
                catch (IOException ex) {
                    System.out.println("Eror reading from file \"" + path1 + "\": " + ex.getMessage());
                    return 1;
                }                
                //System.out.print(metadata.toStringMultiline());
                System.out.println("upload complete to "+path2);
                return 0;
        	}
        	
        	if(mode.equals("download")){
        		//System.out.print("in4download");
                FileOutputStream outputStream = new FileOutputStream(path1);
                DbxEntry.File downloadedFile;
                try {
        			try {
        				downloadedFile = dbxClient.getFile(path2, null,outputStream);
        			} catch (DbxException ex) {
            			System.out.println("Error downloading file : " + ex.getMessage());
            			//ex.printStackTrace();
            			return 1;
        			}
                } finally {
                    outputStream.close();
                }
                //System.out.print("Metadata: " + downloadedFile.toStringMultiline());
                System.out.println("download complete to "+path1);
                return 0;
        	}
        	
        	if(mode.equals("copy")){
        		DbxEntry copy1;
        		try {
					dbxClient.copy(path1,path2);
				} catch (DbxException ex) {
        			System.out.println("Error in copy(): " + ex.getMessage());
        			return 1;
				}
        		System.out.println("copy file or folder complete");
        		System.out.println("from "+path1+" to "+path2);
        		return 0;
        	}
        	
        	if(mode.equals("move")){
        		try {
					dbxClient.move(path1,path2);
				} catch (DbxException ex) {
        			System.out.println("Error in move(): " + ex.getMessage());
        			return 1;
				}
        		System.out.println("move file or folder complete");
        		System.out.println("from "+path1+" to "+path2);
        		return 0;
        	}
        	
        	
        	return 0;
        }
		return 1;
	}
	private static double spacereadable(long size,String unit){
		if(unit.equals("b")||unit.equals("B")){
			return size;
		}
		if(unit.equals("kb")||unit.equals("Kb")||unit.equals("kB")||unit.equals("KB")){
			return size/1024;
		}
		if(unit.equals("mb")||unit.equals("Mb")||unit.equals("mB")||unit.equals("MB")){
			return size/(1024*1024);
		}
		if(unit.equals("gb")||unit.equals("Gb")||unit.equals("gB")||unit.equals("GB")){
			return size/(1024*1024*1024);
		}
		return 0;
	}
	private int checkpath(String path){
        String pathError = DbxPath.findError(path);
        if (pathError != null) {
            System.out.println("Invalid <dropbox-path>: " + pathError);
            return 1;
        }else{
        	return 0;
        }
	}
	private static void printAuth(PrintStream out)
    {
        out.println("Usage: COMMAND <app-info-file> <auth-file-output>");
        out.println("");
        out.println("<app-info-file>: a JSON file with information about your API app.  Example:");
        out.println("");
        out.println("  {");
        out.println("    \"key\": \"Your Dropbox API app key...\",");
        out.println("    \"secret\": \"Your Dropbox API app secret...\"");
        out.println("  }");
        out.println("");
        out.println("  Get an API app key by registering with Dropbox:");
        out.println("    https://dropbox.com/developers/apps");
        out.println("");
        out.println("<auth-file-output>: If authorization is successful, the resulting API");
        out.println("  access token will be saved to this file, which can then be used with");
        out.println("  other example programs, such as the one in \"examples/account-info\".");
        out.println("");
    }
}
