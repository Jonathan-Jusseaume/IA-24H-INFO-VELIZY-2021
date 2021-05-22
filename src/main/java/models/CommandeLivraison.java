package models;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class CommandeLivraison {
    int code;
    double valeur;
    Point restaurant;
    Point maison;
    int tempsRestant;
    boolean retire = false;
    boolean livre = false;

    public CommandeLivraison (int code, double valeur, int restauX, int restauY, int maisonX, int maisonY, int tempsRestant) {
        this.code = code;
        this.valeur = valeur;
        this.restaurant = new Point(restauX, restauY);
        this.maison = new Point(maisonX,maisonY);
        this.tempsRestant = tempsRestant;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public double getValeur() {
        return valeur;
    }

    public void setValeur(double valeur) {
        this.valeur = valeur;
    }

    public Point getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Point restaurant) {
        this.restaurant = restaurant;
    }

    public Point getMaison() {
        return maison;
    }

    public void setMaison(Point maison) {
        this.maison = maison;
    }

    public int getTempsRestant() {
        return tempsRestant;
    }

    public void setTempsRestant(int tempsRestant) {
        this.tempsRestant = tempsRestant;
    }

    public boolean isRetire() {
        return retire;
    }

    public void setRetire(boolean retire) {
        this.retire = retire;
    }

    public boolean isLivre() {
        return livre;
    }

    public void setLivre(boolean livre) {
        this.livre = livre;
    }
}
