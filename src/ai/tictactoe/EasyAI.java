package ai.tictactoe;

import ai.IAIPlayer;
import games.IGameLayout;
import games.TicTacToe;

import java.util.Random;

/**
 * Created by akatchi on 12-8-15.
 */
public class EasyAI implements IAIPlayer
{
    @Override
    public String getBestMove(IGameLayout gameType)
    {
        String[] moves = new String[]{
            "0,0", "0,1", "0,2",
            "1,0", "1,1", "1,2",
            "2,0", "2,1", "2,2"
        };

        for( String move : moves )
        {
            if( gameType.isValidMove(move) )
            {
                return move;
            }
        }

        return null;
    }
}
