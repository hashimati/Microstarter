package io.hashimati.microstarter.util;

import org.apache.commons.io.FileUtils;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

public class ScanningUtils {


    // make path of group of words.
    public static String constructPath(String... words)
    {
        String result = Arrays.asList(words).stream().map(x-> x.trim()+"/")
                .reduce("", String::concat);
        return result.substring(0, result.length()-1);

    }

    public static File getFile(String path)
    {
        return new File(path);
    }
    //return path starts with "/" and the "." is replaced with "___"/
    public static String refinePath(String startString, String path){

        return Mono.just(path)
                .map(x ->"/"+ x.substring(x.indexOf(startString)).replace(".","___").replace("\\", "/"))
                .block();

    }
    public static String unresolveEntry(String str){
        return str.replace(".", "___");
    }
    public static String resolveEntry(String str)
    {
        return str.replaceAll("___", ".");
    }


    public static String saveFile(String file) throws Exception {

        File in = new File(file);
        File out = new File(in.getName());

        FileUtils.copyFile(in, out);
//        if(i <= 0)
//            throw new Exception("Cannot copy the file: " + in.getCanonicalPath());
        return out.getAbsolutePath();

    }
    //getting the content of a file.
    public static String getFileContent(String file) throws Exception
    {
        File f = new File(file);
        if(f.exists() && f.isFile()) {
            Scanner s = new Scanner(f);
            String result = "";
            while (s.hasNextLine())
                result += s.nextLine() + "\n";

            s.close();
            return result.trim();
        }
        return null;

    }

    public static String tab(int i)
    {
        return i<=0 ? "":"\t" + tab(i-1);
    }
    public static String space(int i)
    {
        return i<=0 ? "":" " + space(i-1);
    }




    public static String unZipRepository() throws Exception {


        String filename = getRepositoryFile();

        File srcFile = new File(filename);

        // create a directory with the same name to which the contents will be extracted
        String zipPath = filename.substring(0, filename.length()-4);
        File temp = new File(zipPath);
        temp.mkdir();

        ZipFile zipFile = null;

        try {

            zipFile = new ZipFile(srcFile);

            // get an enumeration of the ZIP file entries
            Enumeration<? extends ZipEntry> e = zipFile.entries();

            while (e.hasMoreElements()) {

                ZipEntry entry = e.nextElement();

                File destinationPath = new File(zipPath, entry.getName());

                //create parent directories
                destinationPath.getParentFile().mkdirs();

                // if the entry is a file extract it
                if (entry.isDirectory()) {
                    continue;
                }
                else {

                  //  System.out.println("Extracting file: " + destinationPath);

                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                    int b;
                    byte buffer[] = new byte[1024];

                    FileOutputStream fos = new FileOutputStream(destinationPath);

                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }

                    bos.close();
                    bis.close();

                }

            }

            zipFile.close();
            System.out.println("trying to delete :"+ srcFile.getAbsolutePath()+ " -> " + srcFile.delete());
            return temp.getAbsolutePath();
        }
        catch (IOException ioe) {
            System.out.println("Error opening zip file" + ioe);
            return null;
        }
//        finally {
//            try {
//                if (zipFile!=null) {
//                    zipFile.close();
//                }
//            }
//            catch (IOException ioe) {
//                System.out.println("Error while closing zip file" + ioe);
//            }
//        }


    }


    public static String getRepositoryFile() throws IOException {
        File result = new File("repoFile.zip");
        FileUtils.copyURLToFile
                (new URL("https://github.com/micronaut-projects/micronaut-profiles/archive/master.zip"),
                        result);
        return result.getAbsolutePath();
    }


}
