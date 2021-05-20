package parser;

import ia.IA;
import lombok.Getter;
import lombok.Setter;

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


    public MessageParseur() {
        historiqueIA = new ArrayList<>();
    }

    /**
     * Méthode qui va nous permettre de décoder le message du
     * serveur afin de créer un objet de la classe IA qui va ensuite
     * pouvoir prendre une décision
     * @param message
     */
    public void decode(String message) {
        /*
        Section dans laquelle on decode le message
         */
        String [] tableau = message.split("_");

        /*
        Instanciation de la classe IA avec les différents champs
        du modèle que l'on remplit
         */
        historiqueIA.add(new IA());
    }


    public String encode() {
        return historiqueIA.get(historiqueIA.size() - 1).takeDecision();
    }
}
