package ia;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe qui va prendre les différentes décisions pour les renvoyer
 * au parseur
 */
@Getter
@Setter
public class IA {

    /**
     * Les différentes données du jeu seront stockées
     * dans les différents champs de cette classe
     */
    private Object object;

    /**
     * Méthode où il y aura tout le processus de réfléxion pour prendre
     * la meilleure décision possible
     * @return
     */
    public String takeDecision() {
        return "MyDecision";
    }

}
