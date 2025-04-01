package ru.netology.web.data;

import com.codeborne.selenide.SelenideElement;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class DataHelper {
    public static int transferAmount;
    public static String cardNumberToReceive;
    public static String differentCardNumber;

    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

    @Data
    @AllArgsConstructor
    public static class Account {
        private final String accountId;
        private final String cardNumber;
        private int balance;

        public Account(SelenideElement element) {
            this.accountId = element.getAttribute("data-test-id");
            this.cardNumber = extractNumberOfCard(element.getText());
            this.balance = extractBalance(element.getText());
        }

        private String extractNumberOfCard(String text) {
            final String numberStart = "**** **** **** ";
            final String numberFinish = ", баланс: ";
            val start = text.indexOf(numberStart);
            val finish = text.indexOf(numberFinish);
            val value = text.substring(start + numberStart.length(), finish);
            return "5559 0000 0000 " + value;
        }

        public static int extractBalance(String text) {
            final String balanceStart = "баланс: ";
            final String balanceFinish = " р.";
            val start = text.indexOf(balanceStart);
            val finish = text.indexOf(balanceFinish);
            val value = text.substring(start + balanceStart.length(), finish);
            return Integer.parseInt(value);
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class AccountGroup {
        private final Map<String, Account> accountsById = new HashMap<>();
        private final Map<String, Account> accountsByCardNumber = new HashMap<>();

        public void addAccount(Account account) {
            accountsById.put(account.getAccountId(), account);
            accountsByCardNumber.put(account.getCardNumber(), account);
        }

        public Optional<Account> findById(String id) {
            return Optional.ofNullable(accountsById.get(id));
        }

        public Optional<Account> findByCardNumber(String cardNumber) {
            return Optional.ofNullable(accountsByCardNumber.get(cardNumber));
        }

        public void updateBalanceById(String id, int newBalance) {
            findById(id).ifPresent(account -> account.setBalance(newBalance));
        }

        public void updateBalanceByCardNumber(String cardNumber, int newBalance) {
            findByCardNumber(cardNumber).ifPresent(account -> account.setBalance(newBalance));
        }

        public int getBalanceById(String id) {
            return findById(id).map(Account::getBalance)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
        }

        public int getBalanceByCardNumber(String cardNumber) {
            return findByCardNumber(cardNumber).map(Account::getBalance)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
        }

        public Optional<Account> findMatchingAccount(Predicate<Account> predicate) {
            return accountsById.values().stream()
                    .filter(predicate)
                    .findFirst();
        }
    }

    public static class AccountDataFactory {
        public static AccountGroup accountGroup;

        public static void createAccountGroup(AccountGroup accountGroup) {
            AccountDataFactory.accountGroup = accountGroup;
        }
    }
}
