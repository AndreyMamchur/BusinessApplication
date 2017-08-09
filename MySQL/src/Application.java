import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        Connection connection = SQLUtilities.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DataBase dataBase = new DataBase();
        dataBase.buildDataBaseFromFile("Customers.csv", "items.csv", "shop");
        dataBase.setItems(FileUtilities.getArrayListOfItemsFromFile("items.csv"));
        dataBase.setCustomers(FileUtilities.getArrayListOfCustomerFromFile("Customers.csv", dataBase.getItems()));
        SQLUtilities.writeItemsToBD(dataBase.getItems(), dataBase.getNameOfDataBase(), connection);
        SQLUtilities.writeCustomersToBD(dataBase.getCustomers(), dataBase.getNameOfDataBase(), connection);
        SQLUtilities.setIdForCustomers(dataBase.getCustomers(), dataBase.getNameOfDataBase(), connection);
        SQLUtilities.writePurchasesToBD(dataBase.getCustomers(), dataBase.getNameOfDataBase(), connection);
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        List<String> itemSumGenderPurchases = Analitics.getItemSumGenderPurchases("female", dataBase.getNameOfDataBase(), connection);

        itemSumGenderPurchases = Analitics.getTOP5ItemByPurchasesFromList(itemSumGenderPurchases);

        itemSumGenderPurchases.add(0, "id;title;code;producer;purchases");

        List<String> itemSumPurchasesForWeak = Analitics.getItemSumPurchasesForWeak(LocalDate.of(2017,6, 1), LocalDate.of(2017, 6, 7), dataBase.getNameOfDataBase(), connection);
        itemSumPurchasesForWeak.add(0, "customerId;itemId;title;code;producer;Purchases");

        FileUtilities.writeFile("Top-5FemalePurchases.csv", itemSumGenderPurchases);
        FileUtilities.writeFile("PurchasesForWeak.csv", itemSumPurchasesForWeak);
    }
}
