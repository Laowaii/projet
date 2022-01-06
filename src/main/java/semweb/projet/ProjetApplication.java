package semweb.projet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetApplication 
{
	public static void main(String[] args) 
    {
		SpringApplication.run(ProjetApplication.class, args);
	    // Ne pas modifier le lien
        // RdfFromServer.DlFileFromServer("https://territoire.emse.fr/kg/");
        // Tous les fichiers sont dans Dl (pour les RDF) ou Données (pour les csv)
        // Modifier les chemin pour l'adapter au PC
        // RdfFromServer.SendRDFToFuseki("C:\\Cours\\M2\\Web Semantic\\Projet\\projet\\DL");
        // RdfFromCSV.ReadCSV("C:\\Cours\\M2\\Web Semantic\\Projet\\projet\\Données");  

        // Ne pas modifier le lien     
        // GetMetoCiel.GetData("https://www.meteociel.fr/temps-reel/obs_villes.php?code2=7475&jour2=16&mois2=10&annee2=2021", "2021-11-16"); 
        // GetMetoCiel.GetData("https://www.meteociel.fr/temps-reel/obs_villes.php?code2=7475&jour2=15&mois2=10&annee2=2021", "2021-11-15"); 
	}
}