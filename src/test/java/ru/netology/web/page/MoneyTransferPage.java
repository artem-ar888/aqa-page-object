package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static ru.netology.web.data.DataHelper.AccountDataFactory.accountGroup;
import static ru.netology.web.data.DataHelper.differentCardNumber;

public class MoneyTransferPage {
    private final SelenideElement mainHeading = $("h1");
    private final SelenideElement amountField = $("[data-test-id=amount] input");
    private final SelenideElement fromField = $("[data-test-id=from] input");
    private final SelenideElement toField = $("[data-test-id=to] input");
    private final SelenideElement transferButton = $("[data-test-id=action-transfer]");

    public MoneyTransferPage() {
        mainHeading.shouldHave(Condition.text("Пополнение карты"))
                .shouldBe(Condition.visible);
    }

    public DashboardPage validDeposit(int amount, boolean isRegularTransfer) {
        String cardNumber;
        String anotherCardNumber;
        amountField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(Integer.toString(amount));
        if (isRegularTransfer) {
            cardNumber = toField.attr("value").replace("**** **** ****", "5559 0000 0000");
            Optional<DataHelper.Account> differentAccount = accountGroup
                    .findMatchingAccount(account -> !account.getCardNumber().equals(cardNumber));
            differentCardNumber = differentAccount.get().getCardNumber();
            anotherCardNumber = differentCardNumber;
            fromField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                    .setValue(differentCardNumber);
        } else {
            cardNumber = differentCardNumber;
            fromField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                    .setValue(DataHelper.cardNumberToReceive);
            anotherCardNumber = DataHelper.cardNumberToReceive;
        }
        transferButton.click();
        accountGroup.updateBalanceByCardNumber(cardNumber,
                accountGroup.getBalanceByCardNumber(cardNumber) + DataHelper.transferAmount);
        accountGroup.updateBalanceByCardNumber(anotherCardNumber,
                accountGroup.getBalanceByCardNumber(anotherCardNumber) - DataHelper.transferAmount);
        return new DashboardPage();
    }
}