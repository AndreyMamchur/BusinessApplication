import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class DataBase {
    private String nameOfDataBase;
    private List<Customer> customers;
    private List<Item> items;

    public DataBase() {
    }

    public String getNameOfDataBase() {
        return nameOfDataBase;
    }

    public void setNameOfDataBase(String name) {
        this.nameOfDataBase = name;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void buildDataBaseFromFile(String customerFile, String itemFile, String nameOfDataBase){
        Connection connection = SQLUtilities.getConnection();
        try {
            connection.setAutoCommit(false);
            this.setNameOfDataBase(nameOfDataBase);
            List<String> customerInStringList = FileUtilities.readFile(customerFile);
            List<String> itemInStringList = FileUtilities.readFile(itemFile);
            this.createDataBaseByName(nameOfDataBase);
            int i=customerFile.indexOf('.');
            String nameOfTable = customerFile.substring(0, i);
            this.createTableByName(nameOfTable, nameOfDataBase);
            this.addColumnInTable(customerInStringList, nameOfTable, nameOfDataBase);

            i=itemFile.indexOf('.');
            nameOfTable = itemFile.substring(0, i);
            this.createTableByName(nameOfTable, nameOfDataBase);
            this.addColumnInTable(itemInStringList, nameOfTable, nameOfDataBase);

            this.createTablePurchases();

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

    //создание базы данных по имени
    public void createDataBaseByName (String nameOfDataBase){
        Connection connection = SQLUtilities.getConnection();
        String comand = "create database if not exists " + nameOfDataBase;
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            connection.setAutoCommit(false);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //создание таблицы по названию
    public void createTableByName (String nameOfTable, String nameOfDataBase){
        Connection connection = SQLUtilities.getConnection();

        String comand = "create table if not exists " + nameOfDataBase + "." + nameOfTable + "\n" +
                "(\n" +
                "id int not null auto_increment,\n" +
                "primary key(id)\n" +
                ")";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            connection.setAutoCommit(false);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //создание таблицы Purchases
    public void createTablePurchases(){
        Connection connection = SQLUtilities.getConnection();
        String comand = "create table if not exists shop.Purchases(\n" +
                "id int not null auto_increment,\n" +
                "DateOfPurchase date not null,\n" +
                "customerId int not null,\n" +
                "itemId int not null,\n" +
                "index (customerId),\n" +
                "index (itemId),\n" +
                "foreign key (customerId)\n" +
                "references customers(id)\n" +
                "on update restrict on delete restrict,\n" +
                "foreign key (itemId)\n" +
                "references items (id)\n" +
                "on update restrict on delete restrict,\n" +
                "primary key(id)\n" +
                ")";
        try (PreparedStatement statement = connection.prepareStatement(comand)) {
            connection.setAutoCommit(false);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //создание колонок для стола по его имени(названия колонок приходят сверху)
    public void addColumnInTable (List<String> stringList, String nameOfTable, String nameOfDataBase){
        Connection connection = SQLUtilities.getConnection();
        Pattern pattern = Pattern.compile(";");
        String[] nameOfFields = pattern.split(stringList.get(0));
        if (nameOfTable.equals("Customers")) {
            this.addColumnInCustomers(nameOfFields, nameOfTable, nameOfDataBase);
        } else if (nameOfTable.equals("items")){
            this.addColumnInItems(nameOfFields, nameOfTable, nameOfDataBase);
        }
    }

    //добавляем колонки в Customers
    public void addColumnInCustomers(String[] nameOfFields, String nameOfTable, String nameOfDataBase){
        Connection connection = SQLUtilities.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<nameOfFields.length; i++){
            String comand = "alter table " + nameOfDataBase +"." + nameOfTable + " add ";
            switch (nameOfFields[i]){
                case "Name" :
                    if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                        continue;
                    } else {
                        comand = comand + nameOfFields[i] + " varchar(30) not null";
                        break;
                    }
                case "DateOfBirth" :
                    if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                        continue;
                    } else {
                        comand = comand + nameOfFields[i] + " date not null";
                        break;
                    }
                case "Address" :
                    if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                        continue;
                    } else {
                        comand = comand + nameOfFields[i] + " varchar (90) not null";
                        break;
                    }
                case "Gender" :
                    if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                        continue;
                    } else {
                        comand = comand + nameOfFields[i] + " varchar(10) not null";
                        break;
                    }
                case "PhoneNumber" :
                    if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                        continue;
                    } else {
                        comand = comand + nameOfFields[i] + " varchar (30)";
                        break;
                    }
                case "LastPurchases" :
                    continue;
                case "DateOfLastPurchase" :
                    if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                        continue;
                    } else {
                        comand = comand + nameOfFields[i] + " date";
                        break;
                    }
            }
            try (PreparedStatement statement = connection.prepareStatement(comand)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        //добавляем колонки в Items
        public void addColumnInItems(String[] nameOfFields, String nameOfTable, String nameOfDataBase){
            Connection connection = SQLUtilities.getConnection();
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (int i = 0; i<nameOfFields.length; i++){
                String comand = "alter table " + nameOfDataBase +"." + nameOfTable + " add ";
                switch (nameOfFields[i]){
                    case "id" :
                        continue;
                    case "title" :
                        if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                            continue;
                        } else {
                            comand = comand + nameOfFields[i] + " varchar(60) not null";
                            break;
                        }
                    case "code" :
                        if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                            continue;
                        } else {
                            comand = comand + nameOfFields[i] + " int not null";
                            break;
                        }
                    case "producer" :
                        if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                            continue;
                        } else {
                            comand = comand + nameOfFields[i] + " varchar(60) not null";
                            break;
                        }
                    case "dateOfLastUpdate" :
                        if (this.checkForAvailability(nameOfFields[i], nameOfTable, nameOfDataBase)){
                            continue;
                        } else {
                            comand = comand + nameOfFields[i] + " datetime";
                            break;
                        }
                }
                try (PreparedStatement statement = connection.prepareStatement(comand)) {
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Проверка на наличие колонки в таблице
        public boolean checkForAvailability (String nameOfColumn, String nameOfTable, String nameOfDataBase){
            Connection connection = SQLUtilities.getConnection();
            ResultSet rs = null;
            String comand = "show columns from " + nameOfDataBase + "." + nameOfTable;
            try (PreparedStatement statement = connection.prepareStatement(comand)){
                rs = statement.executeQuery();
                while (rs.next()){
                    if(nameOfColumn.equals(rs.getString("Field"))){
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
}
