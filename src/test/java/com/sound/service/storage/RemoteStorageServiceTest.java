package com.sound.service.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sound.exception.RemoteStorageException;
import com.sound.service.storage.impl.RemoteStorageService;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RemoteStorageServiceTest extends TestCase {
	private static final String TestFileName = "test-file.txt";

	private com.sound.service.storage.itf.RemoteStorageService remoteStorageService;

	protected void setUp() throws Exception {
		super.setUp();
		remoteStorageService = new RemoteStorageService();
	}

	public void testUpload() {
		File file = new File(TestFileName);
		try {
			file.createNewFile();
			remoteStorageService.upload(file);
		} catch (RemoteStorageException e) {
			e.printStackTrace();
			fail("Upload error");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Cannot create local file");
		} finally {
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public void testListOwnedFiles() {
		try {
			List<String> files = remoteStorageService.listOwnedFiles("test");
			Assert.assertNotNull(files);
		} catch (RemoteStorageException e) {
			e.printStackTrace();
			fail("List Error");
		}
	}

	public void testDownloadToFile() {
		try {
			remoteStorageService.downloadToFile(TestFileName, TestFileName
					+ ".local");
			File local = new File(TestFileName + ".local");
			Assert.assertTrue(local.exists());
		} catch (RemoteStorageException e) {
			e.printStackTrace();
			fail("Error download to File");
		} finally {
			File f = new File(TestFileName + ".local");
			if (f.exists()) {
				f.delete();
			}
		}

	}

	public void testDownloadToMemory() {
		InputStream is = null;
		try {
			is = remoteStorageService.downloadToMemory(TestFileName);
			Assert.assertNotNull(is);
		} catch (RemoteStorageException e) {
			e.printStackTrace();
			fail("Error download to memory");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public void testDelete() {
		try {
			remoteStorageService.delete(TestFileName);
		} catch (RemoteStorageException e) {
			e.printStackTrace();
			fail("Delete error");
		}
	}

}