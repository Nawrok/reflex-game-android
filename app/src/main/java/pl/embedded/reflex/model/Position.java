package pl.embedded.reflex.model;

import pl.embedded.reflex.R;

public enum Position
{
    IDLE(-1),
    LEFT(R.drawable.arrow_left),
    RIGHT(R.drawable.arrow_right),
    UP(R.drawable.arrow_up),
    DOWN(R.drawable.arrow_down);

    private final int drawable_id;

    Position(int drawable_id)
    {
        this.drawable_id = drawable_id;
    }

    public int getImage()
    {
        return drawable_id;
    }
}
