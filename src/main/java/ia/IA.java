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
import java.util.stream.Collectors;

/**
 * Classe qui va prendre les différentes décisions pour les renvoyer
 * au parseur
 */
@Getter
@Setter
public class IA {
    MessageParseur mp;
    String[][] map;
    CommandeLivraison[] commandes;
    Biker[] coursiers;
    int pa;

    int actionDisponible = 0;

    public IA(MessageParseur messageParseur) {
        this.mp = messageParseur;
    }

    /**
     * Méthode où il y aura tout le processus de réfléxion pour prendre
     * la meilleure décision possible
     *
     * @return decision prise
     */
    public String takeDecision() {
        for (int i = 0; i < coursiers.length; i++) {
            ArrayList<CommandeLivraison> coffre = coursiers[i].getCoffre();

            if (coffre.size() > 0) {                          //a partir d'une commande dans le coffre on livre
                coursiers[i].setChercherCommande(false);
                coursiers[i].setLivrerCommande(true);
            } else {                                       //si pas de commande, on vas chercher la commande
                coursiers[i].setLivrerCommande(false);
                coursiers[i].setChercherCommande(true);
            }

            ArrayList<CommandeLivraison> commande = coursiers[i].getCoffre();
            for (int j = 0; j < commande.size(); j++) {
                if (commande.get(j).isLivre()) {
                    commande.remove(j);
                }
            }

            coursiers[i].setCoffre(commande);
        }

        applyDescision();

        return "MyDecision";
    }

    public void applyDescision() {
        while (pa > 0) {
            String[] cheminASuivre;
            String[] obstacles = new String[0];

            for (int i = 0; i < coursiers.length; i++) {            //Pour chaque coursier
                if (getOtherCoursier(coursiers[i].getId()).getCoffre().size() > 0) {
                    continue;
                }

                String[] objectifs = new String[1];

                if (coursiers[i].isChercherCommande()) {
                    objectifs = new String[]{"RA"};
                    obstacles = new String[]{"E", "H", "S", "L" + coursiers[0].getId(), "L" + coursiers[0].getId()};
                } else if (coursiers[i].isLivrerCommande()) {
                    objectifs = new String[]{"L" + coursiers[i].getId()};
                    obstacles = new String[]{"E", "H", "S", "RA"};
                }

                ParcoursLargeur.setObjectifs(objectifs);
                ParcoursLargeur.setObstacles(obstacles);

                cheminASuivre = ParcoursLargeur.parcoursLargeur(map, coursiers[i].getPosition().x, coursiers[i].getPosition().y, this, coursiers[i].getId());

                if (cheminASuivre.length <= 1) {
                    if (coursiers[i].isChercherCommande()) {
                        checkPickUpRestaurant(cheminASuivre, i);
                    } else if (coursiers[i].isLivrerCommande()) {
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

    private void deplaceLivreur(String[] cheminASuivre, int i) {
        Biker coursier = coursiers[i];

        String deplacement = "MOVE|" + coursier.getId() + "|" + cheminASuivre[0];

        try {
            Commande.sendCommand(deplacement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String recup;
        try {
            recup = Commande.sendCommand("GETBIKERS|" + SocketClient.clientId);
            mp.setBikers(recup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pa--;
    }

    private void checkDeliverOrder(String[] cheminASuivre, int i) {
        if (cheminASuivre.length == 1) {
            Point mouv = ParcoursLargeur.getMouvement(cheminASuivre[0]);
            Point biker = coursiers[i].getPosition();
            CommandeLivraison commande = null;

            commande = getCommandeByMaisonPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), coursiers[i], true);
            Point client = commande.getMaison();

            if (getCommandeByMaisonPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), coursiers[i], false) == null) {
                map[client.y][client.x] = "E";
            }

            try {
                Commande.sendCommand("DELIVER|" + coursiers[i].getId() + "|" + commande.getCode());
                coursiers[i].getCoffre().remove(commande);
                pa--;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPickUpRestaurant(String[] cheminASuivre, int i) {
        if (cheminASuivre.length == 1) {
            Point mouv = ParcoursLargeur.getMouvement(cheminASuivre[0]);
            Point biker = coursiers[i].getPosition();
            CommandeLivraison commande = null;

            System.out.println(biker);
            commande = getCommandeByRestaurantPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), true);
            Point client = commande.getMaison();


            if (getCommandeByRestaurantPosition(new Point(biker.x - mouv.x, biker.y - mouv.y), false) == null) {
                map[biker.y - mouv.y][biker.x - mouv.x] = "R";
            }

            map[client.y][client.x] = "L" + coursiers[i].getId();//+coursiers[i].getId()+commande.getCode();

            try {
                Commande.sendCommand("TAKE|" + coursiers[i].getId() + "|" + commande.getCode());
                coursiers[i].getCoffre().add(commande);
                pa--;
                coursiers[i].listePoint.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCommande(CommandeLivraison[] commandes) {
        this.commandes = commandes;
    }

    public void setBikers(Biker[] coursiers) {
        this.coursiers = coursiers;
    }

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


    public CommandeLivraison getPlusCher() {
        if (commandes.length > 0) {
            CommandeLivraison commandeLivraison = commandes[0];
            for (int i = 1; i < commandes.length; i++) {
                if (commandes[i].getValeur() > commandeLivraison.getValeur()) {
                    commandeLivraison = commandes[i];
                }
            }
            return commandeLivraison;
        }
        return null;
    }

    public void start() {
        pa = 8;
        takeDecision();
    }

    public void setMap(String[][] map) {
        this.map = map;
    }

    public String[][] getMap() {
        return map;
    }

    public Biker[] getCoursiers() {
        return coursiers;
    }

    public Biker getOtherCoursier(int id) {
        if (coursiers[0].getId() == id) {
            return coursiers[1];
        } else {
            return coursiers[0];
        }
    }

    public Biker getCoursierById(int id) {
        if (coursiers[0].getId() == id) {
            return coursiers[0];
        } else {
            return coursiers[1];
        }
    }
}
