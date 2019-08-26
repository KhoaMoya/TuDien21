package com.khoa.tudien.Controller;

import com.khoa.tudien.Model.Word;

import java.util.Comparator;

public class SortWordByAbc implements Comparator<Word> {
    @Override
    public int compare(Word o1, Word o2) {
        return o1.getWord().compareTo(o2.getWord());
    }
}