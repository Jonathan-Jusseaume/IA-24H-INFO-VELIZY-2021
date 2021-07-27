package utils;

import ia.IA;
import lombok.Getter;
import lombok.Setter;
import models.Biker;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

@Getter
@Setter
public class ParcoursLargeur {
    /**
     *     Tableau des éléments qui bloquent le passage
     */
    private static String[] obstacles = {"E", "H", "S"};

    /**
     *     Tableau des cases à atteindre
     */
    private static String[] objectifs = {"S"};


    /**
     * Permet de savoir si on autorise les déplacements en diagonale
     */
    private static boolean diagonale = false;

    private static int distMin;

    /**
     * @param map carte
     * @param x   x de départ (0 à gauche)
     * @param y   y de départ (0 en haut)
     * @return le chemin le plus court vers un objectif
     * <p>
     * Elements présents dans le tableau retourné :
     * 'D' = droite
     * 'G' = gauche
     * 'H' = haut
     * 'B' = bas
     * 'HG' = diagonale haut gauche
     * 'BG' = diagonale bas gauche
     * 'HD' = diagonale haut droite
     * 'BD' = diagonale bas droite
     * <p>
     * Exemple d'utilisation :
     * <p>
     * String[][] map = {{" "," "," "," ","M"," "},
     * {" "," "," ","M","X"," "},
     * {" ","M"," "," ","M"," "},
     * {" "," ","M","M"," "," "},
     * {" "," "," "," ","M","M"},
     * {" "," "," "," "," "," "},};
     * <p>
     * String[] chemin;
     * <p>
     * chemin = ParcoursLargeur.parcoursLargeur(map, 0, 0);
     */
    public static String[] parcoursLargeur(String[][] map, int x, int y, IA ia, int idLivreur) {
        String[] ret;
        Point[][] precedent = new Point[map.length][map[0].length];
        int[][] distance = new int[map.length][map[0].length];
        Deque<Point> queue = new ArrayDeque<Point>();
        boolean trouve = false;

        Point racine = new Point(x, y);
        Point courant;
        Point[] voisins;

        queue.addLast(racine);
        for (int i = 0; i < distance.length; i++) {
            Arrays.fill(precedent[i], null);
            Arrays.fill(distance[i], -1);
        }
        distance[racine.y][racine.x] = 0;

        while (!queue.isEmpty() && !trouve)  //Tant que élément candidat + pas trouvé
        {
            courant = queue.pop();
            voisins = getAllVoisins(map, courant);

            for (int i = 0; i < voisins.length && voisins[i] != null; i++) {
                if (distance[voisins[i].y][voisins[i].x] == -1)  //Si pas encore de distance trouvée pour le voisin
                {
                    if (precedent[voisins[i].y][voisins[i].x] == null) {
                        distance[voisins[i].y][voisins[i].x] = distance[courant.y][courant.x] + 1;
                        precedent[voisins[i].y][voisins[i].x] = courant;
                    }
                    queue.addLast(voisins[i]);
                }
            }
        }

        Point[] cordObjectifs = getAllObectifs(map);

        distMin = 1000;

        Point objectifProche = selectObjectif(cordObjectifs, distance, racine, ia, idLivreur);

        if (distMin == 1000) {
            distMin = 0;
        }

        ret = new String[distMin];
        Point prec;
        int cpt = distMin - 1;

        while (objectifProche != racine) {
            prec = precedent[objectifProche.y][objectifProche.x];
            ret[cpt] = getDirection(prec.x - objectifProche.x, prec.y - objectifProche.y);
            cpt--;
            objectifProche = prec;
        }

        return ret;
    }

    private static Point selectObjectif(Point[] cordObjectifs, int[][] distance, Point racine, IA ia, int idLivreur) {
        Point objectifProche = racine;
        Biker autreCoursier = ia.getOtherCoursier(idLivreur);

        for (int i = 0; i < cordObjectifs.length && cordObjectifs[i] != null; i++) {
            if (distance[cordObjectifs[i].y][cordObjectifs[i].x] < distMin
                    && !autreCoursier.listePoint.contains(cordObjectifs[i])) {

                distMin = distance[cordObjectifs[i].y][cordObjectifs[i].x];
                objectifProche = cordObjectifs[i];
            }
        }

        if (ia.getCoursierById(idLivreur).isChercherCommande()) {
            ia.getCoursierById(idLivreur).listePoint.add(objectifProche);
        }

        return objectifProche;
    }

    private static Point[] getAllVoisins(String[][] map, Point courant) {
        Point[] voisins = new Point[9];
        int cpt = 0;

        if (diagonale) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!(i == 0 && j == 0) &&
                            !(courant.x + i < 0 || courant.y + j < 0 || courant.y + j > map.length - 1 || courant.x + i > map[0].length - 1) &&
                            !tabContient(obstacles, map[courant.y + j][courant.x + i])) {

                        voisins[cpt] = new Point(courant.x + i, courant.y + j);
                        cpt++;
                    }
                }
            }
        } else {
            for (int i = -1; i < 2; i += 2) {
                if (!(courant.x + i < 0 || courant.x + i > map[0].length - 1) &&
                        !tabContient(obstacles, map[courant.y][courant.x + i])) {

                    voisins[cpt] = new Point(courant.x + i, courant.y);
                    cpt++;
                }

                if (!(courant.y + i < 0 || courant.y + i > map.length - 1) &&
                        !tabContient(obstacles, map[courant.y + i][courant.x])) {

                    voisins[cpt] = new Point(courant.x, courant.y + i);
                    cpt++;
                }
            }
        }

        return voisins;
    }

    private static boolean tabContient(String[] tab, String element) {
        boolean ret = false;

        for (int i = 0; i < tab.length && !ret; i++) {
            ret = tab[i].equals(element);
        }

        return ret;
    }

    private static Point[] getAllObectifs(String[][] map) {
        Point[] ret = new Point[(map.length * map[0].length)];
        int cpt = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (tabContient(objectifs, map[i][j])) {
                    ret[cpt] = new Point(j, i);
                    cpt++;
                }
            }
        }

        return ret;
    }

    private static String getDirection(int i, int j) {
        String ret = "";

        if (j == 1) {
            ret += "L";
        } else if (j == -1) {
            ret += "R";
        }

        if (i == 1) {
            ret += "T";
        } else if (i == -1) {
            ret += "B";
        }

        return ret;
    }

    public static Point getMouvement(String direction) {
        Point ret = new Point(0, 0);

        if (direction.equals("L")) {
            ret.y = 1;
        } else if (direction.equals("R")) {
            ret.y = -1;
        }

        if (direction.equals("T")) {
            ret.x = 1;
        } else if (direction.equals("B")) {
            ret.x = -1;
        }

        return ret;
    }

    public static void setObstacles(String[] obstacles) {
        ParcoursLargeur.obstacles = obstacles;
    }

    public static void setObjectifs(String[] objectifs) {
        ParcoursLargeur.objectifs = objectifs;
    }
}
