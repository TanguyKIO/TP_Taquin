package viewController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Agent;

import java.io.File;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class TaquinView extends Application implements Observer {
    private BorderPane root; // pour pouvoir accéder facilement aux différents composants graphiques de l'interface
    private GridPane grille;
    private HashMap <String, Image> tabImages; // ensemble des images du jeu
    private model.Application app;
    private Stage currentStage;
    private int nbAgents;
    private int nbLignes;
    private int nbColonnes;
    private int strategie;
    private int vitesseAffichage;

    @Override
    public void start(Stage primaryStage) {
        loadImages();
        creerMenuParametres();
    }

    private void loadImages(){
        File folder = new File("res/images");
        File[] listOfFiles = folder.listFiles();
        tabImages = new HashMap<>();
        for (File file:listOfFiles){
            tabImages.put(file.getName(), new Image ("file:res/images/" + file.getName()));
        }
    }

    private void creerMenuParametres(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Initialisation des paramètres : ");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label lblNbLignes = new Label("Nombre de lignes (entre 3 et 6) : ");
        grid.add(lblNbLignes, 0, 1);

        TextField nbLignesTextField = new TextField();
        nbLignesTextField.setText("5");
        grid.add(nbLignesTextField, 1, 1);

        Label lblNbColonnes = new Label("Nombre de colonnes (entre 3 et 6) : ");
        grid.add(lblNbColonnes, 0, 2);

        TextField nbColonnesTextField = new TextField();
        nbColonnesTextField.setText("5");
        grid.add(nbColonnesTextField, 1, 2);

        Label lblNbAgents = new Label("Nombre d'agents (entre 3 et nombre de cases - 1) :");
        grid.add(lblNbAgents, 0, 3);

        TextField nbAgentsTextField = new TextField();
        nbAgentsTextField.setText("20");
        grid.add(nbAgentsTextField, 1, 3);

        Label lblStrategie = new Label("Stratégie (0: En ligne, 1: Contours d'abord, 2: En spirale, 3: Pas de contrainte) :");
        grid.add(lblStrategie, 0, 4);

        TextField strategieTextField = new TextField();
        strategieTextField.setText("0");
        grid.add(strategieTextField, 1, 4);

        Label lblAffichage = new Label("Vitesse d'affichage (0: Lent, 1: Normal, 2: Rapide, 3: Très rapide) :");
        grid.add(lblAffichage, 0, 5);

        TextField affichageTextField = new TextField();
        affichageTextField.setText("2");
        grid.add(affichageTextField, 1, 5);

        Text text = new Text();
        text.setText("Les stratégies contours d'abord et en spirale ne s'adaptent qu'à des grilles d'au moins 5 lignes et 5 colonnes");
        text.setStyle("-fx-font-weight: bold");
        grid.add(text, 0, 6);

        Button btn = new Button("Valider");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 6);

        Stage primaryStage = new Stage();
        currentStage = primaryStage;
        Scene sceneInit = new Scene(grid);
        primaryStage.setScene(sceneInit); // set the scene
        primaryStage.setTitle("Initialisation des paramètres");
        primaryStage.show();

        primaryStage.setScene(sceneInit);
        btn.setOnAction(e-> {
            boolean parametersOK = true;
            try{
                nbLignes = Integer.parseInt(nbLignesTextField.getText());
                if (nbLignes < 3 || nbLignes > 6){
                    throw new Exception();
                }
            }
            catch (Exception error){
                nbLignesTextField.setText("");
                parametersOK = false;
            }
            try{
                nbColonnes = Integer.parseInt(nbColonnesTextField.getText());
                if (nbColonnes < 3 || nbColonnes > 6){
                    throw new Exception();
                }
            }
            catch (Exception error){
                nbColonnesTextField.setText("");
                parametersOK = false;
            }
            try{
                nbAgents = Integer.parseInt(nbAgentsTextField.getText());
                if (nbAgents < 3 || nbAgents > nbLignes*nbColonnes-1){
                    throw new Exception();
                }
            }
            catch (Exception error){
                nbAgentsTextField.setText("");
                parametersOK = false;
            }
            try{
                strategie = Integer.parseInt(strategieTextField.getText());
                if (strategie < 0 || strategie > 3){
                    throw new Exception();
                }
            }
            catch (Exception error){
                strategieTextField.setText("");
                parametersOK = false;
            }
            try{
                vitesseAffichage = Integer.parseInt(affichageTextField.getText());
                if (vitesseAffichage < 0 || vitesseAffichage > 3){
                    throw new Exception();
                }
            }
            catch (Exception error){
                affichageTextField.setText("");
                parametersOK = false;
            }
            if (parametersOK) {
                primaryStage.close();
                creerSceneJeu();
                lancerPartie();
            }
        });
    }

    private void creerSceneJeu() {
        Stage stage = new Stage();
        currentStage = stage;
        // On crée un BorderPane pour placer les composants principaux
        // de la fenêtre
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));

        ajouterComposants();
        initialiserPartie();

        Scene scene2 = new Scene(root);
        stage.setMaxHeight(1000);
        stage.setMaxWidth(1000);
        stage.setResizable(false); // pour éviter d'avoir un affichage bizarre
        stage.setTitle("Jeu du Taquin");
        stage.setScene(scene2);
        stage.show();
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }


    private void ajouterComposants(){
        // En haut, on mettra une barre de menu, puis en-dessous, une toolbar
        // avec le nombre de vies et le score
        VBox vb_haut = new VBox ();
        root.setTop (vb_haut);

        /* MENU */
        MenuBar mb_menu = new MenuBar ();
        ToolBar a_toolbar = new ToolBar ();
        vb_haut.getChildren ().addAll(mb_menu, a_toolbar);


        /* MENUS */
        Menu m_fichier = new Menu ("_Fichier"); // _ devant = raccourci-clavier affiché avec ALT
        mb_menu.getMenus ().addAll(m_fichier);

        /* ELEMENTS DU MENU */
        // FICHIER
        MenuItem m_partie = new MenuItem ("_Nouvelle partie");
        MenuItem m_quitter = new MenuItem ("_Quitter");
        MenuItem m_parametres = new MenuItem ("_Choix des paramètres");

        m_quitter.setOnAction (actionEvent -> Platform.exit ()); //lambda function
        m_quitter.setAccelerator (new KeyCodeCombination (KeyCode.X, KeyCombination.CONTROL_DOWN));
        // raccourci-clavier
        m_partie.setOnAction (actionEvent -> reinitialiserPartie()); //lambda function
        m_partie.setAccelerator (new KeyCodeCombination (KeyCode.N, KeyCombination.CONTROL_DOWN));
        // raccourci-clavier
        m_parametres.setOnAction (actionEvent -> {
            app.getEnv().stopAgents();
            currentStage.close();
            creerMenuParametres();
        }); //lambda function
        m_parametres.setAccelerator (new KeyCodeCombination (KeyCode.P, KeyCombination.CONTROL_DOWN));

        m_fichier.getItems ().addAll(m_partie, m_quitter, m_parametres);


        // En-dessous de la barre de menu et de la toolbar, on ajoute un contenant
        // pour pouvoir mettre la grille
        StackPane contenantCentre = new StackPane ();
        root.setCenter(contenantCentre);

        GridPane grille = new GridPane (); // création de la grille
        grille.getStyleClass().add("grille");
        contenantCentre.getChildren ().add (grille);

        this.grille = (GridPane) ((StackPane) root.getCenter ()).getChildren().get(0);

        Label lblTitle = new Label("Jeu du Taquin");
        lblTitle.setFont(Font.font("Arial Black", FontWeight.NORMAL, 32));
        lblTitle.setTextFill(Color.FIREBRICK);
        grille.add(lblTitle, 0, 0, 2, 1);

        Label lblSolutionCourante = new Label("Solution en cours");
        lblSolutionCourante.setFont(Font.font("Arial Black", FontWeight.NORMAL, 15));
        lblSolutionCourante.setTextFill(Color.FIREBRICK);
        grille.add(lblSolutionCourante, 0, 1, 1, 1);
        GridPane.setHalignment(lblSolutionCourante, HPos.CENTER);

        Label lblSolutionFinale = new Label("Solution finale");
        lblSolutionFinale.setFont(Font.font("Arial Black", FontWeight.NORMAL, 15));
        lblSolutionFinale.setTextFill(Color.FIREBRICK);
        grille.add(lblSolutionFinale, 1, 1, 1, 1);
        GridPane.setHalignment(lblSolutionFinale, HPos.CENTER);

        GridPane.setHalignment(lblTitle, HPos.CENTER);
        GridPane.setMargin(lblTitle, new Insets(0, 0, 10,0));
    }

    private void afficherGrille(){
        /* GRILLE */

        GridPane subGrid = new GridPane();
        subGrid.getStyleClass().add("grille");
        grille.add (subGrid, 0, 2);

        GridPane subGrid2 = new GridPane();
        subGrid2.getStyleClass().add("grille");
        grille.add (subGrid2, 1, 2);
        GridPane.setHalignment(subGrid, HPos.RIGHT);

        if (nbLignes * nbColonnes <= 16)
            createSmallGrid();
        else
            createBigGrid();

    }

    private void createSmallGrid(){
        createSubSmallGridCurrent();
        createSubSmallGridSolution();
        //--- GridPane properties
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));
        grille.setHgap(30);
        grille.setVgap(10);
    }

    private void createBigGrid(){
        createSubBigGridCurrent();
        createSubBigGridSolution();
        //--- GridPane properties
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));
        grille.setHgap(30);
        grille.setVgap(10);
    }

    private void createSubSmallGridCurrent(){
        Agent [][] map = app.getEnv().getMap();
        int [][] finalMap = app.getEnv().getFinalMap();
        GridPane subGrid = (GridPane) grille.getChildren().get(3);
        ImageView image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, 0, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, nbColonnes+1, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, 0, nbLignes+1);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, nbColonnes+1, nbLignes+1);

        for (int i =1; i<nbLignes+1; i++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid.add(image, 0, i);
            for (int j=1; j<nbColonnes+1; j++){
                image = new ImageView();
                if (map[i-1][j-1] != null) {
                    if (finalMap[i-1][j-1] != 0 && map[i-1][j-1].getNom() == finalMap[i-1][j-1]){
                        image.setImage(tabImages.get(map[i-1][j-1].getNom() + "_win.jpg"));
                    }
                    else {
                        image.setImage(tabImages.get(map[i - 1][j - 1].getNom() + ".jpg"));
                    }
                }
                else{
                    image.setImage(tabImages.get("blanc.jpg"));
                }
                subGrid.add(image, j, i);
            }
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid.add(image, nbColonnes+1, i);
        }

        for (int j=1; j < nbColonnes+1; j++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid.add(image, j, 0);
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid.add(image, j, nbLignes+1);
        }
    }

    private synchronized void updateSubSmallGridCurrent(){
        int iter = 4;
        ImageView image;
        Agent [][] map = app.getEnv().getMap();
        int [][] finalMap = app.getEnv().getFinalMap();
        GridPane subGrid = (GridPane) grille.getChildren().get(3);
        for (int i =1; i<nbLignes+1; i++){
            iter += 1;
            for (int j=1; j<nbColonnes+1; j++){
                image = (ImageView)subGrid.getChildren().get(iter);
                if (map[i-1][j-1] != null) {
                    if (finalMap[i-1][j-1] != 0 && map[i-1][j-1].getNom() == finalMap[i-1][j-1]){
                        image.setImage(tabImages.get(map[i-1][j-1].getNom() + "_win.jpg"));
                    }
                    else {
                        image.setImage(tabImages.get(map[i - 1][j - 1].getNom() + ".jpg"));
                    }
                }
                else{
                    image.setImage(tabImages.get("blanc.jpg"));
                }
                iter += 1;
            }
            iter += 1;
        }
    }

    private void createSubSmallGridSolution(){
        int [][] finalMap = app.getEnv().getFinalMap();
        ImageView image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));

        GridPane subGrid2 = (GridPane) grille.getChildren().get(4);
        subGrid2.add(image, 0, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid2.add(image, nbColonnes+1, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid2.add(image, 0, nbLignes+1);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid2.add(image, nbColonnes+1, nbLignes+1);

        for (int i =1; i<nbLignes+1; i++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid2.add(image, 0, i);
            for (int j=1; j<nbColonnes+1; j++){
                image = new ImageView();
                if (finalMap[i-1][j-1] != 0) {
                    image.setImage(tabImages.get(finalMap[i-1][j-1] + ".jpg"));
                }
                else{
                    image.setImage(tabImages.get("blanc.jpg"));
                }
                subGrid2.add(image, j, i);
            }
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid2.add(image, nbColonnes+1, i);
        }

        for (int j=1; j < nbColonnes+1; j++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid2.add(image, j, 0);
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid2.add(image, j, nbLignes+1);
        }
    }

    private synchronized void updateSubSmallGridSolution(){
        int iter = 4;
        ImageView image;
        int [][] finalMap = app.getEnv().getFinalMap();
        GridPane subGrid = (GridPane) grille.getChildren().get(4);
        for (int i =1; i<nbLignes+1; i++){
            iter += 1;
            for (int j=1; j<nbColonnes+1; j++){
                image = (ImageView)subGrid.getChildren().get(iter);
                if (finalMap[i-1][j-1] != 0) {
                    image.setImage(tabImages.get(finalMap[i-1][j-1] + ".jpg"));
                }
                else{
                    image.setImage(tabImages.get("blanc.jpg"));
                }
                iter += 1;
            }
            iter += 1;
        }
    }

    private void createSubBigGridCurrent(){
        StackPane stack;
        Rectangle rect;
        Text text;
        Agent [][] map = app.getEnv().getMap();
        int [][] finalMap = app.getEnv().getFinalMap();
        GridPane subGrid = (GridPane) grille.getChildren().get(3);
        ImageView image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, 0, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, nbColonnes+1, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, 0, nbLignes+1);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid.add(image, nbColonnes+1, nbLignes+1);

        for (int i =1; i<nbLignes+1; i++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid.add(image, 0, i);
            for (int j=1; j<nbColonnes+1; j++){
                stack = new StackPane();
                rect = new Rectangle(40, 40);
                rect.setStyle("-fx-stroke: black; -fx-stroke-width: 1;");
                if (map[i-1][j-1] != null) {
                    if (finalMap[i-1][j-1] != 0 && map[i-1][j-1].getNom() == finalMap[i-1][j-1]){
                        rect.setFill(Color.GREEN);
                    }
                    else {
                        rect.setFill(Color.BISQUE);
                    }
                    text = new Text(String.valueOf(map[i-1][j-1].getNom()));
                }
                else{
                    rect.setFill(Color.WHITE);
                    text = new Text();
                }
                text.setFont(new Font(20));
                text.setFill(Color.RED);
                text.setStyle("-fx-font-weight: bold");
                stack.getChildren().addAll(rect, text);
                subGrid.add(stack, j, i);
            }
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid.add(image, nbColonnes+1, i);
        }

        for (int j=1; j < nbColonnes+1; j++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid.add(image, j, 0);
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid.add(image, j, nbLignes+1);
        }
    }

    private synchronized void updateSubBigGridCurrent(){
        int iter = 4;
        Rectangle rect;
        Text text;
        Agent [][] map = app.getEnv().getMap();
        int [][] finalMap = app.getEnv().getFinalMap();
        GridPane subGrid = (GridPane) grille.getChildren().get(3);
        for (int i =1; i<nbLignes+1; i++){
            iter += 1;
            for (int j=1; j<nbColonnes+1; j++){
                rect = (Rectangle) ((StackPane)subGrid.getChildren().get(iter)).getChildren().get(0);
                text = (Text) ((StackPane)subGrid.getChildren().get(iter)).getChildren().get(1);
                if (map[i-1][j-1] != null) {
                    if (finalMap[i-1][j-1] != 0 && map[i-1][j-1].getNom() == finalMap[i-1][j-1]){
                        rect.setFill(Color.GREEN);
                    }
                    else {
                        rect.setFill(Color.BISQUE);
                    }
                    try {
                        text.setText(String.valueOf(map[i - 1][j - 1].getNom()));
                    }
                    catch (Exception e){
                        continue;
                    }
                }
                else{
                    rect.setFill(Color.WHITE);
                    text.setText("");
                }
                iter += 1;
            }
            iter += 1;
        }
    }

    private synchronized void createSubBigGridSolution(){
        StackPane stack;
        Rectangle rect;
        Text text;
        int [][] finalMap = app.getEnv().getFinalMap();
        ImageView image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));

        GridPane subGrid2 = (GridPane) grille.getChildren().get(4);
        subGrid2.add(image, 0, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid2.add(image, nbColonnes+1, 0);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid2.add(image, 0, nbLignes+1);
        image = new ImageView();
        image.setImage(tabImages.get("contour_carre.jpg"));
        subGrid2.add(image, nbColonnes+1, nbLignes+1);

        for (int i =1; i<nbLignes+1; i++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid2.add(image, 0, i);
            for (int j=1; j<nbColonnes+1; j++){
                stack = new StackPane();
                rect = new Rectangle(40, 40);
                rect.setStyle("-fx-stroke: black; -fx-stroke-width: 1;");
                if (finalMap[i-1][j-1] != 0) {
                    rect.setFill(Color.BISQUE);
                    text = new Text(String.valueOf(finalMap[i-1][j-1]));
                }
                else{
                    rect.setFill(Color.WHITE);
                    text = new Text();
                }
                text.setFont(new Font(20));
                text.setFill(Color.RED);
                text.setStyle("-fx-font-weight: bold");
                stack.getChildren().addAll(rect, text);
                subGrid2.add(stack, j, i);
            }
            image = new ImageView();
            image.setImage(tabImages.get("contour_vertical.jpg"));
            subGrid2.add(image, nbColonnes+1, i);
        }

        for (int j=1; j < nbColonnes+1; j++){
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid2.add(image, j, 0);
            image = new ImageView();
            image.setImage(tabImages.get("contour_horizontal.jpg"));
            subGrid2.add(image, j, nbLignes+1);
        }
    }

    private synchronized void updateSubBigGridSolution(){
        Rectangle rect;
        Text text;
        int iter = 4;
        int [][] finalMap = app.getEnv().getFinalMap();
        GridPane subGrid = (GridPane) grille.getChildren().get(4);
        for (int i =1; i<nbLignes+1; i++){
            iter += 1;
            for (int j=1; j<nbColonnes+1; j++){
                rect = (Rectangle) ((StackPane)subGrid.getChildren().get(iter)).getChildren().get(0);
                text = (Text) ((StackPane)subGrid.getChildren().get(iter)).getChildren().get(1);
                if (finalMap[i-1][j-1] != 0) {
                    rect.setFill(Color.BISQUE);
                    text.setText(String.valueOf(finalMap[i-1][j-1]));
                }
                else{
                    rect.setFill(Color.WHITE);
                    text.setText("");
                }
                iter += 1;
            }
            iter += 1;
        }
    }

    private void initialiserPartie () {
        app = new model.Application(nbLignes, nbColonnes, nbAgents, strategie, vitesseAffichage);
        afficherGrille (); // On réinitialise la grille
    }

    private void lancerPartie(){
        for (Agent agent:app.getEnv().getAgents()){
            agent.addObserver(this);
        }
        app.getEnv().start();
    }

    private void reinitialiserPartie () {
        app.getEnv().reinitialiser();
        if (nbLignes * nbColonnes <= 16) {
            updateSubSmallGridCurrent();
            updateSubSmallGridSolution();
        }
        else {
            updateSubBigGridSolution();
            updateSubBigGridCurrent();
        }
        app.getEnv().restart();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        try {
            if (nbLignes * nbColonnes <= 16)
                updateSubSmallGridCurrent();
            else
                updateSubBigGridCurrent();
        }
        catch (Exception ignored){
        }

    }
}