package stelztech.youknowehv4.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by alex on 12/9/2017.
 */

public class FragmentCommon extends Fragment{

    private int animationLayoutPosition;

    private boolean animationFade;

    public static final int FADE_ANIMATION = -1;

    public FragmentCommon(int animationLayoutPosition, boolean animationFade) {
        this.animationLayoutPosition = animationLayoutPosition;
        this.animationFade = animationFade;
    }

    public int getAnimationLayoutPosition() {
        return animationLayoutPosition;
    }

    public void setAnimationLayoutPosition(int animationLayoutPosition) {
        this.animationLayoutPosition = animationLayoutPosition;
    }

    public boolean isAnimationFade() {
        return animationFade;
    }

    public void setAnimationFade(boolean animationFade) {
        this.animationFade = animationFade;
    }
}
