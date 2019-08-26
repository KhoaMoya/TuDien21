package com.khoa.tudien.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.khoa.tudien.Interface.CallBackDelete;
import com.khoa.tudien.Model.KeyAndValue;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.Interface.OnLoadMoreListener;
import com.khoa.tudien.R;
import com.khoa.tudien.Adapter.RecycleViewSuggestAdapter;
import com.khoa.tudien.Adapter.RecycleViewWordAdapter;
import com.khoa.tudien.Service.SearchService;
import com.khoa.tudien.Controller.SettingActivity;
import com.khoa.tudien.Controller.TranslateDataBase;
import com.khoa.tudien.Model.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CallBackDelete {

    private FloatingActionButton fab;
    private RelativeLayout actionBar;
    private RelativeLayout searchBar;
    private TextView title;
    private ImageButton ibtnCancel, ibtnRecord, ibtnSearch, ibtnMore;
    private EditText edtSearch;
    final private String History_Table = "History";
    private ArrayList<Word> arrHistory = new ArrayList<>();
    private MyDataBase myDataBase;
    private RecyclerView recycleViewSuggest, recyclerViewHistory;
    public static SharedPreferences sharedPreferences;
    private boolean firstTime;
    private TextView txtHead;
    private TranslateDataBase translateDataBase;
    private LinearLayout layoutFuntion, funFavorite, funIv, funIdioms, funToeic;
    private boolean keyboardOpening = false;
    private int MaxLoadSize = 10;
    private int MaxLoadHistory = 20;
    private CardView btn_window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        myDataBase = new MyDataBase(this);
        fab = findViewById(R.id.fab);
        title = findViewById(R.id.title);
        actionBar = findViewById(R.id.actionbar);
        searchBar = findViewById(R.id.search_bar);
        edtSearch = findViewById(R.id.edt_search);
        ibtnRecord = findViewById(R.id.action_record);
        ibtnCancel = findViewById(R.id.action_cancel);
        ibtnSearch = findViewById(R.id.action_search);
        ibtnMore = findViewById(R.id.action_more);
        recycleViewSuggest = findViewById(R.id.recycle_view_suggest);
        recyclerViewHistory = findViewById(R.id.recycle_view_history);
        txtHead = findViewById(R.id.txt_history);
        layoutFuntion = findViewById(R.id.layout_funtion);
        funFavorite = findViewById(R.id.funtion_favorite);
        funIv = findViewById(R.id.funtion_iv);
        funIdioms = findViewById(R.id.funtion_idioms);
        funToeic = findViewById(R.id.funtion_toeic);
        btn_window = findViewById(R.id.btn_window);

        funToeic.setOnClickListener(this);
        funFavorite.setOnClickListener(this);
        funIv.setOnClickListener(this);
        funIdioms.setOnClickListener(this);
        fab.setOnClickListener(this);
        ibtnCancel.setOnClickListener(this);
        ibtnRecord.setOnClickListener(this);
        ibtnSearch.setOnClickListener(this);
        ibtnMore.setOnClickListener(this);
        btn_window.setOnClickListener(this);
        actionBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.appbar_appear));

        recycleViewSuggest.setHasFixedSize(true);
        recycleViewSuggest.getRecycledViewPool().setMaxRecycledViews(0, 0);
        recycleViewSuggest.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewHistory.setHasFixedSize(true);
        recyclerViewHistory.getRecycledViewPool().setMaxRecycledViews(0, 0);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        OverScrollDecoratorHelper.setUpOverScroll(recyclerViewHistory, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        OverScrollDecoratorHelper.setUpOverScroll(recycleViewSuggest, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        sharedPreferences = getSharedPreferences("SETTING", MODE_PRIVATE);
        firstTime = sharedPreferences.getBoolean("FIRST_TIME", true);
        if(isMyServiceRunning(SearchService.class)){
            Log.e("Loi", "service is running");
            stopService(new Intent(this, SearchService.class));
        }
        initDataBase();
        loadHistory();
        showHistory();
        initSearch();
        listenerKeyboard();
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void initDataBase() {
        if (firstTime) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            dialog.setMessage("Khởi tạo dữ liệu cho lần đầu\nVui lòng đợi");
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    translateDataBase = new TranslateDataBase(getApplicationContext(), true);
                    dialog.dismiss();
                    sharedPreferences.edit().putBoolean("FIRST_TIME", false).apply();
                }
            }).start();
        } else {
            translateDataBase = new TranslateDataBase(this, false);
        }
    }

    public void loadHistory() {
        arrHistory = myDataBase.getWord(History_Table, MaxLoadHistory);
        Collections.reverse(arrHistory);
        recyclerViewHistory.setAdapter(new RecycleViewWordAdapter(this, History_Table, arrHistory));
    }

    public void initSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = s.toString();
                if (key.length() > 1) {
                    ArrayList<KeyAndValue> arrKV = translateDataBase.findKeyAndValue(key);
                    txtHead.setText(getResources().getText(R.string.result_search) + " " + String.valueOf(arrKV.size()));
                    new SearchBackground().execute(arrKV.toArray(new KeyAndValue[0]));
                } else {
                    txtHead.setText(getResources().getText(R.string.result_search) + " nhiều lắm");
                    recycleViewSuggest.setAdapter(new RecycleViewSuggestAdapter(MainActivity.this, null, recycleViewSuggest));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void showTraNhanh() {
        final boolean canShow = showChatHead(this);
        if (!canShow) {
            @SuppressLint("InlinedApi") final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 12345);
        }
        this.finish();
    }
    private boolean showChatHead(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.startService(new Intent(context, SearchService.class));
            return true;
        }

        if (Settings.canDrawOverlays(context)) {
            context.startService(new Intent(context, SearchService.class));
            return true;
        }

        return false;
    }
    public void onClickFab() {
        if (searchBar.getVisibility() == View.GONE) {
            onClickSearch();
        } else {
            if (keyboardOpening) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                edtSearch.clearFocus();
            } else {
                edtSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public void showHistory() {
        recyclerViewHistory.setVisibility(View.VISIBLE);
        txtHead.startAnimation(AnimationUtils.loadAnimation(this, R.anim.view_down));
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.recycleview_up);
        animation.setStartOffset(100);
        recyclerViewHistory.startAnimation(animation);
        showLayoutFuntion();
        txtHead.setText(getResources().getText(R.string.last_search));
    }

    public void hideHistory() {
        hideLayoutFuntion();
        txtHead.startAnimation(AnimationUtils.loadAnimation(this, R.anim.view_up));
        txtHead.setText(getResources().getText(R.string.result_search));
        recyclerViewHistory.startAnimation(AnimationUtils.loadAnimation(this, R.anim.recycleview_down));
        recyclerViewHistory.setVisibility(View.GONE);
    }

    public void showLayoutFuntion() {
        btn_window.setVisibility(View.VISIBLE);
        layoutFuntion.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.view_zoom_out);
        animation.setStartOffset(300);
        layoutFuntion.startAnimation(animation);
        btn_window.startAnimation(animation);
    }

    public void hideLayoutFuntion() {
        btn_window.setVisibility(View.GONE);
        layoutFuntion.setVisibility(View.GONE);
        layoutFuntion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.view_zoom_in));
    }

    public void onClickCancle() {
        if (edtSearch.getText().toString().equals("")) {
            searchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.searchbar_close));
            searchBar.setVisibility(View.GONE);
            ibtnSearch.setVisibility(View.VISIBLE);
//            Animation animation = AnimationUtils.loadAnimation(this, R.anim.view_zoom_out);
//            animation.setStartOffset(150);
//            ibtnSearch.startAnimation(animation);
            title.setVisibility(View.VISIBLE);
            title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.view_zoom_out));
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
//            edtSearch.setText("");
            edtSearch.clearFocus();
            recycleViewSuggest.setVisibility(View.GONE);
            loadHistory();
            showHistory();
        } else {
            edtSearch.setText("");
            recycleViewSuggest.setAdapter(new RecycleViewSuggestAdapter(this, null, recycleViewSuggest));
        }
    }

    public void showEdtSearch() {
        ibtnSearch.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.searchbar_appear);
        searchBar.setVisibility(View.VISIBLE);
        searchBar.startAnimation(animation);
//        title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.view_zoom_in));
        title.setVisibility(View.GONE);
        edtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    public void onClickSearch() {
        hideHistory();
        recycleViewSuggest.setVisibility(View.VISIBLE);
        showEdtSearch();
    }

    public void onClickMore() {
        PopupMenu pm = new PopupMenu(this, ibtnMore);
        pm.getMenuInflater().inflate(R.menu.menu_popup_main, pm.getMenu());
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_setting:
                        Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intentSetting);
                        break;
                    case R.id.action_information:
                        Intent intentInformation = new Intent(MainActivity.this, InformationActivity.class);
                        startActivity(intentInformation);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        pm.show();
    }

    @Override
    public void onBackPressed() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            onClickCancle();
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int idClicked = v.getId();
        switch (idClicked) {
            case R.id.funtion_favorite:
                Intent intentFavorite = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intentFavorite);
                break;
            case R.id.funtion_iv:
                Intent intentIv = new Intent(MainActivity.this, IrregularVerbsActivity.class);
                startActivity(intentIv);
                break;
            case R.id.funtion_idioms:
                Intent intentIdioms = new Intent(MainActivity.this, IdiomsActivity.class);
                startActivity(intentIdioms);
                break;
            case R.id.funtion_toeic:
                Intent intentToeic = new Intent(MainActivity.this, ToeicActivity.class);
                startActivity(intentToeic);
                break;
            case R.id.fab:
                onClickFab();
                break;
            case R.id.action_cancel:
                onClickCancle();
                break;
            case R.id.action_search:
                onClickSearch();
                break;
            case R.id.action_record:
                promptSpeechInput();
                break;
            case R.id.action_more:
                onClickMore();
                break;
            case R.id.btn_window:
                showTraNhanh();
                break;
            default:
                break;
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.speech_something));
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            Log.e("Loi", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    onClickSearch();
                    edtSearch.setText("");
                    edtSearch.append(result.get(0).toLowerCase());
                }
                break;
            }

        }
    }

    public void listenerKeyboard() {
        final CoordinatorLayout contentView = findViewById(R.id.contentView);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    keyboardOpening = true;
                    fab.setImageResource(R.drawable.ic_action_close_keyboard);
                } else {
                    // keyboard is closed
                    keyboardOpening = false;
                    fab.setImageResource(R.drawable.ic_action_search);
                }
            }
        });
    }

    @Override
    public void deleteWord() {
        loadHistory();
    }

    class SearchBackground extends AsyncTask<KeyAndValue, RecycleViewSuggestAdapter, Void> {

        @Override
        protected Void doInBackground(final KeyAndValue... keyAndValues) {
            final ArrayList<KeyAndValue> arrKV = new ArrayList<>();
            final int length = keyAndValues.length;
            if (length > MaxLoadSize) {
                for (int i = 0; i < MaxLoadSize; i++) {
                    arrKV.add(keyAndValues[i]);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    arrKV.add(keyAndValues[i]);
                }
            }

            final RecycleViewSuggestAdapter recycleViewSuggestAdapter = new RecycleViewSuggestAdapter(MainActivity.this, arrKV, recycleViewSuggest);
            recycleViewSuggestAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (arrKV.size() < length) {
                        arrKV.add(null);
                        recycleViewSuggestAdapter.notifyItemInserted(arrKV.size() - 1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                arrKV.remove(arrKV.size() - 1);
                                recycleViewSuggestAdapter.notifyItemRemoved(arrKV.size());
                                int current = arrKV.size();
                                // load more
                                if ((current + MaxLoadSize) < length) {
                                    for (int i = current; i < (current + MaxLoadSize); i++) {
                                        arrKV.add(keyAndValues[i]);
                                        recycleViewSuggestAdapter.notifyItemInserted(arrKV.size() - 1);
                                    }
                                } else {
                                    for (int i = current; i < length; i++) {
                                        arrKV.add(keyAndValues[i]);
                                        recycleViewSuggestAdapter.notifyItemInserted(arrKV.size() - 1);
                                    }
                                }

                                recycleViewSuggestAdapter.setLoaded();
                            }
                        }, 1000);
                    }
                }
            });
            publishProgress(recycleViewSuggestAdapter);
            return null;
        }

        @Override
        protected void onProgressUpdate(RecycleViewSuggestAdapter... recycleViewAdapters) {
            recycleViewSuggest.setAdapter(recycleViewAdapters[0]);
            super.onProgressUpdate(recycleViewAdapters);
        }
    }
}
