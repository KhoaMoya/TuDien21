package com.khoa.scrollabledialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchListener implements View.OnTouchListener {
    private Dialog dialog;
    private ScrollableScrollView scrollView;
    private View baseLayout;
    private Handler handler;
    private Runnable runnable;

    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;

    private boolean isScrolled = false;
    private boolean closeDialog = false;
    private boolean scrollBottom = false;
    private boolean scrollTop = false;
    private boolean scrollLayout = false;
    private float alpha;


    public OnTouchListener(View baseLayout, ScrollableScrollView scrollView, Dialog dialog) {
        this.baseLayout = baseLayout;
        this.scrollView = scrollView;
        this.dialog = dialog;
        this.handler = new Handler();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Lấy vị trí của ngón tay
        final int Y = (int) event.getRawY();

        switch (event.getAction()) {

            // Khi vừa đặt tay vào màn hình
            case MotionEvent.ACTION_DOWN:

                // lấy chiều cao mặc định của layout dialog
                defaultViewHeight = baseLayout.getHeight();

                // khởi tạo vị trí ngón tay lần trước bằng lần vị trí lần đầu
                previousFingerPosition = Y;

                // lấy vị trí mặc định của layout dialog
                baseLayoutPosition = (int) baseLayout.getY();

                // hủy hành động cuộn về khi chạm
                if (runnable != null)
                    handler.removeCallbacks(runnable);

                break;

            // Khi bỏ tay ra khỏi màn hình
            case MotionEvent.ACTION_UP:

                // kích hoạt cuộn cho scrollView
                scrollView.setEnableScrolling(true);
                scrollLayout = false;
                // Xử lý nếu người dùng đã cuộn
                if (isScrolled) {
                    // Lấy trị của layout dialog hiện tại
                    int currentYPosition = (int) baseLayout.getY();
                    // tính khoảng cách giữa vị trí của layout mặc định và vị trí của layout hiện tại
                    float m = Math.abs(baseLayoutPosition - currentYPosition);
                    // kiểm tra xem scroll đã đủ để close dialog chưa
                    if (m > defaultViewHeight / 3) {
                        closeDialog = true;
                    }
                    // Chỉ đóng dialog khi scrollView đã scroll tới Top hoặc Bottom và closeDialog==true
                    if (scrollTop  && closeDialog) {
                        dialog.dismiss();
                    }
                    // Không đóng dialog khi scroll chưa đủ dài hoặc scroll ở Bottom
                    else {
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (Math.round(baseLayout.getY()) != 0) {
                                    baseLayout.setY(baseLayout.getY() - baseLayout.getY() / 5);
//                                    if (alpha < 1) {
//                                        alpha = alpha + 0.2f;
//                                        baseLayout.setAlpha(alpha);
//                                    }
                                    handler.post(runnable);
                                }
                            }
                        };
                        handler.post(runnable);
                    }
                    // scroll bây giờ không được cuộn nữa
                    isScrolled = false;
                }
                break;

            // Khi di chuyển (scroll)
            case MotionEvent.ACTION_MOVE:


                // scroll đã được cuộn
                if (!isScrolled) isScrolled = true;
// xác định xem scrollView đang đã cuộn đến top hay bottom chưa
//                scrollBottom = scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY());
                scrollTop = scrollView.getScrollY() == 0;

                // độ mờ khi scroll
//                alpha = defaultViewHeight / (m + 100) / 5;
//                baseLayout.setAlpha(alpha);


                // tính khoảng cách giữa vị trí của ngón tay hiện tại với vị trí lần chạm trước đó
                int n = Y - previousFingerPosition;

                // cuộn cả layout khi scroll ở Top và đang cuộn xuống
                if (scrollTop && n > 0) {
                    scrollLayout = true;
                    scrollView.setEnableScrolling(false);
                    baseLayout.setY((baseLayout.getY() + n));
                }
                // cuộn cả layout khi scroll ở Top và đang cuộn xuống
                if (scrollTop && scrollLayout && n < 0) {
                    scrollView.setEnableScrolling(false);
                    baseLayout.setY((baseLayout.getY() + n));
                }
                // cuộn cả layout khi scroll ở Bottom và đang cuộn lên
//                if (scrollBottom && n < 0) {
//                    scrollLayout = true;
//                    scrollView.setEnableScrolling(false);
//                    baseLayout.setY((baseLayout.getY() + n));
//                }
//                if (scrollBottom && scrollLayout && n > 0) {
//                    scrollView.setEnableScrolling(false);
//                    baseLayout.setY((baseLayout.getY() + n));
//                }
                break;
        }
        previousFingerPosition = Y;
        return false;
    }

}
