package games;

/**
 * Created by akatchi on 12-8-15.
 */
public abstract class AbstractGameLayout implements IGameLayout
{
    private String playerOne;
    private String playerTwo;
    private int[][] board;
    private int boardHeight;
    private int boardWidth;

    public AbstractGameLayout(String playerOne, String playerTwo, int boardHeight, int boardWidth)
    {
        board = new int[boardHeight][boardWidth];

        // Set all the valuews in the bord to -1 to indicate
        // that the fields are empty
        for( int i = 0; i < boardHeight; i++ )
        {
            for( int j = 0; j < boardWidth; j++ )
            {
                board[i][j] = -1;
            }
        }

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;

        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
    }

    @Override
    public int[][] getBoard()
    {
        return board;
    }

    public void setBoard(int[][] board)
    {
        this.board = board;
    }

    @Override
    public String getPlayerOne()
    {
        return playerOne;
    }

    @Override
    public String getPlayerTwo()
    {
        return playerTwo;
    }

    @Override
    public int getBoardHeight()
    {
        return boardHeight;
    }

    @Override
    public int getBoardWidth()
    {
        return boardWidth;
    }
}
