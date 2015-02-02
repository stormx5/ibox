package edu.csupomona.cs585.ibox.sync;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.*;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.Drive.Files.Update;



public class UnitTest {
	public GoogleDriveFileSyncManager gmgr;
	public Drive mockdrive;
	public Files mockFiles;
	public Insert mockinsert;
	public List mocklist;
	public Delete mockdelete;
	public Update mockupdate;
	public java.io.File file;
	public FileList filelist;
	ArrayList<File> arrlist;
	
	@Before
	public void setup() throws IOException
	{
		mockFiles = mock(Files.class);
		mockdrive = mock(Drive.class);
		mockinsert = mock(Insert.class);
		mocklist = mock(List.class);
		mockdelete = mock(Delete.class);
		mockupdate = mock(Update.class);
		
		file = mock(java.io.File.class);
		filelist = new FileList();
		arrlist = new ArrayList<File>();
		gmgr = new GoogleDriveFileSyncManager(mockdrive);
		
		// Add File test
		when(mockdrive.files()).thenReturn(mockFiles);
		when(mockFiles.insert(isA(File.class),isA(AbstractInputStreamContent.class))).thenReturn(mockinsert);
		when(mockinsert.execute()).thenReturn(new File());
		
		// Get File Id test
		when(file.getName()).thenReturn("MAHMOOOD");
		when(mockFiles.list()).thenReturn(mocklist);
		when(mocklist.execute()).thenReturn(filelist);
		arrlist.add(new File().setTitle(file.getName()).setId("0"));
		
		//Delete File test
		when(mockFiles.delete(isA(String.class))).thenReturn(mockdelete);
		when(mockdelete.execute()).thenReturn(null);
		
		//update File test
		when(mockFiles.update(isA(String.class), isA(File.class),isA(FileContent.class))).thenReturn(mockupdate);
		when(mockupdate.execute()).thenReturn(new File());
		
	}
	@Test
	public void addFileTest() throws IOException{

		gmgr.addFile(file);
		
		verify(mockdrive).files();
		verify(mockFiles).insert(isA(File.class),isA(AbstractInputStreamContent.class));
		verify(mockinsert).execute();
		

	}
	@Test
	public void GetFileIDTest() throws IOException {
 
		filelist.setItems(arrlist);
		
		gmgr.getFileId(file.getName());

		verify(mockdrive).files();
		verify(mockFiles).list();
		verify(mocklist).execute();

	}
	
	@Test
	public void deleteFileTest() throws IOException{
		
		filelist.setItems(arrlist);
		
		gmgr.deleteFile(file);

		verify(mockFiles).delete(isA(String.class));
		verify(mockdelete).execute();
		
	}
	
	
	@Test
	public void UpdateFileTest() throws IOException {
		filelist.setItems(arrlist);
		
		gmgr.updateFile(file);
		
		verify(mockFiles).update(isA(String.class),isA(File.class), isA(FileContent.class));
		verify(mockupdate).execute();
	}
	

}
