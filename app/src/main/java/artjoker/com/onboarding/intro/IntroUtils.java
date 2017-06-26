package artjoker.com.onboarding.intro;

import android.view.View;

import static artjoker.com.onboarding.intro.IntroActivity.LONG_DURATION;
import static artjoker.com.onboarding.intro.IntroActivity.MIDDLE_DURATION;
import static artjoker.com.onboarding.intro.IntroActivity.SCALE;

class IntroUtils {

    final static int ZIRO = 0;
    final static int HALF_CIRCLE = 180;
    final static int FULL_CIRCLE = 360;

    static float getAngle(float currentX, float currentY) {
        float angle = (float) Math.toDegrees(Math.atan2(currentY, currentX));
        final int i = HALF_CIRCLE;
        angle -= i;
        if (angle < ZIRO) {
            angle += FULL_CIRCLE;
        }
        return angle;
    }

    static void revertY(View v, float y) {
        v.animate()
                .y(y)
                .setDuration(MIDDLE_DURATION)
                .start();
    }

    static void revertX(View v, float x) {
        v.animate()
                .x(x)
                .setDuration(MIDDLE_DURATION)
                .start();
    }

    static void revertTransferView(View view, float x, float y) {
        view.animate()
                .translationX(x)
                .translationY(y)
                .setDuration(MIDDLE_DURATION)
                .start();
    }

    static void scaleTranslate(ViewWrapper viewWrapper) {
        viewWrapper.getView().animate()
                .scaleX(SCALE)
                .scaleY(SCALE)
                .translationX(viewWrapper.getInitialTransitionX())
                .translationY(viewWrapper.getInitialTransitionY())
                .setStartDelay(MIDDLE_DURATION)
                .setDuration(LONG_DURATION * 2)
                .withEndAction(() -> {
                    viewWrapper.getView().animate().setStartDelay(0);
                    viewWrapper.getView().animate().setDuration(MIDDLE_DURATION);
                })
                .start();
    }

    static void checkAndScaleView(View v, float toScale) {
        if (v.getScaleX() != toScale) {
            v.animate().scaleX(toScale)
                    .scaleY(toScale)
                    .setDuration(MIDDLE_DURATION)
                    .start();
        }
    }

    static void checkAndAlphaView(View v, float toAlpha) {
        if (v.getAlpha() != toAlpha) {
            v.animate().alpha(toAlpha).setDuration(MIDDLE_DURATION).start();
        }
    }
}
