package com.khoa.tudien.Controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.khoa.tudien.Model.Idioms;
import com.khoa.tudien.Model.KeyAndValue;
import com.khoa.tudien.Model.Word;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class TranslateDataBase {
    private String dbName = "TranslateDataBase";
    private DB tranDb;
    private Context context;
    private MyDataBase myDataBase;
    private String DB_NAME = "Directory.db";
    private String DB_PATH = "/data/data/com.khoa.tudien/databases/";

    public TranslateDataBase(Context context, boolean isFirsttime) {
        try {
            this.tranDb = DBFactory.open(context, dbName);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        this.context = context;
        this.myDataBase = new MyDataBase(context);
        if (isFirsttime) {
            putJsonFile("vietanh.json");
            putJsonFile("anhviet.json");
//            createDbIdioms();
//            createDbIv();
//            creatBbToeic();
            copyDataBase();
        }
    }

    private void copyDataBase() {
        try {
            //Open your local db as the input stream
            InputStream myInput = context.getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DB_NAME;

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }catch (IOException e){
            Log.e("Loi", e.getMessage());
        }

    }
    private void createDbIv(){
        String line;
        String v1, v2, v3, meaning;
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("verbs.txt")));
            while((line = bf.readLine())!= null){
                line = bf.readLine();
                v1 = line;
                v2 = bf.readLine();
                v3 = bf.readLine();
                meaning = bf.readLine();
                bf.readLine();
                myDataBase.addIv(v1, v2, v3, meaning);
            }
        } catch (Exception e){
            Log.e("Loi", e.getMessage());
        }
    }

    private void creatBbToeic(){
        String line, key, phonetic, simpleMeaning, value, content;
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("toeic.txt")));
            while((line = bf.readLine())!= null){
                line = line.toLowerCase().trim();
                if(findKeyAndValue(line).size()>0) {
                    KeyAndValue keyAndValue = findKeyAndValue(line).get(0);
                    key = keyAndValue.getKey();
                    value = keyAndValue.getValue();
                    ArrayList<String> strings = new Reformat().spilit(value);
                    phonetic = new Reformat().getPhonetic(strings);
                    simpleMeaning = new Reformat().getSimpleMeaning(strings);
                    content = new Reformat().toViewFormat(key, strings);
                    myDataBase.addWord("Toeic", new Word(key, phonetic, simpleMeaning, content));
                } else {
                    Log.e("Loi", line);
                }
            }
        } catch (Exception e){
            Log.e("Loi", e.getMessage());
        }
    }
    private void createDbIdioms() {
        String line;
        String sentence;
        String meaning;
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("idioms.json")));
            while((line = bf.readLine()) != null ){
                if(line.length()==1) continue;
                String[] reString = line.split(":", 2);
                sentence = reString[0].substring(1,reString[0].length()-1);
                meaning = reString[1].substring(2,reString[1].length()-2);
                myDataBase.addIdioms(new Idioms(sentence, meaning));
            }
        }catch (IOException e){
            Log.e("Loi", e.getMessage());
        }
    }


    private void putJsonFile(String filePath) {
        String line;
        String[] reString;
        String key;
        String value;
        try {
            AssetManager asset = context.getAssets();
            BufferedReader bf;
            bf = new BufferedReader(
                    new InputStreamReader(asset.open(filePath)));
            while ((line = bf.readLine()) != null) {
                if (line.length() == 1) continue;
                reString = line.split(":", 2);
                if (reString.length > 1) {
                    key = reString[0].substring(1, reString[0].length() - 1);
                    if (reString[1].length() > 2) {
                        if (reString[1].charAt(reString[1].length() - 1) == ',')
                            value = reString[1].substring(2, reString[1].length() - 2);
                        else {
                            value = reString[1].substring(2, reString[1].length() - 1);
                        }
                    } else
                        value = "";
                    set(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Word findWord(String key){
        KeyAndValue keyAndValue = findKeyAndValue(key).get(0);
        key = keyAndValue.getKey();
        String value = keyAndValue.getValue();
        ArrayList<String> strings = new Reformat().spilit(value);
        String phonetic = new Reformat().getPhonetic(strings);
        String simpleMeaning = new Reformat().getSimpleMeaning(strings);
        String content = new Reformat().toViewFormat(key, strings);
        return new Word(key, phonetic, simpleMeaning, content);
    }

    public ArrayList<KeyAndValue> findKeyAndValue(String key) {
        try {
            ArrayList<KeyAndValue> arr = new ArrayList<>();
            String[] list = this.tranDb.findKeys(key);
            for (String string : list) {
                arr.add(new KeyAndValue(string, this.tranDb.get(string)));
            }
            return arr;
        } catch (SnappydbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<String> findKeys(String key){
        try {
            return new ArrayList<>(Arrays.asList(this.tranDb.findKeys(key)));
        } catch (SnappydbException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }
    public void set(String key, String value) {
        try {
            this.tranDb.put(key, value);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        try {
            return this.tranDb.get(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void delete(String key) {
        try {
            this.tranDb.del(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }
}
