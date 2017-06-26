package artjoker.com.onboarding.intro.pager;


public class AnimationInfoHolder {

    private int prevPosition;
    private int position;
    private float offset;

    public AnimationInfoHolder(int prevPosition, int position, float offset) {
        this.prevPosition = prevPosition;
        this.position = position;
        this.offset = offset;
    }

    public int getPrevPosition() {
        return prevPosition;
    }

    public int getPosition() {
        return position;
    }

    public float getOffset() {
        return offset;
    }
}
