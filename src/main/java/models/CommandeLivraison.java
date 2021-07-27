package models;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class CommandeLivraison {

    /**
     * Le code de la commande
     */
    private int code;

    /**
     * La valeur de la commande
     */
    double valeur;

    /**
     * La localisation du restaurant qui a préparé la commande
     */
    Point restaurant;

    /**
     * La localisation de la maison qui a fait la commande
     */
    Point maison;

    /**
     * Le temps restant avant la pénalité
     */
    int tempsRestant;

    /**
     * Booléen permettant de savoir si la commande a été prise au restaurant
     */
    boolean retire = false;

    /**
     * Booléen permettant de savoir si la commande a été livrée
     */
    boolean livre = false;

    public CommandeLivraison (int code, double valeur, int restauX, int restauY, int maisonX, int maisonY, int tempsRestant) {
        this.code = code;
        this.valeur = valeur;
        this.restaurant = new Point(restauX, restauY);
        this.maison = new Point(maisonX,maisonY);
        this.tempsRestant = tempsRestant;
    }

}
