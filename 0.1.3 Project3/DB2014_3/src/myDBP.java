
public class myDBP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		readSQL.read("A_Foodmart.sql");
		readSQL.read("B_Census.sql");
		
		MakeTransactionMatrix.make();
		
		ForFPGrowth.makeAssoc();
		DicisionTree.makeTree();
	}
}
