package semweb.projet.Controlleur;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import semweb.projet.Model.Btn;
import semweb.projet.Model.TripleFuseki;

@Controller
public class ControlleurPage {
    
    @RequestMapping("")
    public String mainredirection(Model model) 
    {

        String queryString = "SELECT ?subject ?predictat ?object \n WHERE { \n <https://territoire.emse.fr/kg/emse/fayol/4ET/> ?predictat ?object \n}";
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = new QueryEngineHTTP("http://localhost:3030/ProjetFinal/sparql", query );

        ResultSet results = qexec.execSelect();        
        List resultsList = ResultSetFormatter.toList(results);
        qexec.close();
        List<Btn> btns = new ArrayList<>();
        for (Object result : resultsList) {
            String[] resultString = result.toString().split("\\)");
            String object = resultString[1].replace("(", "")
                    .replace("?object = ", "").replaceAll("\"", "").replace("^^xsd:float", "").trim();
            if(object.contains("/4ET/4"))
            {
                Btn btn = new Btn();
                object = object.split("/")[7].replace(">", "");
                btn.name = object;
                btns.add(btn);
            }
        }
        
        model.addAttribute("btns", btns);
        return "main";
    }

    @PostMapping("index/**")
    public String ShowRoom(Model model, HttpServletRequest request) 
    {
        String weburl =  request.getRequestURL().toString();        
        String roomNumber = weburl.split("\\/")[4];
        String url = "<https://territoire.emse.fr/kg/emse/fayol/4ET/" + roomNumber + ">";
        String queryString = "PREFIX xsd: <https://www.w3.org/2001/XMLSchema#>" + "\n" +
        "SELECT ?subject ?predicate ?object"+"\n"+"WHERE {" +"\n" +
        url + "?predicate ?object" + "\n" +
        "}" + "\n" ;

        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = new QueryEngineHTTP("http://localhost:3030/ProjetFinal/sparql", query );

        ResultSet results = qexec.execSelect();        
        List resultsList = ResultSetFormatter.toList(results);
        qexec.close();
        List<TripleFuseki> triples = new ArrayList();
        
        for (Object result : resultsList) {
            if(result.toString().contains("/temperature") && result.toString().contains("+00:00"))
            {
                TripleFuseki tripleFuseki = new TripleFuseki();
                String[] resultString = result.toString().split("\\)");
                String[] predicate = resultString[0].replace("(", "").replace("?predicate =", "").trim().split("\\/");
                String heure = predicate[8].substring(0, 13);
                
                if(!containsheure(triples,heure))
                {
                    tripleFuseki.heure = heure;       
                    tripleFuseki.temperature = resultString[1].replace("(", "")
                    .replace("?object = ", "").replaceAll("\"", "").replace("^^xsd:float", "").trim();
                    triples.add(tripleFuseki);
                }                
            }
        }
        for (TripleFuseki tripleFuseki : triples) {

            String heureExt = tripleFuseki.heure.substring(tripleFuseki.heure.length() - 2);
                    String heureExtDebut = tripleFuseki.heure.substring(0, tripleFuseki.heure.length() - 2);
                    if(heureExt.substring(0,1).equals("0"))
                    {
                        heureExt = heureExt.substring(1);
                    }
                    String url2 = "<http://www.w3.org/example/" + heureExtDebut  + heureExt + ":00:00+00:00>";
                    String queryString2 = "SELECT ?object"+"\n"+"WHERE {" +"\n" +
                    url2 + " <http://www.w3.org/example/temp> ?object" + "\n" +
                    "}LIMIT 1" + "\n" ;    

                    Query query2 = QueryFactory.create(queryString2);
                    QueryEngineHTTP qexec2 = new QueryEngineHTTP("http://localhost:3030/ProjetFinal/sparql", query2 );
                    ResultSet result2 = qexec2.execSelect();
                    
                    List values = ResultSetFormatter.toList(result2);
                    String tempExt = values.get(0).toString();
                    tempExt = tempExt.replace("(", "").replace(")", "").replace("?object = ", "").replaceAll("\"", "").trim();
                    tripleFuseki.TempExt = tempExt;
                    qexec2.close();

                    if(((Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) < 5 
                        && (Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) > 0)
                        || ((Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) > 5 
                        && (Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) < 0))
                    {
                        tripleFuseki.alarm = "alarm";
                    }
                    else if(((Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) < 10 
                    && (Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) > 0)
                    || ((Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) > 10 
                    && (Double.parseDouble(tripleFuseki.temperature) - Double.parseDouble(tripleFuseki.TempExt.substring(0,tripleFuseki.TempExt.length()-3)) ) < 0))
                    {
                        tripleFuseki.alarm = "interest";
                    }
                    else{
                        tripleFuseki.alarm = "normal";
                    }
                    tripleFuseki.temperature = tripleFuseki.temperature + "°C";
        }
        
        model.addAttribute("triples", triples);
        return "index";
    }

    public static boolean containsheure(List<TripleFuseki> c, String heure) {
        for(TripleFuseki o : c) {
            if(o != null && o.getHeure().equals(heure)) {
                return true;
            }
        }
        return false;
    }
}
