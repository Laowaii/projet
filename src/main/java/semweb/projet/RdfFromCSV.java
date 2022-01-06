package semweb.projet;

import com.opencsv.bean.CsvToBeanBuilder;

import org.apache.jena.datatypes.xsd.XSDDatatype.XSDGenericType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import semweb.projet.Model.SensorMeasures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;


public class RdfFromCSV 
{
    
    public static File[] listFilesForFolder(String folderPath) 
    {
        File folder = new File(folderPath);
        return  folder.listFiles();
    }


    //Procesus pour la récupération des données depuis les fichiers csv
    public static void ReadCSV(String folderPath)
    {
       File[] folder =  listFilesForFolder(folderPath);
       for (File file : folder)
        {
            GetCsvData(folderPath + "\\" + file.getName()); 
        }        
    }

    public static void GetCsvData(String fileName) throws NumberFormatException
    {
        List<SensorMeasures> measures;
        try 
        {
            measures = new CsvToBeanBuilder(new FileReader(fileName))
                    .withType(SensorMeasures.class)
                    .build()
                    .parse();

            String datasetURL = "http://localhost:3030/ProjetFinal";
            String sparqlEndpoint = datasetURL + "/sparql";
            String sparqlUpdate = datasetURL + "/update";
            String graphStore = datasetURL + "/data";
            RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
                    
                    
            for (SensorMeasures measure : measures) 
            {
                if (!measure.temperature.isEmpty())
                {
                    measure.time = new java.text.SimpleDateFormat("yyyy-MM-dd")
                    .format(new java.util.Date (Long.parseLong(measure.time)/1000000))
                    +"T"+new java.text.SimpleDateFormat("HH:mm:ss")
                    .format(new java.util.Date (Long.parseLong(measure.time)/1000000))+"+00:00";
    
                    Model m = ModelFactory.createDefaultModel();
    
                    //Define the prefix rdf:
                    m.setNsPrefix("rdf", RDF.uri);
                    //Define the prefix ex:
                    m.setNsPrefix("ex", "http://example.com/");
                    //Define the prefix rdfs:
                    m.setNsPrefix("rdfs", RDFS.uri);
                    //Define the prefix xsd:
                    m.setNsPrefix("xsd", XSD.getURI());
                    String url = "https://territoire.emse.fr/kg/" + measure.location.replace("e1", "1ET")
                                                                                    .replace("e2", "2ET").replace("e3", "3ET").replace("e4", "4ET")
                                                                                    .replace("e5", "5ET").replace("e6", "6ET").replace("S", "");
                    Resource r = m.createResource(url);

                    // r.addProperty(m.createProperty(url + "/" + measure.time + "/type"),measure.type,XSDGenericType.XSDstring);
                    // https://territoire.emse.fr/kg/emse/fayol/4ET/425/heure/type    type
                    // r.addProperty(m.createProperty(url + "/" + measure.time + "/name"),measure.name,XSDGenericType.XSDstring);
                    // r.addProperty(m.createProperty("http://www.w3.org/example/location/" + measure.time),measure.location,XSDGenericType.XSDstring);
                    // if (!measure.time.isEmpty()) {
                    //     r.addProperty(m.createProperty(url + "/" + measure.time + "/time"),measure.time,XSDGenericType.XSDdateTime);
                    // }
                    // if (!measure.humidity.isEmpty()) {
                    //     r.addProperty(m.createProperty(url + "/" + measure.time + "/humidity"),measure.humidity,XSDGenericType.XSDfloat);
                    // }
                    // else{
                    //     r.addProperty(m.createProperty("http://www.w3.org/example/humidity/" + measure.time)," ",XSDGenericType.XSDstring);
                    // }
                    // if (!measure.luminosity.isEmpty()) {
                    //     r.addProperty(m.createProperty(url + "/" + measure.time + "/luminosity"),measure.luminosity,XSDGenericType.XSDinteger);
                    // }
                    // else{
                    //     r.addProperty(m.createProperty("http://www.w3.org/example/luminosity/" + measure.time)," ",XSDGenericType.XSDstring);
                    // }
                    // if (!measure.snd.isEmpty()) {
                    //     r.addProperty(m.createProperty(url + "/" + measure.time + "/snd"),measure.snd,XSDGenericType.XSDinteger);
                    // }
                    // else{
                    //     r.addProperty(m.createProperty("http://www.w3.org/example/snd/" + measure.time)," ",XSDGenericType.XSDstring);
                    // }
                    // if (!measure.sndf.isEmpty()) {
                    //     r.addProperty(m.createProperty(url + "/" + measure.time + "/sndf"),measure.sndf,XSDGenericType.XSDinteger);
                    // }
                    // else{
                    //     r.addProperty(m.createProperty("http://www.w3.org/example/sndf/" + measure.time)," ",XSDGenericType.XSDstring);
                    // }
                    // if (!measure.sndm.isEmpty()) {
                    //     r.addProperty(m.createProperty(url + "/" + measure.time + "/sndm"),measure.sndm,XSDGenericType.XSDinteger);
                    // }
                    // else{
                    //     r.addProperty(m.createProperty("http://www.w3.org/example/sndm/" + measure.time)," ",XSDGenericType.XSDstring);
                    // }
                    // if (!measure.temperature.isEmpty()) {
                        r.addProperty(m.createProperty(url + "/" + measure.time + "/temperature"),measure.temperature,XSDGenericType.XSDfloat);
                    // }
                    // else{
                    //     r.addProperty(m.createProperty("http://www.w3.org/example/temperature/" + measure.time)," ",XSDGenericType.XSDstring);
                    // }
                    // if (!measure.id.isEmpty()) {
                    //     r.addProperty(m.createProperty(url + "/" + measure.time + "/id"),measure.id,XSDGenericType.XSDID);
                    // }
                    // else{
                    //     r.addProperty(m.createProperty("http://www.w3.org/example/id/" + measure.time)," ",XSDGenericType.XSDstring);
                    // }
    
                    conneg.load(m); // add the content of model to the triplestore
                    conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
                    // AddFuseki(m);
                }
            }
            conneg.close();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
