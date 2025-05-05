package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardInfo = DataHelper.getSecondCardInfo();
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    }

    @Test
    void shouldTransferMoneyFromFirstToSecond() {
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferMoneyPage = dashboardPage.selectCardTransfer(secondCardInfo);
        dashboardPage = transferMoneyPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        dashboardPage.reloadDashboardPage();
        assertAll(() -> dashboardPage.checkCardBalance(firstCardInfo, String.valueOf(expectedBalanceFirstCard)),
                () -> dashboardPage.checkCardBalance(secondCardInfo, String.valueOf(expectedBalanceSecondCard)));
    }

    @Test
    void shouldBeErrorMassageIfAmountMoreBalance() {
        var amount = DataHelper.generateInvalidAmount(secondCardBalance);
        var transferMoneyPage = dashboardPage.selectCardTransfer(firstCardInfo);
        transferMoneyPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        assertAll(() -> transferMoneyPage.findErrorMessage("Ошибка! "),
                () -> dashboardPage.reloadDashboardPage(),
                () -> dashboardPage.checkCardBalance(firstCardInfo, String.valueOf(firstCardBalance)),
                () -> dashboardPage.checkCardBalance(secondCardInfo, String.valueOf(secondCardBalance)));
    }
}