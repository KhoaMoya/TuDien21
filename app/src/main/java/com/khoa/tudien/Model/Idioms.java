package com.khoa.tudien.Model;

public class Idioms {
    private String sentence;
    private String meaning;

    public Idioms(String sentence, String meaning) {
        this.sentence = sentence;
        this.meaning = meaning;
    }

    public String getSentence() {
        return sentence;
    }

    public String getMeaning() {
        return meaning;
    }

}
