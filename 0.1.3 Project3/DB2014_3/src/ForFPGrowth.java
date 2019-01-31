import java.io.BufferedWriter;
import java.io.FileWriter;
import weka.associations.FPGrowth;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToBinary;
import weka.filters.unsupervised.attribute.Remove;

public class ForFPGrowth {
  //JDBC database URL
  static final String DB_URL = "jdbc:mysql://localhost/";

  //Database credentials
  static final String DB_NAME = "foodmart";
  static final String DB_USER = "root";
  static final String DB_PASS = "12341234";
  
  //main function
  public static void makeAssoc() {

    try {
      
      // 1. LOAD INSTANCES
      //set query
      InstanceQuery query = new InstanceQuery();
      query.setDatabaseURL(DB_URL + DB_NAME);
      query.setUsername(DB_USER);
      query.setPassword(DB_PASS);
      
      
      String queryMatrix = "SELECT * from ordermatrix";            
      query.setQuery(queryMatrix);
      
      //load data
      Instances data = query.retrieveInstances();    
      System.out.println("data retrieved"); 

      
      // 1-1. INSTANCE FILTER
      //filter data
      NumericToBinary numericToBinary = new NumericToBinary();
      Remove remove = new Remove();
      
      remove.setInputFormat(data);
      remove.setOptions(new String[] {"-R", "1-4"});
      Instances removedData = Filter.useFilter(data, remove);
      
      numericToBinary.setInputFormat(removedData);
      Instances filteredData = Filter.useFilter(removedData, numericToBinary); //change numeric value(0, 1) to binary
      
      System.out.println(filteredData.attribute(0)); //check output (attribute name changes: " "_binarized)
       
      
      
      // 2. FPGrowth
      //FPGrowth association
      weka.associations.FPGrowth associationA = new FPGrowth();
      associationA.setOptions(new String[]{"-T", "0"});
      associationA.setNumRulesToFind(100);
      associationA.setLowerBoundMinSupport(0.0002);
      associationA.setDelta(0.05);
      associationA.setMinMetric(0.5);
      associationA.setMaxNumberOfItems(21);
      
      weka.associations.FPGrowth associationB = new FPGrowth();
      associationB.setOptions(new String[]{"-T", "1"});
      associationB.setNumRulesToFind(100);
      associationB.setLowerBoundMinSupport(0.0002);
      associationB.setDelta(0.05);
      associationB.setMinMetric(10);
      associationB.setMaxNumberOfItems(32);
      
      
      //build associator
      associationA.buildAssociations(filteredData); //use filtered(Numeric to Binary) data
      associationB.buildAssociations(filteredData);
      
      //output associator
      System.out.println();
      System.out.println();
      System.out.println("-----------------------------------------------------------");
      System.out.println("FP-growth with Confidence rule");
      System.out.println(associationA);
      
      System.out.println();
      System.out.println();
      System.out.println("-----------------------------------------------------------");
      System.out.println("FP-growth with Lift rule");
      System.out.println(associationB); 
      
      FileWriter fwA = new FileWriter("associaion_onfidence.txt");
	  BufferedWriter bwA = new BufferedWriter(fwA); 
	  bwA.write(associationA.toString());
	  bwA.close();
	  
	  FileWriter fwB = new FileWriter("association_lift.txt");
	  BufferedWriter bwB = new BufferedWriter(fwB); 
	  bwB.write(associationB.toString());
	  bwB.close();
      

    } catch (Exception e) {
      
      e.printStackTrace();
      
    }
 
    
  }
  


}
