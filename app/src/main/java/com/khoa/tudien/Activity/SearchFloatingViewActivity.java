package com.khoa.tudien.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.khoa.tudien.Model.KeyAndValue;
import com.khoa.tudien.R;
import com.khoa.tudien.Service.SearchService;
import com.khoa.tudien.Controller.TranslateDataBase;
import com.khoa.tudien.Model.Word;

import java.util.ArrayList;

public class SearchFloatingViewActivity extends AppCompatActivity {
    private EditText editText;
    private ImageButton btnCancel;
    private ImageButton btnHome;
    private TextView txtContent;
    private TextView txtPhonetic;
    private ArrayList<String> arrayList;
    private TranslateDataBase translateDataBase;
    private ListView listView;
    private LinearLayout layoutLisView;
    private boolean backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fabting_view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editText = findViewById(R.id.edt_search);
        btnCancel = findViewById(R.id.action_cancel);
//        btnHome = findViewById(R.id.btn_home);
        layoutLisView = findViewById(R.id.layout_list_view);
        txtContent = findViewById(R.id.txt_meaning);
        txtPhonetic = findViewById(R.id.txt_phonetic);
        listView = findViewById(R.id.list_view);
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        translateDataBase = new TranslateDataBase(getBaseContext(), false);
        try {
            initListener();
        }catch (Exception e){
            Log.e("Loi", e.getMessage());
        }
    }

    public void initListener() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (listView.getVisibility() == View.GONE)
                    listView.setVisibility(View.VISIBLE);
                if (s.length() > 1) {
                    layoutLisView.setVisibility(View.VISIBLE);
                    ArrayList<KeyAndValue> arrKV = translateDataBase.findKeyAndValue(s.toString().trim());
                    arrayList = new ArrayList<>();
                    for (KeyAndValue keyAndValue : arrKV)
                        arrayList.add(keyAndValue.getKey());
                    listView.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, arrayList));
                } else {
                    layoutLisView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = arrayList.get(position);
                editText.setText("");
                editText.append(string);
                listView.setVisibility(View.GONE);
                Word word = translateDataBase.findWord(string);
                txtPhonetic.setText(word.getPhonetic());
                txtContent.setText(Html.fromHtml(word.getContent()));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")) finish();
                else editText.setText("");
            }
        });
//        btnHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                backHome = true;
//                finish();
//                startActivity(new Intent(getBaseContext(), MainActivity.class));
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(!backHome) startService(new Intent(this, SearchService.class));
    }

}
