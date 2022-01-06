package semweb.projet.Model;

import com.opencsv.bean.CsvBindByName;

public class SensorMeasures {
   //Format des données des csv
   @CsvBindByName(column = "name")
   public String name;
   @CsvBindByName(column = "time")
   public String time;
   @CsvBindByName(column = "HMDT")
   public String humidity;
   @CsvBindByName(column = "LUMI")
   public String luminosity;
   @CsvBindByName(column = "SND")
   public String snd;
   @CsvBindByName(column = "SNDF")
   public String sndf;
   @CsvBindByName(column = "SNDM")
   public String sndm;
   @CsvBindByName(column = "TEMP")
   public String temperature;
   @CsvBindByName(column = "id")
   public String id;
   @CsvBindByName(column = "location")
   public String location;
   @CsvBindByName(column = "type")
   public String type;
}
