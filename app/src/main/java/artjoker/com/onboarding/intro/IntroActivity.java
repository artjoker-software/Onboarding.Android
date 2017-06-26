package artjoker.com.onboarding.intro;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import artjoker.com.onboarding.R;
import artjoker.com.onboarding.intro.pager.AnimationInfoHolder;
import artjoker.com.onboarding.intro.pager.TextSlide;
import artjoker.com.onboarding.intro.pager.ViewPager;
import artjoker.com.onboarding.intro.pager.ViewPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static artjoker.com.onboarding.intro.IntroHolder.ARROW_HOUR_CIRCLE;
import static artjoker.com.onboarding.intro.IntroHolder.ARROW_MINUTE_CIRCLE;
import static artjoker.com.onboarding.intro.IntroHolder.DELAY_MILLIS;
import static artjoker.com.onboarding.intro.IntroHolder.MARGIN;
import static artjoker.com.onboarding.intro.IntroHolder.STEPS_DELIVER;
import static artjoker.com.onboarding.intro.IntroHolder.TRANSFER_END_Y;
import static artjoker.com.onboarding.intro.IntroHolder.TRANSFER_MAX_SCALE;
import static artjoker.com.onboarding.intro.IntroHolder.TRANSFER_MIN_SCALE;
import static artjoker.com.onboarding.intro.IntroHolder.TRANSFER_PHONE_MAX_Y;
import static artjoker.com.onboarding.intro.IntroHolder.TRANSFER_START_Y;
import static artjoker.com.onboarding.intro.IntroHolder.TRANSFER_TRANSLATE_STAP;
import static artjoker.com.onboarding.intro.IntroUtils.checkAndAlphaView;
import static artjoker.com.onboarding.intro.IntroUtils.checkAndScaleView;
import static artjoker.com.onboarding.intro.IntroUtils.revertTransferView;
import static artjoker.com.onboarding.intro.IntroUtils.revertX;
import static artjoker.com.onboarding.intro.IntroUtils.revertY;
import static artjoker.com.onboarding.intro.IntroUtils.scaleTranslate;

public class IntroActivity extends AppCompatActivity {

    static final int MIDDLE_DURATION = 300;
    static final int LONG_DURATION = 500;
    static final float SCALE = 1f;
    public static final int FIRST_SLIDE = 0;
    public static final int SECOND_SLIDE = 1;
    public static final int THIRD_SLIDE = 2;
    public static final int FOURTH_SLIDE = 3;

    private int currentSlide = FIRST_SLIDE;

    @BindView(R.id.intro_container) FrameLayout rootLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.layoutDots) LinearLayout dotsLayout;

    @BindView(R.id.brassiere) ImageView brassiereImageView;
    @BindView(R.id.ball) ImageView ballImageView;
    @BindView(R.id.photo) ImageView photoImageView;
    @BindView(R.id.pomade) ImageView pomadeImageView;
    @BindView(R.id.tent) ImageView tentImageView;
    @BindView(R.id.washer) ImageView washerImageView;
    @BindView(R.id.phone) ImageView phoneImageView;
    @BindView(R.id.comp) ImageView compImageView;

    @BindView(R.id.transfer) ImageView transferImageView;
    @BindView(R.id.transfer_phone) ImageView transferPhoneImageView;
    @BindView(R.id.transfer_finger) ImageView transferFingerImageView;

    @BindView(R.id.terminal) ImageView terminalImageView;
    @BindView(R.id.clock) ImageView clockImageView;
    @BindView(R.id.clock_center) ImageView clockCenterImageView;

    @BindView(R.id.arrowHour) ImageView arrowHourImageView;
    @BindView(R.id.arrowMinute) ImageView arrowMinuteImageView;

    @BindView(R.id.dollar) ImageView dollarImageView;
    @BindView(R.id.rootView) FrameLayout rootAnimLayout;

    ClipDrawable clipDollar;
    List<ViewWrapper> views;
    IntroHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        clipDollar = (ClipDrawable) dollarImageView.getDrawable();
        clipDollar.setLevel(0);

        holder = new IntroHolder();

        initStartViewPosition();
        initViewPager();
        initAnimate();
    }

    private void animateOut(float dist) {
        for (ViewWrapper viewWrapper : views) {
            View view = viewWrapper.getView();
            boolean canYTranslation = Math.abs(Math.abs(viewWrapper.getInitialTransitionY()) - Math.abs(getViewY0(view))) > 3;
            boolean canXTranslation = viewWrapper.getInitialTransitionX() - getViewX0(view) > 3;

            if (dist < 0 && !canXTranslation && !canYTranslation) return;

            translateViewRelative(view, dist);
        }
    }

    private void firstWithSecondAnimation(float distanceX) {
        animateOut(distanceX);

        // --- Comp & Transfer
        compAndTransferFadeInOut(distanceX);

        float distForTransfer = compImageView.getTranslationY() - (distanceX * TRANSFER_TRANSLATE_STAP);
        if (distForTransfer > TRANSFER_END_Y && distForTransfer < TRANSFER_START_Y) {
            compImageView.setTranslationY(distForTransfer);
            transferImageView.setTranslationY(distForTransfer);
        }

        // --- Phone
        if (transferPhoneImageView.getY() + distanceX < TRANSFER_PHONE_MAX_Y) {
            transferPhoneImageView.setTranslationY(transferPhoneImageView.getY() + distanceX);
        }

        // --- Finger
        float distForFingerX = transferFingerImageView.getX() + distanceX;
        if (distForFingerX < holder.TRANSFER_FINGER_END_X && distForFingerX > holder.TRANSFER_FINGER_START_X) {
            transferFingerImageView.setTranslationX(distForFingerX);
        }

        float distForFingerTransferY = transferFingerImageView.getTranslationY() - (distanceX * TRANSFER_TRANSLATE_STAP);
        if (distForFingerTransferY > holder.TRANSFER_FINGER_END_Y && distForFingerTransferY < holder.TRANSFER_FINGER_START_Y) {
            transferFingerImageView.setTranslationY(distForFingerTransferY);
        }
    }

    private void secondWithThirdAnimation(float distanceX) {

        // --- Phone
        if (transferPhoneImageView.getY() - distanceX < TRANSFER_PHONE_MAX_Y) {
            transferPhoneImageView.setTranslationY(transferPhoneImageView.getY() - distanceX);
        }

        // --- Finger
        float distForFingerX = transferFingerImageView.getX() - distanceX;
        if (distForFingerX < holder.TRANSFER_FINGER_END_X && distForFingerX > holder.TRANSFER_FINGER_START_X) {
            transferFingerImageView.setTranslationX(distForFingerX);
        }

        float distForFingerTransferY = transferFingerImageView.getTranslationY() + (distanceX * TRANSFER_TRANSLATE_STAP);
        if (distForFingerTransferY > holder.TRANSFER_FINGER_END_Y && distForFingerTransferY < holder.TRANSFER_FINGER_START_Y) {
            transferFingerImageView.setTranslationY(distForFingerTransferY);
        }

        // --- Transfer
        if (transferImageView.getTranslationY() - distanceX < TRANSFER_END_Y) {
            transferImageView.setTranslationY(transferImageView.getTranslationY() - distanceX);
        }

        float distanceXProc = distanceX / (holder.ANIM_CENTER_Y + TRANSFER_END_Y - holder.TRANSFER_HIGHER_Y);
        double transferScale = transferImageView.getScaleX() - TRANSFER_MIN_SCALE * distanceXProc;
        if (transferScale > TRANSFER_MIN_SCALE && transferScale < TRANSFER_MAX_SCALE) {
            transferImageView.setScaleX((float) transferScale);
            transferImageView.setScaleY((float) transferScale);
        }

        // --- Terminal
        if (terminalImageView.getX() + distanceX < holder.ANIM_CENTER_X) {
            terminalImageView.setTranslationX(terminalImageView.getX() + distanceX);
        }

        // --- Dollar
        if (dollarImageView.getX() + distanceX < holder.DOLLAR_END_X) {
            dollarImageView.setTranslationX(dollarImageView.getX() + distanceX);
        }

        float proc = distanceX / holder.DOLLAR_DISTANCE_X;
        float level = proc * holder.DOLLAR_CLIP_LEVEL;
        float dollarY = proc * holder.DOLLAR_DISTANCE_Y;

        if (clipDollar.getLevel() + Math.round(level) < holder.DOLLAR_CLIP_LEVEL) {
            clipDollar.setLevel(clipDollar.getLevel() + Math.round(level));
        }
        if (dollarImageView.getY() + dollarY < holder.DOLLAR_END_Y) {
            dollarImageView.setY(dollarImageView.getY() + dollarY);
        }

        // --- Clock
        if (clockImageView.getX() - distanceX > holder.ANIM_CENTER_X - clockImageView.getWidth() / 2) {
            clockImageView.setTranslationX(clockImageView.getX() - distanceX);
        }
        if (clockCenterImageView.getX() - distanceX > holder.CLOCK_CENTER_THIRD_X) {
            clockCenterImageView.setTranslationX(clockCenterImageView.getX() - distanceX);
        }

        // --- Arrows
        if (arrowHourImageView.getX() - distanceX > holder.ANIM_CENTER_X) {
            arrowHourImageView.setTranslationX(arrowHourImageView.getX() - distanceX);
        }

        if (arrowMinuteImageView.getX() - distanceX > holder.ANIM_CENTER_X) {
            arrowMinuteImageView.setTranslationX(arrowMinuteImageView.getX() - distanceX);
        }

        float hourRotation = distanceX / holder.ARROW_HOUR_STEP_DIVIDER;
        if (holder.ARROW_HOUR_STEP + hourRotation < ARROW_HOUR_CIRCLE) {
            holder.ARROW_HOUR_STEP += hourRotation;
            arrowHourImageView.setRotation(arrowHourImageView.getRotation() + hourRotation);
        }

        float minuteRotation = distanceX / holder.ARROW_MINUTE_STEP_DIVIDER;
        if (holder.ARROW_MINUTE_STEP + minuteRotation < ARROW_MINUTE_CIRCLE) {
            holder.ARROW_MINUTE_STEP += minuteRotation;
            arrowMinuteImageView.setRotation(arrowMinuteImageView.getRotation() + minuteRotation);
        }
    }

    private void thirdWithFourthAnimation(float distanceX) {
        // --- Clock
        clockImageView.setTranslationY(clockImageView.getTranslationY() - distanceX);
        clockCenterImageView.setTranslationY(clockCenterImageView.getTranslationY() - distanceX);

        // --- Arrows
        arrowHourImageView.setTranslationY(arrowHourImageView.getTranslationY() - distanceX);
        arrowMinuteImageView.setTranslationY(arrowMinuteImageView.getTranslationY() - distanceX);

        float hourRotation = distanceX / holder.ARROW_HOUR_STEP_DIVIDER;
        if (holder.ARROW_HOUR_STEP - hourRotation > -ARROW_HOUR_CIRCLE) {
            holder.ARROW_HOUR_STEP -= hourRotation;
            arrowHourImageView.setRotation(arrowHourImageView.getRotation() - hourRotation);
        }

        float minuteRotation = distanceX / holder.ARROW_MINUTE_STEP_DIVIDER;
        if (holder.ARROW_MINUTE_STEP - minuteRotation > -ARROW_MINUTE_CIRCLE) {
            holder.ARROW_MINUTE_STEP -= minuteRotation;
            arrowMinuteImageView.setRotation(arrowMinuteImageView.getRotation() - minuteRotation);
        }

        terminalImageView.setTranslationX(terminalImageView.getX() - distanceX);
        dollarImageView.setTranslationX(dollarImageView.getX() - distanceX);
        float proc = distanceX / holder.DOLLAR_DISTANCE_X;
        float level = proc * holder.DOLLAR_CLIP_LEVEL;
        float dollarY = proc * holder.DOLLAR_DISTANCE_Y;

        if (clipDollar.getLevel() - Math.round(level) > 0) {
            clipDollar.setLevel(clipDollar.getLevel() - Math.round(level));
        }
        if (dollarImageView.getY() - dollarY > holder.DOLLAR_START_Y) {
            dollarImageView.setY(dollarImageView.getY() - dollarY);
        }
    }

    private void initViewPager() {
        List<TextSlide> textSlides = new ArrayList<>();
        textSlides.add(new TextSlide(R.string.lorem_ipsum, R.string.lorem_ipsum_text));
        textSlides.add(new TextSlide(R.string.lorem_ipsum, R.string.lorem_ipsum_text));
        textSlides.add(new TextSlide(R.string.lorem_ipsum, R.string.lorem_ipsum_text));
        textSlides.add(new TextSlide(R.string.lorem_ipsum, R.string.lorem_ipsum_text));

        viewPager.setAdapter(new ViewPagerAdapter(this, textSlides));
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setOnTouchListener(new OnSwipeTouchListener());

        viewPager.getMessageBus()
                .subscribe(infoHolder -> {
                    if (currentSlide != infoHolder.getPosition()) {
                        currentSlide = infoHolder.getPosition();
                    }
                    holder.PAGE_SCROLL_DEVIATION = holder.PAGE_SCROLL;
                    switch (currentSlide) {
                        case FIRST_SLIDE:   firstSlideAnimation();              break;
                        case SECOND_SLIDE:  secondSlideAnimation(infoHolder);   break;
                        case THIRD_SLIDE:   thirdSlideAnimation(infoHolder);    break;
                        case FOURTH_SLIDE:  fourthSlideAnimation(infoHolder);   break;
                    }
                });
    }

    private void firstSlideAnimation() {
        for (ViewWrapper viewWrapper : views) {
            viewWrapper.getView().animate()
                    .translationX(viewWrapper.getInitialTransitionX())
                    .translationY(viewWrapper.getInitialTransitionY())
                    .start();
        }

        checkAndAlphaView(compImageView, 1);
        checkAndAlphaView(transferImageView, 0);

        // --- PHONE FINGER TRANSFER
        revertY(transferPhoneImageView, -transferPhoneImageView.getDrawable().getIntrinsicHeight());

        revertTransferFinger(holder.TRANSFER_FINGER_START_X, holder.TRANSFER_FINGER_START_Y);

        revertTransferView(compImageView, 0, TRANSFER_START_Y);
        revertTransferView(transferImageView, 0, TRANSFER_START_Y);
        // --- PHONE FINGER TRANSFER

        // --- TERMINAL DOLLAR CLOCK, ARROWS
        revertX(terminalImageView, holder.TERMINAL_X);
        revertX(dollarImageView, holder.DOLLAR_START_X);

        revertX(clockImageView, rootAnimLayout.getWidth() + MARGIN);
        revertY(clockImageView, holder.ANIM_CENTER_Y - clockImageView.getHeight() / 2);

        revertX(clockCenterImageView, holder.CLOCK_CENTER_X);

        revertX(arrowHourImageView, holder.ARROW_HOUR_START_X);
        revertX(arrowMinuteImageView, holder.ARROW_MINUTE_START_X);
        // --- TERMINAL DOLLAR CLOCK, ARROWS
    }

    private boolean isFirstElementsSet = false;

    private void secondSlideAnimation(AnimationInfoHolder infoHolder) {

        //Revert First Elements
        for (ViewWrapper viewWrapper : views) {
            viewWrapper.getView().animate()
                    .translationX(viewWrapper.getEndTransitionX())
                    .translationY(viewWrapper.getEndTransitionY())
                    .start();
        }

        //transfer
        checkAndAlphaView(compImageView, 0);
        checkAndAlphaView(transferImageView, 1);

        revertY(transferPhoneImageView, TRANSFER_PHONE_MAX_Y);

        revertTransferFinger(holder.TRANSFER_FINGER_END_X, holder.TRANSFER_FINGER_END_Y);

        revertTransferView(compImageView, 0, TRANSFER_END_Y);
        revertTransferView(transferImageView, 0, TRANSFER_END_Y);
        checkAndScaleView(transferImageView, TRANSFER_MAX_SCALE);

        // --- TERMINAL DOLLAR CLOCK, ARROWS
        revertX(terminalImageView, holder.TERMINAL_X);
        revertX(dollarImageView, holder.DOLLAR_START_X);

        holder.duration = (1 - infoHolder.getOffset()) * MIDDLE_DURATION;
        dollarImageView.postDelayed(hideClipDollar, 0);

        revertX(clockImageView, rootAnimLayout.getWidth() + MARGIN);
        revertY(clockImageView, holder.ANIM_CENTER_Y - clockImageView.getHeight() / 2);

        revertX(clockCenterImageView, holder.CLOCK_CENTER_X);

        revertX(arrowHourImageView, holder.ARROW_HOUR_START_X);
        revertX(arrowMinuteImageView, holder.ARROW_MINUTE_START_X);

        revertArrows(infoHolder.getOffset(), false);
        // --- TERMINAL DOLLAR CLOCK, ARROWS
    }

    private void thirdSlideAnimation(AnimationInfoHolder infoHolder) {

        //Revert First Elements
        for (ViewWrapper viewWrapper : views) {
            viewWrapper.getView().animate()
                    .translationX(viewWrapper.getEndTransitionX())
                    .translationY(viewWrapper.getEndTransitionY())
                    .start();
        }

        revertY(transferPhoneImageView, -transferPhoneImageView.getDrawable().getIntrinsicHeight());
        revertTransferFinger(holder.TRANSFER_FINGER_START_X, holder.TRANSFER_FINGER_START_Y);

        transferImageView.animate()
                .y(holder.TRANSFER_HIGHER_Y)
                .setDuration(MIDDLE_DURATION)
                .start();

        checkAndScaleView(transferImageView, TRANSFER_MIN_SCALE);

        revertX(terminalImageView, holder.ANIM_CENTER_X);
        revertX(dollarImageView, holder.DOLLAR_END_X);

        holder.duration = (1 - infoHolder.getOffset()) * MIDDLE_DURATION;
        dollarImageView.postDelayed(showDollar, 0);
        revertX(clockImageView, holder.ANIM_CENTER_X - clockImageView.getWidth() / 2);
        revertY(clockImageView, holder.ANIM_CENTER_Y - clockImageView.getHeight() / 2);

        revertX(clockCenterImageView, holder.CLOCK_CENTER_THIRD_X);
        revertY(clockCenterImageView, holder.ANIM_CENTER_Y - clockCenterImageView.getHeight() / 2);

        revertY(arrowHourImageView, holder.ANIM_CENTER_Y - arrowHourImageView.getHeight() / 2);
        revertY(arrowMinuteImageView, holder.ANIM_CENTER_Y - arrowMinuteImageView.getHeight());
        revertX(arrowHourImageView, holder.ARROW_HOUR_END_X);
        revertX(arrowMinuteImageView, holder.ARROW_MINUTE_END_X);

        revertArrows(infoHolder.getOffset(), true);
    }

    private void fourthSlideAnimation(AnimationInfoHolder infoHolder) {

        revertArrows(0.5f, false);

        revertY(clockImageView, -clockImageView.getHeight());
        revertY(clockCenterImageView, -clockImageView.getHeight() / 2 - arrowHourImageView.getHeight() / 2);
        revertY(arrowHourImageView, -clockImageView.getHeight() / 2 - arrowHourImageView.getHeight() / 2);
        revertY(arrowMinuteImageView, -clockImageView.getHeight() / 2 - arrowMinuteImageView.getHeight());

        revertX(terminalImageView, holder.TERMINAL_X);
        revertX(dollarImageView, holder.DOLLAR_START_X);

        holder.duration = (1 - infoHolder.getOffset()) * MIDDLE_DURATION;
        dollarImageView.postDelayed(hideClipDollar, 0);
    }

    private Runnable hideClipDollar = new Runnable() {
        @Override
        public void run() {

            if (!holder.isHideLevel) {
                int CLIP_STEPS = Math.round(holder.duration) / STEPS_DELIVER;

                float wayLeft = Math.abs(holder.DOLLAR_START_X - dollarImageView.getX());
                float proc = wayLeft / holder.DOLLAR_DISTANCE_X / CLIP_STEPS;
                holder.hideLevel = Math.round(proc * holder.DOLLAR_CLIP_LEVEL);
                holder.hideY = Math.round(proc * holder.DOLLAR_DISTANCE_Y);

                holder.isHideLevel = true;
            }

            if (clipDollar.getLevel() - holder.hideLevel > 0) {
                clipDollar.setLevel(clipDollar.getLevel() - holder.hideLevel);
                dollarImageView.setY(dollarImageView.getY() - holder.hideY);
                dollarImageView.postDelayed(this, DELAY_MILLIS);
            } else {
                clipDollar.setLevel(0);
                dollarImageView.setY(holder.DOLLAR_START_Y);
                holder.isHideLevel = false;
                dollarImageView.removeCallbacks(this);
            }
        }
    };
    private Runnable showDollar = new Runnable() {
        @Override
        public void run() {

            if (!holder.isShowLevel) {
                int CLIP_STEPS = Math.round(holder.duration) / STEPS_DELIVER;

                float wayLeft = holder.DOLLAR_END_X - dollarImageView.getX();
                float proc = wayLeft / holder.DOLLAR_DISTANCE_X / CLIP_STEPS;
                holder.showLevel = Math.round(proc * holder.DOLLAR_CLIP_LEVEL);
                holder.showY = Math.round(proc * holder.DOLLAR_DISTANCE_Y);

                holder.isShowLevel = true;
            }

            if (clipDollar.getLevel() + holder.showLevel < holder.DOLLAR_CLIP_LEVEL) {
                clipDollar.setLevel(clipDollar.getLevel() + holder.showLevel);
                dollarImageView.setY(dollarImageView.getY() + holder.showY);
                dollarImageView.postDelayed(this, DELAY_MILLIS);
            } else {
                clipDollar.setLevel(holder.DOLLAR_CLIP_LEVEL);
                dollarImageView.setY(holder.DOLLAR_END_Y);
                holder.isShowLevel = false;
                dollarImageView.removeCallbacks(this);
            }
        }
    };

    private void initStartViewPosition() {
        transferPhoneImageView.setY(-transferPhoneImageView.getDrawable().getIntrinsicHeight());

        rootAnimLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        holder.ANIM_CENTER_X = rootAnimLayout.getWidth() / 2;
                        holder.ANIM_CENTER_Y = rootAnimLayout.getHeight() / 2;

                        holder.TRANSFER_FINGER_START_X = -transferFingerImageView.getDrawable().getIntrinsicWidth();
                        holder.TRANSFER_FINGER_START_Y = holder.ANIM_CENTER_Y;
                        holder.TRANSFER_FINGER_END_X = holder.ANIM_CENTER_X - transferFingerImageView.getDrawable().getIntrinsicWidth();
                        holder.TRANSFER_FINGER_END_Y = holder.TRANSFER_FINGER_START_Y + TRANSFER_END_Y;

                        transferFingerImageView.setX(holder.TRANSFER_FINGER_START_X);
                        transferFingerImageView.setY(holder.TRANSFER_FINGER_START_Y);

                        holder.TERMINAL_X = -terminalImageView.getDrawable().getIntrinsicWidth() - MARGIN;
                        holder.TERMINAL_Y = holder.ANIM_CENTER_Y + terminalImageView.getDrawable().getIntrinsicWidth() / 2.5f;
                        terminalImageView.setX(holder.TERMINAL_X);
                        terminalImageView.setY(holder.TERMINAL_Y);

                        holder.DOLLAR_MARGIN = terminalImageView.getDrawable().getIntrinsicWidth() * 0.08f;
                        holder.DOLLAR_START_X = holder.TERMINAL_X + holder.DOLLAR_MARGIN;
                        holder.DOLLAR_START_Y = holder.TERMINAL_Y + terminalImageView.getDrawable().getIntrinsicHeight() / 2 - dollarImageView.getDrawable().getIntrinsicHeight();
                        holder.DOLLAR_END_X = holder.ANIM_CENTER_X + holder.DOLLAR_MARGIN;
                        holder.DOLLAR_END_Y = holder.DOLLAR_START_Y + dollarImageView.getDrawable().getIntrinsicHeight() * holder.DOLLAR_CLIP_LEVEL / 10000;

                        holder.DOLLAR_DISTANCE_X = holder.DOLLAR_END_X - holder.DOLLAR_START_X;
                        holder.DOLLAR_DISTANCE_Y = holder.DOLLAR_END_Y - holder.DOLLAR_START_Y;

                        dollarImageView.setX(holder.DOLLAR_START_X);
                        dollarImageView.setY(holder.DOLLAR_START_Y);

                        clockImageView.setX(rootAnimLayout.getWidth() + MARGIN);

                        holder.CLOCK_CENTER = clockImageView.getX() + clockImageView.getWidth() / 2;
                        holder.ARROW_HOUR_START_X = holder.CLOCK_CENTER;
                        holder.ARROW_HOUR_START_Y = clockImageView.getY() + clockImageView.getHeight() / 2 - arrowHourImageView.getHeight() / 2;
                        holder.ARROW_HOUR_END_X = holder.ANIM_CENTER_X;
                        arrowHourImageView.setX(holder.ARROW_HOUR_START_X);
                        arrowHourImageView.setY(holder.ARROW_HOUR_START_Y);

                        arrowHourImageView.setPivotX(0);
                        //arrowHourImageView.setPivotY(arrowHourImageView.getHeight()/2);

                        holder.ARROW_MINUTE_START_X = holder.CLOCK_CENTER;
                        holder.ARROW_MINUTE_START_Y = clockImageView.getY() + clockImageView.getHeight() / 2 - arrowMinuteImageView.getHeight();
                        holder.ARROW_MINUTE_END_X = holder.ANIM_CENTER_X;
                        arrowMinuteImageView.setX(holder.ARROW_MINUTE_START_X);
                        arrowMinuteImageView.setY(holder.ARROW_MINUTE_START_Y);
                        arrowMinuteImageView.setPivotX(0);
                        arrowMinuteImageView.setPivotY(arrowMinuteImageView.getHeight());

                        holder.CLOCK_CENTER_X = holder.CLOCK_CENTER - clockCenterImageView.getWidth() / 2;
                        holder.CLOCK_CENTER_THIRD_X = holder.ANIM_CENTER_X - clockCenterImageView.getWidth() / 2;
                        clockCenterImageView.setX(holder.CLOCK_CENTER_X);

                        holder.PAGE_SCROLL = rootAnimLayout.getWidth() / 2;
                        holder.PAGE_SCROLL_DEVIATION = holder.PAGE_SCROLL;

                        initFirstElements();

                        rootAnimLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        holder.TRANSFER_HIGHER_Y = -(transferImageView.getDrawable().getIntrinsicHeight() * 2);
    }

    private void initFirstElements () {
        if (!isFirstElementsSet) {
            for (ViewWrapper viewWrapper : views) {
                View view = viewWrapper.getView();
                float x, y;
                if (viewWrapper.getInitialTransitionX() == 0) {
                    x = 0;
                } else if (viewWrapper.getInitialTransitionX() > 0){
                    x = holder.ANIM_CENTER_X + view.getWidth();
                } else {
                    x = - holder.ANIM_CENTER_X - view.getWidth();
                }

                if (viewWrapper.getInitialTransitionY() == 0) {
                    y = 0;
                } else if (viewWrapper.getInitialTransitionY() > 0){
                    y = holder.ANIM_CENTER_Y + view.getHeight();
                } else {
                    y = - holder.ANIM_CENTER_Y - view.getHeight();
                }
                viewWrapper.setEndTransitionX(viewWrapper.getInitialTransitionX() + x);
                viewWrapper.setEndTransitionY(viewWrapper.getInitialTransitionY() + y);
            }
            isFirstElementsSet = true;
        }
    }

    private void revertTransferFinger(float x, float y) {
        transferFingerImageView.animate()
                .x(x).y(y)
                .setDuration(MIDDLE_DURATION)
                .start();
    }

    private void revertArrows(float offset, boolean toPositive) {

        if (holder.ARROW_MINUTE_STEP == 0) return;

        float arrowHourRotationBy, arrowMinuteRotationBy;
        if (toPositive) {
            arrowHourRotationBy = ARROW_HOUR_CIRCLE - holder.ARROW_HOUR_STEP;
            arrowMinuteRotationBy = ARROW_MINUTE_CIRCLE - holder.ARROW_MINUTE_STEP;
        } else {
            arrowHourRotationBy = - holder.ARROW_HOUR_STEP - ARROW_HOUR_CIRCLE;
            arrowMinuteRotationBy = - holder.ARROW_MINUTE_STEP - ARROW_MINUTE_CIRCLE;
        }
        holder.ARROW_HOUR_STEP = 0;
        holder.ARROW_MINUTE_STEP = 0;
        arrowHourImageView.animate().rotationBy(arrowHourRotationBy).setDuration(MIDDLE_DURATION).start();
        arrowMinuteImageView.animate().rotationBy(arrowMinuteRotationBy).setDuration(MIDDLE_DURATION).start();
    }

    private void compAndTransferFadeInOut(float distanceX) {
        float percentage = Math.abs(distanceX / rootAnimLayout.getWidth());

        float fadeComp, fadeTransfer;
        if (distanceX > 0) {
            fadeComp = compImageView.getAlpha() - percentage;
            fadeTransfer = transferImageView.getAlpha() + percentage;
        } else {
            fadeComp = compImageView.getAlpha() + percentage;
            fadeTransfer = transferImageView.getAlpha() - percentage;
        }

        compImageView.setAlpha(fadeComp);
        transferImageView.setAlpha(fadeTransfer);
    }

    private void initAnimate() {
        views = new ArrayList<>();
        views.add(new ViewWrapper(brassiereImageView, IntroHolder.BRASSIERE_INIT_X, IntroHolder.BRASSIERE_INIT_Y));
        views.add(new ViewWrapper(ballImageView, IntroHolder.BALL_INIT_X, IntroHolder.BALL_INIT_Y));
        views.add(new ViewWrapper(photoImageView, IntroHolder.PHOTO_INIT_X, IntroHolder.PHOTO_INIT_Y));
        views.add(new ViewWrapper(pomadeImageView, IntroHolder.POMADE_INIT_X, IntroHolder.POMADE_INIT_Y));
        views.add(new ViewWrapper(tentImageView, IntroHolder.TENT_INIT_X, IntroHolder.TENT_INIT_Y));
        views.add(new ViewWrapper(washerImageView, IntroHolder.WASHER_INIT_X, IntroHolder.WASHER_INIT_Y));
        views.add(new ViewWrapper(phoneImageView, IntroHolder.PHONE_INIT_X, IntroHolder.PHONE_INIT_Y));

        for (ViewWrapper wrapper : views) scaleTranslate(wrapper);

        scaleTranslate(new ViewWrapper(compImageView, 0, 0));
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            for (int i = 0; i < dotsLayout.getChildCount(); i++) {
                ImageView imageView = (ImageView) dotsLayout.getChildAt(i);
                if (position == i) {
                    imageView.setImageResource(R.drawable.indicator_primary);
                } else {
                    imageView.setImageResource(R.drawable.indicator_normal);
                }
            }

            if (position == FOURTH_SLIDE) onIntroFinish();
        }

        @Override public void onPageSelected(int position) { }

        @Override public void onPageScrollStateChanged(int state) { }
    };

    private void translateViewRelativeWithCoordinates(View v, float dist, float x0, float y0) {

        float ang = IntroUtils.getAngle(getViewCenterX(v) - holder.ANIM_CENTER_X, (getViewCenterY(v) - holder.ANIM_CENTER_Y));
        double x2 = x0 - Math.cos(Math.toRadians(ang)) * dist;
        double y2 = y0 + Math.sin(Math.toRadians(ang)) * dist;
        y2 *= -1;

        v.setTranslationX((float) x2);
        v.setTranslationY((float) y2);
    }

    private void translateViewRelative(View v, float dist) {
        translateViewRelativeWithCoordinates(v, dist, getViewX0(v), getViewY0(v));
    }

    private float getViewX0(View v) {
        return getViewCenterX(v) - holder.ANIM_CENTER_X;
    }

    private float getViewY0(View v) {
        return holder.ANIM_CENTER_Y - getViewCenterY(v);
    }

    private float getViewCenterX(View v) {
        return v.getX() + v.getWidth() / 2;
    }

    private float getViewCenterY(View v) {
        return v.getY() + v.getHeight() / 2;
    }

    protected int getLayoutId() {
        return R.layout.activity_intro;
    }

    private void onIntroFinish() { }

    private class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

        public boolean onTouch(final View v, final MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                try {
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        distanceX /= 1.25f;
                        switch (currentSlide) {
                            case FIRST_SLIDE:
                                firstWithSecondAnimation(distanceX);
                                break;

                            case SECOND_SLIDE: {
                                holder.PAGE_SCROLL_DEVIATION += distanceX;
                                if (holder.PAGE_SCROLL_DEVIATION < holder.PAGE_SCROLL) {
                                    firstWithSecondAnimation(distanceX);
                                } else {
                                    secondWithThirdAnimation(distanceX);
                                }
                                break;
                            }
                            case THIRD_SLIDE: {
                                holder.PAGE_SCROLL_DEVIATION += distanceX;
                                if (holder.PAGE_SCROLL_DEVIATION < holder.PAGE_SCROLL) {
                                    secondWithThirdAnimation(distanceX);
                                } else {
                                    thirdWithFourthAnimation(distanceX);
                                }
                                break;
                            }
                            case FOURTH_SLIDE: {
                                thirdWithFourthAnimation(distanceX);
                                break;
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return false;
            }
        }
    }
}
