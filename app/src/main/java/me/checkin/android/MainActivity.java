package me.checkin.android;

import android.animation.ValueAnimator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.checkin.android.helper.CheckInMonthly;
import me.checkin.android.helper.DisplayUtil;
import me.checkin.android.observable.ViewAnimHelper;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindString(R.string.text_check_in)
    String checkInStr;
    @BindString(R.string.text_check_in_finish)
    String checkInFinishStr;
    @BindDimen(R.dimen.check_in_default_margin_top)
    int defaultMarginTop;
    @BindDimen(R.dimen.check_in_nav_margin)
    int navMargin;
    @BindDimen(R.dimen.check_in_nav_margin_5)
    int navMargin5;

    @BindView(R.id.vp_check_in_progress)
    ViewPager viewPager;
    @BindView(R.id.rl_check_in_layout)
    LinearLayout linearLayout;
    @BindView(R.id.tv_check_in_days)
    TextView tvCheckInDays;
    @BindView(R.id.tv_check_in_point)
    TextView tvCheckInPoints;
    @BindView(R.id.tv_check_in_get_points)
    TextView tvCheckInGetPoints;
    @BindView(R.id.tv_check_in_reward)
    TextView tvCheckInReward;
    @BindView(R.id.tv_check_in_reward_level)
    TextView tvCheckInRewardLevel;
    @BindView(R.id.iv_next_mask_view)
    ImageView ivNextMaskView;
    @BindView(R.id.ll_sign_text_layout)
    LinearLayout llSignTextLayout;
    @BindView(R.id.check_in_button)
    CheckInStartButton checkInStartButton;

    private int mCurrCheckInDays;
    private ValueAnimator mTranslationAnimator;
    private CheckInPageAdapter mCheckInPageAdapter;
    private CheckInMonthly mCheckInMonthly = new CheckInMonthly();

    private Integer[] mCheckInIcons = new Integer[]{
            R.mipmap.ic_checkin_whale_1, R.mipmap.ic_checkin_whale_2, R.mipmap.ic_checkin_whale_3, R.mipmap.ic_checkin_whale_4,
            R.mipmap.ic_checkin_whale_5, R.mipmap.ic_checkin_whale_6, R.mipmap.ic_checkin_whale_7, R.mipmap.ic_checkin_whale_8, R.mipmap.ic_checkin_whale_9, // END index 8
            R.mipmap.ic_checkin_bear_1, R.mipmap.ic_checkin_bear_2, R.mipmap.ic_checkin_bear_3, R.mipmap.ic_checkin_bear_4,
            R.mipmap.ic_checkin_bear_5, R.mipmap.ic_checkin_bear_6, R.mipmap.ic_checkin_bear_7, R.mipmap.ic_checkin_bear_8, R.mipmap.ic_checkin_bear_9, // END index 17
            R.mipmap.ic_checkin_crane_1, R.mipmap.ic_checkin_crane_2, R.mipmap.ic_checkin_crane_3, R.mipmap.ic_checkin_crane_4,
            R.mipmap.ic_checkin_crane_5, R.mipmap.ic_checkin_crane_6, R.mipmap.ic_checkin_crane_7, R.mipmap.ic_checkin_crane_8, R.mipmap.ic_checkin_crane_9}; // END index 26

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.bind(this);

        mCheckInPageAdapter = new CheckInPageAdapter();
        mCheckInPageAdapter.setSignMaskViews(fillCheckInViews());
        viewPager.setAdapter(mCheckInPageAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(this);

        if (DisplayUtil.isNavigationBarShow(this)) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
            layoutParams.setMargins(0, layoutParams.topMargin - navMargin, 0, layoutParams.bottomMargin - navMargin);
            viewPager.requestLayout();

            ViewGroup.MarginLayoutParams tvLayoutParams = (ViewGroup.MarginLayoutParams) tvCheckInDays.getLayoutParams();
            tvLayoutParams.setMargins(0, tvLayoutParams.topMargin - navMargin5, 0, tvLayoutParams.bottomMargin - navMargin5);
            tvCheckInDays.requestLayout();
        }

        ivNextMaskView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_arrow));

        llSignTextLayout.setAlpha(0f);
        mCheckInMonthly.setMonthlySignedInDays(0);
        int wh = viewPager.getHeight() - getResources().getDimensionPixelSize(R.dimen.WheelItemSpace);
        mCheckInPageAdapter.getSignMaskViews().get(0).updateTodayCheckInUI(wh, 0, mCheckInIcons);
        mCheckInPageAdapter.getSignMaskViews().get(1).updateTodayCheckInUI(wh, 0, mCheckInIcons);
        mCheckInPageAdapter.getSignMaskViews().get(2).updateTodayCheckInUI(wh, 0, mCheckInIcons);
    }

    private List<CheckInPhaseView> fillCheckInViews() {
        List<CheckInPhaseView> signMaskViews = new ArrayList<>();
        CheckInPhaseView checkInPhaseView = new CheckInPhaseView(this);
        checkInPhaseView.init(CheckInPhaseView.CHECK_IN_TYPE_WHALE);
        CheckInPhaseView checkInPhaseView2 = new CheckInPhaseView(this);
        checkInPhaseView2.init(CheckInPhaseView.CHECK_IN_TYPE_BEAR);
        CheckInPhaseView checkInPhaseView3 = new CheckInPhaseView(this);
        checkInPhaseView3.init(CheckInPhaseView.CHECK_IN_TYPE_CRANE);
        signMaskViews.add(checkInPhaseView);
        signMaskViews.add(checkInPhaseView2);
        signMaskViews.add(checkInPhaseView3);
        return signMaskViews;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.iv_next_mask_view, R.id.check_in_button})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_in_button:
                toggleTextAlpha(true);
                tvCheckInPoints.setAlpha(0f);
                mCheckInMonthly.setMonthlySignedInDays(mCurrCheckInDays++);
                //btn action
                checkInStartButton.checkInAnimShow(() -> {
                    //checkIn begin
                    CheckInPhaseView checkInPhaseView = mCheckInPageAdapter.getSignMaskViews().get(viewPager.getCurrentItem());
                    checkInPhaseView.updateTodayCheckInUI(0, mCheckInMonthly.getMonthlySignedInDays(), mCheckInIcons);
                    checkInStartButton.postDelayed(() -> {
                        checkInPhaseView.startCheckIn(() -> checkInPhaseView.checkComplete(mCheckInMonthly, viewPager.getCurrentItem()));
                        checkInStartButton.checkInAnimHide();
                        updateCheckInPanel();
                        toggleTextAlpha(false);
                        if (mCheckInMonthly.getMonthlySignedInDays() == 0) {
                            translationCheckInY(true);
                        }
                    }, 800L);
                });
                break;
            case R.id.iv_next_mask_view:
                if (viewPager.getCurrentItem() != 2)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
        }
    }

    private void updateCheckInPanel() {
        int curryPoints = 5 + mCurrCheckInDays;
        tvCheckInDays.setText(getString(R.string.text_check_in_day, mCheckInMonthly.getMonthlySignedInDays()));
        tvCheckInGetPoints.setText(getString(R.string.text_check_in_points, curryPoints));
    }

    private void toggleTextAlpha(boolean hide) {
        if (hide) {
            tvCheckInGetPoints.setAlpha(0);
        } else {
            tvCheckInGetPoints.animate().alpha(1.0f).setDuration(600).start();
        }
    }

    private void translationCheckInY(boolean down) {
        float currY = ViewAnimHelper.getTranslationY(checkInStartButton);
        float targetY = ViewAnimHelper.getTranslationY(checkInStartButton) + defaultMarginTop;
        if (mTranslationAnimator == null) {
            mTranslationAnimator = ValueAnimator.ofFloat(currY, targetY).setDuration(400);
            mTranslationAnimator.addUpdateListener(animation -> {
                float translationY = (float) animation.getAnimatedValue();
                ViewAnimHelper.setTranslationY(checkInStartButton, translationY);
            });
        }
        if (down) {
            if (currY == targetY)
                return;
            mTranslationAnimator.start();
            llSignTextLayout.animate().alpha(1.0f).setDuration(400L).start();
        } else {
            if (currY == 0)
                return;
            mTranslationAnimator.reverse();
        }
    }
}
