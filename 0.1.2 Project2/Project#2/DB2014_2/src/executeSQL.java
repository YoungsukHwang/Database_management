import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;



public class executeSQL {
	//JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/";

	//Database credentials
	static final String DB_NAME = "db2014_2";
	static final String DB_USER = "root";
	static final String DB_PASS = "12341234";//"12341234";
	static int count = 0;


	private static void parsing(Statement stmt, String sql){
		count ++;
		try{
			//start time
			long startTime = System.currentTimeMillis();
			
			//sending queries
			ResultSet rs = stmt.executeQuery(sql);
			
			//get metadata for printing
			ResultSetMetaData rsmd = rs.getMetaData();
			
			//printing loops
			while(rs.next()){
				
				for (int i = 1; i <= rsmd.getColumnCount(); i++){
					if (rsmd.getColumnTypeName(i).equals("INT")){
						int para = rs.getInt(i);
						System.out.print(rsmd.getColumnName(i) + " : " + para);
					}
					else if (rsmd.getColumnTypeName(i).equals("BIGINT")){
						long para = rs.getLong(i);
						System.out.print(rsmd.getColumnName(i) + " : " + para);
					}
					else if (rsmd.getColumnTypeName(i).equals("VARCHAR")){
						String para = rs.getString(i);
						System.out.print(rsmd.getColumnName(i) + " : " + para);
					}
					else if (rsmd.getColumnTypeName(i).equals("DECIMAL")){
						double para = rs.getDouble(i);
						System.out.print(rsmd.getColumnName(i) + " : " + para);
					}
					else {
		        		System.out.println("New!!!! Type!!! : " + rsmd.getColumnTypeName(i));
		        		break;
					}
					if(i < rsmd.getColumnCount()) System.out.print(", ");
				}
				System.out.println();
			}
			
			//calculate the elapsed time
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println(count + ". ElapsedTime(millisec): " + elapsedTime);
			System.out.println(count + ". ElapsedTime(sec): " + elapsedTime/1000);
			
		} catch(SQLException e) {

			//Handle errors for JDBC
			System.out.println("SQL Exception occured");
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		count = 0;

		//Register JDBC driver
		try {

			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			System.out.println("Error: unable to load driver class!");
			e.printStackTrace();

			return;
		}

		// initializing
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		
		
		//define queries
		String sql6_1 = 
				"SELECT PROD_ID, (SALES-QUAN_IN_STOCK) as AMOUNT, 0.8*PRICE*(SALES-QUAN_IN_STOCK) as MONEY " +
				"FROM db2014_2.products " +
				"WHERE SALES > QUAN_IN_STOCK;";
		String sql6_2 = 
				"SELECT PROD_ID, (SALES-QUAN_IN_STOCK) as AMOUNT, 0.8*PRICE*(SALES-QUAN_IN_STOCK) as MONEY " +
				"FROM db2014_2.products_nokey " +
				"WHERE SALES > QUAN_IN_STOCK;";
		
		
		String sql7_1 =
	            "SELECT STATE, COUNT(DISTINCT C.CUSTOMERID) as CUSTOMER_NUMVER, SUM(TOTALAMOUNT) as SUM, AVG(TOTALAMOUNT) as AVERAGE "+
	            "FROM db2014_2.customers as C "+
	            "LEFT JOIN db2014_2.orders as O "+
	            "ON C.CUSTOMERID = O.CUSTOMERID "+
	            "GROUP BY STATE "+
	            "HAVING SUM > 800000 "+
	            "ORDER BY SUM DESC;";
	      
	      String sql7_2 =
	            "SELECT STATE, COUNT(DISTINCT C.CUSTOMERID) as CUSTOMER_NUMVER, SUM(TOTALAMOUNT) as SUM, AVG(TOTALAMOUNT) as AVERAGE "+
	            "FROM db2014_2.customers_nokey as C "+
	            "LEFT JOIN db2014_2.orders_nokey as O "+
	            "ON C.CUSTOMERID = O.CUSTOMERID "+ 
	            "GROUP BY STATE "+
	            "HAVING SUM > 800000 "+
	            "ORDER BY SUM DESC;";
	    
	      
	      
	      String sql8_1 =
	            "SELECT CATEGORYNAME, SUM(QUANTITY) as SELL "+
	            "FROM db2014_2.customers as C "+
	            "JOIN db2014_2.orders as OD "+
	            "ON C.CUSTOMERID = OD.CUSTOMERID "+
	            "JOIN db2014_2.orderlines as OL "+
	            "ON OD.ORDERID = OL.ORDERID "+
	            "JOIN db2014_2.products as P "+
	            "ON OL.PROD_ID = P.PROD_ID "+
	            "JOIN db2014_2.categories as CAT "+
	            "ON P.CATEGORY = CAT.CATEGORY "+
	            "WHERE (GENDER = 'F' AND INCOME >= 100000 AND AGE >= 20) OR (GENDER = 'M' AND INCOME >= 80000 AND AGE >= 40) "+
	            "GROUP BY CAT.CATEGORY "+
	            "ORDER BY SELL DESC;";
	      
	      String sql8_2 =
	            "SELECT CATEGORYNAME, SUM(QUANTITY) as SELL "+
	            "FROM db2014_2.customers_nokey as C "+
	            "JOIN db2014_2.orders_nokey as OD "+
	            "ON C.CUSTOMERID = OD.CUSTOMERID "+
	            "JOIN db2014_2.orderlines_nokey as OL "+
	            "ON OD.ORDERID = OL.ORDERID "+
	            "JOIN db2014_2.products_nokey as P "+
	            "ON OL.PROD_ID = P.PROD_ID "+
	            "JOIN db2014_2.categories_nokey as CAT "+
	            "ON P.CATEGORY = CAT.CATEGORY "+
	            "WHERE (GENDER = 'F' AND INCOME >= 100000 AND AGE >= 20) OR (GENDER = 'M' AND INCOME >= 80000 AND AGE >= 40) "+
	            "GROUP BY CAT.CATEGORY "+
	            "ORDER BY SELL DESC;";

	      
		try {
			//Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useUnicode=true&characterEncoding=utf8", DB_USER, DB_PASS);
			stmt = conn.createStatement();

			
			//sending each queries
			parsing(stmt, sql6_1);  //for requirement R6 (key)
			parsing(stmt, sql6_2);  //for requirement R6 (no key)
			
			parsing(stmt, sql7_1);  //for requirement R7 (key)
			parsing(stmt, sql7_2);  //for requirement R7 (no key)
			
			parsing(stmt, sql8_1);  //for requirement R8 (key)
			parsing(stmt, sql8_2);  //for requirement R8 (no key)

		} catch (SQLException e) {

			//Handle errors for JDBC
			System.out.println("SQL Exception occured");
			e.printStackTrace();

		} catch (Exception e) {

			//Handle errors for Class.forName
			System.out.println("Exception occured");
			e.printStackTrace();

		} finally {

			//finally block used to close resources
			System.out.println("Closing DB resources");
			try {

				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();

			} catch (SQLException e) {

				e.printStackTrace();

			}
		}
	}
}