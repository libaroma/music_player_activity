package cool.hyz.musicplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @author xujiayi
 * @date 2024/7/11
 * 我只是个自由的主！
 */
public class MainActivity  extends AppCompatActivity {

    private RelativeLayout mLayout;
    private ImageView mCover;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        mLayout = findViewById(R.id.music_action_bar);
        mLayout.setOnClickListener(v->{
            MusicPlayerActivity.show(this);
        });
        mCover = findViewById(R.id.song_cover);
        Glide.with(this).load(R.mipmap.ic_launcher)
                .apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.ic_launcher))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(mCover);
    }
}
