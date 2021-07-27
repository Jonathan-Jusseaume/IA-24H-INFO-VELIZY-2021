package socket;

import lombok.Getter;
import lombok.Setter;
import models.Commande;
import parser.MessageParseur;
import utils.ParcoursLargeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Le SocketClient est la classe qui va être l'interface entre notre
 * programme et le serveur. Il va transmettre les informations reçues
 * directement au serveur.
 */
@Getter
@Setter
public class SocketClient {

    private Socket connexion;

    /**
     * Champ avec lequel on va obtenir les messages du serveur
     */
    private BufferedReader entree;

    /**
     * Champ avec lequel on va envoyer un message au serveur
     */
    private PrintWriter sortie;

    public static int clientId;

    private Commande commande;

    public SocketClient(String ip, int port) throws Exception {
        // création de la socket
        connexion = new Socket(ip, port);

        // Gestion des flux d'entrées et sorties
        entree = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
        sortie = new PrintWriter(connexion.getOutputStream(), true);
        Commande.setSocket(this);

        init();
    }

    /**
     * Lors de la connexion pour la première fois, le serveur s'attend à recevoir le nom de l'équipe et renvoie
     * ensuite l'id du client
     */
    private void init() throws Exception {
        Commande.recvCommand("NAME");
    }

    public String recv() throws IOException {
        while (!entree.ready()) ; // Attends qu'un message arrive
        return entree.readLine();
    }

    public void send(String s) {
        System.out.println("[SENDING] " + s);
        sortie.println(s);
    }

    public void run() throws Exception {
        MessageParseur mp = new MessageParseur();
        String id = Commande.recvCommand("START").split("\\|")[1];
        clientId = Integer.parseInt(id);

        while (true) {
            mp.setMap(Commande.sendCommand("GETMAP"));
            mp.setBikers(Commande.sendCommand("GETBIKERS|" + clientId));
            mp.setCommandes(Commande.sendCommand("GETDELIVERIES"));
            mp.launchIA();
            id = Commande.recvCommand("START").split("\\|")[1];
            clientId = Integer.parseInt(id);
        }
    }
}
