import java.io.BufferedWriter;
import java.io.FileWriter;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

public class DicisionTree {
  //JDBC database URL
  static final String DB_URL = "jdbc:mysql://localhost/";

  //Database credentials
  static final String DB_NAME = "census";
  static final String DB_USER = "root";
  static final String DB_PASS = "12341234";
  
  //main function
  public static void makeTree() {

    try {
      
      // 4. J48 TREE
      //set query
      InstanceQuery queryTree = new InstanceQuery();
      queryTree.setDatabaseURL(DB_URL + DB_NAME);
      queryTree.setUsername(DB_USER);
      queryTree.setPassword(DB_PASS);
      
      String queryTreeMatrix = "SELECT * from adult;";            
      queryTree.setQuery(queryTreeMatrix);
      
      //load data
      Instances dataTree = queryTree.retrieveInstances();     
      dataTree.setClassIndex(13); //class index is column 4 (class to classify) (index start with 0)

      //System.out.println(dataTree);
      
      //build tree
      J48 tree = new J48();
      tree.setBinarySplits(true);
      tree.setUnpruned(true);
      tree.setMinNumObj(300);
      tree.buildClassifier(dataTree);
      
      //output tree
      System.out.println("-----------------------------------------------------------");
      System.out.println(tree);
      
	  FileWriter fwB = new FileWriter("tree_income.txt");
	  BufferedWriter bwB = new BufferedWriter(fwB); 
	  bwB.write(tree.toString());
	  bwB.close();
      

    } catch (Exception e) {
      
      e.printStackTrace();
      
    }
 
    
  }
  


}
