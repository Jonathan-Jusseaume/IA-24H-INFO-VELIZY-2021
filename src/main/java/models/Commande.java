package models;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

import socket.SocketClient;

public class Commande {

    private static SocketClient sock;

    public static void setSocket(SocketClient _sock) {
        sock = _sock;
    }

    /*
     * JOUEUR -> SERVEUR
     */

    /*
     * Envoie la commande est renvoie "YES" si la commande n'a pas eu d'erreur sinon "NO"
     */
    private static String errCommand(String cmd) throws IOException {
        String res = command(cmd);
        if (res.compareTo("OK") != 0) {
            System.err.println("[ERR] " + cmd);
            return "NO";
        }
        return "YES";
    }

    /*
     * Execute la commande et renvoie le résultat
     */
    private static String command(String cmd) throws IOException {
        sock.send(cmd);
        String r =sock.recv();
        System.out.println("recv: " + r);
        return r;
    }

    /*
     * SERVEUR -> JOUEUR
     */
    private static String name() throws IOException {
        waitCommand("NAME");
        sock.send("VelDev");
        return "";
    }

    private static String start() throws IOException {
        return waitCommand("START");
    }

    private static String endGame() throws IOException {
        return isCommand("ENDGAME") ? "YES" : "NO";
    }

    /*
     * Wrappers
     */

    /*
     * Vérifie si la prochaine commande reçu est celle demandée.
     */
    private static boolean isCommand(String command) throws IOException {
        return sock.recv().compareTo(command) == 0;
    }

    /*
     * Bloque tant que la commande reçu n'est pas celle attendu
     */
    private static String waitCommand(String command) throws IOException {
        String msg = "";
        while ((msg = sock.recv()).startsWith(command) == false);
        return msg;
    }

    /*
     * Juste un wrapper de sendCommand pour bien montrer qu'on s'attend à recevoir une commande
     * et non en envoyer une
     */
    public static String recvCommand(String command) throws Exception {
        return sendCommand(command);
    }

    /*
     * Envoie une commande vers le serveur si elle existe
     */
    public static String sendCommand(String command) throws Exception {
        String commandName = command.split("\\|")[0];

        System.out.println("[Commande] '" + commandName + "'");
        String res = null;

        switch (commandName) {
            case "MOVE": res = errCommand(command); break;
            case "GETBIKERS": res = command(command); break;
            case "TEAMS": res =  command(command); break;
            case "GETMAP": res =  command(command);  break;
            case "GETDELIVERIES": res =  command(command); break;
            case "TAKE": res =  errCommand(command); break;
            case "DELIVER": res =  errCommand(command); break;
            case "ENDTURN": res =  command(command); break;
            case "SCORE": res =  command(command); break;
            case "START": res =  start(); break;
            case "NAME": res =  name(); break;
            case "ENDGAME": res =  endGame(); break;
        }

        if (res == null) {
            System.out.println("Commande inconnue : " + commandName);
        }

        return res;
    }
}