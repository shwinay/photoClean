package sample.source;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;

public class Controller
{
    //VARIABLES

    @FXML
    ChoiceBox sortSelector;
    @FXML
    Label dialogueText;

    @FXML
    TextField fileField;
    ObservableList<String> sortOptionsList;
    Sort sort;

    //CONSTRUCTOR
    public Controller()
    {
        sortOptionsList = FXCollections.observableArrayList("Sort by Date", "Unsort", "Sort by Size", "Sort by Type");
    }

    //METHODS
    @FXML
    public void initialize()
    {
        //initialize sort options
        sortSelector.setValue("Sort by Date");
        sortSelector.setItems(sortOptionsList);
        dialogueText.setVisible(false);
    }

    //select files
    public void fileSelectButton (ActionEvent event)
    {
        System.out.println("file button pressed");
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Open Folder");
        File file = fileChooser.showDialog(null);
        if (file != null)
        {
            fileField.setText(file.getAbsolutePath().toString());
        }
        else
        {
            fileField.setText("");
        }
    }

    //call sort functions
    public void goButton ()
    {
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> dialogueText.setVisible(false));
        File directory = new File(fileField.getText());
        sort = new Sort(directory);

        //sort by date
        if (sortSelector.getValue().equals("Sort by Date"))
        {
            try
            {
                sort.getImages(directory);

                for (int i = 0; i < sort.fileArrayList.size(); i ++)
                {
                    sort.readMetadata(sort.fileArrayList.get(i));
                }
                dialogueText.setText("Sorting Process Completed!");
                dialogueText.setVisible(true);
                visiblePause.play();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                dialogueText.setText("There was an error.");
                dialogueText.setVisible(true);
                visiblePause.play();
            }
        }
        //unsort files
        else if (sortSelector.getValue().equals("Unsort"))
        {
            try
            {
                sort.getImages(directory);

                for (int i = 0; i < sort.fileArrayList.size(); i ++)
                {
                    sort.unsort(sort.fileArrayList.get(i));
                }

                dialogueText.setText("Unsorting Process Completed!");
                dialogueText.setVisible(true);
                visiblePause.play();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                dialogueText.setText("There was an error.");
                dialogueText.setVisible(true);
                visiblePause.play();
            }
        }

    }
}
