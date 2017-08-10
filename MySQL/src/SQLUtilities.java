import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class SQLUtilities {
    private static Connection connection;

    public SQLUtilities() {
    }

    //проверяем Connection на наличие и создаем если он отсутствует
    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        Properties properties = null;
        try (InputStreamReader in = new InputStreamReader(new FileInputStream("appProperties.txt"), "UTF-8")) {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            properties = new Properties();
            properties.load(in);
            String url = properties.getProperty("DBConnectionString");
            String userName = properties.getProperty("userName");
            String password = properties.getProperty("password");
            connection = DriverManager.getConnection(url, userName, password);
            System.out.println("Connection created");
            return connection;

        } catch (FileNotFoundException e)  {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //записываем данные в базу данных
    public static void writeItemsToDB(List<Item> items, String nameOfBD) {
        Connection connection = SQLUtilities.getConnection();
        if (items == null) {
            System.out.println("items = null");
            return;
        }
        String comand = "insert into " + nameOfBD + ".items (title, code, producer, dateOfLastUpdate) values (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            connection.setAutoCommit(false);
            List<Integer> idItems = SQLUtilities.getListIdByNameFromDB("items", nameOfBD); //вынес метод получающий лист айдишников из цикла
            for (int i = 0; i < items.size(); i++) {
                if (idItems.contains(items.get(i).getId())) {
                    continue;
                }
                statement.setString(1, items.get(i).getTitle());
                statement.setInt(2, items.get(i).getCode());
                statement.setString(3, items.get(i).getProducer());
                Timestamp timestamp = Timestamp.valueOf(items.get(i).getDateOfLastUpdate());
                statement.setTimestamp(4, timestamp);
                statement.executeUpdate();
            }
            connection.commit();
            System.out.println("All items is recorded successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    //возвращает лист айдишников по имени таблицы
    public static List<Integer> getListIdByNameFromDB(String name, String nameOfBD) {
        Connection connection = SQLUtilities.getConnection();
        ResultSet rs = null;
        String comand = "Select id from " + nameOfBD + "." + name;
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            rs = statement.executeQuery();
            List<Integer> id = new ArrayList<>();
            while (rs.next()) {
                id.add(rs.getInt("id"));
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //записывает всех Customers в DataBase
    public static void writeCustomersToDB(List<Customer> customers, String nameOfBD) {
        Connection connection = SQLUtilities.getConnection();
        if (customers == null) {
            System.out.println("customers = null");
            return;
        }
        String comand = "insert into " + nameOfBD + ".customers (name, dateOfBirth, address, gender, phoneNumber, DateOfLastPurchase) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            connection.setAutoCommit(false);
            for (int i = 0; i < customers.size(); i++) {
                if (SQLUtilities.getNamesCustomersFromBD(nameOfBD).contains(customers.get(i).getName())) {
                    continue;
                }
                statement.setString(1, customers.get(i).getName());
                statement.setDate(2, Date.valueOf(customers.get(i).getDateOfBirth()));
                statement.setString(3, customers.get(i).getAddress());
                statement.setString(4, customers.get(i).getGender());
                statement.setString(5, customers.get(i).getPhoneNumber());
                statement.setDate(6, Date.valueOf(customers.get(i).getDateOfLustPurchase()));
                statement.executeUpdate();

            }
            connection.commit();
            System.out.println("All customers is recorded successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }


    //Возвращает лист имен Customers уже записаных в DataBase
    public static List<String> getNamesCustomersFromBD(String nameOfBD) {
        Connection connection = SQLUtilities.getConnection();
        ResultSet rs = null;
        String comand = "Select name from " + nameOfBD + ".customers";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            rs = statement.executeQuery();
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            return names;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //проставляем объектам Customer Id вытянутые из DataBase
    public static void setIdForCustomers(List<Customer> customers, String nameOfBD) {
        Connection connection = SQLUtilities.getConnection();
        if (customers == null) {
            System.out.println("customers = null");
            return;
        }
        for (int i = 0; i < customers.size(); i++) {
            customers.get(i).setId(getIdCustomerFromDBByName(customers.get(i).getName(), nameOfBD));
        }
    }

    //достает из DataBase и возвращает Id Customer по его имени
    public static int getIdCustomerFromDBByName(String name, String nameOfBD) {
        Connection connection = SQLUtilities.getConnection();
        int id = 0;
        ResultSet rs = null;
        String comand = "SELECT id from " + nameOfBD + ".customers where name=?";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            statement.setString(1, name);
            rs = statement.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return 0;
    }

    //записывает Purchases из List<Customer> в DataBase
    public static void writePurchasesToDB(List<Customer> customers, String nameOfBD) {
        Connection connection = SQLUtilities.getConnection();
        if (customers == null) {
            System.out.println("customers = null");
            return;
        }
        String comand = "insert into " + nameOfBD + ".purchases (DateOfPurchase, customerId, itemId) values (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            connection.setAutoCommit(false);
            for (int i = 0; i < customers.size(); i++) {
                for (int j = 0; j < customers.get(i).getLastPurchases().size(); j++) {

                    statement.setDate(1, Date.valueOf(customers.get(i).getDateOfLustPurchase()));
                    statement.setInt(2, customers.get(i).getId());
                    statement.setInt(3, customers.get(i).getLastPurchases().get(j).getId());
                    statement.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

}
