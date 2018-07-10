package me.checkin.android.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Device display helper util
 * <p/>
 * Created by alvince on 16/7/19.
 *
 * @version 2.2, 8/4/2017
 */
@SuppressWarnings("unused")
public class DisplayUtil {

    private static final String IDENTIFIER_NAV_BAR_SIZE = "navigation_bar_height";
    private static final String IDENTIFIER_STATUS_BAR_SIZE = "status_bar_height";

    /**
     * Convert dimens from dip to px.
     */
    public static float convertDp2Px(@Nullable Context context, float dip) {
        Resources r = context != null ? context.getResources() : Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }

    // 将px值转换为dip或dp值，保证尺寸大小不变
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 将px值转换为sp值，保证文字大小不变
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    // 将sp值转换为px值，保证文字大小不变
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    // 屏幕宽度（像素）
    public static int getWindowWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    // 屏幕高度（像素）
    public static int getWindowHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getActionBarHeight(@NonNull Context context) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
    }

    public static int getNavigationBarSize(@NonNull Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(IDENTIFIER_NAV_BAR_SIZE, "dimen", "android");
        return resourceId > 0 ? res.getDimensionPixelSize(resourceId) : 0;
    }

    public static int getStatusBarHeight(@NonNull Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(IDENTIFIER_STATUS_BAR_SIZE, "dimen", "android");
        return resourceId > 0 ? res.getDimensionPixelSize(resourceId) : 0;
    }

    public static float getScreenHeight(@NonNull Context context) {
        Resources res = context.getResources();
        float heightDp = res.getConfiguration().screenHeightDp;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, res.getDisplayMetrics());
    }

    public static float getScreenWidth(@NonNull Context context) {
        Resources res = context.getResources();
        float widthDp = res.getConfiguration().screenWidthDp;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, res.getDisplayMetrics());
    }


    /**
     * Obtain display dimension size.
     *
     * @param context Context
     * @param rs      Integer array as result tray
     */
    public static int[] obtainDisplaySize(@NonNull Context context, @NonNull @Size(2) int[] rs) {
        if (rs.length < 2) {
            throw new IllegalArgumentException("Result array size must be 2 at least.");
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        rs[0] = metrics.widthPixels;
        rs[1] = metrics.heightPixels;
        return rs;
    }

    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    public static void hideNavigationBar(Activity activity) {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; // hide nav bar
        if (Build.VERSION.SDK_INT >= 19) {
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        systemUiVisibility |= uiFlags;
        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

}