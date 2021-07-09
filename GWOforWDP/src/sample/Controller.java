package sample;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    List<String> extensions;
    String paramsErrorText = "", folder;
    boolean paramsError = false;
    int fileNb =1;

    boolean fileSelected = false;   //To know if the user selected a fileNb yet
    double time =0;

    // GWO parameters
    WDPinstance WDP;

    @FXML
    private Label labelFileChosen, labelBidsNb, labelObjectsNb;

    @FXML
    private Label LabelNbItems;

    @FXML
    private CheckBox checkResolve;

    @FXML
    private TextField popSize;
    @FXML
    private Button btnSolve;

    @FXML
    private TextField maxIterations;

    @FXML
    private TextArea satBidsArea;

    @FXML
    private Label labelTotal;

    @FXML
    private Label timeLabel;

    @FXML
    void oneFileChooser(){
        /*
        This function permits the user to choose the cnf fileNb to solve
        It is triggered bu the Choose button, it calls the function that displays the fileNb information into the UI
         */
        //Open a single fileNb chooser and limit the extentions
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files",extensions ));
        File f = fc.showOpenDialog(null);
        folder  = f.getParent();
        if (f!= null)
        {
            labelFileChosen.setText("Selected File : "+ f.getName());
            fileSelected = true;
            WDP = new WDPinstance(f.getAbsolutePath());
            getInfoFromFile();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
         * Initialize the controller and fill the extensions list
         * This function Specfies the files accepted by the program
         * initializes the fileNb viewer & calls initialization functions
         */
        extensions = new ArrayList<>();
        extensions.add("*");

        popSize.setText("6");
        maxIterations.setText("25");//1000

        popSize.setPromptText("Integer");
        maxIterations.setPromptText("Integer");

        popSize.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                popSize.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        maxIterations.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxIterations.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /* Get all info from the fileNb and separate it into variables, and an array of lists (and displays it)
     * The list should be of integers but for now it's of Strings
     * */
    private void getInfoFromFile(){
        //Display the results
        labelBidsNb.setText(WDP.getNbBids()+"");
        labelObjectsNb.setText(WDP.getNbLots()+"");
    }

    @FXML
    void solveButton(ActionEvent event) {
        if(fileSelected) {
            if (!checkFields()){
                AlgorithmTask task = new AlgorithmTask();
                task.setOnSucceeded(resultList -> task.getValue());

                Node node = (Node) event.getSource();
                Stage thisStage = (Stage) node.getScene().getWindow();

                Alert alert = createProgressAlert(thisStage, task);

                // Run the choosen algorithme on a alternate thread
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
            Alert alert = new Alert(Alert.AlertType.NONE, "No fileNb selected.\nPlease select a fileNb", ButtonType.OK);
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }

    private boolean checkFields() {
        if (!popSize.getText().equals(""))
        {
            int p = Integer.parseInt(popSize.getText());
            if (p<3){
                paramsErrorText = "Population size has to be >= 3";
                return true;
            }
            return false;
        }
        paramsErrorText = "Parameters missing";
        return true;
    }

    private Solution executeAlgorithme(AlgorithmTask task){
        /*
        Function executed on separate thread, the task passed on parameters permits cancellation from inside the algorithm, returns the solution.
        Throws an out of memory error
         */
        Solution resultList=new Solution();
        try{
            GWO GWO = new GWO();
            int iterations =  Integer.parseInt(maxIterations.getText());
            int pop = Integer.parseInt(popSize.getText());


            if(checkResolve.isSelected()) {
                for (fileNb = 1; fileNb <= 100; fileNb++) {
                    String path = folder + "\\in" + (fileNb + 600);//f.getAbsolutePath()
                    //String path = "E:\\Cours\\E-Commerce\\Projet\\groupe5\\instance\\in"+(fileNb +600);//f.getAbsolutePath()
                    System.out.println(path);

                    WDP = new WDPinstance(path);

                    double start = System.currentTimeMillis();
                    resultList = GWO.solve(pop, iterations, WDP, task);
                    time = (System.currentTimeMillis() - start) * 0.001;
                    writeToFile(resultList);

                    // cancel button pressed
                    if (task.isCancelled() ) {
                        System.out.println("Canceling...");
                        return resultList;
                    }
                }
                String ss = new File(this.getClass().getResource("").getPath()).getParentFile().getParent().replaceAll("(!|file:\\\\)", "").replace("%20", " ");

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Results have been saved to:\n"+ss+"\\results.csv", ButtonType.OK);
                alert.setTitle("File saved");
                alert.showAndWait();
                return resultList;
            } else {
                double start = System.currentTimeMillis();
                resultList = GWO.solve(pop, iterations, WDP, task);
                time = (System.currentTimeMillis() - start) * 0.001;
                System.out.println("Executed in "+time+" seconds");
                return resultList;
            }

        }
        catch (java.lang.OutOfMemoryError e){
            Alert alert = new Alert(Alert.AlertType.NONE, "Error.\nOut of Memory", ButtonType.OK);
            alert.setTitle("Error");
            alert.showAndWait();
        }
        return new Solution();
    }

    public void copyToClipBoardT() {
        StringSelection stringSelection = new StringSelection(timeLabel.getText().replace('.', ',').replace("s", "").trim());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

    }

    public void copyToClipBoardV() {
        StringSelection stringSelection = new StringSelection(labelTotal.getText().replace('.', ','));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    class AlgorithmTask extends Task<sample.Solution> {
        /*
        The task that executes the needed algorithm
        it shows a dialog for the progress
        & calls the display results function at the end
         */
        private AlgorithmTask() {
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
            System.out.println("Task failed.");//Task failed ;-)
        }
    }

    private void displayResults(Solution s) {

        StringBuilder text = new StringBuilder();
        for (int i=0; i<s.getBids().size(); i++){
            text.append(s.getBids().get(i)).append("\n");
        }
        DecimalFormat df = new DecimalFormat("#.###");

        satBidsArea.setText(text.toString());
        satBidsArea.setEditable(false);
        labelTotal.setText(df.format(s.getGain())+"");
        timeLabel.setText(df.format(time)+" s");
        int it=0;
        for (int i=0; i<s.getBids().size(); i++){
            it +=s.getBids().get(i).getItems().size();
        }
        labelObjectsNb.setText(it+"");

        writeToFile(s);

    }

    private void writeToFile(Solution s) {

        try {
            String Results = (fileNb + 600) + "," + maxIterations.getText() + "," + popSize.getText() + "," + s.getGain() + "," + time;
            String ss = new File(this.getClass().getResource("").getPath()).getParentFile().getParent().replaceAll("(!|file:\\\\)", "").replace("%20", " ");
            //System.out.println(ss + "\\results.csv");
            File file = new File(ss + "\\results.csv");
            if (file.createNewFile() || file.length() == 0) { // if fileNb already exists will do nothing
                FileOutputStream output = new FileOutputStream(file, false);
                try (Writer w = new OutputStreamWriter(output, "UTF-8")) {
                    w.write("Benchmark,nbIterations,NbIndividus,Gain,Temps" + "\n");
                    w.write(Results + "\n");//.replace(".", ",")
                    w.flush();
                }
                output.flush();
                output.close();
            }
            else {
                InputStream in = new FileInputStream(file);
                ArrayList<String> buffer = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while(reader.ready()) {
                    buffer.add(reader.readLine());
                }
                buffer.add(Results);
                FileOutputStream output = new FileOutputStream(file, false);
                try (Writer w = new OutputStreamWriter(output, "UTF-8")) {
                    for (String value : buffer) {
                        w.write(value + "\n");
                        w.flush();
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
