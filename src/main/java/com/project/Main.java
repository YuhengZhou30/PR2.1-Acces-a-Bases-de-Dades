package com.project;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        String basePath = System.getProperty("user.dir") + "/data/";
        String filePath = basePath + "database.db";

        // Si no hi ha l'arxiu creat, el crea i li posa dades
        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) { initDatabase(filePath); }

        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Llistar les taules

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Menú:");
            System.out.println("1. Mostrar una taula");
            System.out.println("2. Mostrar personatges per facció");
            System.out.println("3. Mostrar el millor atacant per facció");
            System.out.println("4. Mostrar el millor defensor per facció");
            System.out.println("5. Sortir");
            System.out.print("Seleccioneu una opció: ");

            int opcio = scanner.nextInt();

            switch (opcio) {
                case 1:
                    mostrarTaula(conn);
                    break;
                case 2:
                    mostrarPersonatgesPerFaccio(conn);
                    break;
                case 3:
                    mostrarMillorAtacantPerFaccio(conn);
                    break;
                case 4:
                    mostrarMillorDefensorPerFaccio(conn);
                    break;
                case 5:
                    System.out.println("Sortint del programa. ¡Fins aviat!");
                    System.exit(0);
                default:
                    System.out.println("Opció no vàlida. Torneu-ho a provar.");
            }
        }
    }

    private static void mostrarTaula(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        ResultSet rs = null;
        ArrayList<String> taules = UtilsSQLite.listTables(conn);
        System.out.println("Quina taula vols que mostri ?");
        int posicio=1;
        for (String t:taules){
            System.out.println(posicio+"."+t);
            posicio++;
        }
        int opcio = scanner.nextInt();
        //Query
        String q="SELECT * FROM " +taules.get(opcio-1)+" ;";
        System.out.println(q);
        // SELECT a la base de dades
        rs = UtilsSQLite.querySelect(conn, q);
        System.out.println("Contingut de la taula:");
        System.out.println("-".repeat(100));
        if (taules.get(opcio-1).equals("Faccio")){
            while (rs.next()) {
                System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom")+ ", " + rs.getString("resum"));
            }
        }else {
            while (rs.next()) {
                System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom")+ ", " + rs.getFloat("atac")+ ", " + rs.getFloat("defensa")+ ", " + rs.getInt("idFaccio"));
            }
        }

        System.out.println("-".repeat(100));


    }

    private static void mostrarPersonatgesPerFaccio(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        //Query
        String q="SELECT * FROM Faccio;";
        ResultSet rs = null;
        System.out.println("Quina facció de personatges vols que mostri ?");

        // SELECT a la base de dades
        rs = UtilsSQLite.querySelect(conn, q);
        while (rs.next()) {
            System.out.println( rs.getInt("id") + "." + rs.getString("nom"));
        }
        int opcio = scanner.nextInt();
        q="SELECT * FROM Personatge where idFaccio= "+opcio+" ;";

        rs = UtilsSQLite.querySelect(conn, q);
        System.out.println("    "+"ID| Nom| Atac| Defensa");
        System.out.println("-".repeat(100));
        while (rs.next()) {
            System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom")+ ", " + rs.getFloat("atac")+ ", " + rs.getFloat("defensa"));
        }
        System.out.println("-".repeat(100));

    }

    private static void mostrarMillorAtacantPerFaccio(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        //Query
        String q="SELECT * FROM Faccio;";
        ResultSet rs = null;
        System.out.println("Quina facció de atacant vols que mostri ?");

        // SELECT a la base de dades
        rs = UtilsSQLite.querySelect(conn, q);
        while (rs.next()) {
            System.out.println( rs.getInt("id") + "." + rs.getString("nom"));
        }
        int opcio = scanner.nextInt();

        q="SELECT * FROM Personatge where idFaccio= "+opcio+" and "+" atac = (SELECT MAX(atac) FROM Personatge WHERE idFaccio = "+opcio+" ) ;";
        rs = UtilsSQLite.querySelect(conn, q);
        System.out.println("    "+"ID| Nom| Atac| Defensa");
        System.out.println("-".repeat(100));
        while (rs.next()) {
            System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom")+ ", " + rs.getFloat("atac")+ ", " + rs.getFloat("defensa"));
        }
        System.out.println("-".repeat(100));
    }

    private static void mostrarMillorDefensorPerFaccio(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        //Query
        String q="SELECT * FROM Faccio;";
        ResultSet rs = null;
        System.out.println("Quina facció de defensor vols que mostri ?");

        // SELECT a la base de dades
        rs = UtilsSQLite.querySelect(conn, q);
        while (rs.next()) {
            System.out.println( rs.getInt("id") + "." + rs.getString("nom"));
        }
        int opcio = scanner.nextInt();

        q="SELECT * FROM Personatge where idFaccio= "+opcio+" and "+" defensa = (SELECT MAX(defensa) FROM Personatge WHERE idFaccio = "+opcio+" ) ;";
        rs = UtilsSQLite.querySelect(conn, q);
        System.out.println("    "+"ID| Nom| Atac| Defensa");
        System.out.println("-".repeat(100));
        while (rs.next()) {
            System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom")+ ", " + rs.getFloat("atac")+ ", " + rs.getFloat("defensa"));
        }
        System.out.println("-".repeat(100));
    }


    static void initDatabase (String filePath) {
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Esborrar la taula (per si existeix)
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Faccio;");
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Personatge;");


        // Crear una nova taula
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE Faccio (\n" +
                "    id INT PRIMARY KEY,\n" +
                "    nom VARCHAR(15),\n" +
                "    resum VARCHAR(500)\n" +
                ");\n");
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE Personatge (\n" +
                "    id INT PRIMARY KEY,\n" +
                "    nom VARCHAR(15),\n" +
                "    atac REAL,\n" +
                "    defensa REAL,\n" +
                "    idFaccio INT,\n" +
                "    FOREIGN KEY (idFaccio) REFERENCES Faccio(id)\n" +
                ");\n");


        // Afegir elements a una taula
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Faccio (id, nom, resum) VALUES\n" +
                "(1, 'Cavallers', 'Facció honorable amb armadures pesades.'),\n" +
                "(2, 'Vikings', 'Guerriers ferotges de terres septentrionals.'),\n" +
                "(3, 'Samurais', 'Segueixen una disciplina mil·lenària.');\n");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id, nom, atac, defensa, idFaccio) VALUES\n" +
                "(1, 'Sir Cedric', 85.5, 90.2, 1),\n" +
                "(2, 'Lagertha', 92.3, 80.1, 2),\n" +
                "(3, 'Kaito', 88.7, 85.6, 3),\n" +
                "(4, 'Lady Eleanor', 78.9, 95.0, 1),\n" +
                "(5, 'Bjorn', 94.2, 75.8, 2),\n" +
                "(6, 'Hana', 86.4, 88.3, 3),\n" +
                "(7, 'Baroness Isabella', 80.0, 92.5, 1),\n" +
                "(8, 'Olaf', 90.1, 82.4, 2),\n" +
                "(9, 'Takeshi', 85.9, 89.7, 3);\n");

        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }
}
