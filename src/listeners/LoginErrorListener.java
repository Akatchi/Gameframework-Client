package listeners;

import models.JsonMessage;

/**
 * Created by akatchi on 12-8-15.
 */
public interface LoginErrorListener
{
    public void loginError(JsonMessage error);
}
