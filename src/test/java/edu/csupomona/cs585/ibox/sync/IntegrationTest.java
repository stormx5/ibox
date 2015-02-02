package edu.csupomona.cs585.ibox.sync;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.FileList;

import edu.csupomona.cs585.ibox.WatchDir;

public class IntegrationTest {
	GoogleDriveFileSyncManager gmgr;
	public Drive drive;
	public String root_path;
	public Drive googleDriveClient;
	public WatchDir watchdir;
	public FileSyncManager fsm;
	
	
	public void initGoogleDriveServices() throws IOException {
	       HttpTransport httpTransport = new NetHttpTransport();
	       JsonFactory jsonFactory = new JacksonFactory();

	       try{
	           GoogleCredential credential = new  GoogleCredential.Builder()
	             .setTransport(httpTransport)
	             .setJsonFactory(jsonFactory)
	             .setServiceAccountId("445239205599-41drhd999fr29f1vu6d1deg6bs8a6lb4@developer.gserviceaccount.com")
	             .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
	             .setServiceAccountPrivateKeyFromP12File(new File("src/My Project-e96d96c45dba.p12"))
	             .build();

	           googleDriveClient = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("ibox").build();  
	       }catch(GeneralSecurityException e){
	           e.printStackTrace();
	       }

	   }
	
	
	@Before
	public void Setup() throws IOException{
		initGoogleDriveServices();
		gmgr = new GoogleDriveFileSyncManager(drive);
		root_path = System.getProperty("user.dir");

	}
	@Test
	public void AddFileTest() throws IOException, InterruptedException {

		File local_file_add = new File(root_path+"/TestDir/AddFileIntTest.txt");
		//new Watch(watchdir).start();
		if(!local_file_add.exists())
		{
			local_file_add.createNewFile();
		}
		// check if the file that was placed in the watched folder is uploaded or not
		// if it is then the method will return true. If not it will return false which means the 
		// file was not uploaded
		Thread.sleep(1000); // To give the App a chance to upload the files
		boolean Exists = GetFilesFromDrive(local_file_add.getName());
		Assert.assertTrue(Exists);
	}
	
	@Test(expected = Exception.class)
	public void DeleteFileTest() throws IOException, InterruptedException {

		Thread thread1 = new Thread () {
			  public void run () {

				  try
				  {
					  File local_file_Delete = new File(root_path+"/TestDir/DeleteFileIntTest.txt");
						if(!local_file_Delete.exists())
						{
							local_file_Delete.createNewFile();
						}
						local_file_Delete.delete();
						Thread.sleep(5000);
				  }
				  catch(Exception e)
				  {
					  e.printStackTrace();
				  }
				  
			  }
			};
			thread1.start();
			// Expected an Exception because If GetFileID called without the file being there it will throw
			// an Exception. There fore the assertion is correct. 
			
		Assert.assertNull(gmgr.getFileId("DeleteFileIntTest.txt"));
	}
	

	@Test
	public void UpdateFileTest() throws IOException, InterruptedException {
		
		Thread thread2 = new Thread () {
			
		  public void run () {
		    try
		    {
		    	File local_file_Update = new File(root_path+"/TestDir/UpdateFileIntTest.txt");
		    	if(!local_file_Update.exists())
		    	{
		    		local_file_Update.createNewFile();
		    	}
		    	Thread.sleep(3000);
				FileWriter fw = new FileWriter(local_file_Update);
				fw.write("Update File Test - Integration Test");
				fw.close();
				
				System.out.println(local_file_Update.getName());
				String fileid = gmgr.getFileId(local_file_Update.getName());
				Assert.assertNotNull(fileid);
		    }
		    catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
		  }
		};thread2.start();
		
//		if(!local_file.exists())
//		{
//			local_file.createNewFile();
//		}
//		File local_file2 = new File(root_path+"/TestDir/IntegTest.txt");
//		local_file.renameTo(local_file2);
//		String str = GetFileName(local_file2);
//		Assert.assertEquals(str, local_file2.getName());
	}
	
	
	@Test
	public void GetFileIDTest() throws IOException, InterruptedException {
		Thread thread3 = new Thread(){
			public void run() {
				try
				{
					File local_fileID = new File(root_path+"/TestDir/FileIDIntTest.txt");
					String FileID = "";
					if(!local_fileID.exists())
					{
						local_fileID.createNewFile();
					}
					Thread.sleep(1000);
					FileID = gmgr.getFileId(local_fileID.getName());
					Assert.assertNotNull(FileID);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		thread3.start();
		
		
	}

	public String GetFileName(File localf) throws IOException
	{
		String name= "";
		
		List request = googleDriveClient.files().list();
		FileList files = request.execute();
		for(com.google.api.services.drive.model.File file : files.getItems())
		{
			
			if(file.getTitle().equals(localf.getName()));
			{
				name = file.getTitle();
			}
		}
		return name;
	}
	public boolean GetFilesFromDrive(String Filename) throws IOException
	{
		boolean Exists = false;
		List request = googleDriveClient.files().list();
		FileList files = request.execute();
		for(com.google.api.services.drive.model.File file : files.getItems())
		{
			if(Filename.equals(file.getTitle()));
			{
				Exists = true;
			}
		}
		return Exists;
	}
}
