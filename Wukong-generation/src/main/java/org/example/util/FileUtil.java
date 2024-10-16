package org.example.util;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {

    public static File file;

    public FileUtil(String filename){
        try {
            file = new File(URLDecoder.decode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void read(){
        //read file
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file));
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while((str = bufferedReader.readLine()) != null)
            {
                System.out.println(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readLine(){
        //read file
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file));
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while((str = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(str);
                stringBuilder.append("\n");
                System.out.println(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

     public static Set<String> readFileAsSet(){
        //read file
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        Set<String> dataSet = new HashSet<>();
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file));
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while((str = bufferedReader.readLine()) != null)
            {
                dataSet.add(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataSet;
    }

    public static boolean writeToFile(String filePath, String content) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
//                log.error("delete file fialed, filepath = " + filePath);
                return false;
            }
        }

        try {
            file.createNewFile();
        } catch (Exception e) {
//            log.error("create newfile failed, filepath = " + filePath, e);
            return false;
        }


        try {
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (Exception e) {
//            log.error("write content into file error, errmsg = " + e.getMessage() + ", filepath = " + filePath, e);
            return false;
        }

        return true;

    }
}
