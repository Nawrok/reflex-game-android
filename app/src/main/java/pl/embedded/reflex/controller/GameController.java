package pl.embedded.reflex.controller;

import java.security.SecureRandom;
import java.util.Arrays;

import pl.embedded.reflex.enums.Position;
import pl.embedded.reflex.model.Game;

public class GameController
{
    private static final SecureRandom random = new SecureRandom();
    private final Game game;

    public GameController(Game game)
    {
        this.game = game;
        randomizePosition();
    }

    public int getScore()
    {
        return game.getScore();
    }

    public Position getPosition()
    {
        return game.getPosition();
    }

    public void addScore(int score)
    {
        game.setScore(game.getScore() + score);
    }

    public void randomizePosition()
    {
        Position last = game.getPosition();
        Position position;
        do
        {
            int rand = random.nextInt(Position.values().length - 1) + 1;
            position = Arrays.asList(Position.values()).get(rand);
        } while (position == last);
        game.setPosition(position);
    }
}
