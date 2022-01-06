package semweb.projet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype.XSDGenericType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import semweb.projet.Model.MeteoCiel;

public class GetMetoCiel 
{
    //Procesus pour la récupération des données depuis Meteo ciel
    public static void GetData(String url, String Date)
    {            
        List<MeteoCiel> mcs = new ArrayList<MeteoCiel>();

        try {
            Document doc = Jsoup.connect(url).get();
            Elements rows = doc.select("tr");
            for(Element row :rows)
            {
                if(row.text().startsWith("23 h") || row.text().startsWith("22 h") || row.text().startsWith("21 h") || row.text().startsWith("20 h")
                || row.text().startsWith("19 h") || row.text().startsWith("18 h") || row.text().startsWith("17 h") || row.text().startsWith("16 h")
                || row.text().startsWith("15 h") || row.text().startsWith("14 h") || row.text().startsWith("13 h") || row.text().startsWith("12 h")
                || row.text().startsWith("11 h") || row.text().startsWith("10 h") || row.text().startsWith("9 h") || row.text().startsWith("8 h")
                || row.text().startsWith("7 h") || row.text().startsWith("6 h") || row.text().startsWith("5 h") || row.text().startsWith("4 h")
                || row.text().startsWith("3 h") || row.text().startsWith("2 h") || row.text().startsWith("1 h") || row.text().startsWith("0 h"))
                {
                    Elements columns = row.select("td");
                    MeteoCiel mc = new MeteoCiel();

                    int i = 0;
                    for (Element column:columns)
                    {
                        switch (i){
                            case 0: mc.HeureLocal = column.text();
                                    break;
                            case 1: mc.Neb = column.text();
                                    break;
                            case 3: mc.Visi = column.text();
                                    if(Double.parseDouble(mc.Visi.split(" ")[0]) <= 4)
                                    {
                                        mc.Temps = "Brume";
                                    }                            
                                    break;
                            case 4: mc.Temperature = column.text();
                                    break;
                            case 5: mc.Humidite = column.text();
                                    break;
                            case 6: mc.Humidex = column.text();
                                    break;
                            case 7: mc.Windchill = column.text();
                                    break;
                            case 9: mc.VentRafale = column.text();
                                    break;
                            case 10: mc.Pression = column.text();
                                    break;
                            case 11: mc.Precip = column.text();
                                    break;
                        }                        
                        i++;
                    }
                    System.out.println();                   
                    mcs.add(mc);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (MeteoCiel meteoCiel : mcs) 
        {
                String date = Date + "T" + meteoCiel.HeureLocal.replace("h", "").trim() + ":00:00+00:00";

                Model m = ModelFactory.createDefaultModel();

                //Define the prefix rdf:
                m.setNsPrefix("rdf", RDF.uri);
                //Define the prefix ex:
                m.setNsPrefix("ex", "http://example.com/");
                //Define the prefix rdfs:
                m.setNsPrefix("rdfs", RDFS.uri);
                //Define the prefix xsd:
                m.setNsPrefix("xsd", XSD.getURI());

                Resource r = m.createResource("http://www.w3.org/example/" + date);
                r.addProperty(m.createProperty("http://www.w3.org/example/heureLocal"),date,XSDGenericType.XSDdateTime);
                r.addProperty(m.createProperty("http://www.w3.org/example/neb"),meteoCiel.Neb,XSDGenericType.XSDstring);
                if (meteoCiel.Temps != null) {
                        r.addProperty(m.createProperty("http://www.w3.org/example/temps"),meteoCiel.Temps,XSDGenericType.XSDstring);
                }
                r.addProperty(m.createProperty("http://www.w3.org/example/visi"),meteoCiel.Visi,XSDGenericType.XSDstring);
                r.addProperty(m.createProperty("http://www.w3.org/example/temp"),meteoCiel.Temperature,XSDGenericType.XSDstring);
                r.addProperty(m.createProperty("http://www.w3.org/example/humidite"),meteoCiel.Humidite,XSDGenericType.XSDstring);
                r.addProperty(m.createProperty("http://www.w3.org/example/humidex"),meteoCiel.Humidex,XSDGenericType.XSDstring);
                r.addProperty(m.createProperty("http://www.w3.org/example/windchill"),meteoCiel.Windchill,XSDGenericType.XSDstring);
                r.addProperty(m.createProperty("http://www.w3.org/example/vents"),meteoCiel.VentRafale,XSDGenericType.XSDstring);
                r.addProperty(m.createProperty("http://www.w3.org/example/pression"),meteoCiel.Pression,XSDGenericType.XSDstring);
                r.addProperty(m.createProperty("http://www.w3.org/example/precipitation"),meteoCiel.Precip,XSDGenericType.XSDstring);

                AddFuseki(m);
        }
    }

        //Ajout des données dans Fuseki
        public static void AddFuseki(Model model) 
        {                           
                String datasetURL = "http://localhost:3030/ProjetFinal";
                String sparqlEndpoint = datasetURL + "/sparql";
                String sparqlUpdate = datasetURL + "/update";
                String graphStore = datasetURL + "/data";
                RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
                conneg.load(model); // add the content of model to the triplestore
                conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
                conneg.close();
        }
}
