package partie2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import partie1.*;

import java.util.List;
import java.util.Map;

// Assure-toi que les imports correspondent bien à tes packages
// Si ta classe Reseaux est dans partie1, décommente la ligne suivante :
// import partie1.Reseaux; 
// Sinon, si elle est dans partie2, c'est bon.

public class MenuProjet extends Application {

    private Stage primaryStage;
    private Reseaux monReseau; // On ne l'initialise pas tout de suite, on attend le chargement

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Projet PAA - Gestion Réseau");
        afficherMenuPrincipal();
        primaryStage.show();
    }

    // =================================================================
    // PAGE 1 : MENU PRINCIPAL (ACCUEIL AVEC CHAMP TEXTE)
    // =================================================================
    private void afficherMenuPrincipal() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f4f4;");

        // Titre
        Label titre = new Label(" GESTION RÉSEAU ÉLECTRIQUE");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setStyle("-fx-text-fill: #2c3e50;");

        // --- ZONE DE CHARGEMENT FICHIER ---
        VBox zoneChargement = new VBox(10);
        zoneChargement.setAlignment(Pos.CENTER);
        zoneChargement.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        zoneChargement.setMaxWidth(400);

        Label lblInstru = new Label("Entrez le nom du fichier à charger :");
        
        // LE CHAMP DE TEXTE
        TextField txtNomFichier = new TextField();
        txtNomFichier.setPromptText("ex: reseau.txt");
        txtNomFichier.setPrefWidth(250);

        // LE BOUTON VALIDER
        Button btnValider = new Button(" Charger le réseau");
        btnValider.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        
        // Action : On récupère le texte et on lance le chargement
        btnValider.setOnAction(e -> {
            String nomFichier = txtNomFichier.getText();
            actionChargerFichier(nomFichier);
        });

        zoneChargement.getChildren().addAll(lblInstru, txtNomFichier, btnValider);

        // ----------------------------------
//MASSILVA
        // Bouton Construction Manuelle (Teammate)
        Button btnManuel = new Button(" Mode Manuel POUR MASSILVA ;)");
        btnManuel.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnManuel.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("En construction");
            alert.setContentText("Pitié");
            alert.showAndWait();
        });

        Button btnQuitter = new Button(" Quitter");
        btnQuitter.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnQuitter.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(titre, new Separator(), zoneChargement, new Separator(), btnManuel, btnQuitter);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }

    // =================================================================
    // PAGE 2 : AFFICHAGE DU RÉSEAU
    // =================================================================
    private void afficherPageReseau() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // En haut
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button btnRetour = new Button("⬅ Retour");
        btnRetour.setOnAction(e -> afficherMenuPrincipal());
        Label lblTitre = new Label("État du Réseau");
        lblTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topBar.getChildren().addAll(btnRetour, lblTitre);
        root.setTop(topBar);

        // Au centre (Affichage texte)
        TextArea zoneAffichage = new TextArea();
        zoneAffichage.setEditable(false);
        zoneAffichage.setFont(Font.font("Consolas", 14));
        zoneAffichage.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00;");
        
        // On génère le texte
        zoneAffichage.setText(genererRapportReseau());
        root.setCenter(zoneAffichage);

        // En bas (Boutons d'action)
        HBox bottomBar = new HBox(15);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(15, 0, 0, 0));
        
        Button btnSauvegarder = new Button("💾 Sauvegarder");
        // Ajoute ici l'action pour sauvegarder si besoin
        
        bottomBar.getChildren().add(btnSauvegarder);
        root.setBottom(bottomBar);

        primaryStage.setScene(new Scene(root, 800, 600));
    }

    // =================================================================
    // FICHIERLOADER
    // =================================================================

    private void actionChargerFichier(String nomFichier) {
        if (nomFichier.isEmpty()) {
            afficherErreur("Veuillez entrer un nom de fichier !");
            return;
        }

        try {
            // APPEL À TON CHARGEUR (Il peut lancer une exception si fichier introuvable)
            this.monReseau = FichierLoader.chargerDepuisFichier(nomFichier);
            
            // Si tout s'est bien passé, on change de page
            afficherPageReseau();

        } catch (Exception e) {
            // Affichage de l'erreur précise (ex: "Fichier introuvable" ou "Erreur ligne 3")
            afficherErreur("Erreur lors du chargement :\n" + e.getMessage());
            e.printStackTrace(); // Utile pour voir l'erreur dans la console VS Code
        }
    }

    private String genererRapportReseau() {
        if (monReseau == null) return "Erreur : Réseau non chargé.";

        StringBuilder sb = new StringBuilder();
        sb.append("=== RÉSEAU CHARGÉ ===\n\n");

        // Utilisation des getters de ta classe Reseaux
        // Adapte les noms getG(), getM() selon ta classe Reseaux exacte
        
        sb.append("Générateurs :\n");
        // Si ta méthode s'appelle getGenerateurs(), change ici :
        for (Generateur g : monReseau.getG()) { 
            sb.append(" - ").append(g.getnom())
              .append(" (Capacité: ").append(g.getcap()).append(" kW)\n");
        }

        sb.append("\nMaisons :\n");
        for (Maison m : monReseau.getM()) {
            sb.append(" - ").append(m.getnom())
              .append(" (Conso: ").append(m.getcons()).append(" kW)\n");
        }

        sb.append("\nConnexions :\n");
        // Si ta classe Reseaux stocke les connexions dans une Map<Generateur, List<Maison>>
        // Adapte cette partie selon tes getters :
        Map<Generateur, List<Maison>> connexions = monReseau.getConnexions();
        for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
            Generateur g = entry.getKey();
            for (Maison m : entry.getValue()) {
                sb.append(" - ").append(m.getnom()).append(" --> ").append(g.getnom()).append("\n");
            }
        }

        return sb.toString();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


public static void main(String[] args) {
    launch(args);
}
}
