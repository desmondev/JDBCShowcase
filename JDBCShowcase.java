package pl.edu.pw.elka.bd.lab6;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

import oracle.jdbc.pool.OracleDataSource;

/**
 * BD.A lab 6 JDBC Showcase.
 *
 * @author B.Twardowski <B.Twardowski@ii.pw.edu.pl>
 *
 */
public class JDBCShowcase {

	// const's
	private static String connectionString = "jdbc:oracle:thin:dswiecki/dswiecki@ora3.elka.pw.edu.pl:1521:ora3inf";

	private OracleDataSource oracleDataSource;
	private Connection connection;
	private Scanner inputScanner = new Scanner(System.in);

	/**
	 * Initializing DB connection.
	 * 
	 * @throws SQLException
	 */
	private void init() throws SQLException {

		oracleDataSource = new OracleDataSource();
		oracleDataSource.setURL(connectionString);
		connection = oracleDataSource.getConnection();
		DatabaseMetaData meta = connection.getMetaData();
		System.out
				.println("Successfully connected to DB. JDBC driver version is "
						+ meta.getDriverVersion());

	}

	/**
	 * Closing DB connection.
	 * 
	 * @throws SQLException
	 */
	private void close() throws SQLException {
		connection.close();
		System.out.println("Database connection successfully closed.");
	}

	/**
	 * Showcase.
	 * 
	 * @throws SQLException
	 */
	public void doShowcase() throws SQLException {
		init();
		simpleTest();
		//insertPracownik();
		preparedStatementShowcace();
		deleteTransaction();
		// transactionShowcace();
		close();
	}

	private void insertPracownik() throws SQLException {
		// TODO Auto-generated method stub
		System.out.println("Insert dodawanie nowego pracownika");

		// Create a statement
		Statement statement = connection.createStatement();

		System.out.println("Podaj imie pracownika");
		String imie = inputScanner.nextLine();

		System.out.println("Podaj nazwisko pracownika");
		String nazwisko = inputScanner.nextLine();

		System.out.println("Podaj id pracownika");
		String idString = inputScanner.nextLine();
		int id = Integer.parseInt(idString);

		System.out.println("Podaj wiek pracownika");
		String wiekString = inputScanner.nextLine();
		int wiek = Integer.parseInt(wiekString);

		System.out.println("Podaj telefon pracownika");
		String telefonString = inputScanner.nextLine();
		int telefon = Integer.parseInt(telefonString);

		System.out.println("Podaj id stanowiska pracownika");
		String stanowiskoString = inputScanner.nextLine();
		int stanowisko = Integer.parseInt(stanowiskoString);

		String insertString = "INSERT INTO PRACOWNICY VALUES (" + id + ", '"
				+ imie + "', '" + nazwisko + "'," + wiek + ", " + telefon
				+ ", " + stanowisko + ", 1500)";
		// Execute SQL
		statement.executeUpdate(insertString);

		ResultSet resultSet = statement.executeQuery("SELECT *\n"
				+ "FROM PRACOWNICY");

		System.out.println("Query result: ");
		int i = 1;
		while (resultSet.next()) {
			System.out.print("[" + i + "]:" + resultSet.getString(1));
			System.out.print("[" + i + "]:" + resultSet.getString(2));
			System.out.print("[" + i + "]:" + resultSet.getString(3));
			System.out.print("[" + i + "]:" + resultSet.getString(4));
			System.out.print("[" + i + "]:" + resultSet.getString(5));
			System.out.print("[" + i + "]:" + resultSet.getString(6));
			System.out.println(" [" + i + "]:" + resultSet.getString(7));
			i++;
		}

		// close the result set, the statement and connect
		resultSet.close();
		statement.close();

	}

	/**
	 * Simple select statement.
	 * 
	 * @throws SQLException
	 */
	private void simpleTest() throws SQLException {

		System.out.println("Select pokazujacy klientow ktorzy zlozyli wiecej niz dwa zamowienia");

		// Create a statement
		Statement statement = connection.createStatement();

		// Execute SQL
		ResultSet resultSet = statement.executeQuery("SELECT IMIE ,NAZWISKO\n"
				+ "FROM KLIENCI\n" + "WHERE (SELECT COUNT(*) FROM ZAMOWIENIA\n"
				+ "WHERE ZAMOWIENIA.ID_KLIENTA = KLIENCI.ID_KLIENTA ) > 1");

		System.out.println("Query result: ");

		int i = 1;
		while (resultSet.next()) {
			System.out.print("[" + i + "]:" + resultSet.getString(1));
			System.out.println(" [" + i + "]:" + resultSet.getString(2));
			i++;
		}

		// close the result set, the statement and connect
		resultSet.close();
		statement.close();

	}

	/**
	 * Prepared statement showcase.
	 * 
	 * @throws SQLException
	 */
	private void preparedStatementShowcace() throws SQLException {

		System.out.println("Prepared statement showcase...");

		// TODO: To modify!!!

		PreparedStatement preparedStatement = connection
				.prepareStatement("select imie,nazwisko from pracownicy where imie like ?");

		// get params from console

		System.out.println("Type employee name :");
		preparedStatement.setString(1, inputScanner.nextLine());

		ResultSet resultSet = preparedStatement.executeQuery();

		int i = 1;
		while (resultSet.next()) {
			System.out.print("[" + i + "]:" + resultSet.getString(1));
			System.out.println(" [" + i + "]:" + resultSet.getString(2));
			i++;
		}

		// close the result set, the statement and connect
		resultSet.close();
		preparedStatement.close();

	}
private void deleteTransaction() throws SQLException {
		
		System.out.println("USUWANIE PRACOWNIKA");
		
		connection.setAutoCommit(false);
		PreparedStatement preparedStatement = null;
		int rowsAffected;
 
		String deleteSQL = "DELETE FROM PRACOWNICY "
				+ "WHERE imie LIKE ? AND nazwisko LIKE ?";
 
		try {
			preparedStatement = connection.prepareStatement(deleteSQL);
			System.out.print("Podaj imie pracownika: ");
			preparedStatement.setString(1, inputScanner.nextLine()+"%");
			System.out.print("Podaj nazwisko pracownika: ");
			preparedStatement.setString(2, inputScanner.nextLine()+"%");
			
			rowsAffected = preparedStatement.executeUpdate();
			if(rowsAffected == 0)
				throw new IOException();
			connection.commit();
			System.out.println("Pracownika usunieto z bazy danych.");
		
 
		} catch (SQLException e) {
		    e.printStackTrace();
			connection.rollback();
			System.out.println("Blad. Nie usunieto z bazy danych.");
		} catch (InputMismatchException | IOException e) {
			connection.rollback();
			System.out.println("Bladne dane. Nie usunieto z bazy danych.");
			
		} finally {
 
			if (preparedStatement != null) {
				preparedStatement.close();
			} 
		}

	}
	/**
	 * Transaction showcase.
	 * 
	 * @throws SQLException
	 */
	private void transactionShowcace() throws SQLException {

		System.out.println("Transactions statement showcase...");

		// TODO: To fill...

		connection.setAutoCommit(false);

		Statement getPrice;
		Statement updatePrice;
		ResultSet resultSet;
		String queryString = "UPDATE  ";

		// operations - selects/updates/inserts with user interactions

		// if something wrong
		// connection.rollback();

		// if no error
		// connection.commit();

	}

	/**
	 * Run showcase.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws SQLException {
		JDBCShowcase showcase = new JDBCShowcase();
		showcase.doShowcase();
	}

}
