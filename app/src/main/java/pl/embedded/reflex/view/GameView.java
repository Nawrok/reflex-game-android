package pl.embedded.reflex.view;

import pl.embedded.reflex.model.Position;

public interface GameView
{
    void showGameScorePosition(int score, Position position);

    void showGameTimer(String time, int progress);

    void switchToResultView(int score);
}
