package com.khoa.tudien.Adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.khoa.tudien.Interface.OnLoadMoreListener;
import com.khoa.tudien.Model.KeyAndValue;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.R;
import com.khoa.tudien.Controller.Reformat;
import com.khoa.tudien.Model.Word;

import java.util.ArrayList;
import java.util.Locale;


public class RecycleViewSuggestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String Favorite_Table = "Favorite";
    private String History_Table = "History";
    private ArrayList<KeyAndValue> arrKV = new ArrayList<>();
    private Context context;
    private SparseBooleanArray sparseBooleanArrayExpand;
    private TextToSpeech textToSpeech;
    private MyDataBase myDataBase;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;

    public RecycleViewSuggestAdapter(Context context, ArrayList<KeyAndValue> arr, RecyclerView recyclerView) {
        this.sparseBooleanArrayExpand = new SparseBooleanArray();
        this.arrKV = arr;
        this.context = context;
        this.myDataBase = new MyDataBase(context);
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate((float) 0.8);
                }
            }
        });
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                Log.e("Loi", String.valueOf(totalItemCount));
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_word_suggest, viewGroup, false);
//        return new ViewHolderItem(view);
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_word_suggest, viewGroup, false);
            return new ViewHolderItem(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_progressbar, viewGroup, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof ViewHolderItem) {
            final ViewHolderItem holderItem = (ViewHolderItem) viewHolder;
            final String key = arrKV.get(i).getKey();
            final String value = arrKV.get(i).getValue();
            final ArrayList<String> arrString = new Reformat().spilit(value);
            final String phonetic = new Reformat().getPhonetic(arrString);
            final String simpleMeaning = new Reformat().getSimpleMeaning(arrString);
            if (!phonetic.isEmpty()) {
                holderItem.txtPhonetic.setText(phonetic);
            } else {
                holderItem.txtPhonetic.setVisibility(View.GONE);
            }
            if (checkFavorite(key))
                holderItem.imageFavorite.setImageResource(R.drawable.ic_favorite);
            holderItem.txtWord.setText(key);
            holderItem.txtMeaning.setText(simpleMeaning);
            holderItem.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!sparseBooleanArrayExpand.get(holderItem.getAdapterPosition(), false)) {
//                    holderItem.imageExpand.setImageResource(R.drawable.ic_expand);
                        holderItem.imageExpand.startAnimation(AnimationUtils.loadAnimation(context, R.anim.view_rotate_open));
                        holderItem.txtMeaning.setMaxLines(100);
                        holderItem.txtMeaning.setText(Html.fromHtml(new Reformat().toViewFormat(key, arrString)));
                        sparseBooleanArrayExpand.put(holderItem.getAdapterPosition(), true);
                    } else {
//                    holderItem.imageExpand.setImageResource(R.drawable.ic_right);
                        holderItem.imageExpand.startAnimation(AnimationUtils.loadAnimation(context, R.anim.view_rotate_close));
                        holderItem.txtMeaning.setMaxLines(1);
                        holderItem.txtMeaning.setText(simpleMeaning);
                        sparseBooleanArrayExpand.put(holderItem.getAdapterPosition(), false);
                    }
                    addToHistory(key, phonetic, simpleMeaning, arrString);
                }
            });

            holderItem.imageSpell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech.speak(key, TextToSpeech.QUEUE_FLUSH, null);
                    addToHistory(key, phonetic, simpleMeaning, arrString);
                }
            });

            holderItem.imageFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkFavorite(key)) {
                        holderItem.imageFavorite.setImageResource(R.drawable.ic_favorite_border);
                        myDataBase.deleteWord(Favorite_Table, key);
                    } else {
                        holderItem.imageFavorite.setImageResource(R.drawable.ic_favorite);
                        String content = new Reformat().toViewFormat(key, arrString);
                        myDataBase.addWord(Favorite_Table, new Word(key, phonetic, simpleMeaning, content));
                    }
                    addToHistory(key, phonetic, simpleMeaning, arrString);
                }
            });
        } else if (viewHolder instanceof ViewHolderLoading){
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private void addToHistory(String key, String phonetic, String simpleMeaning, ArrayList<String> arrString){
        String content = new Reformat().toViewFormat(key, arrString);
        if(myDataBase.exist(History_Table, key)) myDataBase.deleteWord(History_Table, key);
        myDataBase.addWord(History_Table, new Word(key, phonetic, simpleMeaning, content));
    }

    private boolean checkFavorite(String key) {
        return myDataBase.exist(Favorite_Table, key);
    }

    @Override
    public int getItemCount() {
        return arrKV == null ? 0 : arrKV.size();
    }

    @Override
    public int getItemViewType(int position) {
        return arrKV.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    class ViewHolderItem extends RecyclerView.ViewHolder {
        private TextView txtWord;
        private TextView txtMeaning;
        private TextView txtPhonetic;
        private ImageView imageExpand;
        private ImageView imageSpell;
        private ImageView imageFavorite;
        private LinearLayout itemLayout;

        ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.item_layout);
            txtWord = itemView.findViewById(R.id.txt_word);
            txtMeaning = itemView.findViewById(R.id.txt_meaning);
            txtPhonetic = itemView.findViewById(R.id.txt_phonetic);
            imageExpand = itemView.findViewById(R.id.img_expand);
            imageSpell = itemView.findViewById(R.id.img_spell);
            imageFavorite = itemView.findViewById(R.id.img_favorite);
        }
    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }
}
