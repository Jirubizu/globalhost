package jirubizu.globalhost.utilities;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NgrokUtilites
{
    public static void CheckNgrok()
    {
        File ngrokFile = new File(getNgrokFile());
        if (!ngrokFile.exists())
        {
            downloadNgrok();
        }

    }

    public static String getNgrokFile()
    {
        return FabricLoader.getInstance().getConfigDir() + "/globalhost/ngrok";
    }

    private static void downloadNgrok()
    {
        File ngrokFile = new File("ngrok.zip");
        switch (Objects.requireNonNull(OSUtilities.getOperatingSystem()))
        {

            case WINDOWS -> downloadFile(ngrokFile, "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip");
            case MACOS -> downloadFile(ngrokFile, "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip");
            case LINUX -> downloadFile(ngrokFile, "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip");
            default -> {

            }
        }
        ngrokFile.delete();
    }

    private static void downloadFile(File file, String downloadUrl)
    {
        System.out.println("File: " + file + " url: " + downloadUrl);
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(downloadUrl).openStream()); FileOutputStream fileOS = new FileOutputStream(file))
        {
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1)
            {
                fileOS.write(data, 0, byteContent);
            }
        }
        catch (IOException e)
        {

        }

        unzip(file.getPath(), getNgrokFile());
    }

    private static void unzip(String zipFilePath, String destDir)
    {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try
        {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null)
            {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
