package com.shrawan.revolut;

import com.shrawan.revolut.api.TransferRequest;
import com.shrawan.revolut.domain.AccountRepository;
import com.shrawan.revolut.domain.InMemoryAccountRepository;
import com.shrawan.revolut.domain.model.Account;
import com.shrawan.revolut.domain.model.Currency;
import io.restassured.RestAssured;
import org.jooby.test.JoobyRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

public class ApiIntegrationTest {

    private static App app = new App();

    @ClassRule
    public static JoobyRule bootstrap = new JoobyRule(app);

    @BeforeClass
    public static void config() {
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
    }

    @Before
    public void clearAccRepo() {
        app.require(InMemoryAccountRepository.class).clear();
    }


    @Test
    public void appIsUp() {
        get("/swagger")
                .then()
                .assertThat()
                .body(notNullValue());
    }

    @Test
    public void testGetAccountsSize() {
        createMockAccount("A1", BigInteger.ZERO, Currency.RUB);
        createMockAccount("A2", BigInteger.ONE, Currency.RUB);
        createMockAccount("A3", BigInteger.valueOf(2526L), Currency.USD);

        when()
                .get("/accounts")
                .then()
                .body("$", hasSize(3));
    }


    @Test
    public void testInvalidUrl() {

        when()
                .get("/acshgsdh")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAccount() {

        createMockAccount("GB000000002", BigInteger.valueOf(200), Currency.RUB);
        createMockAccount("GB000000205", BigInteger.valueOf(205000), Currency.EUR);

        when()
                .get("/accounts/GB000000002")
                .then()
                .body("accountNumber", is("GB000000002"))
                .body("accountBalance", comparesEqualTo(new BigDecimal("2.00")));

        when()
                .get("/accounts/GB000000205")
                .then()
                .body("accountNumber", is("GB000000205"))
                .body("accountBalance", comparesEqualTo(new BigDecimal(205).setScale(3, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void testGetMissingAccount() {

        createMockAccount("GB000000002", BigInteger.valueOf(200), Currency.RUB);

        when()
                .get("/accounts/GB0000022222")
                .then()
                .statusCode(404);
    }

    @Test
    public void testValidTransfer() {

        createMockAccount("GB000000002", BigInteger.valueOf(200), Currency.RUB);
        createMockAccount("GB000000105", BigInteger.valueOf(10500), Currency.RUB);

        given().
                when()
                .body(new TransferRequest("GB000000105", "GB000000002", new BigDecimal("2")))
                .contentType("application/json")
                .post("/accounts/transfer")
                .then().statusCode(200);

        when()
                .get("/accounts/GB000000002")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("4.00")));

        when()
                .get("/accounts/GB000000105")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("103.00")));
    }

    @Test
    public void testInvalidTransferNoAccount() {
        createMockAccount("GB000000002", BigInteger.valueOf(200), Currency.RUB);
        createMockAccount("GB000000105", BigInteger.valueOf(10500), Currency.RUB);

        given().
                when()
                .body(new TransferRequest("GB000000305", "GB000000002", new BigDecimal("2")))
                .contentType("application/json")
                .post("/accounts/transfer")
                .then()
                .statusCode(400)
                .body(containsString("GB000000305 not found"));

    }

    @Test
    public void testInvalidTransferInsufficientFunds() {
        createMockAccount("GB000000002", BigInteger.valueOf(200), Currency.RUB);
        createMockAccount("GB000000100", BigInteger.valueOf(10000), Currency.RUB);

        given().
                when()
                .body(new TransferRequest("GB000000002", "GB000000100", new BigDecimal("200")))
                .contentType("application/json")
                .post("/accounts/transfer")
                .then()
                .statusCode(400)
                .body(containsString("insufficient funds"));

        // make sure no money was transferred
        when()
                .get("/accounts/GB000000002")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("2.00")));

        when()
                .get("/accounts/GB000000100")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("100")));

    }

    private void createMockAccount(String accNum, BigInteger balance, Currency currency) {
        app.require(AccountRepository.class).save(new Account(accNum, currency, balance));
    }


}