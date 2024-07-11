package cool.hyz.musicplayer.base;

import android.app.Application;
import android.os.Handler;


/**
 * @author xujiayi
 * @date 2024/7/11
 * 我只是个自由的主！
 */
public class BaseApplication extends Application {

    private static BaseApplication sInstance;

    public static BaseApplication getInstance() {
        return sInstance;
    }

    private static Handler sHandler;

    public  static Handler getHandler(){
        if (sHandler ==null) {
            synchronized (Handler.class){
                if(sHandler==null){
                    sHandler = new Handler();
                }
            }
        }
        return sHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
