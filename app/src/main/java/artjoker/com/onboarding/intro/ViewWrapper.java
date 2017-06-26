package artjoker.com.onboarding.intro;

import android.view.View;

public class ViewWrapper {

    private View view;
    private int initialTransitionX;
    private int initialTransitionY;
    private float endTransitionX;
    private float endTransitionY;

    public ViewWrapper(View view, int initialTransitionX, int initialTransitionY) {
        this.view = view;
        this.initialTransitionX = initialTransitionX;
        this.initialTransitionY = initialTransitionY;
    }

    public View getView() {
        return view;
    }

    public int getInitialTransitionX() {
        return initialTransitionX;
    }

    public int getInitialTransitionY() {
        return initialTransitionY;
    }

    public void setEndTransitionX(float endTransitionX) {
        this.endTransitionX = endTransitionX;
    }

    public void setEndTransitionY(float endTransitionY) {
        this.endTransitionY = endTransitionY;
    }

    public float getEndTransitionX() {
        return endTransitionX;
    }

    public float getEndTransitionY() {
        return endTransitionY;
    }
}
