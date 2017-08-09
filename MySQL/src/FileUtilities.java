import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileUtilities {

    private FileUtilities() {
    }

    public static void writeFile(String nameOfFile, List<String> line){
        try (PrintWriter outputStream = new PrintWriter(new FileWriter(nameOfFile))) {
            for (String s : line){
                outputStream.println(s);
            }
            System.out.println("The file is recorded successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String nameOfFile){
        try (BufferedReader in = new BufferedReader(new FileReader(nameOfFile))) {
            String line;
            List<String> fileInList = new ArrayList<>();
            while ((line = in.readLine()) != null){
                fileInList.add(line);
            }
            return fileInList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static List<Customer> getArrayListOfCustomerFromFile(String nameOfFile, List<Item> items) {
        if (nameOfFile.equals(null)) {
            System.out.println("nameOfFile = null");
            return null;
        }
        List<Customer> customers = new ArrayList<>();
        Customer customer = new Customer();
        List<String> fileInList = FileUtilities.readFile(nameOfFile);
        String[] nameOfFields = null;
        for (int i = 0; i<fileInList.size(); i++) {
            if (i == 0) {
                    Pattern pattern = Pattern.compile(";");
                    nameOfFields = pattern.split(fileInList.get(i));
                    continue;
                }
            customer = FileUtilities.getCustomerFromLine(fileInList.get(i), nameOfFields, items);
            customers.add(customer);
        }
        return customers;
    }

    public static Customer getCustomerFromLine(String line, String[] nameOfFields, List<Item> items) {
        if (line.equals(null)) {
            System.out.println("line = null");
            return null;
        }
        if (line.equals("")) {
            System.out.println("line = \"\"");
            return null;
        }
        Customer customer = new Customer();
        Pattern pattern = Pattern.compile(";");
        String[] fields = pattern.split(line);

        customer.setName(fields[0]);

        pattern = Pattern.compile(" ");
        String[] dateOfBirth = pattern.split(fields[1]);
        customer.setDateOfBirth(LocalDate.of(Integer.parseInt(dateOfBirth[2]), Month.valueOf(dateOfBirth[1].toUpperCase()), Integer.parseInt(dateOfBirth[0])));

        customer.setAddress(fields[2]);
        customer.setGender(fields[3]);
        customer.setPhoneNumber(fields[4]);

        String result = fields[5].replaceAll("\"", "");
        fields[5] = result;
        pattern = Pattern.compile(",");
        String[] lastPurchasesArray = pattern.split(fields[5]);
        List<Item> lastPurchases = new ArrayList<>();
        for (int i = 0; i<items.size();i++){
            for (int j = 0; j<lastPurchasesArray.length;j++) {
                if (items.get(i).getId() == Integer.parseInt(lastPurchasesArray[j])) {
                    lastPurchases.add(items.get(i));
                }
            }
        }
        customer.setLastPurchases(lastPurchases);

        pattern = Pattern.compile("/");
        String[] dateOfLustPurchase = pattern.split(fields[6]);
        customer.setDateOfLustPurchase(LocalDate.of(Integer.parseInt(dateOfLustPurchase[2]), Month.of(Integer.parseInt(dateOfLustPurchase[0])), Integer.parseInt(dateOfLustPurchase[1])));
        return customer;
    }



    //Заполняет и возвращает List<Item> используя информацию из файла
    public static List<Item> getArrayListOfItemsFromFile(String nameOfFile) {
        if (nameOfFile.equals(null)) {
            System.out.println("nameOfFile = null");
            return null;
        }
        List<Item> items = new ArrayList<>();
        Item item = null;
        try (BufferedReader in = new BufferedReader(new FileReader(nameOfFile))) {
            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {
                count++;
                //пропускаем первую строку с заголовками
                if (count == 1) {
                    continue;
                }
                //отправляем строку в метод возвращающий готовый объект класса Item
                item = FileUtilities.getItemFromLine(line);
                items.add(item);
            }
            return items;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Получаем и возвращаем объект класса Item с заполненными данными из полученой строки
    public static Item getItemFromLine(String line){
        if (line.equals(null)){
            System.out.println("line = null");
            return null;
        }
        line += ";";
        Item item = new Item();
        char[] lineCharArray = line.toCharArray();
        String buffer = "";
        int count = 0;
        for (char c : lineCharArray) {
            if (c == ';') {
                count++;
                switch (count) {
                    case 1:
                        item.setId(Integer.parseInt(buffer));
                        break;
                    case 2:
                        item.setTitle(buffer);
                        break;
                    case 3:
                        item.setCode(Integer.parseInt(buffer));
                        break;
                    case 4:
                        item.setProducer(buffer);
                        break;
                    case 5:
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                        buffer = FileUtilities.formatterStringForDate(buffer);
                        LocalDateTime localDateTime = LocalDateTime.parse(buffer, formatter);
                        item.setDateOfLastUpdate(localDateTime);
                        break;
                }
                buffer = "";
            } else {
                buffer += c;
            }
        }
        return item;
    }

    public static String formatterStringForDate(String buffer){
        char[] lineCharArray = buffer.toCharArray();
        String dateTime = "";
        for (int i = 0; i<lineCharArray.length;i++) {
            if (lineCharArray[i] == ' ' && lineCharArray[i+2] == ':'){
                dateTime += lineCharArray[i];
                dateTime += '0';
            } else {
                dateTime += lineCharArray[i];
            }
        }
        return dateTime;
    }
}
