package cool.hyz.musicplayer;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cool.hyz.musicplayer.base.BaseApplication;
import cool.hyz.musicplayer.utils.DateUtils;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayerActivity";

    private RelativeLayout mLayout;
    private ImageView mCover;
    private TextView mTitle;
    private TextView mSinger;
    private ImageView mPlayOrPause;
    private ImageView mBackground;
    private ImageView mPlayerBar;
    private RelativeLayout mPlayer;
    private SeekBar mProgress;
    private ImageView mNext;
    private ImageView mPre;
    private ImageView mPlayMode;
    private ImageView mPlayList;
    private TextView mCurrent;
    private TextView mDuration;
    private MediaPlayer mMediaPlayer;
    private RotateAnimation mPlayerBarRotationAnimExit;
    private RotateAnimation mPlayerBarRotationAnimEnter;

    public static void show(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, MusicPlayerActivity.class));
        activity.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        initView();
        initPlayer();
    }

    private void initView() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
        setNavAndStatusBarTransparent(this);
        mLayout = findViewById(R.id.media_player_layout);

        mCover = findViewById(R.id.song_cover);
        Glide.with(this).load(R.drawable.lover_cover)
                .apply(RequestOptions.circleCropTransform())
                .into(mCover);

        mTitle = findViewById(R.id.song_title);
        mSinger = findViewById(R.id.song_singer);
        mPlayOrPause = findViewById(R.id.song_play_or_pause);
        mBackground = findViewById(R.id.song_background);
        Glide.with(this).load(R.drawable.lover_cover)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                .into(mBackground);

        mPlayOrPause.setOnClickListener(v -> {
            updatePlayingState();
        });

        mPlayerBar = findViewById(R.id.player_bar);
        mPlayer = findViewById(R.id.player);
        mProgress = findViewById(R.id.song_progress);

        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mNext = findViewById(R.id.song_next);
        mNext.setOnClickListener(v -> {
        });

        mPre = findViewById(R.id.song_pre);
        mPre.setOnClickListener(v -> {
        });

        mPlayMode = findViewById(R.id.song_play_mode);

        mPlayList = findViewById(R.id.song_play_list);
        mPlayMode.setOnClickListener(v -> {
        });

        mCurrent = findViewById(R.id.song_current);
        mDuration = findViewById(R.id.song_duration);

    }

    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.ronghaoli_lover);
        }
        Glide.with(this).
                load(R.drawable.lover_cover).apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(mCover);

        Glide.with(this).
                load(R.drawable.lover_cover).
                apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                into(mBackground);
        mTitle.setText("恋人");
        mSinger.setText("李荣浩");
    }

    private void updatePlayingState() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mPlayOrPause.setImageResource(R.drawable.ic_music_pause);
            mMediaPlayer.pause();
        } else {
            mPlayOrPause.setImageResource(R.drawable.ic_music_play);
            mMediaPlayer.start();
        }
        //定时任务-更新进度
        initTimer();
        initAnim();
    }

    private Timer timer;

    private void initTimer() {
        boolean playing = mMediaPlayer.isPlaying();
        if (timer != null) timer.cancel();
        if (!playing) return;
        timer = new Timer();
        mProgress.setMax(mMediaPlayer.getDuration());
        mDuration.setText(DateUtils.formatTime(mMediaPlayer.getDuration()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mProgress.setMin(0);
        }
        mProgress.setProgress(mMediaPlayer.getCurrentPosition());
        BaseApplication.getHandler().post(() -> {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MusicPlayerActivity.this.runOnUiThread(() -> {
                        long currentPosition = mMediaPlayer.getCurrentPosition();
                        mProgress.setProgress((int) currentPosition, true);
                        mCurrent.setText(DateUtils.formatTime(currentPosition));
                    });
                }
            }, 0, 1000);
        });
    }

    private ObjectAnimator mPlayerRotationAnim;

    private void initAnim() {
        if (mPlayerRotationAnim != null) {
            mPlayerRotationAnim.pause();
        }
        if (mPlayerBarRotationAnimEnter != null) mPlayerBarRotationAnimEnter.reset();
        mPlayerBarRotationAnimEnter = new RotateAnimation(0.0f, 25.0f, mPlayerBar.getPivotX(),
                mPlayerBar.getPivotY());
        mPlayerBarRotationAnimEnter.setInterpolator(new LinearInterpolator());
        mPlayerBarRotationAnimEnter.setFillEnabled(true);
        mPlayerBarRotationAnimEnter.setFillAfter(true);
        mPlayerBarRotationAnimEnter.setDuration(300);

        if (mPlayerBarRotationAnimExit != null) mPlayerBarRotationAnimExit.reset();
        mPlayerBarRotationAnimExit = new RotateAnimation(25.0f, 0.f, mPlayerBar.getPivotX(),
                mPlayerBar.getPivotY());
        mPlayerBarRotationAnimExit.setInterpolator(new LinearInterpolator());
        mPlayerBarRotationAnimExit.setFillEnabled(true);
        mPlayerBarRotationAnimExit.setFillAfter(true);
        mPlayerBarRotationAnimExit.setDuration(300);
        if (mMediaPlayer.isPlaying()) {

            mPlayerBar.clearAnimation();
            mPlayerBar.setAnimation(mPlayerBarRotationAnimEnter);

            if (mPlayerRotationAnim == null) {
                mPlayerRotationAnim = ObjectAnimator.ofFloat(mPlayer, "rotation", mPlayer.getRotation(), 360.0f);
                mPlayerRotationAnim.setDuration(5000);
                mPlayerRotationAnim.setRepeatCount(Animation.INFINITE);
                mPlayerRotationAnim.setInterpolator(new LinearInterpolator());
                mPlayerRotationAnim.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
                mPlayerRotationAnim.start();
            } else
                mPlayerRotationAnim.resume();

        } else {
            mPlayerBar.clearAnimation();
            mPlayerBar.setAnimation(mPlayerBarRotationAnimExit);
        }
    }

    public void setNavAndStatusBarTransparent(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            Window window = activity.getWindow();
            if (window != null) {
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    float touchDownY = 0;
    float layoutY = 0;
    float closeY = (float) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.3);
    boolean isTouched = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: ACTION_DOWN....");
                touchDownY = event.getY();
                isTouched = true;
                layoutY = mLayout.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouched) {
                    float aimY = layoutY + event.getY() - touchDownY;
                    if (aimY < 0) aimY = 0;
                    Log.d(TAG, "onTouchEvent: " + aimY);
                    mLayout.setY(aimY);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: ACTION_UP....");
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent: ACTION_CANCEL....");
                isTouched = false;
                if (mLayout.getY() < closeY) {
                    ObjectAnimator enterAnim = ObjectAnimator.ofFloat(mLayout, "y", mLayout.getY(), 0);
                    enterAnim.setDuration(300);
                    enterAnim.start();
                } else {
                    finish();
                }
                break;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            if (timer != null) {
                timer.cancel();
            }
        }
    }
}