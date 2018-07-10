package me.checkin.android;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujunchen
 * on 17/9/1.
 */
public class CheckInPageAdapter extends PagerAdapter {

    public CheckInPageAdapter() {
    }

    private List<CheckInPhaseView> signMaskViews = new ArrayList<>();

    @Override
    public int getCount() {
        return signMaskViews == null ? 0 : signMaskViews.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CheckInPhaseView signMaskView = signMaskViews.get(position);
        if (signMaskView.getParent() != null) {
            container.removeView(signMaskView);
        }
        container.addView(signMaskView);
        return signMaskView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setSignMaskViews(List<CheckInPhaseView> signMaskViews) {
        this.signMaskViews = signMaskViews;
        notifyDataSetChanged();
    }

    public List<CheckInPhaseView> getSignMaskViews() {
        return signMaskViews;
    }

    public void destroy() {
        for (CheckInPhaseView checkInPhaseView : signMaskViews) {
            checkInPhaseView.destroy();
        }
        signMaskViews.clear();
        signMaskViews = null;
    }
}
