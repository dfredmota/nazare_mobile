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
			// produção 189.89.227.199
			Class.forName("org.postgresql.Driver");
			Connection con = DriverManager
					.getConnection("jdbc:postgresql://189.89.227.199:5432/postgres", "postgres", "Nazare@3873");
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