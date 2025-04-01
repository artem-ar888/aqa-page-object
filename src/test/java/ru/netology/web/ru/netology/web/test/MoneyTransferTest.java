package ru.netology.web.ru.netology.web.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class MoneyTransferTest {

    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        DataHelper.cardNumberToReceive = "5559 0000 0000 0002";
        DataHelper.transferAmount = 500;
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        dashboardPage.extractAndStoreAccounts();
        var moneyTransferPage = dashboardPage.makeTransferByCardNumber(DataHelper.cardNumberToReceive);
        dashboardPage = moneyTransferPage.validDeposit(DataHelper.transferAmount, true);
        Assertions.assertFalse(dashboardPage.compareBalances());

        // Возвращаем первоначальный баланс на картах
        moneyTransferPage = dashboardPage.makeTransferByCardNumber(DataHelper.differentCardNumber);
        dashboardPage = moneyTransferPage.validDeposit(DataHelper.transferAmount, false);
        Assertions.assertFalse(dashboardPage.compareBalances());
    }
}
