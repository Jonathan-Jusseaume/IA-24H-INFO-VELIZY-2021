package parser;

import ia.IA;
import lombok.Getter;
import lombok.Setter;
import models.Biker;
import models.CommandeLivraison;

import javax.swing.text.Position;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Notre parseur qui va décoder les messages mais aussi
 * renvoyés les décisions prises par l'IA au SocketClient
 */
@Getter
@Setter
public class MessageParseur {

    /**
     * Liste qui contient l'historique des IA au fur et à mesure de
     * la partie
     * (utile pour voir ce qu'a fait un adversaire par exemple, faire des
     * statistiques etc...)
     */
    List<IA> historiqueIA;

    IA currentIA;


    public MessageParseur() {

        historiqueIA = new ArrayList<>();
        currentIA = new IA(this);
        Biker[] bikers = new Biker[2];
        for (int i = 0; i < 2; i++) {
            bikers[i] = new Biker();
            bikers[i].setCoffre(new ArrayList<CommandeLivraison>());
        }
        currentIA.setBikers(bikers);
    }

    public String encode() {
        return historiqueIA.get(historiqueIA.size() - 1).takeDecision();
    }

    public void setBikers(String bikers) {
        String[] result = bikers.split("\\|");

        if (result[0].equals("OK")) {
            for (int i = 1; i < result.length; i++) {
                String courant = result[i];
                String[] coursier = courant.split(";");

                currentIA.getCoursiers()[i-1].setPosition(new Point(toInt(coursier[1]), toInt(coursier[2])));
                currentIA.getCoursiers()[i-1].setId(toInt(coursier[0]));
            }
        }
    }

    public void setCommandes(String comStr) {
        String[] result = comStr.split("\\|");
        CommandeLivraison[] commandes = new CommandeLivraison[result.length - 1];

        if (result[0].equals("OK")) {
            for (int i = 1; i < result.length; i++) {
                String courant = result[i];
                String[] commande = courant.split(";");

                System.out.println(toInt(commande[3]) + "|" +toInt(commande[2]));

                commandes[i - 1] = new CommandeLivraison(toInt(commande[0]), toDouble(commande[1]), toInt(commande[2]), toInt(commande[3]), toInt(commande[4]), toInt(commande[5]), toInt(commande[6]));
                currentIA.getMap()[toInt(commande[3])][toInt(commande[2])] = "RA";
            }
        }

        currentIA.setCommande(commandes);
    }

    /**
     * utile pour les commandes
     * TAKE
     * DELIVER
     */
    public boolean isReturnOK(String chaine) {
        boolean ret = true;

        String[] resultat = chaine.split("\\|");

        ret = chaine.split("\\|").length == 1;

        return ret;
    }

    public int getScore(String score) {
        return toInt(score.split("\\|")[1]);
    }

    public void setMap(String message) {
        Biker[] coursiers = currentIA.getCoursiers();
        ArrayList<CommandeLivraison> commandes;

        String[][] map = new String[31][31];
        if (message.substring(0,2).equals("OK")) {
            String toParse = message.substring(3, message.length());
            for (int i = 0; i < map.length; i++) {
                String ligne = toParse.substring(i * 31, (i + 1) * 31);
                for (int j = 0; j < map[0].length; j++) {
                    map[j][i] = String.valueOf(ligne.charAt(j));
                }
            }
        }

        for (int i = 0; i < coursiers.length; i++) {
            commandes = coursiers[i].getCoffre();
            for (CommandeLivraison c : commandes){
                map[c.getMaison().y][c.getMaison().x]="L" + coursiers[i].getId();
            }

        }

        currentIA.setMap(map);
    }

    public int toInt(String chaine) {
        return Integer.parseInt(chaine);
    }

    public double toDouble(String chaine) {
        return Double.parseDouble(chaine);
    }

    public String encode(String[] chaine) {
        String resultat = "";

        for (int i = 0; i < chaine.length - 1; i++) {
            resultat += chaine[i] + "|";
        }

        resultat += chaine[chaine.length - 1];

        return resultat;
    }

    public void launchIA(){
        currentIA.start();
    }

    public List<IA> getHistoriqueIA() {
        return historiqueIA;
    }

    public void setHistoriqueIA(List<IA> historiqueIA) {
        this.historiqueIA = historiqueIA;
    }

    public IA getCurrentIA() {
        return currentIA;
    }

    public void setCurrentIA(IA currentIA) {
        this.currentIA = currentIA;
    }
}
