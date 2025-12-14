Projet PAA - Gestion et optimisation d'un rseau lectrique 1. Classe principale 
------------------- Le programme se lance avec la classe suivante :
    partie2.Test Cette classe contient la mthode `main()` qui permet soit de 
charger un rseau depuis un fichier, soit de construire le rseau manuellement 
via le menu console. 2. Algorithme d'optimisation automatique 
---------------------------------------- Nous avons implment un algorithme 
d'automatisation qui optimise la rpartition des maisons entre les gnrateurs. 
Il est bas sur les principes suivants : - Approche gloutonne : on assigne 
chaque maison au gnrateur le moins charg possible. - Itrations : le programme 
peut effectuer un grand nombre d'itrations (par dfaut entre 2000 et 50000)
  pour amliorer le rseau et rduire le cot total et la surcharge. - Critres 
d'optimisation : le cot total du rseau, la dispersion entre gnrateurs et la 
surcharge. Cet algorithme est plus efficace que la simple attribution manuelle 
car il tente de minimiser la surcharge et le cot global en redistribuant 
automatiquement les maisons entre gnrateurs lorsque cest possible. 3. 
Fonctionnalits implmentes ------------------------------- - Chargement d'un 
rseau depuis un fichier texte avec validation complte des gnrateurs,
  maisons et connexions. - Construction manuelle du rseau via menu interactif 
:
    * Ajout de gnrateurs et maisons * Ajout et suppression de connexions - 
Vrification automatique que chaque maison est connecte exactement un gnrateur. 
- Calcul du cot total du rseau. - Optimisation automatique du rseau via 
l'algorithme glouton avec itrations. - Sauvegarde du rseau dans un fichier 
texte. - Tests unitaires pour toutes les classes importantes (partie1 et 
partie2). - Documentation JavaDoc gnre dans le dossier `doc/`. 4. 
Fonctionnalits manquantes ou problmes connus 
------------------------------------------------- - Pas d'interface graphique, 
uniquement console. - L'algorithme glouton n'est pas garanti d'obtenir la 
solution optimale globale, mais il amliore
  le rseau par rapport une rpartition manuelle. 5. Structure du projet 
--------------------- Projet PAA/
 src/
    partie1/
       Generateur.java Maison.java Reseaux.java
    partie2/
        Automatisation.java FichierLoader.java SauvegardeReseau.java Test.java 
        <-- classe principale avec main()
 tests/
    partie1/ partie2/
 bin/ doc/ 6. Lancement du programme ------------------------- Depuis le 
terminal, compilez les fichiers Java si ncessaire :
    javac -d bin src/partie1/*.java src/partie2/*.java Puis lancez le 
programme avec :
    java -cp bin partie2.Test [fichier_reseau.txt] [lambda] - 
`fichier_reseau.txt` (optionnel) : chemin vers le fichier rseau charger. - 
`lambda` (optionnel) : coefficient pour le calcul du cot (par dfaut 10.0). Si 
aucun fichier nest fourni, le programme propose la construction manuelle du 
rseau.
