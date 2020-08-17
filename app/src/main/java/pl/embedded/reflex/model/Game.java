package pl.embedded.reflex.model;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class Game
{
    private int score;
    private Position position;

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }
}
