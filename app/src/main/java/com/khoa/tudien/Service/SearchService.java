package com.khoa.tudien.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.khoa.tudien.Activity.SearchFloatingViewActivity;
import com.khoa.tudien.R;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class SearchService extends Service implements FloatingViewListener {

    private static final String TAG = "Loi";
    public static final String EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area";
    private static final int NOTIFICATION_ID = 9083150;
    private FloatingViewManager mFloatingViewManager;

    public SearchService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final ImageView iconView = (ImageView) inflater.inflate(R.layout.widget_chathead, null, false);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Click");
            }
        });
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), SearchFloatingViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(intent);
                    stopSelf();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("SETTING", MODE_PRIVATE);
        int x = sharedPreferences.getInt("X", 0);
        int y = sharedPreferences.getInt("Y", 0);
        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        mFloatingViewManager.setSafeInsetRect((Rect) intent.getParcelableExtra(EXTRA_CUTOUT_SAFE_AREA));
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.overMargin = (int) (16 * metrics.density);
        options.animateInitialMove = true;
        options.floatingViewX = x;
        options.floatingViewY = y;
        mFloatingViewManager.addViewToWindow(iconView, options);

//        startForeground(NOTIFICATION_ID, createNotification(this));

        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onFinishFloatingView() {
        stopSelf();
        Log.e(TAG, "finish sẻvice");
    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        if (isFinishing) {
            Log.e(TAG, "delete soon");
        } else {
            Log.e(TAG, "touch_finished_position" + x +"-"+ y);
            SharedPreferences sharedPreferences = getSharedPreferences("SETTING", MODE_PRIVATE);
            sharedPreferences.edit().putInt("X", x).apply();
            sharedPreferences.edit().putInt("Y", y).apply();
        }
    }


    private void destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }


    private static Notification createNotification(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default_floatingview_channel_id");
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_logo_tudien);
        builder.setContentTitle("Title");
        builder.setContentText("Content");
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        return builder.build();
    }
}
