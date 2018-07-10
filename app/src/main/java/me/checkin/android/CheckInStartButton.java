package me.checkin.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.BindView;
import me.checkin.android.helper.BaseFrameView;
import me.checkin.android.helper.CircularAnim;
import me.checkin.android.helper.ProgressWheel;

/**
 * Created by zhoujunchen
 * on 17/9/13.
 */

public class CheckInStartButton extends BaseFrameView {
    @BindView(R.id.progress_load_check_in)
    ProgressWheel progressLoadCheckIn;
    @BindView(R.id.tv_check_in)
    TextView tvCheckIn;

    public interface OnCheckInStartListener {
        void startCheckIn();
    }

    public CheckInStartButton(Context context) {
        super(context);
    }

    public CheckInStartButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckInStartButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.view_check_in_start_button;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tvCheckIn.setSelected(selected);
    }

    public void setText(String text) {
        tvCheckIn.setText(text);
    }

    public String getText() {
        return tvCheckIn.getText().toString().trim();
    }

    public void checkInAnimShow(OnCheckInStartListener onCheckInStartListener) {
        CircularAnim.hide(tvCheckIn).go(() -> {
            progressLoadCheckIn.spin();
            progressLoadCheckIn.setVisibility(VISIBLE);
            onCheckInStartListener.startCheckIn();
        });
    }

    public void checkInAnimHide() {
        postDelayed(() -> CircularAnim.show(tvCheckIn).go(() -> {
            progressLoadCheckIn.stopSpinning();
            progressLoadCheckIn.setVisibility(GONE);
        }), 100);
    }
}
