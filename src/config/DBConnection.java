package config;

import util.ConfigReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String HOST = ConfigReader.get("db.host", "localhost");
    private static final String PORT = ConfigReader.get("db.port", "5432");
    private static final String DATABASE = ConfigReader.get("db.name", "ERPDB");
    private static final String USERNAME = ConfigReader.get("db.username", "postgres");
    private static final String PASSWORD = ConfigReader.get("db.password", "postgres123");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?loginTimeout=" + ConfigReader.getInt("db.pool.timeout", 10);

    private static Connection connection = null;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver bulunamadı!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✓ " + DATABASE + " veritabanına bağlantı başarılı!");
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantısı başarısız.");
            System.err.println("Sunucu: " + HOST + ":" + PORT);
            System.err.println("Veritabanı: " + DATABASE);
            System.err.println("Kullanıcı: " + USERNAME);
            System.err.println("Hata: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Veritabanı bağlantısı kapatıldı.");
            }
        } catch (SQLException e) {
            System.err.println("Bağlantı kapatılamadı");
            e.printStackTrace();
        }
    }
}
