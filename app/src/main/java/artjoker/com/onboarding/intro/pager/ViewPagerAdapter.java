package artjoker.com.onboarding.intro.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import artjoker.com.onboarding.R;
import artjoker.com.onboarding.intro.IntroActivity;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<TextSlide> textSlides;

    public ViewPagerAdapter(Context mContext, List<TextSlide> textSlides) {
        this.mContext = mContext;
        this.textSlides = textSlides;
    }

    @Override
    public int getCount() {
        return textSlides.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.text_slide, container, false);
        if (position != IntroActivity.FOURTH_SLIDE) {
            ((TextView) view.findViewById(R.id.header)).setText(textSlides.get(position).getHeader());
            ((TextView) view.findViewById(R.id.text)).setText(textSlides.get(position).getText());
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
