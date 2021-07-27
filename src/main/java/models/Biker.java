package models;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public class Biker {

    /**
     * ID du biker
     */
    private int id;

    /**
     * Position du biker sur la MAP
     */
    private Point position;

    /**
     * Liste des commandes que portent le biker sur lui
     */
    private ArrayList<CommandeLivraison> coffre = new ArrayList<>();

    /**
     * Liste des points que doit viser le biker
     */
    public ArrayList<Point> listePoint = new ArrayList<>();

    /**
     * Booléen permettant de savoir si le biker est en train de chercher une commande
     */
    private boolean chercherCommande = false;

    /**
     * Booléen permettant de savoir si le biker est en train de livrer une commande
     */
    private boolean livrerCommande = false;


    public Biker() {
    }

}
