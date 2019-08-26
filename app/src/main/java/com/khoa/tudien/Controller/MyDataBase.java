package com.khoa.tudien.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.khoa.tudien.Model.Idioms;
import com.khoa.tudien.Model.IrregularVerbs;
import com.khoa.tudien.Model.Word;

import java.util.ArrayList;

public class MyDataBase extends SQLiteOpenHelper {

    private static String DBName = "Directory.db";
    private String Favorite_Table = "Favorite";
    private String History_Table = "History";
    private String Idioms_Table = "Idioms";
    private String Toeic_Table = "Toeic";
    private String IrregularVerbs_Table = "IrregularVerbs";
    private String Unknown_Word_Table = "UnknownWord";
    private String Word = "word";
    private String Phonetic = "Phonetic";
    private String SimpleMeaning = "SimpleMeaning";
    private String Value = "Value";
    private String Sentence = "Sentence";
    private String SentenceMeaning = "SentenceMeaning";
    private String Infinitive = "Infinitive";
    private String Simple = "Simple";
    private String Participle = "Participle";
    private String Meaning = "Meaning";

    public MyDataBase(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMyWordTable = "create table " + Favorite_Table + " ("
                + Word + " text primary key, "
                + Phonetic + " text, "
                + SimpleMeaning + " text, "
                + Value + " text);";
        String createHistoryTable = "create table " + History_Table + " ("
                + Word + " text primary key, "
                + Phonetic + " text, "
                + SimpleMeaning + " text, "
                + Value + " text);";
        String createIdiomsTable = "create table " + Idioms_Table + "("
                + Sentence + " text, "
                + SentenceMeaning + " text);";
        String createIvTable = "create table " + IrregularVerbs_Table + "("
                + Infinitive + " text, "
                + Simple + " text, "
                + Participle + " text, "
                + Meaning + " text);";
        String createToeicTable = "create table " + Toeic_Table + " ("
                + Word + " text primary key, "
                + Phonetic + " text, "
                + SimpleMeaning + " text, "
                + Value + " text);";
//        String createUnknownWordTable = "create table " + Unknown_Word_Table + " ("
//                + Word + " text primary key, "
//                + Phonetic + " text, "
//                + SimpleMeaning + " text, "
//                + Value + " text);";

//        db.execSQL(createUnknownWordTable);
        db.execSQL(createMyWordTable);
        db.execSQL(createHistoryTable);
        db.execSQL(createIdiomsTable);
        db.execSQL(createIvTable);
        db.execSQL(createToeicTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropMyWordTable = "drop table if exists " + Favorite_Table;
        String dropHistoryTable = "drop table if exists " + History_Table;
        String dropIdiomsTable = "drop table if exists " + Idioms_Table;
        String dropIvTable = "drop table if exists " + IrregularVerbs_Table;
        String dropToeicTable = "drop table if exists " + Toeic_Table;
//        String dropUWTable = "drop table if exists " + Unknown_Word_Table;
        db.execSQL(dropMyWordTable);
        db.execSQL(dropHistoryTable);
        db.execSQL(dropIdiomsTable);
        db.execSQL(dropIvTable);
        db.execSQL(dropToeicTable);
//        db.execSQL(dropUWTable);
        onCreate(db);
    }

    public boolean exist(String tableName, String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(tableName, new String[]{Word}, Word + " = ? ", new String[]{key},
                    null, null, null);
            int count = cursor.getCount();
            cursor.close();
            db.close();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void addWord(String tableName, com.khoa.tudien.Model.Word word) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Word, word.getWord());
        values.put(Phonetic, word.getPhonetic());
        values.put(SimpleMeaning, word.getSimpleMeaning());
        values.put(Value, word.getContent());
        db.insert(tableName, null, values);
        db.close();
    }

    public void deleteWord(String tableName, String key) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, Word + " = ? ", new String[]{key});
        db.close();
    }

    public void addIdioms(Idioms idioms) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Sentence, idioms.getSentence());
        values.put(SentenceMeaning, idioms.getMeaning());
        db.insert(Idioms_Table, null, values);
        db.close();
    }

    public ArrayList<Idioms> getAllIdioms() {
        ArrayList<Idioms> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String stringQuery = "select * from " + Idioms_Table;
        Cursor cursor = db.rawQuery(stringQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                arr.add(new Idioms(cursor.getString(0), cursor.getString(1)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return arr;
    }

    public void addIv(String v1, String v2, String v3, String meaning) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Infinitive, v1);
        values.put(Simple, v2);
        values.put(Participle, v3);
        values.put(Meaning, meaning);

        db.insert(IrregularVerbs_Table, null, values);
        db.close();
    }

    public ArrayList<IrregularVerbs> getAllIv() {
        ArrayList<IrregularVerbs> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String stringQuery = "select * from " + IrregularVerbs_Table;
        Cursor cursor = db.rawQuery(stringQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                arr.add(new IrregularVerbs(cursor.getString(0), cursor.getString(1)
                        , cursor.getString(2), cursor.getString(3)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return arr;
    }


    public ArrayList<Word> getWord(String tableName, int number) {
        ArrayList<Word> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String stringQuery = "select * from " + tableName;
        Cursor cursor = db.rawQuery(stringQuery, null);
        int count = cursor.getCount();
        cursor.moveToFirst();
        if (count > 0) {
            if (number >= count || number == 0) {
                for (int i = 0; i < count; i++) {
                    Word w = new Word(cursor.getString(0), cursor.getString(1)
                            , cursor.getString(2), cursor.getString(3));
                    arr.add(w);
                    cursor.moveToNext();
                }
            } else {
                for (int i = 0; i < number; i++) {
                    Word w = new Word(cursor.getString(0), cursor.getString(1)
                            , cursor.getString(2), cursor.getString(3));
                    arr.add(w);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        db.close();
        return arr;
    }
}
