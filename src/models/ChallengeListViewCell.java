package models;

import javafx.scene.control.ListCell;

/**
 * Created by akatchi on 14-8-15.
 */
public class ChallengeListViewCell extends ListCell<Challenge>
{
    @Override
    public void updateItem(Challenge challenge, boolean empty)
    {
        super.updateItem(challenge, empty);

        if( challenge != null )
        {
            ChallengeCell data = new ChallengeCell();
            data.setInfo(challenge);
            setGraphic(data.getBox());
        }
    }
}
