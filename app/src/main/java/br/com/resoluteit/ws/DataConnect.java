package br.com.resoluteit.ws;

import java.sql.Connection;
import java.sql.DriverManager;

//"jdbc:postgresql://localhost:5432/fabricam_sindquimica", "fabricam_developer", "Valente@3873"
//"jdbc:postgresql://localhost:5432/sindquimica", "postgres", "postgres2530");
//service postgresql start
public class DataConnect {

	public static Connection getConnection() {
		try {

			// local 192.168.25.3
			// produção
			Class.forName("org.postgresql.Driver");
			Connection con = DriverManager
					.getConnection("jdbc:postgresql://191.252.100.171:5432/postgres", "postgres", "Root@3873");
			return con;
		} catch (Exception ex) {
			System.out.println("Database.getConnection() Error -->"
					+ ex.getMessage());
			return null;
		}
	}

	public static void close(Connection con) {
		try {
			con.close();
		} catch (Exception ex) {
		}
	}
}