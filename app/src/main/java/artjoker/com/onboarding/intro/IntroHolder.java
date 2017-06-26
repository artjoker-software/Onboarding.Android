package artjoker.com.onboarding.intro;

public class IntroHolder {

    static final int TRANSFER_START_Y = 0;
    static final int TRANSFER_END_Y = -150;
    static final float TRANSFER_TRANSLATE_STAP = 0.1f;
    static final int TRANSFER_PHONE_MAX_Y = -125;
    static final float TRANSFER_MIN_SCALE = 0.5f;
    static final float TRANSFER_MAX_SCALE = 1f;
    static final int MARGIN = 50;
    static final int ARROW_MINUTE_CIRCLE = 720;
    static final int ARROW_HOUR_CIRCLE = 45;
    static final int STEPS_DELIVER = 10;
    static final int DELAY_MILLIS = 1;

    static final int BRASSIERE_INIT_X = 0, BRASSIERE_INIT_Y = -300,
            BALL_INIT_X = 200, BALL_INIT_Y = -375,
            PHOTO_INIT_X = 250, PHOTO_INIT_Y = -175,
            POMADE_INIT_X = -300, POMADE_INIT_Y = 0,
            TENT_INIT_X = -200, TENT_INIT_Y = -225,
            WASHER_INIT_X = -200, WASHER_INIT_Y = 225,
            PHONE_INIT_X = 230, PHONE_INIT_Y = 200;

    final int DOLLAR_CLIP_LEVEL = 5760;

    float TRANSFER_HIGHER_Y, TRANSFER_FINGER_START_X, TRANSFER_FINGER_START_Y, TRANSFER_FINGER_END_X, TRANSFER_FINGER_END_Y,
            ARROW_HOUR_START_Y, ARROW_HOUR_START_X, ARROW_HOUR_END_X, ARROW_MINUTE_START_Y, ARROW_MINUTE_START_X, ARROW_MINUTE_END_X,
            CLOCK_CENTER, CLOCK_CENTER_X, CLOCK_CENTER_THIRD_X,
            DOLLAR_MARGIN, DOLLAR_START_X, DOLLAR_END_X, DOLLAR_START_Y, DOLLAR_END_Y, DOLLAR_DISTANCE_X, DOLLAR_DISTANCE_Y,
            ARROW_HOUR_STEP, ARROW_MINUTE_STEP,
            TERMINAL_Y, TERMINAL_X;

    int ANIM_CENTER_X, ANIM_CENTER_Y;

    float ARROW_HOUR_STEP_DIVIDER = 15f;
    float ARROW_MINUTE_STEP_DIVIDER = 1.5f;

    boolean isHideLevel = false, isShowLevel = false;
    int hideLevel, hideY, showLevel, showY;
    float duration = 0;

    long PAGE_SCROLL, PAGE_SCROLL_DEVIATION;
}
