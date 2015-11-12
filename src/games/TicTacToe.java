package games;

import controller.Application;

/**
 * Created by akatchi on 12-8-15.
 */
public class TicTacToe extends AbstractGameLayout
{
    private int playerOne = 1;
    private int playerTwo = 2;
    private String playerOneIndicator = "X";
    private String playerTwoIndicator = "0";
    private String emptyIndicator = "-";

    public TicTacToe(String playerOne, String playerTwo)
    {
        super(playerOne, playerTwo, 3, 3);
    }

    @Override
    public String getPlayerOneIndicator()
    {
        return playerOneIndicator;
    }

    @Override
    public String getPlayerTwoIndicator()
    {
        return playerTwoIndicator;
    }

    @Override
    public String getEmptyIndicator()
    {
        return emptyIndicator;
    }

    @Override
    public int getPlayerOneArrayIndicator()
    {
        return playerOne;
    }

    @Override
    public int getPlayerTwoArrayIndicator()
    {
        return playerTwo;
    }

    @Override
    public boolean isValidMove(String move)
    {
        String[] coordinates = move.split(",");
        int x = Integer.valueOf(coordinates[0]);
        int y = Integer.valueOf(coordinates[1]);

        int[][] board = super.getBoard();

        // Check if the value is within bounds
        if( x >= board.length )
        {
            return false;
        }

        if( y >= board[0].length )
        {
            return false;
        }

        // Check if the cell is empty so we can place our move there
        if( board[x][y] == -1 )
        {
            return true;
        }

        return false;
    }

    @Override
    public void setMove(String move, String playerName)
    {
        String[] coordinates = move.split(",");
        int x = Integer.valueOf(coordinates[0]);
        int y = Integer.valueOf(coordinates[1]);

        int[][] board = super.getBoard();

        if( isValidMove(move) )
        {
            // Check for which player we need to put the number
            if( playerName.equals(super.getPlayerOne()) )
            {
                board[x][y] = playerOne;
            }
            else
            {
                board[x][y] = playerTwo;
            }

            super.setBoard(board);
        }
        else
        {
            // TODO on invalid move show error message and ask for new move
            // Invalid move ask for another move input
        }
    }
}
