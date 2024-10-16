package alex.utils;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class FileUtil {

    public File file;

    public FileUtil(String filename){
        try {
            file = new File(URLDecoder.decode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public void read(){
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

    public String readLine(){
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

     public Set<String> readFileAsSet(){
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
}
