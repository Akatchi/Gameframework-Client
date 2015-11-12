package models;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by akatchi on 14-8-15.
 */
public class ChallengeCell
{
    public VBox vBox;
    public Label opponent;
    public Label challenger;
    public Label gameType;

    private Challenge challenge;

    public ChallengeCell()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/challengeCell.fxml"));
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(Challenge challenge)
    {
        this.challenge = challenge;

        gameType.setText(challenge.getGameType());
        challenger.setText(challenge.getChallenger());
        opponent.setText(challenge.getOpponent());
    }

    public VBox getBox()
    {
        return vBox;
    }

    public int getChallengeNumber()
    {
        return challenge.getChallengeNumber();
    }
}
