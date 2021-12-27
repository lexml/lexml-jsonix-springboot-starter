package br.leg.camara.lexmljsonixspringbootstarter.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
	
	private ZipUtils() {}
	
	public static byte[] readEntry(byte[] zip, String entryName) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(zip));
        ZipEntry entry = zipIn.getNextEntry();
        byte[] result = null;
        
        while (entry != null) {
        	if (entryName.equals(entry.getName())) {
        		result = readContents(zipIn);
        		zipIn.closeEntry();
        	}
        	entry = zipIn.getNextEntry();
        }
        
        return result;
	}
	
	private static byte[] readContents(InputStream zip) throws IOException {
//	    byte contents[] = new byte[4096];
//	    int direct;
//	    while ((direct = zip.read(contents, 0, contents.length)) >= 0) {
//	        System.out.println("Read " + direct + "bytes content.");
//	    }
	    
	    BufferedOutputStream bos = null;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
    		bos = new BufferedOutputStream(out);
	    	byte[] bytesIn = new byte[4096];
	    	int read = 0;
	    	while ((read = zip.read(bytesIn)) != -1) {
	    		bos.write(bytesIn, 0, read);
	    	}
    	} catch (IOException e) {
    		throw new IOException(e);
    	} finally {
    		if (bos!=null)
    			bos.close();
    	}
    	out.close();
    	return out.toByteArray();
	    
	}	
	
	public static void unzip(byte[] data, String dirName) throws IOException {
        File destDir = new File(dirName);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(data));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            String filePath = dirName + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    	BufferedOutputStream bos = null;
    	try {
	        bos = new BufferedOutputStream(new FileOutputStream(filePath));
	    	byte[] bytesIn = new byte[4096];
	    	int read = 0;
	    	while ((read = zipIn.read(bytesIn)) != -1) {
	    		bos.write(bytesIn, 0, read);
	    	}
    	} catch (IOException e) {
    		throw new IOException(e);
    	} finally {
    		if (bos!=null)
    			bos.close();    		
    	}
    }
	
}
