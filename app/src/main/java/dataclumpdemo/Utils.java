package dataclumpdemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.checkerframework.checker.nullness.qual.NonNull;

 class CompanyUtils{
    public void paySalary(String iban, double amount, Date executeOn, String id){
        if(iban.length()==22){
            System.out.println("Paying salary to "+iban+" with amount "+amount+" on "+executeOn+" with id "+id);
        }
        else{
            throw new IllegalArgumentException("Invalid IBAN");
        }
    }

    public void payBill(String iban, double amount, Date executeOn, String id, int departmentId){
        if(iban.length()==22){
            System.out.println("Paying bill to "+iban+" with amount "+amount+" on "+executeOn+" with id "+id);
        }
        else{
            throw new IllegalArgumentException("Invalid IBAN");
        }
    }

    public void requestCredit(String iban, double amount, Date executeOn, String id, double maxInterest){
        if( iban.length()==22 && maxInterest>0 ){
            System.out.println("Requesting credit to "+iban+" with amount "+amount+" on "+executeOn+" with id "+id + " with max interest "+maxInterest);
        }
        else{
            throw new IllegalArgumentException("Invalid input");
        }
    }




















    public class FileUtils {

    public static final String DATA_BASEDIR ="/tmp/dolphinscheduler";

    public static final String APPINFO_PATH = "appInfo.log";

    public static final String KUBE_CONFIG_FILE = "config";
    
    public static final String RESOURCE_VIEW_SUFFIXES = "resource.view.suffixes";

    public static String FORMAT_S_S = "%s/%s";

    public static final String RESOURCE_VIEW_SUFFIXES_DEFAULT_VALUE = "txt,log,sh,conf,cfg,py,java,sql,hql,xml,yml,properties,template,log,txt";



    private static final Set<PosixFilePermission> PERMISSION_755 = PosixFilePermissions.fromString("rwxr-xr-x");

    /**
     * get download file absolute path and name
     *
     * @param filename file name
     * @return download file name
     */
    public static String getDownloadFilename(String filename) {
        return Paths.get(DATA_BASEDIR, "tmp")+ "-" + filename.toString();
    }

    /**
     * Generate a local tmp absolute path of the uploaded file
     */
    public static String getUploadFileLocalTmpAbsolutePath() {
        return Paths.get(DATA_BASEDIR, "tmp").toString();
    }

    /**
     * directory of process execution
     *
     * @param tenant               tenant
     * @param projectCode          project code
     * @param processDefineCode    process definition Code
     * @param processDefineVersion process definition version
     * @param processInstanceId    process instance id
     * @param taskInstanceId       task instance id
     * @return directory of process execution
     */
    public static String getTaskInstanceWorkingDirectory(String tenant,
                                                         long projectCode,
                                                         long processDefineCode,
                                                         int processDefineVersion,
                                                         int processInstanceId,
                                                         int taskInstanceId) {
        return String.format(
                "%s/exec/process/%s/%d/%d_%d/%d/%d",
                DATA_BASEDIR,
                tenant,
                projectCode,
                processDefineCode,
                processDefineVersion,
                processInstanceId,
                taskInstanceId);
    }

    /**
     * absolute path of kubernetes configuration file
     *
     * @param execPath
     * @return
     */
    public static String getKubeConfigPath(String execPath) {
        return String.format(FORMAT_S_S, execPath, KUBE_CONFIG_FILE);
    }

    /**
     * absolute path of appInfo file
     *
     * @param execPath directory of process execution
     * @return
     */
    public static String getAppInfoPath(String execPath) {
        return String.format("%s/%s", execPath, APPINFO_PATH);
    }

    /**
     * @return get suffixes for resource files that support online viewing
     */
    public static String getResourceViewSuffixes() {
        return  RESOURCE_VIEW_SUFFIXES_DEFAULT_VALUE;
    }

    /**
     * write content to file ,if parent path not exists, it will do one's utmost to mkdir
     *
     * @param content  content
     * @param filePath target file path
     * @return true if write success
     */
    public static boolean writeContent2File(String content, String filePath) {
        FileOutputStream fos = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists() && !distFile.getParentFile().mkdirs()) {
                System.out.println("create file parent directory failed");
                return false;
            }
            fos = new FileOutputStream(filePath);
        } catch (IOException e) {
            System.out.println("create file failed");
            return false;
        } finally {
           
        }
        return true;
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param filename file name
     */
    public static void deleteFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
        file.delete();
    }

    /**
     * Get Content
     *
     * @param inputStream input stream
     * @return string of input stream
     */
    public static String readFile2Str(InputStream inputStream) {

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return output.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            System.out.println("read file to string failed");
            throw new RuntimeException(e);
        }
    }

    public static String FOLDER_SEPARATOR = "/";

    /**
     * Check whether the given string type of path can be traversal or not, return true if path could
     * traversal, and return false if it is not.
     *
     * @param filename String type of filename
     * @return whether file path could be traversal or not
     */
    public static boolean directoryTraversal(String filename) {
        if (filename.contains(FOLDER_SEPARATOR)) {
            return true;
        }
        File file = new File(filename);
        try {
            File canonical = file.getCanonicalFile();
            File absolute = file.getAbsoluteFile();
            return !canonical.equals(absolute);
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Calculate file checksum with CRC32 algorithm
     *
     * @param pathName
     * @return checksum of file/dir
     */
    public static String getFileChecksum(String pathName) throws IOException {
        CRC32 crc32 = new CRC32();
        File file = new File(pathName);
        String crcString = "";
        if (file.isDirectory()) {
            // file system interface remains the same order
            String[] subPaths = file.list();
            StringBuilder concatenatedCRC = new StringBuilder();
            for (String subPath : subPaths) {
                concatenatedCRC.append(getFileChecksum(pathName + FOLDER_SEPARATOR + subPath));
            }
            crcString = concatenatedCRC.toString();
        } else {
            try (
                    FileInputStream fileInputStream = new FileInputStream(pathName);
                    CheckedInputStream checkedInputStream = new CheckedInputStream(fileInputStream, crc32);) {
                while (checkedInputStream.read() != -1) {
                }
            } catch (IOException e) {
                throw new IOException("Calculate checksum error.");
            }
            crcString = Long.toHexString(crc32.getValue());
        }

        return crcString;
    }

    public static void createFileWith755(@NonNull Path path) throws IOException {
        if (path.toFile().exists()) {
            return;
        } else {
            Path parent = path.getParent();
            if (parent != null && !parent.toFile().exists()) {
                createDirectoryWith755(parent);
            }

            File.createTempFile(APPINFO_PATH, RESOURCE_VIEW_SUFFIXES);
        }
    }

    public static void createDirectoryWith755(@NonNull Path path) throws IOException {
        if (path.toFile().exists()) {
            return;
        }
        if (path.toFile().isFile()) {
           createDirectoryWith755(path);
        } else {
            Path parent = path.getParent();
            if (parent != null && !parent.toFile().exists()) {
                createDirectoryWith755(parent);
            }

            try {
                createDirectoryWith755(path);
               
            } catch (FileAlreadyExistsException fileAlreadyExistsException) {
                // Catch the FileAlreadyExistsException here to avoid create the same parent directory in parallel
               System.out.println("create directory failed");
            }

        }
    }

    public static void setFileTo755(File file) throws IOException {
      
        if (file.isFile()) {
            file.setWritable(true,true);
            return;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                setFileTo755(f);
            }
        }
    }

    public static String concatFilePath(String... paths) {
        if (paths.length == 0) {
            throw new IllegalArgumentException("At least one path should be provided");
        }
        StringBuilder finalPath = new StringBuilder(paths[0]);
        if (finalPath.isEmpty()) {
            throw new IllegalArgumentException("The path should not be empty");
        }
        String separator = File.separator;
        for (int i = 1; i < paths.length; i++) {
            String path = paths[i];
            if (path.contentEquals("")) {
                throw new IllegalArgumentException("The path should not be empty");
            }
            if (finalPath.toString().endsWith(separator) && path.startsWith(separator)) {
                finalPath.append(path.substring(separator.length()));
                continue;
            }
            if (!finalPath.toString().endsWith(separator) && !path.startsWith(separator)) {
                finalPath.append(separator).append(path);
                continue;
            }
            finalPath.append(path);
        }
        return finalPath.toString();
    }

    public static String getClassPathAbsolutePath(Class clazz) {
        return Optional.ofNullable(clazz.getResource("/"))
                .map(URL::getPath)
                .orElseThrow(() -> new IllegalArgumentException("class path: " + clazz + " is null"));
    }

    /**
     * copy input stream to file, if the file already exists, will append the content to the beginning of the file, otherwise will create a new file.
     */
    public static void copyInputStreamToFile(InputStream inputStream, String destFilename) {
        try (FileOutputStream outputStream = new FileOutputStream(destFilename, true)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException("copy input stream to file failed", e);
        }
    }
}
}