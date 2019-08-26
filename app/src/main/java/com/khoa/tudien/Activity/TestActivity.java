package com.khoa.tudien.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.khoa.scrollabledialog.FullScreenDialog;
import com.khoa.tudien.Model.DialogWord;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.R;
import com.khoa.tudien.Model.Word;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    private FlowLayout layout1, layout2;
    private LinearLayout layout4;
    private Button btnFinish, btnNext, btnDetail, btnSkip, btnRepeat;
    private int width, height, elevation, textSize, margin;
    private LinearLayout.LayoutParams params;
    private int idTop;
    private int length;
    private String string;
    private Animation animationRemove, animationAdd;
    private boolean pass;
    private ImageView imgSad, imgSmile;
    private ArrayList<Word> arrWord;
    private ArrayList<String> arrSkipWord;
    private Word word;
    private String meaning;
    private int index = 0, count = 1;
    private TextView txtMeaning, txtResult;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportActionBar().hide();

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        btnFinish = findViewById(R.id.btn_finish);
        btnNext = findViewById(R.id.btn_next);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnSkip = findViewById(R.id.btn_skip);
        btnDetail = findViewById(R.id.btn_detail);
        imgSad = findViewById(R.id.img_sad);
        imgSmile = findViewById(R.id.img_smile);
        txtMeaning = findViewById(R.id.txt_meaning);
        txtResult = findViewById(R.id.txt_result);
        layout4  = findViewById(R.id.layout4);

        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnDetail.setOnClickListener(this);

        arrSkipWord = new ArrayList<>();
        arrWord = new MyDataBase(this).getWord("Toeic", 0);
        Collections.shuffle(arrWord);

        initDimens();
        load(index);
    }

    public void load(int index) {
        pass = false;
        word = arrWord.get(index);
        string = word.getWord();
        if(!arrSkipWord.contains(string)) arrSkipWord.add(string);
        Log.e("Loi", string);
        meaning = word.getSimpleMeaning().split(";")[0];
        txtMeaning.setText(meaning);
        init1(string);
        init2(shuffleString(string));
        idTop = 1000;
        length = 1000 + string.length() - 1;
        btnRepeat.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnSkip.setVisibility(View.VISIBLE);
        btnDetail.setVisibility(View.INVISIBLE);
        layout4.setVisibility(View.GONE);
        layout1.setVisibility(View.VISIBLE);
    }

    public String shuffleString(String string) {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        String shuffled = "";
        for (String letter : letters) {
            shuffled += letter;
        }
        return shuffled;
    }

    public void initDimens() {
        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
        elevation = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(margin, margin, margin, margin);
        animationAdd = AnimationUtils.loadAnimation(this, R.anim.view_zoom_out);
        animationRemove = AnimationUtils.loadAnimation(this, R.anim.view_zoom_in);
    }

    public void init1(String string) {
        layout1.removeAllViews();
        for (int i = 0; i < string.length(); i++) {
            TextView textView = getTextView();
            textView.setText("");
            textView.setBackground(getDrawable(R.drawable.background_char_null));
            textView.setId(1000 + i);
            layout1.addView(textView);
        }

    }

    public void init2(String string) {
        layout2.removeAllViews();
        for (int i = 0; i < string.length(); i++) {
            TextView textView = getTextView();
            textView.setText(string.charAt(i) + "");
            textView.setBackground(getDrawable(R.drawable.background_char));
            textView.setId(2000 + i);
            layout2.addView(textView);
        }

    }

    public TextView getTextView() {
        TextView textView = new TextView(this);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(textSize);
        textView.setElevation(elevation);
        textView.setLayoutParams(params);
        textView.setClickable(true);
        textView.setOnClickListener(this);
        return textView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_detail) {
            Word w = arrWord.get(index);
            ArrayList<Word> arr = new ArrayList<>();
            arr.add(w);
            new DialogWord(TestActivity.this, "Toeic", arr).show(0);
        } else if (v.getId() == R.id.btn_repeat) {
            load(index);
        } else if (v.getId() == R.id.btn_next) {
            count++;
            index++;
            load(index);
        } else if (v.getId() == R.id.btn_finish) {
            onBackPressed();
        } else if (v.getId() == R.id.btn_skip) {
            showAnswer();
        } else {
            try {
                int id = v.getId();
                if (id >= 2000) {
                    TextView txt2 = findViewById(id);
                    String str = txt2.getText().toString();
                    TextView txt1 = findViewById(idTop);
                    txt1.setText(str);
                    txt1.setBackground(getDrawable(R.drawable.background_char));
                    txt1.startAnimation(animationAdd);
                    idTop++;
                    txt2.startAnimation(animationRemove);
                    txt2.setVisibility(View.INVISIBLE);
                    if (checkEnd()) {
                        if (checkCorrect()) {
                            pass = true;
                            onPass();
                            showAnswer();
                        } else onFail();
                    }
                } else {
                    if (!pass) {
                        TextView txt1 = findViewById(id);
                        String string = txt1.getText().toString();
                        if (!string.equals("")) {
                            txt1.setText("");
                            txt1.setBackground(getDrawable(R.drawable.background_char_null));
                            idTop = id;
                            while (true) {
                                if (idTop == length) break;
                                else {
                                    TextView txt = findViewById(idTop + 1);
                                    if (!txt.getText().toString().equals("")) {
                                        txt1.setText(txt.getText().toString());
                                        txt1.setBackground(getDrawable(R.drawable.background_char));
                                        txt.setText("");
                                        txt.setBackground(getDrawable(R.drawable.background_char_null));
                                        idTop += 1;
                                        txt1 = findViewById(idTop);
                                    } else break;
                                }
                            }
                            for (int i = 2000; i <= (1000 + length); i++) {
                                TextView txt2 = layout2.findViewById(i);
                                if (txt2.getText().equals(string) && txt2.getVisibility() == View.INVISIBLE) {
                                    txt2.setVisibility(View.VISIBLE);
                                    txt2.startAnimation(animationAdd);
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Chọn chậm thôi bạn :))", Toast.LENGTH_LONG).show();
                load(index);
            }
        }
    }

    public boolean checkCorrect() {
        String strCheck = "";
        for (int i = 1000; i <= length; i++) {
            TextView txt = findViewById(i);
            strCheck = strCheck + txt.getText().toString();
        }
        return string.equals(strCheck);
    }

    public boolean checkEnd() {
        int count = 0;
        for (int i = 1000; i <= length; i++) {
            TextView txt = findViewById(i);
            if (!txt.getText().toString().equals("")) count++;
        }
        return count == string.length();
    }

    public void onPass(){
        imgSmile.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgSmile.setVisibility(View.GONE);
            }
        }, 2000);
        arrSkipWord.remove(string);
    }

    public void showAnswer() {
        btnDetail.setVisibility(View.VISIBLE);
        btnDetail.startAnimation(animationAdd);
        btnRepeat.setVisibility(View.VISIBLE);
        btnRepeat.startAnimation(animationAdd);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.startAnimation(animationAdd);
        btnSkip.setVisibility(View.GONE);
        layout2.removeAllViews();
        layout1.setVisibility(View.GONE);
        layout4.setVisibility(View.VISIBLE);
        txtResult.setText(string);
        txtResult.setVisibility(View.VISIBLE);
        txtResult.startAnimation(animationAdd);
    }

    public void onFail() {
        imgSad.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgSad.setVisibility(View.GONE);
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new FullScreenDialog(this).getIntanse();
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(false);
        TextView txtTotal = dialog.findViewById(R.id.txt_total);
        TextView txtTotalCorrect = dialog.findViewById(R.id.txt_total_correct);
        TextView txtTotalSkip = dialog.findViewById(R.id.txt_total_skip);
        ImageView imgView = dialog.findViewById(R.id.img_view);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        ListView listView = dialog.findViewById(R.id.list_view_unknow_word);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, arrSkipWord));
        LinearLayout.LayoutParams paramsListView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int m = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        paramsListView.setMargins(m, m, m,0);
        if(arrSkipWord.size()<4){
            listView.setLayoutParams(paramsListView);
        } else {
            int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            paramsListView.height = h;
            listView.setLayoutParams(paramsListView);
        }
        int countCorrect = count - arrSkipWord.size();
        float f = countCorrect / count;
        if (f >= 0.9) imgView.setImageDrawable(getDrawable(R.drawable.cuoi));
        else imgView.setImageDrawable(getDrawable(R.drawable.buon));
        txtTotal.setText("Tổng số từ đã làm:  " + count);
        txtTotalCorrect.setText("Số từ làm đúng:  " + countCorrect);
        if(arrSkipWord.size()==0) txtTotalSkip.setVisibility(View.GONE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}
