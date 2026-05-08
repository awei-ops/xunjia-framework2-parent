package com.xunjia.framework.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author 姜浩
 * 文件操作工具类
 */
public class FileUtils {

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名，包含“.”
     */
    public static String getExtendName(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    /**
     * 生成唯一文件名
     *
     * @param extendName 扩展名
     * @return 唯一文件名
     */
    public static String getUniqueFileName(String extendName) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + extendName;
    }

    /**
     * 将上传的文件复制到本地文件夹
     *
     * @param file 用户上传的文件对象
     * @param saveDir 存储文件夹的相对路径
     * @return 文件名
     */
    public static String copyFile(MultipartFile file, String saveDir) {
        String originalFileName = file.getOriginalFilename();
        String extendName = "";
        if (originalFileName.lastIndexOf('.') != -1) {
            extendName = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }
        String uniqueFileName = getUniqueFileName(extendName);

        File realSaveDir = new File(saveDir);
        if (!realSaveDir.exists()) {
            realSaveDir.mkdirs();
        }
        File realSaveFile = new File(realSaveDir + "/" + uniqueFileName);

        InputStream fis = null;
        FileOutputStream fos = null;
        byte[] buff = new byte[4096];
        int len = -1;
        try {
            fos = new FileOutputStream(realSaveFile);
            fis = file.getInputStream();
            while ((len = fis.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e1) {
            }
        }
        return uniqueFileName;
    }

    public static void copyFile(String saveDir, String fileName, InputStream is) {
        File realSaveDir = new File(saveDir);
        if (!realSaveDir.exists()) {
            realSaveDir.mkdirs();
        }
        File file = new File(realSaveDir + "/" + fileName);
        if (file.exists()) {
            file.delete();
        }

        OutputStream os = null;
        byte[] buff = new byte[4096];
        int len = 0;
        try {
            os = new FileOutputStream(file);
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param path 文件存储相对路径
     * @return 操作是否成功
     */
    public static boolean deleteFile(String path) {
        boolean result = false;
        File file = new File(path);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    /**
     * 下载文件
     *
     * @param response         响应对象
     * @param path             文件存储相对路径
     * @param originalFileName 原文件名
     */
    public static void downloadFile(HttpServletResponse response, String path, String originalFileName) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        FileInputStream fis = null;
        ServletOutputStream sos = null;
        byte[] buff = new byte[4096];
        int len = 0;
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(originalFileName.getBytes("utf-8"), "iso8859-1"));
            sos = response.getOutputStream();
            fis = new FileInputStream(file);
            while ((len = fis.read(buff)) != -1) {
                sos.write(buff, 0, len);
            }
            sos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void downloadFile(HttpServletResponse response, InputStream is, String originalFileName){
        ServletOutputStream sos = null;
        byte[] buff = new byte[4096];
        int len = 0;
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(originalFileName.getBytes("utf-8"), "iso8859-1"));
            sos = response.getOutputStream();
            while ((len = is.read(buff)) != -1) {
                sos.write(buff, 0, len);
            }
            sos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件内容
     *
     * @param path 文件存储相对路径
     * @return 文件内容
     */
    public static byte[] getFileContent(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        byte[] fileContent = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fileContent = new byte[fis.available()];
            fis.read(fileContent, 0, fileContent.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ignored) { }
        }
        return fileContent;
    }

    public static boolean deleteDirectory(String path) {
        boolean result = true;
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if (!ArrayUtils.isArrayEmpty(subFiles)) {
                    for (File f : subFiles) {
                        if (f.isDirectory()) {
                            deleteDirectory(f.getAbsolutePath());
                        } else {
                            f.delete();
                        }
                    }
                    result = file.delete();
                } else {
                    result = file.delete();
                }
            } else {
                result = file.delete();
            }
        }
        return result;
    }
}
