package pl.embedded.reflex.model;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class Game
{
    private int score, moves;
    private Position position;

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public int getMoves()
    {
        return moves;
    }

    public void setMoves(int moves)
    {
        this.moves = moves;
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
