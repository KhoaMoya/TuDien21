package com.khoa.tudien.Activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.khoa.tudien.Controller.SortWordByAbc;
import com.khoa.tudien.Interface.CallBackDelete;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.R;
import com.khoa.tudien.Adapter.RecycleViewWordAdapter;
import com.khoa.tudien.Model.Word;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteActivity extends AppCompatActivity implements CallBackDelete {

    private RecyclerView recyclerView;
    private ArrayList<Word> arrFavorite;
    private MyDataBase myDataBase;
    private String Favorite_Table = "Favorite";
    private SearchView edtSearh;
    private int MaxLoadFavorite = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        loadBackdrop();
        Toolbar toolbar = findViewById(R.id.tool_bar_favorite);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.my_word));

        myDataBase = new MyDataBase(this);
        recyclerView = findViewById(R.id.recycler_view_favorite);
        recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadFavorite();
    }
    private void loadBackdrop() {
        final ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this)
                .load(R.drawable.custom_background02)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(imageView);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite_activity, menu);
        edtSearh = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchFavorite();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_sort_by_abc:
                sortByAbc();
                break;
            case R.id.action_sort_by_time:
                sortByTime();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchFavorite() {
        edtSearh.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<Word> arr = new ArrayList<>();
                for (Word word : arrFavorite) {
                    if (word.getWord().toLowerCase().contains(newText)) arr.add(word);
                }
                recyclerView.setAdapter(new RecycleViewWordAdapter(FavoriteActivity.this, Favorite_Table, arr));
                return true;
            }
        });
    }

    @Override
    public void deleteWord() {
        loadFavorite();
    }

    public void loadFavorite() {
        arrFavorite = myDataBase.getWord(Favorite_Table, MaxLoadFavorite);
        Collections.reverse(arrFavorite);
        recyclerView.setAdapter(new RecycleViewWordAdapter(this, Favorite_Table, arrFavorite));
    }

    public void sortByTime() {
        arrFavorite = myDataBase.getWord(Favorite_Table, 20);
        Collections.reverse(arrFavorite);
        recyclerView.setAdapter(new RecycleViewWordAdapter(this, Favorite_Table, arrFavorite));
    }

    public void sortByAbc() {
        Collections.sort(arrFavorite, new SortWordByAbc());
        recyclerView.setAdapter(new RecycleViewWordAdapter(this, Favorite_Table, arrFavorite));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            overridePendingTransition(R.anim.popup_right,R.anim.popup_zoom_out);
        }
    }
}
