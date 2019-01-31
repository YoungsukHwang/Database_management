
public class myDBP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		makeDB.make();   //make DB if not exist
		
		readSQL.read("create.sql");   // make tables (read create.sql)
		
		// read CSV files...
		readCSVtable.read("Categories.csv");         			//Categories.csv -> categories
		readCSVcol.read("Customers.csv", "REGION");  			//Customers.csv -> region
		readCSVtable.read("Customers.csv");   					//Customers.csv -> customers
		readCSVtables.read("Products.csv", "Inventory.csv");	//Products.csv, Inventory.csv -> products
		readCSVtable.read("Orders.csv");						//Orders.csv -> orders
		readCSVtable.read("Orderlines.csv");					//Orderlines.csv -> orderlines
		readCSVcol.read("Customers.csv", "ADDRESS2");			//Customers.csv -> address2
		readCSVcol.read("Products.csv", "SPECIAL");				//Products.csv -> special
	}
}
