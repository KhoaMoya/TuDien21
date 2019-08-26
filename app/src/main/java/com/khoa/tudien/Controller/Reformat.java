package com.khoa.tudien.Controller;

import java.util.ArrayList;
import java.util.Collections;

public class Reformat {

    public ArrayList<String> spilit(String value) {
        String[] at = value.split("(?=@)");
        ArrayList<String> al = new ArrayList<>();
        for (String tmpAt : at) {
            String[] hyphen = tmpAt.split("(?=-)");
            for (String tmpHyphen : hyphen) {
                String[] slash = tmpHyphen.split("(?=/)");
                for (String tmpSlash : slash) {
                    String[] equals = tmpSlash.split("(?==)");
                    for (String tmpEquals : equals) {
                        String[] plusSign = tmpEquals.split("(?=\\+)");
                        for (String tmpPlusSign : plusSign) {
                            String[] asterisk = tmpPlusSign.split("(?=\\*)");
                            for (String tmpAsterisk : asterisk) {
                                int n;
                                int pos = 0;
                                String x = tmpAsterisk;
                                while ((n = x.indexOf('!')) != -1) {
                                    if (tmpAsterisk.length() > n + pos + 1) {
                                        if (Character.isLetter(tmpAsterisk.charAt(n + pos + 1)) ||
                                                tmpAsterisk.charAt(n + pos + 1) == '[') {
                                            tmpAsterisk = tmpAsterisk.substring(0, n + pos) + '^' + tmpAsterisk.substring(n + pos + 1);
                                        }
                                    }
                                    x = x.substring(n + 1);
                                    pos += n + 1;
                                }
                                String[] caret = tmpAsterisk.split("(?=\\^)");
                                Collections.addAll(al, caret);
//                                for (String tmpCaret : caret) {
//                                    al.add(tmpCaret);
//                                }
                            }
                        }
                    }
                }
            }
        }
        return al;
    }

    public String toViewFormat(String key, ArrayList<String> strings) {
        String returnString = "";
        for (String string : strings) {
            if (string.length() > 0) {
                switch (string.charAt(0)) {
                    case '@': // nhãn
                        if (!key.equals(string.substring(1).trim())) {
//                            returnString += "<b>" + string.substring(1).toUpperCase() + "</b>";
//                        } else {
                            returnString += "<br/><br/><b>" + string.substring(1).toUpperCase() + "</b>";
                        }
                        break;
                    case '-': // nghĩa
                        returnString += "<br/>" + string;
                        break;
                    case '/': // phien am
//                        if (string.length() > 1) {
//                            returnString += "<br/><i>" + string + "/</i>";
//                        }
                        break;
                    case '*':
                        returnString += "<br/><b><font color='#b71c1c'>" + string.substring(2) + "</font></b>";
                        break;
                    case '^':
                        returnString += "<br/><font color='#3f51b5'><b>" + string.substring(1) + "</b></font>";
                        break;
                    case '=':
                        if (Character.isLetter(string.charAt(1))) {
                            returnString += "<br/><font color='#607d8b'><i>" + string.substring(1) + "</i></font>";
                        } else {
                            returnString += string;
                        }
                        break;
                    case '+':
                        returnString += "<br/><font color='#607d8b'></i>->" + string.substring(1) + "</i></font>";
                        break;
                    default:
//                        returnString += string;
                        break;
                }
            }
        }
        return returnString;
    }

    public String getSimpleMeaning(ArrayList<String> strings) {
        String simpleMeaning = "";
        for (String string : strings) {
            if (string.length() > 0) {
                switch (string.charAt(0)) {
                    case '-':
                        simpleMeaning += string.substring(1) + "; ";
                        break;
                }
            }
        }
        return simpleMeaning;
    }

    public String getPhonetic(ArrayList<String> strings) {
        for (String string : strings) {
            if (string.length() > 0) {
                if (string.charAt(0) == '/') {
                    return string + "/";
                }
            }
        }
        return "";
    }
}
