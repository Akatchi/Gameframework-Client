package ai.tictactoe;

import ai.IAIPlayer;
import games.IGameLayout;
import games.TicTacToe;

/**
 * Created by akatchi on 23-10-15.
 */
public class MinMaxAI implements IAIPlayer
{
    private TicTacToe activeGame;

    @Override
    public String getBestMove(IGameLayout gameType)
    {
        activeGame = (TicTacToe) gameType;
        return null;
    }

    private String calculateBestMove()
    {
        return "1,1";
    }

    private boolean isAWin(int side)
    {

        return false;
    }
}
