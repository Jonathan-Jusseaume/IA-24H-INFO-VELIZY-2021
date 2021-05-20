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
        this.connexion = new Socket(ip, port);
        this.entree = new BufferedReader(new InputStreamReader(this.connexion.getInputStream()));
        this.sortie = new PrintWriter(this.connexion.getOutputStream(), true);
        this.sortie.println("JOUEUR 1");

        while (!this.entree.ready()) ;
        this.clientId = Integer.parseInt(this.entree.readLine());
        this.messageParseur = new MessageParseur();
    }

    public void run() throws IOException {
        while (true) {
            if (this.entree.ready()) {
                String message = this.entree.readLine();
                if (message == null) {
                    break;
                }
                this.messageParseur.decode(message);
                String encodedReturnMessage = this.messageParseur.encode();
                this.sortie.println(encodedReturnMessage);
            }
        }
    }


}
