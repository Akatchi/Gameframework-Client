package games;

import ai.IAIPlayer;

/**
 * Created by akatchi on 12-8-15.
 */
public interface IGameLayout
{
    public int[][] getBoard();

    public String getPlayerOne();

    public String getPlayerTwo();

    public String getPlayerOneIndicator();

    public String getPlayerTwoIndicator();

    public String getEmptyIndicator();

    public int getBoardHeight();

    public int getBoardWidth();

    public int getPlayerOneArrayIndicator();

    public int getPlayerTwoArrayIndicator();

    public boolean isValidMove(String move);

    public void setMove(String move, String player);
}
