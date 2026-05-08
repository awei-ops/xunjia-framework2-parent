package com.xunjia.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final int CACHE_SIZE = 2048;

	public static void zip(String sourceFolder, String zipFilePath) throws Exception {
        OutputStream out = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;
        try {
        	 out = new FileOutputStream(zipFilePath);
             bos = new BufferedOutputStream(out);
             zos = new ZipOutputStream(bos);
        	
        	File file = new File(sourceFolder);
            String basePath = null;
            if (file.isDirectory()) {
                basePath = file.getPath();
            } else {
                basePath = file.getParent();
            }
            zipFile(file, basePath, zos);
        } catch (Exception e) {
        	throw e;
        } finally {
        	zos.closeEntry();
            zos.close();
            bos.close();
            out.close();
        }
    }
	
	public static void unZip(String zipFilePath, String destDir) throws Exception {
		ZipFile zipFile = new ZipFile(zipFilePath);
		Enumeration<?> emu = zipFile.entries();
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		byte[] cache = new byte[CACHE_SIZE];
		
		try {
			while (emu.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) emu.nextElement();
				if (entry.isDirectory()) {
					new File(destDir + entry.getName()).mkdirs();
					continue;
				}
				bis = new BufferedInputStream(zipFile.getInputStream(entry));
				File file = new File(destDir + entry.getName());
				File parentFile = file.getParentFile();
				if (parentFile != null && (!parentFile.exists())) {
					parentFile.mkdirs();
				}
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos, CACHE_SIZE);
				int nRead = 0;
				while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
					fos.write(cache, 0, nRead);
				}
				bos.flush();
				bos.close();
				fos.close();
				bis.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {

			zipFile.close();
		}
	}
	
	private static void zipFile(File parentFile, String basePath, ZipOutputStream zos) throws Exception {
        File[] files = new File[0];
        if (parentFile.isDirectory()) {
            files = parentFile.listFiles();
        } else {
            files = new File[1];
            files[0] = parentFile;
        }
        String pathName = null;
        InputStream is = null;
        BufferedInputStream bis = null;

        byte[] cache = new byte[CACHE_SIZE];
        try {
        	for (File file : files) {
                if (file.isDirectory()) {
                    pathName = file.getPath().substring(basePath.length() + 1) + "/";
                    zos.putNextEntry(new ZipEntry(pathName));
                    zipFile(file, basePath, zos);
                } else {
                    pathName = file.getPath().substring(basePath.length() + 1);
                    is = new FileInputStream(file);
                    bis = new BufferedInputStream(is);
                    zos.putNextEntry(new ZipEntry(pathName));
                    int nRead = 0;
                    while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                        zos.write(cache, 0, nRead);
                    }
                }
            }
        } catch (Exception e) {
        	throw e;
        } finally {
            try {
            	if (bis != null) {
            		bis.close();
            	}
            	if (is != null) {
            		is.close();
            	}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}
