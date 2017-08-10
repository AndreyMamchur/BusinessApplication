import java.time.LocalDate;
import java.util.List;

public class Application {
    public static void main(String[] args) {

        DataBase dataBase = new DataBase("Customers.csv", "items.csv", "shop");
        dataBase.setItems(FileUtilities.getArrayListOfItemsFromFile("items.csv"));
        dataBase.setCustomers(FileUtilities.getArrayListOfCustomerFromFile("Customers.csv", dataBase.getItems()));
        SQLUtilities.writeItemsToDB(dataBase.getItems(), dataBase.getNameOfDataBase());
        SQLUtilities.writeCustomersToDB(dataBase.getCustomers(), dataBase.getNameOfDataBase());
        SQLUtilities.setIdForCustomers(dataBase.getCustomers(), dataBase.getNameOfDataBase());
        SQLUtilities.writePurchasesToDB(dataBase.getCustomers(), dataBase.getNameOfDataBase());

        List<String> top5GenderPurchases = Analitics.top5GenderPurchases("female", dataBase.getNameOfDataBase());

        top5GenderPurchases.add(0, "itemId;title;code;producer;purchases");

        List<String> top5PurchasesPerWeek = Analitics.top5PurchasesPerWeek(LocalDate.of(2017,6, 1), LocalDate.of(2017, 6, 7), dataBase.getNameOfDataBase());
        top5PurchasesPerWeek.add(0, "itemId;title;code;producer;Purchases");

        FileUtilities.writeListToFile("Top-5FemalePurchases.csv", top5GenderPurchases);
        FileUtilities.writeListToFile("Top-5PurchasesPerWeek.csv", top5PurchasesPerWeek);
    }
}
