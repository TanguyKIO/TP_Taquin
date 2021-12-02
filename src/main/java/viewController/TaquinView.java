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
import java.util.List;


public class TaquinView extends Application {
    private BorderPane root; // pour pouvoir accéder facilement aux différents composants graphiques de l'interface
    private GridPane grille;
    private HashMap <String, Image> tabImages; // ensemble des images du jeu
    private model.Application app;


    @Override
    public void start(Stage primaryStage) throws IOException {
        app = new model.Application();
        // On crée un BorderPane pour placer les composants principaux
        // de la fenêtre
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));

        ajouterComposants();
        afficherGrille();

        Scene scene = new Scene (root);
        //primaryStage.setWidth(1000);
        //primaryStage.setHeight(1000);
        primaryStage.setMaxHeight(1000);
        primaryStage.setMaxWidth(1000);
        primaryStage.setResizable(false); // pour éviter d'avoir un affichage bizarre
        primaryStage.setTitle("Jeu du Taquin");
        primaryStage.setScene(scene);
        primaryStage.show();
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
        MenuItem mi_quitter = new MenuItem ("_Quitter");

        mi_quitter.setOnAction (actionEvent -> Platform.exit ()); //lambda function
        mi_quitter.setAccelerator (new KeyCodeCombination (KeyCode.X, KeyCombination.CONTROL_DOWN));
        // raccourci-clavier
        m_partie.setOnAction (actionEvent -> initialiserPartie()); //lambda function
        m_partie.setAccelerator (new KeyCodeCombination (KeyCode.N, KeyCombination.CONTROL_DOWN));
        // raccourci-clavier

        m_fichier.getItems ().addAll(m_partie, mi_quitter);


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
        grille.getStyleClass().add("grille");
        grille.add (subGrid, 0, 2);

        GridPane subGrid2 = new GridPane();
        grille.getStyleClass().add("grille");
        grille.add (subGrid2, 1, 2);
        GridPane.setHalignment(subGrid, HPos.RIGHT);

        Case [][] map = app.getEnv().getMap();
        Case [][] finalMap = app.getEnv().getFinalMap();
        int xLength = map.length;
        int yLength = map[0].length;

        ImageView image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid.add(image, 0, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid.add(image, xLength+1, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid.add(image, 0, yLength+1);
        image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid.add(image, xLength+1, yLength+1);

        for (int i =1; i<xLength+1; i++){
            image = new ImageView();
            image.setImage(new Image("file:res/contour_vertical.jpg"));
            subGrid.add(image, 0, i);
            for (int j=1; j<yLength+1; j++){
                image = new ImageView();
                if (map[i-1][j-1].isOccupied()) {
                    if (finalMap[i-1][j-1].isOccupied() && map[i-1][j-1].getOccupation().getNom() == finalMap[i-1][j-1].getOccupation().getNom()){
                        image.setImage(new Image("file:res/" + map[i-1][j-1].getOccupation().getNom() + "_win.jpg"));
                    }
                    else {
                        image.setImage(new Image("file:res/" + map[i - 1][j - 1].getOccupation().getNom() + ".jpg"));
                    }
                }
                else{
                    image.setImage(new Image("file:res/blanc.jpg"));
                }
                subGrid.add(image, i, j);
            }
            image = new ImageView();
            image.setImage(new Image("file:res/contour_vertical.jpg"));
            subGrid.add(image, xLength+1, i);
        }

        for (int j=1; j < yLength+1; j++){
            image = new ImageView();
            image.setImage(new Image("file:res/contour_horizontal.jpg"));
            subGrid.add(image, j, 0);
            image = new ImageView();
            image.setImage(new Image("file:res/contour_horizontal.jpg"));
            subGrid.add(image, j, yLength+1);
        }

        image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid2.add(image, 0, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid2.add(image, xLength+1, 0);
        image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid2.add(image, 0, yLength+1);
        image = new ImageView();
        image.setImage(new Image("file:res/contour_carre.jpg"));
        subGrid2.add(image, xLength+1, yLength+1);

        for (int i =1; i<xLength+1; i++){
            image = new ImageView();
            image.setImage(new Image("file:res/contour_vertical.jpg"));
            subGrid2.add(image, 0, i);
            for (int j=1; j<yLength+1; j++){
                image = new ImageView();
                if (finalMap[i-1][j-1].isOccupied()) {
                    image.setImage(new Image("file:res/" + finalMap[i-1][j-1].getOccupation().getNom() + ".jpg"));
                }
                else{
                    image.setImage(new Image("file:res/blanc.jpg"));
                }
                subGrid2.add(image, i, j);
            }
            image = new ImageView();
            image.setImage(new Image("file:res/contour_vertical.jpg"));
            subGrid2.add(image, xLength+1, i);
        }

        for (int j=1; j < yLength+1; j++){
            image = new ImageView();
            image.setImage(new Image("file:res/contour_horizontal.jpg"));
            subGrid2.add(image, j, 0);
            image = new ImageView();
            image.setImage(new Image("file:res/contour_horizontal.jpg"));
            subGrid2.add(image, j, yLength+1);
        }

        //--- GridPane properties
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));
        grille.setHgap(30);
        grille.setVgap(10);
    }

    private void initialiserPartie () {
        app.initialiserPartie ();
        afficherGrille (); // On réinitialise la grille
    }

    public static void main(String[] args) {
        launch();
    }
}