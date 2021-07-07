package sample;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    List<String> extensions;
    String paramsErrorText = "";
    boolean paramsError = false;

    boolean fileSelected = false;   //To know if the user selected a file yet
    double time =0;

    // GWO parameters
    double a;
    FormuleWDP WDP;

   // @FXML
    //private Slider sliderA;

    @FXML
    private Label aValue;

    // First part of the UI, file selection & Second part file informations
    @FXML
    private Label labelFileChoosen, labelBidsNb, labelObjectsNb, labelClLength;

    @FXML
    private TextField popSize;

    @FXML
    private TextField maxIter;

    @FXML
    void oneFileChooser(){
        /*
        This function permits the user to choose the cnf file to solve
        It is triggered bu the Choose button, it calls the function that displays the file information into the UI
         */
        WDP = new FormuleWDP("E:\\Cours\\E-Commerce\\Projet\\groupe5\\instance\\in600");
        fileSelected = true;
        getInfoFromFile();
/*
        //Open a single file chooser and limit the extentions
        FileChooser fc = new FileChooser();
        //fc.setTitle("Choose a CNF File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CNF files",extensions ));
        File f = fc.showOpenDialog(null);
        if (f!= null)
        {
            labelFileChoosen.setText("Selected File : "+ f.getName());
            fileSelected = true;
            WDP = new FormuleWDP(f.getAbsolutePath());
            getInfoFromFile();
        }*/

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
         * Initialize the controller and fill the extensions list
         * This function Specfies the files accepted by the program
         * initializes the file viewer & calls initialization functions
         */
        extensions = new ArrayList<>();
        extensions.add("*");
/*
        sliderA.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                a = (double)new_val*2/100;
                aValue.setText(String.format("%.2f", a));
            }
        });
*/
        popSize.setText("25");
        maxIter.setText("2");//1000

        popSize.setPromptText("Integer");
        maxIter.setPromptText("Integer");
        //sliderA.setValue(50);

        popSize.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                popSize.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        maxIter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxIter.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

    }

    /* Get all info from the file and separate it into variables, and an array of lists (and displays it)
     * The list should be of integers but for now it's of Strings
     * */
    private void getInfoFromFile(){ //TODO : Change
        /*
         Get the variables number and clauses number from the 7th line of the cnf file and the clauses length from the 5th
         */

        //Display the results
        labelBidsNb.setText(WDP.getNbBids()+"");
        labelObjectsNb.setText(WDP.getNbLots()+"");
    }

    @FXML
    void solveButton(ActionEvent event) {
        /*
        Calls the selected algorithme and checks for errors.
        Error types :
            - Empty parameters for GA, PSO & BSO
            - Wrong float format (ex: 1.11..1.) in GA parameters
            - The mutation rate & the crossover rate must be between 0 & 1 in GA
            - Flip value in BSO
            - Solve button pressed before choosing a file
         */
        if(fileSelected) {
            if (!checkFields()){
                /* Code seulement pour faire des tests
                Solution s = executeAlgorithme();
                // Afficher le résultat
                solList.getItems().clear();
                for(int i=0; i<s.getSolution().size(); i++){
                    solList.getItems().add("x"+String.valueOf(i+1)+":\t\t"+s.getSolution().get(i));
                }*/

                // Run the choosen algorithme on a alternate thread
                AlgorithmeTask task = new AlgorithmeTask();
                task.setOnSucceeded(resultList -> task.getValue());

                Node node = (Node) event.getSource();
                Stage thisStage = (Stage) node.getScene().getWindow();

                Alert alert = createProgressAlert(thisStage, task);
                executeTask(task);
                alert.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.NONE, paramsErrorText, ButtonType.OK);
                alert.setTitle("Error");
                alert.showAndWait();
                paramsErrorText="";
                paramsError = false;
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.NONE, "No file selected.\nPlease select a file", ButtonType.OK);
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }

    private boolean checkFields() {
        //TODO : chech the 0<a<2
        return false;
    }

    private Solution executeAlgorithme(AlgorithmeTask task){
        /*
        Function executed on separate thread, the task passed on parameters permits cancellation from inside the algorithms, returns the solution.
        Throws an out of memory error
         */
        Solution resultList;
        try{
            GWO GWO = new GWO();
            double start = System.currentTimeMillis();
            int iterations =  Integer.parseInt(maxIter.getText());
            int pop = Integer.parseInt(popSize.getText());
            resultList = GWO.solve(a,pop, iterations, WDP, task);
            time = (System.currentTimeMillis() - start) * 0.001;
            return resultList;
        }
        catch (java.lang.OutOfMemoryError e){
            Alert alert = new Alert(Alert.AlertType.NONE, "Error.\nOut of Memory", ButtonType.OK);
            alert.setTitle("Error");
            alert.showAndWait();
        }
        return new Solution();
    }

    class AlgorithmeTask extends Task<sample.Solution> {
        /*
        The task that executes the needed algorithme
        it shows a dialog for the progress
        & calls the display results function at the end
         */
        private AlgorithmeTask() {
            updateTitle("Solving in progress");
        }

        @Override
        protected Solution call() {
            updateMessage("Solving in progress...\nThis task could take up to several minutes.");
            Solution s = executeAlgorithme(this);
            Platform.runLater(() -> {
                // Afficher le résultat
               displayResults(s);
            });
            updateMessage("Search completed.");
            updateProgress(1, 1);
            return s;
        }

        @Override
        protected void running() {
            System.out.println("Solving task is running...");
        }

        @Override
        protected void succeeded() {
            System.out.println("Solving task successful.");
        }

        @Override
        protected void failed(){
            System.out.println("Task failed successfully.");//Task failed ;-)
        }

    }

    private void displayResults(Solution s) {
        //TODO : Display the results
    }

    private void executeTask(Task<?> task) {
        /*
        executes task on a separate, daemon thread
         */
        Thread t = new Thread(task, "solving-thread");
        t.setDaemon(true);
        t.start();
    }

    private Alert createProgressAlert(Stage owner, Task<?> task) {
        /*
         *  creates the Alert and necessary controls to observe the task
         */
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initOwner(owner);
        alert.titleProperty().bind(task.titleProperty());
        alert.contentTextProperty().bind(task.messageProperty());

        ProgressIndicator pIndicator = new ProgressIndicator();

        pIndicator.progressProperty().bind(task.progressProperty());
        alert.setGraphic(pIndicator);

        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        // Ok button (disabeld while running if there are no errors with the task)
        alert.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty().bind(task.runningProperty());

        //Cancel button
        alert.getDialogPane().lookupButton(ButtonType.CANCEL)
                .addEventFilter(ActionEvent.ACTION, event -> task.cancel()
                );

        alert.getDialogPane().cursorProperty().bind(
                Bindings.when(task.runningProperty())
                        .then(Cursor.WAIT)
                        .otherwise(Cursor.DEFAULT)
        );
        return alert;
    }
}
