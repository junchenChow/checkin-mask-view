package me.checkin.android;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import me.checkin.android.helper.AnimatorListenerSimple;
import me.checkin.android.helper.BaseFrameView;
import me.checkin.android.helper.BitmapHelper;
import me.checkin.android.helper.CheckInMonthly;

/**
 * Created by zhoujunchen
 * on 17/9/1.
 */

public class CheckInPhaseView extends BaseFrameView implements CheckInMaskView.OnMaskListener {

    public static final int CHECK_IN_TYPE_WHALE = 0;
    public static final int CHECK_IN_TYPE_BEAR = 1;
    public static final int CHECK_IN_TYPE_CRANE = 2;

    @BindView(R.id.mask_view)
    CheckInMaskView maskView;

    private int wh;
    private int checkInType;
    private OnCheckInListener onCheckInListener;

    public interface OnCheckInListener {
        void onCheckInComplete();
    }

    public CheckInPhaseView(Context context) {
        super(context);
    }

    public CheckInPhaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckInPhaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int checkInType) {
        this.checkInType = checkInType;
        wh = getResources().getDimensionPixelSize(R.dimen.check_in_min_wh);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.view_check_in;
    }

    @Override
    public void onStartDrawMaskComplete() {
        if (onCheckInListener != null) {
            onCheckInListener.onCheckInComplete();
        }
    }

    public void startCheckIn(OnCheckInListener onCheckInListener) {
        this.onCheckInListener = onCheckInListener;
        maskView.start(this);
    }

    public void updateTodayCheckInUI(int wh, int monthlyDay, Integer[] icons) {
        int convertDay = monthlyDay - 1;
        maskView.updateWh(wh);
        int backgroundWh = wh > 0 ? wh : this.wh;
        switch (checkInType) {
            case CHECK_IN_TYPE_WHALE: {
                maskView.setImageBitmap(monthlyDay >= 9 ? BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_whale_blue_bg, backgroundWh, backgroundWh)
                        : BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_whale_bg, backgroundWh, backgroundWh));
                if (monthlyDay <= 9)
                    maskView.updateTodayCheckInUI(monthlyDay, icons[convertDay + 1], getComposeIcons(convertDay, icons, checkInType));
                break;
            }
            case CHECK_IN_TYPE_BEAR: {
                maskView.setImageBitmap(monthlyDay >= 18 ? BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_bear_blue_bg, backgroundWh, backgroundWh)
                        : BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_bear_bg, backgroundWh, backgroundWh));
                if (monthlyDay >= 9 && monthlyDay < 19)
                    maskView.updateTodayCheckInUI(monthlyDay, icons[convertDay + 1], getComposeIcons(convertDay, icons, checkInType));
                break;
            }
            case CHECK_IN_TYPE_CRANE:
                maskView.setImageBitmap(monthlyDay >= 27 ? BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_crane_red_bg, backgroundWh, backgroundWh)
                        : BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_crane_bg, backgroundWh, backgroundWh));
                if (monthlyDay >= 18 && monthlyDay < 27)
                    maskView.updateTodayCheckInUI(monthlyDay, icons[convertDay + 1], getComposeIcons(convertDay, icons, checkInType));
                break;
        }
    }

    public void checkComplete(CheckInMonthly checkInMonthly, int checkInType) {
        if (isCompleteCurrPhase(checkInMonthly)) {
            maskView.animate().alpha(0f).setListener(new AnimatorListenerSimple() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    maskView.complete();
                    switch (checkInType) {
                        case CHECK_IN_TYPE_WHALE:
                            maskView.setImageBitmap(BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_whale_blue_bg, wh, wh));
                            break;
                        case CHECK_IN_TYPE_BEAR:
                            maskView.setImageBitmap(BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_bear_blue_bg, wh, wh));
                            break;
                        case CHECK_IN_TYPE_CRANE:
                            maskView.setImageBitmap(BitmapHelper.getScaleBitmapByByte(getContext(), R.mipmap.ic_checkin_crane_red_bg, wh, wh));
                            break;
                    }
                    maskView.animate().alpha(1.0f).setDuration(400L).start();

                }
            }).setDuration(400L).start();
        }
    }

    public boolean isCompleteCurrPhase(CheckInMonthly checkInMonthly) {
        if (checkInMonthly == null)
            return false;
        int monthlySignedInDays = checkInMonthly.getMonthlySignedInDays();
        return monthlySignedInDays == 9 || monthlySignedInDays == 18 || monthlySignedInDays == 27;
    }

    private List<Integer> convertList(Integer[] integers) {
        List<Integer> integerArrayList = new ArrayList<>();
        Collections.addAll(integerArrayList, integers);
        return integerArrayList;
    }

    private List<Integer> getComposeIcons(int convertDay, Integer[] icons, int checkInType) {
        List<Integer> integers = convertList(icons);
        List<Integer> finalIcons = new ArrayList<>();
        int startPosition = checkInType == CHECK_IN_TYPE_WHALE ? 0 : checkInType == CHECK_IN_TYPE_BEAR ? 9 : 18;
        for (int i = 0, len = icons.length; i < len; i++) {
            if (i >= startPosition && i <= convertDay)
                finalIcons.add(integers.get(i));
        }
        return finalIcons;
    }

    public void destroy() {
        onCheckInListener = null;
        maskView.destroy();
    }
}
