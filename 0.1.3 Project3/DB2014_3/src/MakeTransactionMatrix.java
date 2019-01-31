import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MakeTransactionMatrix {
	//JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/";

	//Database credentials
	static final String DB_NAME = "foodmart";
	static final String DB_USER = "root";
	static final String DB_PASS = "12341234";//"12341234";

	public static void make() {
		ResultSet rs = null;
		String sql = null;

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
		Statement stmt = null;
		
		
		//define queries
		String sql1= 
				"CREATE VIEW OrderMatrix AS " +
				"SELECT sales_all.customer_id, sales_all.time_id, sales_all.promotion_id, sales_all.store_id";
		
	      
		try {
			//Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useUnicode=true&characterEncoding=utf8", DB_USER, DB_PASS);
			stmt = conn.createStatement();
			sql = "SELECT product_class_id FROM product_class;";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				sql1 = sql1 +
						", max(IF(product.product_class_id="+rs.getInt(1)+",1, 0)) as Category"+rs.getInt(1);
			}
			
			sql1 = sql1 +
					" FROM sales_all, product where sales_all.product_id=product.product_id "+
					"GROUP by sales_all.customer_id, sales_all.time_id;";
			
			//sending each queries
			stmt.execute(sql1);


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
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();

			}
		}
	}
}