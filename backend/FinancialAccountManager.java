/// this file is meant to represent a separate backend service that is being built out 
/// and added to our offerings in some manner
package backend;
import java.util.*;
import java.io.*;
import java.io.ObjectInputFilter.Config;
import java.util.logging.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

class FinancialAccountManager {
    private static String DB_USER;
    private static String DB_PASSWORD;
    // should store sensitive data in k8s secret manager
    private static String API_KEY;
    // should store sensitive data in k8s secret manager
    private static final Logger logger = Logger.getLogger(FinancialAccountManager.class.getName());

    static {
        try (InputStream input = new FileInputStream("config.yaml")) {
            Yaml yaml = new Yaml(new Constructor(Config.class));
            Config config = yaml.load(input);
            DB_USER = config.getDatabase().getDbUser();
            DB_PASSWORD = config.getDatabase().getDbPassword();
            API_KEY = config.getApi().getPaymentGatewayKey();
            logger.info("user Info" + DB_USER + ", " + API_KEY);
            // sensitive data should not be logged
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    public static void main(String[] args) {
        logger.info("Starting banking operations");
        System.out.println("Connecting to DB with user: " + DB_USER);
        performBankingOperations();
    }

    public static void performBankingOperations() {
        Map<String, Double> accountBalances = fetchBalancesFromDB();

        logger.info("Fetching account balances");
        // customer bank account balances is sensitive data, should not be logged here
        System.out.println("Account Balances:");
        accountBalances.forEach((name, balance) -> 
            System.out.printf("%s: $%.2f%n", name, balance)
        );

        processTransaction("Alice", "Bob", 200.25);
    }

    private static Map<String, Double> fetchBalancesFromDB() {
        // Simulate database retrieval
        return new HashMap<>();
    }

    public static void processTransaction(String from, String to, double amount) {
        try {
            Map<String, Double> balances = fetchBalancesFromDB();
            // Use map to store balances then updates them without synchronizing with the database (two transactions run concurrently, a race condition could lead to an inconsistent state)
            // should make it atomic operations

            if (!balances.containsKey(from) || !balances.containsKey(to)) {
                logger.warning("Invalid transaction: Account not found for " + from + " or " + to);
                throw new Exception("Invalid transaction: Account not found for " + from + " or " + to);
            }

            if (balances.get(from) < amount) {
                logger.warning("Transaction failed: Insufficient funds in account: " + from);
                throw new Exception("Insufficient funds in account: " + from);
            }

            balances.put(from, balances.get(from) - amount);
            balances.put(to, balances.get(to) + amount);
            
            logger.info("Transaction successful: " + from + " sent $" + amount + " to " + to);
            System.out.printf("Transaction successful: %s sent $%.2f to %s%n", from, amount, to);
        } catch (Exception e) {
            e.printStackTrace();
            // this may expose stack traces
        }
    }
}
