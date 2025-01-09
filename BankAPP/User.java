import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class User {

    protected String fullName;
    protected long cardNumber;
    protected int sortCode;
    protected int accountNumber;
    protected YearMonth validFrom;
    protected YearMonth expiryDate;
    protected int securityCode;
    protected static List<Long> cardNumberHolder = new ArrayList<>();
    protected static List<Integer> sortCodeHolder = new ArrayList<>();
    protected static List<Integer> accountNumberHolder = new ArrayList<>();
    protected static List<User> userHolder = new ArrayList<>();
    protected List<recordTransactions> previousTransactions;
    protected double accountBalance;

    Random random = new Random();

    public User(String fullName) {
        this.fullName = fullName;
        this.cardNumber = generateCardNumber();
        this.sortCode = generateSortCode();
        this.accountNumber = generateAccountNumber();
        this.validFrom = YearMonth.now();
        this.expiryDate = this.validFrom.plus(4, ChronoUnit.YEARS);
        this.securityCode = generateSecurityCode();
        this.accountBalance = 0;
        userHolder.add(this);
        previousTransactions = new ArrayList<>();
    }

    private long generateCardNumber() {
        long min = 1000000000000000L;
        long max = 9999999999999999L;
        while (true){
            long randomNumber = min + (long) (random.nextDouble() * (max - min));
            if(!cardNumberHolder.contains(randomNumber)) {
                cardNumberHolder.add(randomNumber);
                return randomNumber;
            }
        }
    }

    private int generateSortCode() {
        int min = 100000;
        int max = 999999;
        while (true){
            int randomNumber = random.nextInt(max - min + 1) + min;
            if(!sortCodeHolder.contains(randomNumber)) {
                sortCodeHolder.add(randomNumber);
                return randomNumber;
            }
        }
    }

    private int generateAccountNumber() {
        int min = 10000000;
        int max = 99999999;
        while (true){
            int randomNumber = random.nextInt(max - min + 1) + min;
            if(!accountNumberHolder.contains(randomNumber)) {
                accountNumberHolder.add(randomNumber);
                return randomNumber;
            }
        }
    }

    private int generateSecurityCode() {
        int min = 100;
        int max = 999;
        return random.nextInt(max - min + 1) + min;
    }

    public void getAccountBalance() {
        String formattedBalance = String.format("%.2f", accountBalance);
        System.out.println(fullName + " Balance: £" + formattedBalance + "\n---------------------------");
    }

    public void addTransaction(String type, double amount) {
        recordTransactions transactions = new recordTransactions(type, amount);
        previousTransactions.add(transactions);

    }

    public void displayTransactions() {
        System.out.println(fullName + " Transactions:");
        for (recordTransactions transaction : previousTransactions) {
            String formattedAmount = String.format("%.2f", transaction.getAmount());
            System.out.println("Transaction Type: " + transaction.getType());
            System.out.println("Amount: £" + formattedAmount);
            System.out.println("Date: " + transaction.getDate());
            System.out.println("------");
        }

    }

    public void action() {
        Scanner s = new Scanner(System.in);

        System.out.println("Please select one of the options below: \n1 - Check balance\n2 - Deposit\n3 - Withdraw\n4 - Transfer\n5 - Display previous " +
                "transactions");
        int choice = s.nextInt();
        switch (choice) {
            case 1:
                this.getAccountBalance();
                break;
            case 2:
                System.out.println("Amount: ");
                double depositAmount = s.nextDouble();
                User.Transactions.deposit(this,depositAmount);
                break;
            case 3:
                System.out.println("Amount: ");
                double withdrawAmount = s.nextDouble();
                User.Transactions.withdraw(this,withdrawAmount);
                break;
            case 4:
                System.out.println("Amount: ");
                double transferAmount = s.nextDouble();
                s.nextLine();
                System.out.println("To who: ");
                String userToName = s.nextLine();

                boolean userFound = false;

                for (User u : User.userHolder) {
                    User userTo;
                    if(u.fullName.equalsIgnoreCase(userToName)){
                        userFound = true;
                        userTo = u;
                        User.Transactions.transfer(this,userTo,transferAmount);
                        break;
                    }
                }

                if(!userFound) {
                    System.out.println("User not found.");
                }

                break;
            case 5:
                this.displayTransactions();
                break;
            default:
                System.out.println("Pick one of the options provided");
                break;
        }

    }

    @Override
    public String toString() {
        return fullName + "\n" + "Card Number: " + this.cardNumber +"\nAccount Number: " + this.accountNumber + "\nSort Code: " + this.sortCode +
                "\nSecurity Code: " + this.securityCode + "\nValid from: " + this.validFrom + "   Expires: " + this.expiryDate + "\n---------------------------";
    }

    public static class Transactions{
        public static void deposit(User user, double amount) {
            String formattedAmount = String.format("%.2f", amount);

            if (amount < 0) {
                throw new IllegalArgumentException("Amount must be non-negative");
            } else if (Math.abs(amount * 100 - (int)(amount * 100)) > 0.001) {
                throw new IllegalArgumentException("Amount must have exactly two decimal places");
            } else {
                user.accountBalance += amount;
                String formattedBalance = String.format("%.2f", user.accountBalance);
                System.out.println("Deposited amount: £" + formattedAmount);
                System.out.println(user.fullName + " - Balance is now: £" + formattedBalance + "\n---------------------------");
                user.addTransaction("Deposit", amount);
            }
        }

        public static void withdraw(User user, double amount) {
            String formattedAmount = String.format("%.2f", amount);

            if (amount < 0) {
                throw new IllegalArgumentException("Amount must be non-negative");
            } else if (Math.abs(amount * 100 - (int)(amount * 100)) > 0.001) {
                throw new IllegalArgumentException("Amount must have exactly two decimal places");
            } else if (amount > user.accountBalance) {
                System.out.println("Can't withdraw this amount as balance will be in negative");
            } else {
                user.accountBalance -= amount;
                String formattedBalance = String.format("%.2f", user.accountBalance);
                System.out.println("Amount withdrawn: £" + formattedAmount);
                System.out.println(user.fullName + " - Balance is now: £" + formattedBalance + "\n---------------------------");
                user.addTransaction("Withdraw", amount);
            }
        }

        public static void transfer(User from, User to, double amount) {
            String formattedAmount = String.format("%.2f", amount);

            if (amount < 0) {
                throw new IllegalArgumentException("Amount must be non-negative");
            } else if (Math.abs(amount * 100 - (int)(amount * 100)) > 0.001) {
                throw new IllegalArgumentException("Amount must have exactly two decimal places");
            } else if (amount > from.accountBalance) {
                System.out.println("Can't withdraw this amount as balance will be in negative");
            } else {
                from.accountBalance -= amount;
                to.accountBalance += amount;
                from.addTransaction("Transferred", amount);
                to.addTransaction("Transfer Received", amount);

                System.out.println(from.fullName + " transferred £" + formattedAmount + " to " + to.fullName + "\n---------------------------");
            }
        }

    }

    public static class Bank{
        public static void printUsers() {
            userHolder.sort(Comparator.comparing(o -> o.fullName));
            userHolder.forEach(System.out::println);
        }
    }

    private class recordTransactions {
        private String type;
        private double amount;
        private LocalDate date;

        public recordTransactions(String type, double amount) {
            this.type = type;
            this.amount = amount;
            this.date = LocalDate.now();
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public LocalDate getDate() {
            return date;
        }
    }
}
