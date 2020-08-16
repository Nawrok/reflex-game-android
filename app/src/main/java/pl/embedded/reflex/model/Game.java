package pl.embedded.reflex.model;

import org.parceler.Parcel;

import pl.embedded.reflex.enums.Position;

@Parcel
public class Game
{
    int score;
    Position position;

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
