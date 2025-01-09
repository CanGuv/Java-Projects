public class Main {

    public static void main(String[] args) {

        User John = new User("John Walter");
        System.out.println(John);

        User Bob = new User("Bob Smith");
        System.out.println(Bob);

        User.Transactions.deposit(John,100);
        User.Transactions.withdraw(John,25.65);
        User.Transactions.transfer(John,Bob,17.80);

        Bob.getAccountBalance();

        John.getAccountBalance();

        User.Bank.printUsers();

        John.displayTransactions();
        Bob.displayTransactions();

        John.action();

    }
}
