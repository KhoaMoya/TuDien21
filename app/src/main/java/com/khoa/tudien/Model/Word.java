package com.khoa.tudien.Model;

public class Word {
    private String word;
    private String phonetic;
    private String simpleMeaning;
    private String content;

    public Word(String word, String phonetic, String simpleMeaning, String content) {
        this.word = word;
        this.phonetic = phonetic;
        this.simpleMeaning = simpleMeaning;
        this.content = content;
    }

    public String getWord() {
        return word;
    }


    public String getPhonetic() {
        return phonetic;
    }


    public String getSimpleMeaning() {
        return simpleMeaning;
    }


    public String getContent() {
        return content;
    }

}
