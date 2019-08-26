package com.khoa.tudien.Model;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.khoa.scrollabledialog.FullScreenDialog;
import com.khoa.scrollabledialog.OnTouchListener;
import com.khoa.scrollabledialog.ScrollableScrollView;
import com.khoa.tudien.Interface.CallBackDelete;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.R;

import java.util.ArrayList;
import java.util.Locale;


public class DialogWord {
    private String tableName;
    private Context context;
    private Dialog dialog;
    private ViewPager viewPager;
    private ArrayList<Word> arrayListWord;
    private TextToSpeech textToSpeech;
    private Word word;
    private SwitchCompat switchCompat;
    private boolean autoSpell;


    public DialogWord(Context context, String tableName, ArrayList<Word> arrayListWord) {
        this.context = context;
        this.tableName = tableName;
        this.arrayListWord = arrayListWord;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate((float) 0.8);
                }
            }
        });
        dialog = new FullScreenDialog(this.context).getIntanse();
        dialog.setContentView(R.layout.dialog_detailed_word);
        viewPager = dialog.findViewById(R.id.view_pager);
        viewPager.setOnPageChangeListener(new PagerChangeListener());
        switchCompat = dialog.findViewById(R.id.switch_compat);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoSpell = isChecked;
            }
        });
    }


    public void show(int position) {
        viewPager.setAdapter(new ViewPagerAdapterDetailedWord());
        viewPager.setCurrentItem(position);
        word = arrayListWord.get(position);
        dialog.show();
    }

    public class ViewPagerAdapterDetailedWord extends PagerAdapter implements View.OnClickListener {

        private TextView txtWord, txtPhonetic, txtMeaning;
        private ScrollableScrollView scrollView;
        private LinearLayout baseLayout;
        private Button btnDelete, btnCopy, btnSpell;


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Word word1 = arrayListWord.get(position);
            ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.detailed_word, container, false);
            baseLayout = view.findViewById(R.id.base_layout);
            txtWord = view.findViewById(R.id.txt_word);
            txtPhonetic = view.findViewById(R.id.txt_phonetic);
            txtMeaning = view.findViewById(R.id.txt_meaning);
            btnDelete = view.findViewById(R.id.btn_delete);
            btnCopy = view.findViewById(R.id.btn_copy);
            btnSpell = view.findViewById(R.id.btn_spell);

            btnDelete.setOnClickListener(this);
            btnCopy.setOnClickListener(this);
            btnSpell.setOnClickListener(this);
            scrollView = view.findViewById(R.id.scroll_view);
            scrollView.setOnTouchListener(new OnTouchListener(baseLayout, scrollView, dialog));
            txtWord.setText(word1.getWord());
            String phonetic = word1.getPhonetic();
            if (phonetic.equals("")) txtPhonetic.setVisibility(View.GONE);
            txtPhonetic.setText(word1.getPhonetic());
            txtMeaning.setText(Html.fromHtml(word1.getContent()));

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return arrayListWord.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_delete:
                    if (tableName.equals("Toeic")) {
                        new AlertDialog.Builder(context)
                                .setTitle("Xoá")
                                .setMessage("Bạn chắc chắn xóa \"" + word.getWord() + "\" chứ ?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        new MyDataBase(context).deleteWord(tableName, word.getWord());
                                        if (context instanceof CallBackDelete)
                                            ((CallBackDelete) context).deleteWord();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    } else {
                        new MyDataBase(context).deleteWord(tableName, word.getWord());
                        if (context instanceof CallBackDelete)
                            ((CallBackDelete) context).deleteWord();
                    }
                    dialog.dismiss();
                    break;
                case R.id.btn_copy:
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(context.getResources().getText(R.string.app_name), word.getWord());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Đã lưu vào bộ nhớ tạm", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_spell:
                    textToSpeech.speak(word.getWord(), TextToSpeech.QUEUE_FLUSH, null);
                    break;
                default:
                    break;
            }
        }
    }

    public class PagerChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            word = arrayListWord.get(position);
            if(autoSpell) textToSpeech.speak(word.getWord(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
