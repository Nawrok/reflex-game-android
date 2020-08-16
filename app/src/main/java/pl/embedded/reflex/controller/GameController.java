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
    }

    public Game getGame()
    {
        return game;
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
