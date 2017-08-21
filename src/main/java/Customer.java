import java.time.LocalDate;
import java.util.List;

public class Customer {
    private int id;
    private String name;
    private LocalDate dateOfBirth;
    private String address;
    private String gender;
    private String phoneNumber;
    private List<Item> lastPurchases;
    private LocalDate dateOfLustPurchase;

    public Customer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Item> getLastPurchases() {
        return lastPurchases;
    }

    public void setLastPurchases(List<Item> lastPurchases) {
        this.lastPurchases = lastPurchases;
    }

    public LocalDate getDateOfLustPurchase() {
        return dateOfLustPurchase;
    }

    public void setDateOfLustPurchase(LocalDate dateOfLustPurchase) {
        this.dateOfLustPurchase = dateOfLustPurchase;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                "name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", lastPurchases=" + lastPurchases +
                ", dateOfLustPurchase=" + dateOfLustPurchase +
                '}';
    }
}
