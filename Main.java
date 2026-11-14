
import java.io.*;
import java.util.*;

class Rental {
    int id;
    String customer;
    String vehicle;
    int days;
    double total;
    String status;

    Rental(int id, String customer, String vehicle,
           int days, double total, String status) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.days = days;
        this.total = total;
        this.status = status;
    }

    void display() {
        System.out.println("\nRental ID: " + id);
        System.out.println("Customer: " + customer);
        System.out.println("Vehicle: " + vehicle);
        System.out.println("Days: " + days);
        System.out.println("Total: Rs. " + total);
        System.out.println("Status: " + status);
    }

    String toFileFormat() {
        return id + "|" + customer + "|" + vehicle + "|"
                + days + "|" + total + "|" + status;
    }
}

public class Main {
    static ArrayList<Rental> rentals = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    static final String FILE_NAME = "rentals.txt";

    static String[] vehicles = {
        "Car", "Bike", "SUV"
    };

    static double[] rates = {
        1500, 500, 2500
    };

    static void loadRentals() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length == 6) {
                    try {
                        int id = Integer.parseInt(data[0]);
                        int days = Integer.parseInt(data[3]);
                        double total = Double.parseDouble(data[4]);

                        if (days > 0 &&
                                (data[5].equals("Active")
                                || data[5].equals("Returned"))) {
                            rentals.add(new Rental(
                                id, data[1], data[2],
                                days, total, data[5]
                            ));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid record.");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading rental records.");
        }
    }

    static void saveRentals() {
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Rental rental : rentals) {
                writer.println(rental.toFileFormat());
            }

        } catch (IOException e) {
            System.out.println("Error saving records.");
        }
    }

    static int generateId() {
        int maxId = 1000;

        for (Rental rental : rentals) {
            if (rental.id > maxId) {
                maxId = rental.id;
            }
        }

        return maxId + 1;
    }

    static boolean isVehicleAvailable(String vehicle) {
        for (Rental rental : rentals) {
            if (rental.vehicle.equalsIgnoreCase(vehicle)
                    && rental.status.equals("Active")) {
                return false;
            }
        }

        return true;
    }

    static void showVehicles() {
        System.out.println("\n--- Vehicle List ---");

        for (int i = 0; i < vehicles.length; i++) {
            String availability =
                    isVehicleAvailable(vehicles[i])
                    ? "Available" : "Rented";

            System.out.println((i + 1) + ". " + vehicles[i]
                    + " - Rs. " + rates[i] + "/day"
                    + " - " + availability);
        }
    }

    static void rentVehicle() {
        System.out.print("Enter Customer Name: ");
        String customer = sc.nextLine().trim();

        if (customer.isEmpty() || customer.contains("|")) {
            System.out.println("Invalid customer name!");
            return;
        }

        showVehicles();

        System.out.print("Choose Vehicle (1-3): ");

        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid vehicle choice!");
            return;
        }

        if (choice < 1 || choice > vehicles.length) {
            System.out.println("Invalid vehicle choice!");
            return;
        }

        String vehicle = vehicles[choice - 1];

        if (!isVehicleAvailable(vehicle)) {
            System.out.println("Vehicle is already rented!");
            return;
        }

        System.out.print("Enter Number of Days: ");

        int days;
        try {
            days = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of days!");
            return;
        }

        if (days <= 0) {
            System.out.println("Days must be greater than zero!");
            return;
        }

        double total = rates[choice - 1] * days;
        int id = generateId();

        rentals.add(new Rental(
            id, customer, vehicle, days, total, "Active"
        ));

        saveRentals();

        System.out.println("\nVehicle rented successfully!");
        System.out.println("Rental ID: " + id);
        System.out.println("Total Charges: Rs. " + total);
    }

    static void viewRentals() {
        if (rentals.isEmpty()) {
            System.out.println("No rental records found.");
            return;
        }

        for (Rental rental : rentals) {
            rental.display();
        }
    }

    static void searchRental() {
        System.out.print("Enter Rental ID: ");

        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID!");
            return;
        }

        for (Rental rental : rentals) {
            if (rental.id == id) {
                rental.display();
                return;
            }
        }

        System.out.println("Rental record not found.");
    }

    static void returnVehicle() {
        System.out.print("Enter Rental ID: ");

        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID!");
            return;
        }

        for (Rental rental : rentals) {
            if (rental.id == id) {
                if (rental.status.equals("Returned")) {
                    System.out.println("Vehicle already returned.");
                    return;
                }

                rental.status = "Returned";
                saveRentals();

                System.out.println("Vehicle returned successfully!");
                return;
            }
        }

        System.out.println("Rental record not found.");
    }

    public static void main(String[] args) {
        loadRentals();

        while (true) {
            System.out.println("\n===== VEHICLE RENTAL SYSTEM =====");
            System.out.println("1. Show Vehicles");
            System.out.println("2. Rent Vehicle");
            System.out.println("3. View All Rentals");
            System.out.println("4. Search Rental");
            System.out.println("5. Return Vehicle");
            System.out.println("6. Exit");

            System.out.print("Enter Choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    showVehicles();
                    break;

                case "2":
                    rentVehicle();
                    break;

                case "3":
                    viewRentals();
                    break;

                case "4":
                    searchRental();
                    break;

                case "5":
                    returnVehicle();
                    break;

                case "6":
                    System.out.println("Thank you!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
