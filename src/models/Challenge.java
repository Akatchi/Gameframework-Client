package models;

/**
 * Created by akatchi on 14-8-15.
 */
public class Challenge
{
    private int challengeNumber;
    private String challenger;
    private String opponent;
    private String gameType;

    public Challenge(int challengeNumber, String challenger, String opponent, String gameType)
    {
        this.challengeNumber = challengeNumber;
        this.challenger = challenger;
        this.opponent = opponent;
        this.gameType = gameType;
    }

    public int getChallengeNumber()
    {
        return challengeNumber;
    }

    public String getChallenger()
    {
        return challenger;
    }

    public String getOpponent()
    {
        return opponent;
    }

    public String getGameType()
    {
        return gameType;
    }
}
