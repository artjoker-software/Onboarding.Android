package artjoker.com.onboarding.intro.pager;

import android.support.annotation.StringRes;

public class TextSlide {
    private int header;
    private int text;

    public TextSlide(@StringRes int header, @StringRes int text) {
        this.header = header;
        this.text = text;
    }

    public int getHeader() {
        return header;
    }

    public int getText() {
        return text;
    }
}