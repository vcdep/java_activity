import java.sql.*;

public class DatabaseConnector {
	private Connection conn;
	private Statement statement;	
	private ResultSet rows;
	private String query;
	
	public DatabaseConnector(){
		this.setQuery("select Name from gadget");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.conn = DriverManager.getConnection("jdbc:mysql://54.172.187.147:3306/Tester", "naman", "chocolate");
			this.statement = this.conn.createStatement();
			this.rows = this.statement.executeQuery(this.query);
			while(this.rows.next()){
				System.out.println(this.rows.getString("Name"));
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getErrorCode());			
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}				
	}
	
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public Statement getStatement() {
		return statement;
	}
	public void setStatement(Statement statement) {
		this.statement = statement;
	}
	public ResultSet getRows() {
		return rows;
	}
	public void setRows(ResultSet rows) {
		this.rows = rows;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}	
}
