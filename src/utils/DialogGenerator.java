package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by akatchi on 12-8-15.
 */
public class DialogGenerator
{
    public static ChoiceDialog<String> getChoiceDialog(String dialogHeader, String dialogTitle, List<String> choices)
    {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Select an option!");
        dialog.setHeaderText(dialogHeader);
        dialog.setContentText(dialogTitle);

        return dialog;
    }

    public static Alert getSimpleInfoAlert(String infoMessage)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);

        return alert;
    }

    public static Alert getSimpleErrorAlert(String errorTitle, String errorMessage)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An error occurred");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorMessage);

        return alert;
    }

    public static Alert getExceptionErrorAlert(String errorTitle, Exception e)
    {
        // Overload the other function to make the errormessage a optional parameter
        return getExceptionErrorAlert(errorTitle, e.getMessage(), e);
    }

    public static Alert getExceptionErrorAlert(String errorTitle, String errorMessage, Exception e)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An error occured");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorMessage);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        return alert;
    }
}
