package org.lamisplus.modules.ndr.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This utility compresses a list of files to a standard ZIP format file.
 * It supports compressing directories recursively and splitting large zip files.
 */
@Slf4j
public class ZipUtility {
    private static final int BUFFER_SIZE = 4096;

    /**
     * Compresses a list of files into a zip file, with optional splitting for large files.
     *
     * @param listFiles   List of files and directories to compress.
     * @param destZipFile Destination zip file path.
     * @param splitSize   Maximum size of a single zip file in bytes.
     * @throws IOException If an I/O error occurs.
     */
    public static void zip(List<File> listFiles, String destZipFile, long splitSize) throws IOException {
        long totalSize = 0L;
        String currentZipFile = destZipFile;

        try (ZipOutputStream zos = createNewZipOutputStream(currentZipFile)) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    zipDirectory(file, file.getName(), zos);
                } else {
                    long compressedSize = zipFile(file, zos);
                    totalSize += compressedSize;

                    if (totalSize > splitSize) {
                        // Start a new zip file when split size is exceeded
                        totalSize = 0L;
                        currentZipFile = generateNewZipFileName(destZipFile);
                        zos.close();

                        try (ZipOutputStream newZos = createNewZipOutputStream(currentZipFile)) {
                            zipFile(file, newZos);
                        }
                    }
                }
            }
        }
    }

    /**
     * Compresses an array of file paths into a zip file.
     *
     * @param files       Array of file paths to compress.
     * @param destZipFile Destination zip file path.
     * @param splitSize   Maximum size of a single zip file in bytes.
     * @throws IOException If an I/O error occurs.
     */
    public static void zip(String[] files, String destZipFile, long splitSize) throws IOException {
        List<File> listFiles = new ArrayList<>();
        for (String file : files) {
            listFiles.add(new File(file));
        }
        zip(listFiles, destZipFile, splitSize);
    }

    private static ZipOutputStream createNewZipOutputStream(String zipFileName) throws FileNotFoundException {
        return new ZipOutputStream(new FileOutputStream(zipFileName));
    }

    private static String generateNewZipFileName(String originalZipFile) {
        File outFile = new File(originalZipFile);
        String name = outFile.getName();
        String parent = outFile.getParent();
        String uniqueSuffix = RandomStringUtils.randomAlphabetic(5);
        return parent + File.separator + name + "_" + uniqueSuffix;
    }

    private static void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String zipEntryName = parentFolder + "/" + file.getName();
            if (file.isDirectory()) {
                zipDirectory(file, zipEntryName, zos);
            } else {
                zipFile(file, zipEntryName, zos);
            }
        }
    }

    private static long zipFile(File file, String zipEntryName, ZipOutputStream zos) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            zos.closeEntry();
            return totalBytes;
        }
    }

    private static long zipFile(File file, ZipOutputStream zos) throws IOException {
        return zipFile(file, file.getName(), zos);
    }

    /**
     * Calculates the total size of a folder (recursively).
     *
     * @param folder The folder to calculate the size of.
     * @return Total size in bytes.
     */
    public static long getFolderSize(File folder) {
        if (folder.isFile()) {
            return folder.length();
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return 0;
        }

        long size = 0;
        for (File file : files) {
            size += getFolderSize(file);
        }
        return size;
    }
}