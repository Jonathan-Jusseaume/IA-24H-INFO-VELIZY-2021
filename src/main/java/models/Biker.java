package models;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public class Biker {
    private int id;
    private Point position;
    private ArrayList<CommandeLivraison> coffre = new ArrayList<CommandeLivraison>();

    private boolean chercherCommande = false;
    private boolean livrerCommande = false;

    public Biker(int id, int x, int y) {
        this.id = id;
        this.position = new Point(x, y);
    }

    public Biker() {
    }


    public Point getCommandeDestination(int index) {
        return coffre.get(index).maison;
    }

    /***
     * Methode de deplacement d'une case du biker
     */
    public void move(int direction) {
        switch (direction) {
            case 0:
                this.position.y -= 1;
                break;
            case 1:
                this.position.x += 1;
                break;
            case 2:
                this.position.y += 1;
                break;
            case 4:
                this.position.x -= 1;
                break;
            default:
                break;
        }
    }

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
    }

    public int getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public ArrayList<CommandeLivraison> getCoffre() {
        return coffre;
    }

    public void setCoffre(ArrayList<CommandeLivraison> coffre) {
        this.coffre = coffre;
    }

    public boolean isChercherCommande() {
        return chercherCommande;
    }

    public void setChercherCommande(boolean chercherCommande) {
        this.chercherCommande = chercherCommande;
    }

    public boolean isLivrerCommande() {
        return livrerCommande;
    }

    public void setLivrerCommande(boolean livrerCommande) {
        this.livrerCommande = livrerCommande;
    }
}
