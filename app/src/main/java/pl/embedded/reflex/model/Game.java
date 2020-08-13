package pl.embedded.reflex.model;

import android.os.Parcel;
import android.os.Parcelable;

import pl.embedded.reflex.enums.Position;

public class Game implements Parcelable
{
    public static final Creator<Game> CREATOR = new Creator<Game>()
    {
        @Override
        public Game createFromParcel(Parcel source)
        {
            return new Game(source);
        }

        @Override
        public Game[] newArray(int size)
        {
            return new Game[size];
        }
    };
    private int score;
    private Position position;

    public Game()
    {
    }

    protected Game(Parcel in)
    {
        this.score = in.readInt();
        int tmpPosition = in.readInt();
        this.position = tmpPosition == -1 ? null : Position.values()[tmpPosition];
    }

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.score);
        dest.writeInt(this.position == null ? -1 : this.position.ordinal());
    }
}
