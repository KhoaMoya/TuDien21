package com.khoa.tudien.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.khoa.tudien.Model.IrregularVerbs;
import com.khoa.tudien.Adapter.ListViewIrregularAdapter;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.R;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class IrregularVerbsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<IrregularVerbs> arrIv;
    private SearchView searchView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irregular_verbs);
        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.irregular_verbs));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);

        listView = findViewById(R.id.listView_iv);
        OverScrollDecoratorHelper.setUpOverScroll (listView);
        arrIv = new MyDataBase(this).getAllIv();
        listView.setAdapter(new ListViewIrregularAdapter(this, arrIv));
    }

    public void initSearchIv(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<IrregularVerbs> arr = new ArrayList<>();
                for(IrregularVerbs iv : arrIv){
                    if(iv.getInfinitive().contains(s.toLowerCase())) arr.add(iv);
                }
                listView.setAdapter(new ListViewIrregularAdapter(IrregularVerbsActivity.this, arr));
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_iv_activity, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        initSearchIv();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            overridePendingTransition(R.anim.popup_right,R.anim.popup_zoom_out);
        }
    }
}
