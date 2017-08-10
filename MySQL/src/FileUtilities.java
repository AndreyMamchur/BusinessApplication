import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class FileUtilities {

    public FileUtilities() {
    }

    //метод получает данные и записывает их в файл
    public static void writeListToFile(String nameOfFile, List<String> line){
        try (PrintWriter outputStream = new PrintWriter(new FileWriter(nameOfFile))) {
            for (String s : line){
                outputStream.println(s);
            }
            System.out.println("The file is recorded successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //метод читает файл построчно и возвращает List<String>
    public static List<String> readFileToList(String nameOfFile){
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

    //метод возвращает масив строк полученый из первой строки из файла (названия полей)
    public static String[] getNameOfFieldsDataBaseFromFile(String nameOfFile){
        List<String> fileInList = FileUtilities.readFileToList(nameOfFile);
        String[] nameOfFieldsDataBase = fileInList.get(0).split(";");
        return nameOfFieldsDataBase;
    }

    //метод возвращает данные из файла, кроме первой строки (значение полей)
    public static List<String> getListWitchInformationFromFile(String nameOfFile){
        List<String> fileInList = FileUtilities.readFileToList(nameOfFile);
        fileInList.remove(0);
        return fileInList;
    }

    //метод возвращает List<Customer> с заполнеными и создаными объектами класса Customer, используя информацию из файла
    public static List<Customer> getArrayListOfCustomerFromFile(String nameOfFile, List<Item> items) {
        if (nameOfFile.equals(null)) {
            System.out.println("nameOfFile = null");
            return null;
        }
        List<Customer> customers = new ArrayList<>();
        Customer customer = null;
        List<String> valueOfFieldsInList = FileUtilities.getListWitchInformationFromFile(nameOfFile);
        for (int i = 0; i<valueOfFieldsInList.size(); i++) {
            customer = FileUtilities.getCustomerFromLine(valueOfFieldsInList.get(i), items);
            customers.add(customer);
        }
        return customers;
    }

    //метод создает объект Customer и заполняет его поля
    public static Customer getCustomerFromLine(String line, List<Item> items) {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.US);
        customer.setDateOfBirth(LocalDate.parse(fields[1], formatter)); //переделал на DateTimeFormatter

        customer.setAddress(fields[2].replaceAll("\"", ""));
        customer.setGender(fields[3]);
        customer.setPhoneNumber(fields[4]);

        //заполнение листа lastPurchases для Customer
        fields[5] = fields[5].replaceAll("\"", ""); //удаляю кавычки
        String[] lastPurchasesArray = fields[5].split(",");  //разбиваю на масив
        List<Item> lastPurchases = new ArrayList<>();
        for (int i = 0; i<items.size();i++){
            for (int j = 0; j<lastPurchasesArray.length;j++) {
                if (items.get(i).getId() == Integer.parseInt(lastPurchasesArray[j])) {
                    lastPurchases.add(items.get(i));
                }
            }
        }
        customer.setLastPurchases(lastPurchases);

        formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        customer.setDateOfLustPurchase(LocalDate.parse(fields[6], formatter)); //переделал на DateTimeFormatter
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
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss");
                        item.setDateOfLastUpdate(LocalDateTime.parse(buffer, formatter));
                        break;

                }
                buffer = "";
            } else {
                buffer += c;
            }
        }
        return item;
    }
}
