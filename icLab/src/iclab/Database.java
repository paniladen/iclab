/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iclab;

import java.sql.*;

/**
 *
 * @author pani
 */
public class Database {

    String driver = "org.postgresql.Driver";
    // --------------------------------------------------------------------------
    String host = "localhost";            // !!! anpassen !!!
    String port = "5432";                 // !!! anpassen !!!
    String database = "postgres";         // !!! anpassen !!!
    String user = "test";                 // !!! anpassen !!!
    String password = "test";             // !!! anpassen !!!
    // --------------------------------------------------------------------------
    Connection connection = null;

    /**
     * Constructor
     */
    public Database() {
        loadJdbcDriver();
        openConnection();
        createSysRel();
        closeConnection();
    }

    /**
     * @return Url-string for postgreSQL-database connection
     */
    private String getUrl() {
        return ("jdbc:postgresql:" + (host != null ? ("//" + host) + (port != null ? ":" + port : "") + "/" : "") + database);
    }

    /**
     * loading the JDBC driver
     */
    private void loadJdbcDriver() {
        System.out.print("[] Load JDBC Driver");
        try {
            Class.forName(driver);
            System.out.println("								OK");
        } catch (ClassNotFoundException e) {
            System.out.println("								FAILURE");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * opening the connection
     */
    private void openConnection() {
        System.out.print("[] Open Postgresqlconnection");
        try {
            connection = DriverManager.getConnection(getUrl(), user, password);
            System.out.println("						OK");
        } catch (SQLException e) {
            System.out.println("						FAILURE");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * close the connection
     */
    private void closeConnection() {
        System.out.print("[] Close Postgresqlconnection");
        try {
            connection.close();
            System.out.println("						OK");
        } catch (SQLException e) {
            System.out.println("						FAILURE");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createSysRel() {

        try {
            System.out.print("[] Check TestSysRel");
            Statement statement = (Statement) connection.createStatement();
            //statement.execute("DROP TABLE  IF EXISTS TestSysRel");
            statement.execute("CREATE TABLE "
                    + "IF NOT EXISTS TestSysRel "
                    + "(Attribut Integer NOT NULL, PRIMARY KEY (Attribut))");

            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM TestSysRel");
            resultSet.next();
            if (resultSet.getString(1).equals("0")) {
                statement.execute("INSERT INTO TestSysRel VALUES (1)");
            }
            resultSet.close();
            statement.close();
            System.out.println("								OK");

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            System.out.println("								FAILURE");
        }

        try {

            System.out.print("[] Check AssertioenSysRel");
            Statement statement = (Statement) connection.createStatement();
            //statement.execute("DROP TABLE  IF EXISTS AssertionSysRel l");
            statement.execute("CREATE TABLE IF NOT EXISTS AssertionSysRel (Assertionname VARCHAR(40) NOT NULL, Bedingung VARCHAR(800) NOT NULL, implementiert BOOL DEFAULT FALSE, PRIMARY KEY (Assertionname))");
            statement.close();
            System.out.println("							OK");

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            System.out.println("							FAILURE");
        }

    }
}
