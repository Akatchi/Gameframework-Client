package listeners;

import models.JsonMessage;

/**
 * Created by akatchi on 13-8-15.
 */
public interface ChallengeInvitedListener
{
    public void challengeInvitationReceived(JsonMessage message);
}
