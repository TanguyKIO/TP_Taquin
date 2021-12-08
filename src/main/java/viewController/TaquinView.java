package viewController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
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
import model.Case;


import java.io.IOException;
import java.util.HashMap;


public class TaquinView extends Application {
    private BorderPane root; // pour pouvoir accéder facilement aux différents composants graphiques de l'interface
    private GridPane grille;
    private HashMap <String, Image> tabImages; // ensemble des images du jeu
    private model.Application app;
    private Stage currentStage;
    private int nbAgents;
    private int nbLignes;
    private int nbColonnes;

    @Override
    public void start(Stage primaryStage) throws IOException {
        creerMenuParamètres();
    }

    private void creerMenuParamètres(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Initialisation des paramètres : ");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label lblNbLignes = new Label("Nombre de lignes (entre 3 et 7) : ");
        grid.add(lblNbLignes, 0, 1);

        TextField nbLignesTextField = new TextField();
        grid.add(nbLignesTextField, 1, 1);

        Label lblNbColonnes = new Label("Nombre de colonnes (entre 3 et 7) : ");
        grid.add(lblNbColonnes, 0, 2);

        TextField nbColonnesTextField = new TextField();
        grid.add(nbColonnesTextField, 1, 2);

        Label lblNbAgents = new Label("Nombre d'agents (entre 3 et nombre de cas - 1) :");
        grid.add(lblNbAgents, 0, 3);

        TextField nbAgentsTextField = new TextField();
        grid.add(nbAgentsTextField, 1, 3);

        Button btn = new Button("Valider");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

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
                if (nbLignes < 3 || nbLignes > 7){
                    throw new Exception();
                }
            }
            catch (Exception error){
                nbLignesTextField.setText("");
                parametersOK = false;
            }
            try{
                nbColonnes = Integer.parseInt(nbColonnesTextField.getText());
                if (nbColonnes < 3 || nbColonnes > 7){
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
            if (parametersOK) {
                primaryStage.close();
                creerSceneJeu();
            }
        });
    }

    private void creerSceneJeu(){
        app = new model.Application(nbLignes, nbColonnes, nbAgents);
        Stage stage = new Stage();
        currentStage = stage;
        // On crée un BorderPane pour placer les composants principaux
        // de la fenêtre
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));

        ajouterComposants();
        afficherGrille();

        Scene scene2 = new Scene(root);
        stage.setMaxHeight(1000);
        stage.setMaxWidth(1000);
        stage.setResizable(false); // pour éviter d'avoir un affichage bizarre
        stage.setTitle("Jeu du Taquin");
        stage.setScene(scene2);
        stage.show();
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
        m_partie.setOnAction (actionEvent -> initialiserPartie()); //lambda function
        m_partie.setAccelerator (new KeyCodeCombination (KeyCode.N, KeyCombination.CONTROL_DOWN));
        // raccourci-clavier
        m_parametres.setOnAction (actionEvent -> {
            currentStage.close();
            creerMenuParamètres();
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

        if (nbAgents <= 15)
            createSmallGrid();
        else
            createBigGrid();

    }

    private void createSmallGrid(){
        Case [][] map = app.getEnv().getMap();
        Case [][] finalMap = app.getEnv().getFinalMap();
        int xLength = map.length;
        int yLength = map[0].length;

        //--- GridPane properties
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));
        grille.setHgap(30);
        grille.setVgap(10);

        GridPane subGrid = (GridPane) grille.getChildren().get(3);
        ImageView image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, 0, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, xLength+1, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, 0, yLength+1);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, xLength+1, yLength+1);

        for (int i =1; i<xLength+1; i++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid.add(image, 0, i);
            for (int j=1; j<yLength+1; j++){
                image = new ImageView();
                if (map[i-1][j-1].isOccupied()) {
                    if (finalMap[i-1][j-1].isOccupied() && map[i-1][j-1].getOccupation().getNom() == finalMap[i-1][j-1].getOccupation().getNom()){
                        image.setImage(new Image("file:res/images/" + map[i-1][j-1].getOccupation().getNom() + "_win.jpg"));
                    }
                    else {
                        image.setImage(new Image("file:res/images/" + map[i - 1][j - 1].getOccupation().getNom() + ".jpg"));
                    }
                }
                else{
                    image.setImage(new Image("file:res/images/blanc.jpg"));
                }
                subGrid.add(image, i, j);
            }
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid.add(image, xLength+1, i);
        }

        for (int j=1; j < yLength+1; j++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid.add(image, j, 0);
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid.add(image, j, yLength+1);
        }

        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));

        GridPane subGrid2 = (GridPane) grille.getChildren().get(4);
        subGrid2.add(image, 0, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid2.add(image, xLength+1, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid2.add(image, 0, yLength+1);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid2.add(image, xLength+1, yLength+1);

        for (int i =1; i<xLength+1; i++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid2.add(image, 0, i);
            for (int j=1; j<yLength+1; j++){
                image = new ImageView();
                if (finalMap[i-1][j-1].isOccupied()) {
                    image.setImage(new Image("file:res/images/" + finalMap[i-1][j-1].getOccupation().getNom() + ".jpg"));
                }
                else{
                    image.setImage(new Image("file:res/images/blanc.jpg"));
                }
                subGrid2.add(image, i, j);
            }
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid2.add(image, xLength+1, i);
        }

        for (int j=1; j < yLength+1; j++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid2.add(image, j, 0);
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid2.add(image, j, yLength+1);
        }
    }

    private void createBigGrid(){
        Case [][] map = app.getEnv().getMap();
        Case [][] finalMap = app.getEnv().getFinalMap();
        int xLength = map.length;
        int yLength = map[0].length;

        //--- GridPane properties
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));
        grille.setHgap(30);
        grille.setVgap(10);

        GridPane subGrid = (GridPane) grille.getChildren().get(3);
        ImageView image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, 0, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, xLength+1, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, 0, yLength+1);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid.add(image, xLength+1, yLength+1);

        for (int i =1; i<xLength+1; i++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid.add(image, 0, i);
            for (int j=1; j<yLength+1; j++){
                image = new ImageView();
                if (map[i-1][j-1].isOccupied()) {
                    if (finalMap[i-1][j-1].isOccupied() && map[i-1][j-1].getOccupation().getNom() == finalMap[i-1][j-1].getOccupation().getNom()){
                        image.setImage(new Image("file:res/images/" + map[i-1][j-1].getOccupation().getNom() + "_win.jpg"));
                    }
                    else {
                        image.setImage(new Image("file:res/images/" + map[i - 1][j - 1].getOccupation().getNom() + ".jpg"));
                    }
                }
                else{
                    image.setImage(new Image("file:res/images/blanc.jpg"));
                }
                subGrid.add(image, i, j);
            }
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid.add(image, xLength+1, i);
        }

        for (int j=1; j < yLength+1; j++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid.add(image, j, 0);
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid.add(image, j, yLength+1);
        }

        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));

        GridPane subGrid2 = (GridPane) grille.getChildren().get(4);
        subGrid2.add(image, 0, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid2.add(image, xLength+1, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid2.add(image, 0, yLength+1);
        image = new ImageView();
        image.setImage(new Image("file:res/images/contour_carre.jpg"));
        subGrid2.add(image, xLength+1, yLength+1);

        for (int i =1; i<xLength+1; i++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid2.add(image, 0, i);
            for (int j=1; j<yLength+1; j++){
                image = new ImageView();
                if (finalMap[i-1][j-1].isOccupied()) {
                    image.setImage(new Image("file:res/images/" + finalMap[i-1][j-1].getOccupation().getNom() + ".jpg"));
                }
                else{
                    image.setImage(new Image("file:res/images/blanc.jpg"));
                }
                subGrid2.add(image, i, j);
            }
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_vertical.jpg"));
            subGrid2.add(image, xLength+1, i);
        }

        for (int j=1; j < yLength+1; j++){
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid2.add(image, j, 0);
            image = new ImageView();
            image.setImage(new Image("file:res/images/contour_horizontal.jpg"));
            subGrid2.add(image, j, yLength+1);
        }
    }


    private void initialiserPartie () {
        app.initialiserPartie (nbLignes, nbColonnes, nbAgents);
        afficherGrille (); // On réinitialise la grille
    }

    public static void main(String[] args) {
        launch();
    }
}