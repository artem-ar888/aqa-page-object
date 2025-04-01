package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import ru.netology.web.data.DataHelper;

import java.time.Duration;
import java.util.Optional;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.web.data.DataHelper.Account.extractBalance;
import static ru.netology.web.data.DataHelper.AccountDataFactory.accountGroup;

public class DashboardPage {
    private final SelenideElement heading = $("[data-test-id=dashboard]");
    private ElementsCollection cards = $$(".list__item div");
    private String buttonId;
    private SelenideElement depositButton;

    public DashboardPage() {
        heading.shouldBe(visible);
        cards.get(0).shouldBe(visible, Duration.ofSeconds(15));
    }

    public void extractAndStoreAccounts() {
        DataHelper.AccountGroup accountGroup = new DataHelper.AccountGroup();
        cards.forEach(element -> {
            DataHelper.Account account = new DataHelper.Account(element);
            accountGroup.addAccount(account);
        });
        DataHelper.AccountDataFactory.createAccountGroup(accountGroup);
    }

    public MoneyTransferPage makeTransferByCardNumber(String cardNumber) {
        Optional<DataHelper.Account> optionalAccount = accountGroup.findByCardNumber(cardNumber);
        if (optionalAccount.isPresent()) {
            buttonId = optionalAccount.get().getAccountId();
            depositButton = $("[data-test-id='" + buttonId + "'] button");
            depositButton.click();
            return new MoneyTransferPage();
        } else {
            // Обработать случай, когда аккаунт не найден
            throw new RuntimeException("Account with card number " + cardNumber + " not found");
        }
    }

    public boolean compareBalances() {
        boolean notAllAreEquivalent = false;
        for (SelenideElement element : cards) {
            String id = element.getAttribute("data-test-id");
            int balanceFromUI = extractBalance(element.getText());
            if (balanceFromUI != accountGroup.getBalanceById(id)) {
                notAllAreEquivalent = true;
                break;
            }
        }
        return notAllAreEquivalent;
    }
}
