package controller;

import ai.IAIPlayer;
import games.IGameLayout;
import utils.Log;

/**
 * Created by akatchi on 15-8-15.
 */
public class GameController
{
    public static final String HUMAN_MUST_MOVE = "Human_Move";

    private IGameLayout gameType;
    private IAIPlayer ai;

    public GameController(IGameLayout gameType, IAIPlayer ai)
    {
        if( ai == null )
        {
            Log.DEBUG("Human playing");
        }

        this.gameType = gameType;
        this.ai = ai;
    }

    public int getBoardHeight()
    {
        return gameType.getBoardHeight();
    }

    public int getBoardWidth()
    {
        return gameType.getBoardWidth();
    }

    public int[][] getBoard()
    {
        return gameType.getBoard();
    }

    public IGameLayout getGameType()
    {
        return gameType;
    }

    public String getBestMove()
    {
        if( ai == null )
        {
            return HUMAN_MUST_MOVE;
        }
        else
        {
            return ai.getBestMove(gameType);
        }
    }

    public void setMove(String move, String player)
    {
        gameType.setMove(move, player);
    }
}
