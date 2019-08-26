package com.khoa.tudien.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.khoa.scrollabledialog.FullScreenDialog;
import com.khoa.tudien.Controller.SortWordByAbc;
import com.khoa.tudien.Interface.CallBackDelete;
import com.khoa.tudien.Model.KeyAndValue;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.R;
import com.khoa.tudien.Adapter.RecycleViewWordAdapter;
import com.khoa.tudien.Controller.TranslateDataBase;
import com.khoa.tudien.Model.Word;

import java.util.ArrayList;
import java.util.Collections;

public class ToeicActivity extends AppCompatActivity implements View.OnClickListener, CallBackDelete {

    private MyDataBase myDataBase;
    private RecyclerView recyclerViewToeic;
    private TextView txtTest;
    private ArrayList<Word> arrToeic;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toeic);
        Toolbar toolbar = findViewById(R.id.tool_bar_toeic);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.toeic_word));
        loadBackdrop();
        recyclerViewToeic = findViewById(R.id.recycler_view_toeic);
        txtTest = findViewById(R.id.txtTest);
        txtTest.setOnClickListener(this);

        myDataBase = new MyDataBase(this);
        recyclerViewToeic.setHasFixedSize(true);
        recyclerViewToeic.getRecycledViewPool().setMaxRecycledViews(0, 0);
        recyclerViewToeic.setLayoutManager(new LinearLayoutManager(this));
        showToeic();
        recyclerViewToeic.addOnScrollListener(new CustomScrollListener());
    }

    private void loadBackdrop() {
        final ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this)
                .load(R.drawable.custom_background03)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toeic, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        querySearchToeic();
        return super.onCreateOptionsMenu(menu);
    }

    public void querySearchToeic() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Word> arrayList = new ArrayList<>();
                for (Word w : arrToeic) {
                    if (w.getWord().toLowerCase().trim().contains(s.toLowerCase().trim()))
                        arrayList.add(w);
                }
                recyclerViewToeic.setAdapter(new RecycleViewWordAdapter(ToeicActivity.this, "Toeic", arrayList));
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_sort_by_random:
                Collections.shuffle(arrToeic);
                recyclerViewToeic.setAdapter(new RecycleViewWordAdapter(ToeicActivity.this, "Toeic", arrToeic));
                break;
            case R.id.action_sort_by_abc:
                Collections.sort(arrToeic, new SortWordByAbc());
                recyclerViewToeic.setAdapter(new RecycleViewWordAdapter(ToeicActivity.this, "Toeic", arrToeic));
                break;
            case R.id.action_add:
                addToeicWord();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToeicWord() {
        final TranslateDataBase translateDataBase = new TranslateDataBase(ToeicActivity.this, false);
        final Dialog dialog = new FullScreenDialog(this).getIntanse();
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.setContentView(R.layout.dialog_add_toeic_word);
        final ListView listView = dialog.findViewById(R.id.lv_add_toeic);
        final EditText editText = dialog.findViewById(R.id.edt_search);
        ImageButton imgCancle = dialog.findViewById(R.id.action_cancel);
        editText.setTextColor(getResources().getColor(R.color.colorBlack));
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    ArrayList<KeyAndValue> arr = translateDataBase.findKeyAndValue(s.toString());
                    ArrayList<String> arrKey = new ArrayList<>();
                    for (KeyAndValue kv : arr)
                        arrKey.add(kv.getKey());
                    listView.setAdapter(new ArrayAdapter<String>(ToeicActivity.this
                            , android.R.layout.simple_dropdown_item_1line, arrKey));
                } else {
                    listView.setAdapter(new ArrayAdapter<String>(ToeicActivity.this
                            , android.R.layout.simple_dropdown_item_1line, new ArrayList<String>()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = listView.getAdapter().getItem(position).toString();
                final Word word = translateDataBase.findWord(key);
                new AlertDialog.Builder(ToeicActivity.this)
                        .setTitle("Thêm")
                        .setMessage("Thêm \"" + key + "\" vào Từ vựng Toeic không ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new MyDataBase(ToeicActivity.this).addWord("Toeic", word);
                                showToeic();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        imgCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    dialog.dismiss();
                } else {
                    editText.setText("");
                    listView.setAdapter(new ArrayAdapter<String>(ToeicActivity.this
                            , android.R.layout.simple_dropdown_item_1line, new ArrayList<String>()));
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtTest:
                startActivity(new Intent(ToeicActivity.this, TestActivity.class));
                break;
            default:
                break;
        }
    }

    public void showToeic() {
        arrToeic = myDataBase.getWord("Toeic", 0);
        Collections.reverse(arrToeic);
        getSupportActionBar().setSubtitle(String.valueOf(arrToeic.size()));
        recyclerViewToeic.setAdapter(new RecycleViewWordAdapter(this, "Toeic", arrToeic));
    }

    @Override
    public void deleteWord() {
        showToeic();
    }

    int n = 0;

    public class CustomScrollListener extends RecyclerView.OnScrollListener {
        public CustomScrollListener() {
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) txtTest.getLayoutParams();

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    // The RecyclerView is not scrolling
                    txtTest.setVisibility(View.VISIBLE);
                    txtTest.startAnimation(AnimationUtils.loadAnimation(ToeicActivity.this, R.anim.fab_appear));
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    // Scrolling now
                    txtTest.setVisibility(View.GONE);
//                    txtTest.startAnimation(AnimationUtils.loadAnimation(ToeicActivity.this, R.anim.popup_hide));
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    // Scroll Settling
                    txtTest.setVisibility(View.GONE);
//                    txtTest.startAnimation(AnimationUtils.loadAnimation(ToeicActivity.this, R.anim.popup_hide));

                    break;

            }

        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            if (dx > 0) {
//                Log.e("Loi","Scrolled Right");
//            } else if (dx < 0) {
//                Log.e("Loi","Scrolled Left");
//            } else {
//                Log.e("Loi","No Horizontal Scrolled");
//            }
//
            if (dy > 0) {
                Log.e("Loi", "Scrolled Downwards");
//                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) txtTest.getLayoutParams();
//                n = n+dy;
//                params.height = n;
//                txtTest.setLayoutParams(params);

            } else if (dy < 0) {
                Log.e("Loi", "Scrolled Upwards");
//                n = n+dy;
//                params.height = n;
//                txtTest.setLayoutParams(params);
            } else {
//                Log.e("Loi","No Vertical Scrolled");
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            overridePendingTransition(R.anim.popup_right,R.anim.popup_zoom_out);
        }
    }
}
