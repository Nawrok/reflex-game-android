package pl.embedded.reflex.model;

import pl.embedded.reflex.enums.Position;

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
