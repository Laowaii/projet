package semweb.projet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.riot.RDFDataMgr ;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RdfFromServer {
    //Ajout des données dans Fuseki
    public static void AddFuseki(String path, Element element) 
    {
        Model model = ModelFactory.createDefaultModel();
        
        model.read(path + element.text());                            
        String datasetURL = "http://localhost:3030/ProjetFinal";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlUpdate = datasetURL + "/update";
        String graphStore = datasetURL + "/data";
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
        conneg.load(model); // add the content of model to the triplestore
        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
        conneg.close();
    }
    //Procesus pour la récupération des données depuis les fichiers RDF
    public static void DlFileFromServer(String path)
    {
        try {
            
            Document doc = Jsoup.connect(path).get();
            Elements elements = doc.select("a[href]");

            for (Element element : elements)
            {
                if (element.text().contains(".ttl"))
                {
                    String[] pathSplit = path.split("/");
                    String filename = element.text();
                    String[] fileNameSplit = filename.split("\\.");
                    String newName = fileNameSplit[0] + "_" + pathSplit[pathSplit.length -1] + "." + fileNameSplit[1];
                    File file = new File("DL\\" + newName);

                    if (file.exists())
                    {
                        System.out.println("File already exist : " + element.text());                        
                    }
                    else
                    {
                            AddFuseki(path,element);

                            Files.copy(
                            new URL(path + "/" + element.text()).openStream(),
                            Paths.get("C:\\Cours\\M2\\Web Semantic\\Projet\\projet\\DL\\" + newName));
                    }
                }
                else if(element.text().contains("/"))
                {
                    DlFileFromServer(path + element.text());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void SendRDFToFuseki(String filePath)
    {
        File folder = new File(filePath);
        
		for (final File fileEntry : folder.listFiles()) 
        {
            String filename = fileEntry.getName();

            PipedRDFIterator<Triple> iter = new PipedRDFIterator<>();
            PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
            ExecutorService executor = Executors.newSingleThreadExecutor();

            Runnable parser = new Runnable() {
                String filePath = "C:\\Cours\\M2\\Web Semantic\\Projet\\projet\\DL\\"+ filename;
                @Override
                public void run() {
                    RDFDataMgr.parse(inputStream,filePath);
                }
            };

            executor.submit(parser);
            // while (iter.hasNext()) {
            //     Triple next = iter.next();
            //     System.out.println("Subject:  "+next.getSubject());
            //     System.out.println("Object:  "+next.getObject());
            //     System.out.println("Predicate:  "+next.getPredicate());
            //     System.out.println("\n");
            // }
        }
    }
}
