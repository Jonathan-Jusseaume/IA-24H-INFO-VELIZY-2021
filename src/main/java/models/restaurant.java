package models;

import java.awt.*;

/***
 * Classe prend en charge les donnees sur les restaurants
 *
 */
public class restaurant {

    Point position;

    Commande [] commandeLivre;

    public restaurant(Point parPosition){
        commandeLivre = new Commande[5];

        position = parPosition;
    }

    public void addCommande(Commande parCommande){
        for(int i = 0; i < 5; i++){
            if(commandeLivre[i] == null){
                commandeLivre[i] = parCommande;
            }
        }
    }

    public void popCommande(Commande parCommande){
        for(int i = 0; i < 5; i++){
            if(commandeLivre[i] == parCommande){
                commandeLivre[i] = null;
            }
        }
    }
}
