import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class Analitics {

    //які товари найчастіше купують жінки
    public static List<String> getItemSumGenderPurchases(String gender, String nameOfBD, Connection connection){
        List<String> itemSumGenderPurchases = new ArrayList<>();
        ResultSet rs = null;
        String comand ="select p.customerId, p.itemId, i.title, i.code, i.producer, count(*) as Purchases\n" +
                "from " + nameOfBD + ".purchases as p\n" +
                "left outer join " + nameOfBD + ".customers as c on c.id=p.customerId\n" +
                "left outer join " + nameOfBD + ".items as i on i.id=p.itemId\n" +
                "where c.gender=?\n" +
                "group by p.itemId";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            statement.setString(1, gender);
            rs = statement.executeQuery();
            while (rs.next()){
                String result = rs.getInt("customerId") + ";" + rs.getInt("itemId") + ";" + rs.getString("title") + ";" + rs.getInt("code") + ";" + rs.getString("producer") + ";" + rs.getInt("purchases");
                itemSumGenderPurchases.add(result);
            }
            return itemSumGenderPurchases;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }


    //найпопулярніші товари за тиждень (дані про період потрібно ввести в якості параметру методу)
    public static List<String> getItemSumPurchasesForWeak(LocalDate start, LocalDate end,  String nameOfBD, Connection connection){
        List<String> itemSumPurchasesForWeak = new ArrayList<>();
        ResultSet rs = null;
        String comand = "select p.customerId, p.itemId, i.title, i.code, i.producer, count(*) as Purchases\n" +
                "from " + nameOfBD + ".purchases as p\n" +
                "left outer join " + nameOfBD + ".items as i on i.id=p.itemId\n" +
                "where p.DateOfPurchase>=? and p.DateOfPurchase<=?\n" +
                "group by p.itemId";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            statement.setDate(1, new Date(0000 - 00 - 00).valueOf(start));
            statement.setDate(2, new Date(0000 - 00 - 00).valueOf(end));
            rs = statement.executeQuery();
            while (rs.next()){
                String result = rs.getInt("customerId") + ";" + rs.getInt("itemId") + ";" + rs.getString("title") + ";" + rs.getInt("code") + ";" + rs.getString("producer") + ";" + rs.getInt("purchases");
                itemSumPurchasesForWeak.add(result);
            }
            return itemSumPurchasesForWeak;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getTOP5ItemByPurchasesFromList (List<String> resultFromAnalitics){
        Comparator<String> comparator = new Comparator<String >() {
            @Override
            public int compare(String str1, String str2) {
                Pattern pattern = Pattern.compile(";");
                String[] strA1 = pattern.split(str1);
                String[] strA2 = pattern.split(str2);
                int length = strA1.length;
                if ((Integer.parseInt(strA1[length-1])- Integer.parseInt(strA2[length-1])) < 0){
                    return 1;
                } else if ((Integer.parseInt(strA1[length-1])- Integer.parseInt(strA2[length-1])) > 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
        //Сортировка с использованием компаратора
        Collections.sort(resultFromAnalitics, comparator);
        List<String> top5 = new ArrayList<>();

        for (int i = 0; i<5; i++){
            top5.add(resultFromAnalitics.get(i));
        }
        return top5;
    }
}
