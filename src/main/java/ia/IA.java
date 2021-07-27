package ia;

import lombok.Getter;
import lombok.Setter;
import models.Biker;
import models.Commande;
import models.CommandeLivraison;
import parser.MessageParseur;
import socket.SocketClient;
import utils.ParcoursLargeur;

import java.awt.*;
import java.util.ArrayList;

/**
 * Classe qui va prendre les différentes décisions pour les renvoyer
 * au parseur
 */
@Getter
@Setter
public class IA {

    /**
     * Le parseur des messages
     */
    private MessageParseur messageParseur;

    /**
     * La carte du jeu
     */
    private String[][] map;

    /**
     * La liste des commandes
     */
    private CommandeLivraison[] commandes;

    /**
     * La liste de nos coursiers
     */
    private Biker[] bikers;

    /**
     * Notre nombre de points d'actions
     */
    int pointsAction;

    int actionDisponible = 0;

    /**
     * Constructeur de la classe qui prend en paramètres le parseur
     *
     * @param messageParseur
     */
    public IA(MessageParseur messageParseur) {
        this.messageParseur = messageParseur;
    }

    /**
     * Méthode où il y aura tout le processus de réfléxion pour prendre
     * la meilleure décision possible
     *
     * @return decision prise
     */
    public void takeDecision() {
        for (int i = 0; i < bikers.length; i++) {
            ArrayList<CommandeLivraison> coffre = bikers[i].getCoffre();

            if (coffre.size() > 0) {                          //a partir d'une commande dans le coffre on livre
                bikers[i].setChercherCommande(false);
                bikers[i].setLivrerCommande(true);
            } else {                                       //si pas de commande, on vas chercher la commande
                bikers[i].setLivrerCommande(false);
                bikers[i].setChercherCommande(true);
            }

            ArrayList<CommandeLivraison> commande = bikers[i].getCoffre();
            for (int j = 0; j < commande.size(); j++) {
                if (commande.get(j).isLivre()) {
                    commande.remove(j);
                }
            }

            bikers[i].setCoffre(commande);
        }
        applyDescision();
    }

    /**
     * Méthode où l'on va appliquer la décision
     */
    public void applyDescision() {
        while (pointsAction > 0) {
            String[] cheminASuivre;
            String[] obstacles = new String[0];

            for (int i = 0; i < bikers.length; i++) {            //Pour chaque coursier
                if (getOtherCoursier(bikers[i].getId()).getCoffre().size() > 0) {
                    continue;
                }

                String[] objectifs = new String[1];

                if (bikers[i].isChercherCommande()) {
                    objectifs = new String[]{"RA"};
                    obstacles = new String[]{"E", "H", "S", "L" + bikers[0].getId(), "L" + bikers[0].getId()};
                } else if (bikers[i].isLivrerCommande()) {
                    objectifs = new String[]{"L" + bikers[i].getId()};
                    obstacles = new String[]{"E", "H", "S", "RA"};
                }

                ParcoursLargeur.setObjectifs(objectifs);
                ParcoursLargeur.setObstacles(obstacles);

                cheminASuivre = ParcoursLargeur.parcoursLargeur(map, bikers[i].getPosition().x, bikers[i].getPosition().y, this, bikers[i].getId());

                if (cheminASuivre.length <= 1) {
                    if (bikers[i].isChercherCommande()) {
                        checkPickUpRestaurant(cheminASuivre, i);
                    } else if (bikers[i].isLivrerCommande()) {
                        checkDeliverOrder(cheminASuivre, i);
                    }
                } else {
                    deplaceLivreur(cheminASuivre, i);
                }
            }

        }
        try {
            Commande.sendCommand("ENDTURN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthodes où on déplace un livreur
     * @param cheminASuivre
     * @param i
     */
    private void deplaceLivreur(String[] cheminASuivre, int i) {
        Biker coursier = bikers[i];

        String deplacement = "MOVE|" + coursier.getId() + "|" + cheminASuivre[0];

        try {
            Commande.sendCommand(deplacement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String recup;
        try {
            recup = Commande.sendCommand("GETBIKERS|" + SocketClient.clientId);
            messageParseur.setBikers(recup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pointsAction--;
    }

    /**
     * Méthode où on regarde si on peut délivrer notre commande à la bonne maison
     * @param cheminASuivre
     * @param i
     */
    private void checkDeliverOrder(String[] cheminASuivre, int i) {
        if (cheminASuivre.length == 1) {
            Point mouv = ParcoursLargeur.getMouvement(cheminASuivre[0]);
            Point biker = bikers[i].getPosition();
            CommandeLivraison commande = null;

            commande = getCommandeByMaisonPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), bikers[i], true);
            Point client = commande.getMaison();

            if (getCommandeByMaisonPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), bikers[i], false) == null) {
                map[client.y][client.x] = "E";
            }

            try {
                Commande.sendCommand("DELIVER|" + bikers[i].getId() + "|" + commande.getCode());
                bikers[i].getCoffre().remove(commande);
                pointsAction--;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode où on regarde les restaurants pour prendre un éventuel repas
     * @param cheminASuivre
     * @param i
     */
    private void checkPickUpRestaurant(String[] cheminASuivre, int i) {
        if (cheminASuivre.length == 1) {
            Point mouv = ParcoursLargeur.getMouvement(cheminASuivre[0]);
            Point biker = bikers[i].getPosition();
            CommandeLivraison commande = null;

            System.out.println(biker);
            commande = getCommandeByRestaurantPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), true);
            Point client = commande.getMaison();


            if (getCommandeByRestaurantPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), false) == null) {
                map[biker.y - mouv.y][biker.x - mouv.x] = "R";
            }

            map[client.y][client.x] = "L" + bikers[i].getId();//+coursiers[i].getId()+commande.getCode();

            try {
                Commande.sendCommand("TAKE|" + bikers[i].getId() + "|" + commande.getCode());
                bikers[i].getCoffre().add(commande);
                pointsAction--;
                bikers[i].listePoint.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode où on récupère la commande en fonction de la position du restaurant
     * @param pointRestau
     * @param take
     * @return
     */
    public CommandeLivraison getCommandeByRestaurantPosition(Point pointRestau, boolean take) {
        for (int i = 0; i < commandes.length; i++) {
            if (commandes[i].getRestaurant().x == pointRestau.x && commandes[i].getRestaurant().y == pointRestau.y && !commandes[i].isRetire()) {
                CommandeLivraison c = commandes[i];
                if (take) {
                    commandes[i].setRetire(true);
                }
                return c;
            }
        }

        return null;
    }

    /**
     * Méthode où on récup_re la commande en fonction de la position de la maison
     * @param pointMaison
     * @param coursier
     * @param take
     * @return
     */
    public CommandeLivraison getCommandeByMaisonPosition(Point pointMaison, Biker coursier, boolean take) {
        ArrayList<CommandeLivraison> commandes = coursier.getCoffre();

        for (int i = 0; i < commandes.size(); i++) {
            if (commandes.get(i).getMaison().x == pointMaison.x && commandes.get(i).getMaison().y == pointMaison.y && commandes.get(i).isRetire() && !commandes.get(i).isLivre()) {
                CommandeLivraison c = commandes.get(i);
                if (take) {
                    commandes.get(i).setLivre(true);
                }
                return c;
            }
        }

        return null;
    }

    /**
     * Déclencheur
     */
    public void start() {
        pointsAction = 8;
        takeDecision();
    }

    /**
     * Obtenir le coursier opposé à celui qui a l'ID passé en paramètre
     * @param id
     * @return
     */
    public Biker getOtherCoursier(int id) {
        if (bikers[0].getId() == id) {
            return bikers[1];
        } else {
            return bikers[0];
        }
    }

    /**
     * Obtenir le coursier qui a l'ID passé en paramètre
     * @param id
     * @return
     */
    public Biker getCoursierById(int id) {
        if (bikers[0].getId() == id) {
            return bikers[0];
        } else {
            return bikers[1];
        }
    }
}
