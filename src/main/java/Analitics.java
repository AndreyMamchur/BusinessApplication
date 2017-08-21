import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class Analitics {

    //Топ-5 самых покупаемых товаров среди заданого в параметрах пола
    public static List<String> top5GenderPurchases(String gender, final String nameOfBD){
        Connection connection = SQLUtilities.getConnection();
        List<String> itemSumGenderPurchases = new ArrayList<>();
        ResultSet rs = null;
        String comand ="select p.itemId, i.title, i.code, i.producer, count(*) as Purchases\n" +
                "from " + nameOfBD + ".purchases as p\n" +
                "left outer join " + nameOfBD + ".customers as c on c.id=p.customerId\n" +
                "left outer join " + nameOfBD + ".items as i on i.id=p.itemId\n" +
                "where c.gender=?\n" +
                "group by p.itemId limit 5";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            statement.setString(1, gender);
            rs = statement.executeQuery();
            while (rs.next()){
                String result = rs.getInt("itemId") + ";" + rs.getString("title") + ";" + rs.getInt("code") + ";" + rs.getString("producer") + ";" + rs.getInt("purchases");
                itemSumGenderPurchases.add(result);
            }
            return itemSumGenderPurchases;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }


    //ТОП-5 самых покупаемых товаров за неделю
    public static List<String> top5PurchasesPerWeek(LocalDate start, LocalDate end,  String nameOfBD){
        Connection connection = SQLUtilities.getConnection();
        List<String> itemSumPurchasesForWeak = new ArrayList<>();
        ResultSet rs = null;
        String comand = "select p.itemId, i.title, i.code, i.producer, count(*) as Purchases\n" +
                "from " + nameOfBD + ".purchases as p\n" +
                "left outer join " + nameOfBD + ".items as i on i.id=p.itemId\n" +
                "where p.DateOfPurchase>=? and p.DateOfPurchase<=?\n" +
                "group by p.itemId limit 5";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2,Date.valueOf(end));
            rs = statement.executeQuery();
            while (rs.next()){
                String result = rs.getInt("itemId") + ";" + rs.getString("title") + ";" + rs.getInt("code") + ";" + rs.getString("producer") + ";" + rs.getInt("purchases");
                itemSumPurchasesForWeak.add(result);
            }
            return itemSumPurchasesForWeak;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
