package com.khoa.tudien.Model;

public class IrregularVerbs {
    private String infinitive, simple, participle;
    private String meaning;

    public IrregularVerbs(String infinitive, String simple, String participle, String meaning) {
        this.infinitive = infinitive;
        this.simple = simple;
        this.participle = participle;
        this.meaning = meaning;
    }

    public String getInfinitive() {
        return infinitive;
    }

    public String getSimple() {
        return simple;
    }

    public String getParticiple() {
        return participle;
    }

    public String getMeaning() {
        return meaning;
    }
}
