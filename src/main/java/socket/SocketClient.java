package socket;

import lombok.Getter;
import lombok.Setter;
import parser.MessageParseur;


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

    private MessageParseur messageParseur;

    /**
     * Champ avec lequel on va obtenir les messages du serveur
     */
    private BufferedReader entree;

    /**
     * Champ avec lequel on va envoyer un message au serveur
     */
    private PrintWriter sortie;

    private int clientId;

    public SocketClient(String ip, int port) throws IOException {
        // création de la socket
        connexion = new Socket(ip, port);

        // Gestion des flux d'entrées et sorties
        entree = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
        sortie = new PrintWriter(connexion.getOutputStream(), true);
        this.messageParseur = new MessageParseur();

        init();
    }

    /*
     * Lors de la connexion pour la première fois, le serveur s'attend à recevoir le nom de l'équipe et renvoie
     * ensuite l'id du client
     */
    private void init() throws IOException {
        send("Joueur 1");
        String idString = recv();
        clientId = Integer.parseInt(idString);
        System.out.println("Id reçu : " + clientId);
    }

    private String recv() throws IOException {
        while (!entree.ready()) ; // Attends qu'un message arrive
        return entree.readLine();
    }

    private void send(String s) {
        sortie.println(s);
    }

    public void run() throws IOException {
        while (true) {
            String message = recv();
            if (message == null) {
                break;
            }

            System.out.println("Lu : " + message);

            this.messageParseur.decode(message);
            this.send(this.messageParseur.encode());

        }
    }
}


