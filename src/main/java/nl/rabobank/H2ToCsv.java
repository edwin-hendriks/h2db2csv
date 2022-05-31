package nl.rabobank;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * tool that exports content of H2 database (v1.*) file to CSV files
 * Usage: java -jar <this jar file> <fully qualified path to H2 .db file but without the .h2.db extension.
 * @param args
 */
class H2ToCsv {
    public static void main(String[] args) {

        // String url = "jdbc:h2:file:C:/tmp/20220530_Test;IFEXISTS=TRUE";
        String url = "jdbc:h2:file:" + args[0] + ";IFEXISTS=TRUE";

        try (var con = DriverManager.getConnection(url);
             var tableNamesStatement = con.createStatement();
             var tableNames = tableNamesStatement.executeQuery("SHOW TABLES")) {

            while (tableNames.next()) {

                String tableName = tableNames.getNString(1);
                String query = "call CSVWRITE ( 'C:/tmp/csv/" + tableName + ".csv', 'select * from " + tableName + "');";
                System.out.println("Executing query: " + query);

                try (var tableContentStatement = con.createStatement();
                     var tableContent = tableContentStatement.executeQuery("call CSVWRITE ( 'C:/tmp/csv/" + tableName + ".csv', 'select * from " + tableName + "');")) {
                        System.out.printf("Exporting content of " + tableName + " to csv file");
                } catch (SQLException ex) {
                    var lgr = Logger.getLogger(H2ToCsv.class.getName());
                    lgr.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        } catch (SQLException ex) {

            var lgr = Logger.getLogger(H2ToCsv.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
