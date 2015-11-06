package ua.nau.edu.Systems;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashSet;

/**
     * Created by gaponec on 04.11.15.
     */
public class FileAdapter {
        private String faculty;
        private String group;

        private File listGroupFile;
        public LinkedHashSet<String> listGroup;

        //URL adress for downloading the file
        final private String BASEURL = "http://fs101.www.ex.ua/get/7d3cb0ea1abd0297f826db702d462ada/205609586/";
        final private String LISTGROUPFILE = "listGroup.txt";



        public FileAdapter(){
            checkFiles();
            getFileListgroup();
            setListGroup();
        }



        public FileAdapter(String faculty,String group){
            this.faculty = faculty;
            this.group = group;
        }



        protected void doDownloadXML(final String url,final String fileName){
            Thread dx = new Thread(){

                public void run(){
                    downloadFileXML();
                }
            };
            dx.start();
        }



        protected void doDownloadListGroup(){
            Thread dx = new Thread(){

                public void run(){
                    downloadFileListGroup();
                }
            };
            dx.start();
        }



        private void checkFiles(){
            if(!this.isListGroupDownloaded()){
                Log.v("APPNAU","NOT");
                doDownloadListGroup();
            } else {
                Log.v("APPNAU", "EXIST");
            }
        }



        public void downloadFileXML(){

            //getFileName
            String fileName;
            faculty.toUpperCase();
            fileName = faculty + group + "xlsx";

            //creating directory for containing the file
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath()+"/ServerStorage/");
            if(!dir.exists()){
                dir.mkdir();
            }

            //downloading the file
            try {
                URL url = new URL(BASEURL);
                URLConnection conection = url.openConnection();
                conection.connect();

                //this value will used for downloading_bar
                int fileLength = conection.getContentLength();

                InputStream in = new BufferedInputStream(url.openStream());
                OutputStream out = new FileOutputStream(dir+fileName);

                byte data[] = new byte[1024];
                long total = 0;
                int counter;
                while ((counter = in.read(data)) != -1){
                    total += counter;

                    out.write(data, 0, counter);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }




        public void downloadFileListGroup(){

            Log.v("APPNAU","StartDownloading");
            String fileName = "listGroup.txt";

            //creating directory for containing the file
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath()+"/ServerStorage");
            if(!dir.exists()){
                dir.mkdir();
            }

            try{
                URL url = new URL(BASEURL+LISTGROUPFILE);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream in = new BufferedInputStream(url.openStream());
                OutputStream out = new FileOutputStream(dir+"/"+fileName);

                byte[] data = new byte[1024];

                long total = 0;
                int counter;
                while ((counter = in.read(data)) != -1){
                    total += counter;

                    out.write(data, 0, counter);
                }
            } catch (Exception e){
                e.printStackTrace();
                Log.v("APPNAU","NOTDOWNLOADED");
            }
        }



        public boolean isListGroupDownloaded(){
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath()+"/ServerStorage");
            if(!dir.exists()){
                return false;
            } else {
                return true;
            }
        }



        private void getFileListgroup(){
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath()+"/ServerStorage/","listGroup.txt");

            this.listGroupFile = dir;
        }




        private void setListGroup(){
            listGroup = new LinkedHashSet<>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(this.listGroupFile));

                String line = "";

                Log.v("APP", "START");

                while ((line = br.readLine()) != null) {
                    Log.v("APP", line);
                    listGroup.add(line);
                }
                br.close();

                Log.v("APP", "STROP");

            } catch (Exception e){
                e.printStackTrace();
            }
        }



}

