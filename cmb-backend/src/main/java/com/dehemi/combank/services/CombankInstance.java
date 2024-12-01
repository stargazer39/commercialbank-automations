package com.dehemi.combank.services;

import com.dehemi.combank.config.TimeoutConfig;
import com.dehemi.combank.dao.Account;
import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.exceptions.WaitForDownloadFileException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v127.network.Network;
import org.openqa.selenium.devtools.v127.network.model.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CombankInstance {
    final ChromeDriver driver;
    @Getter
    final User user;

    final TimeoutConfig timeoutConfig;

    public CombankInstance(User user, ChromeDriver driver, TimeoutConfig timeoutConfig) {
        this.driver = driver;
        this.user = user;
        this.timeoutConfig = timeoutConfig;
    }

    public void init() throws InterruptedException {
        driver.get("https://www.combankdigital.com/#/login");
        driver.navigate().refresh();
    }

    public void login(){
        log.info("starting login step");
        driver.get("https://www.combankdigital.com/#/login");
        // wait for loading element to disappear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder=\"Username\"]")));
        driver.findElement(By.cssSelector("input[placeholder=\"Username\"]")).sendKeys(user.getCombank().getUsername());
        driver.findElement(By.cssSelector("input[type=\"submit\"][value=\"Continue\"]")).click();
        this.waitForLoading();

        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder=\"Password\"]")));

        driver.findElement(By.cssSelector("input[placeholder=\"Password\"]")).sendKeys(user.getCombank().getPassword());
        driver.findElement(By.cssSelector("input[type=\"submit\"][value=\"Login\"]")).click();

        this.waitForLoading();

        WebDriverWait wait3 = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait3.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id=\"accounts-header\"]/h2/span[1]"), "My Accounts"));

       try {
           By errorModalSelector = By.className("ui-dialog");
           WebElement elements = driver.findElement(errorModalSelector);
           elements.findElement(By.cssSelector("a.close-dialog")).click();
       } catch (NoSuchElementException e) {
           log.info("no message this time");
       }

        String welcomeMessage = driver.findElement(By.cssSelector(".welcome-message")).getText();
        log.info("welcome message: " + welcomeMessage);
    }

    public List<Account> getAccounts(){
        this.goToMyPortfolio();
        List<Account> accounts = new ArrayList<>();

        WebElement savingsElement = this.driver.findElement(By.cssSelector("div[ng-if=\"displayAccountGroupType(accountGroup, 'savings')\"]"));
        WebElement creditCardElements = this.driver.findElement(By.cssSelector("div[ng-if=\"displayAccountGroupType(accountGroup, 'credit-card')\"]"));

        List<WebElement> savingsAccountElements = savingsElement.findElements(By.cssSelector(".savings"));
        List<WebElement> creditCardElementsElements = creditCardElements.findElements(By.cssSelector(".credit-card"));
        savingsAccountElements.addAll(creditCardElementsElements);

        for(WebElement accountElement : savingsAccountElements){
            List<WebElement> spans = accountElement.findElements(By.cssSelector("span"));
            String accountName = spans.get(0).getText();
            String accountType = spans.get(1).getText();
            String aClass = accountElement.getAttribute("class");

            if(aClass.contains("savings")) {
                accountType =  "Savings";
            } else if(aClass.contains("credit-card")) {
                accountType =  "CreditCard";
            }

            By totalSelector = By.xpath("//span[contains(@class,'current total')]");
            By availableTotalSelector = By.xpath("//span[contains(@class,'available total')]");

            WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait2.until(ExpectedConditions.visibilityOfElementLocated(totalSelector));
            wait2.until(ExpectedConditions.visibilityOfElementLocated(availableTotalSelector));

            String total = accountElement.findElement(totalSelector).getText();
            String availableTotal = accountElement.findElement(availableTotalSelector).getText();

            log.info("found account {} {}", accountName, accountType);
            Account account = new Account();
            account.setAccountNumber(accountName);
            account.setAccountType(accountType);
            account.setCurrentTotal(total);
            account.setAvailableTotal(availableTotal);

            accounts.add(account);
        }

        return accounts;
    }

    public void goToMyPortfolio() {
        this.waitForLoading();
        driver.findElement(By.cssSelector(".responsive-menu .menu-icon")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        By myMoneySelector = By.xpath("//a[normalize-space()='My Money']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(myMoneySelector));
        driver.findElement(myMoneySelector).click();

        By myPortfolio = By.xpath("//a[normalize-space()='My Portfolio']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(myPortfolio));
        driver.findElement(myPortfolio).click();

        this.waitForLoading();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//span[normalize-space()='My Accounts']"), "My Accounts"));
    }

    private void waitForLoading(){
        WebDriverWait waitModal = new WebDriverWait(driver, Duration.ofSeconds(30));
        waitModal.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal-overlay")));
    }

    public synchronized List<Transaction> getTransactions(Account account, boolean maximum) throws InterruptedException, CsvValidationException, IOException, CSVProcessException {
        this.goToMyPortfolio();
        this.retryIfStale(() -> {
            this.waitForLoading();
            By accountElementLocator = By.xpath("//span[normalize-space()='"+ account.getAccountNumber() +"']");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(accountElementLocator));
            driver.findElement(accountElementLocator).click();
        });

        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[normalize-space()='Transactions']")));

        if(maximum) {
            // set date to year before
            driver.findElement(By.xpath("//a[@id='filters-trigger']")).click();
            this.waitForLoading();
            driver.findElement(By.xpath("//input[@id='toDate']")).click();
            String toMonth = driver.findElement(By.xpath("//select[@aria-label='Select month']")).getAttribute("value");
            String toYear = driver.findElement(By.xpath("//select[@aria-label='Select year']")).getAttribute("value");
            String toDate = driver.findElement(By.cssSelector(".ui-state-default.ui-state-highlight.ui-state-active.ui-state-hover")).getAttribute("data-date");

            int lastYear = (Integer.parseInt(toYear) - 1);
            int lastMonth = (Integer.parseInt(toMonth) + 1);
            int nextDay = (Integer.parseInt(toDate) + 1);

            driver.findElement(By.xpath("//input[@id='fromDate']")).click();

//            driver.findElement(By.xpath("//table[@class='ui-datepicker-calendar']//option[@value='1']"));
            driver.executeScript("$(\"[aria-label='Select month']\")[0].value = " + toMonth);

            driver.executeScript("$(\"[aria-label='Select year']\")[0].value = " + lastYear);

            driver.findElement(By.xpath(String.format("//table[@class='ui-datepicker-calendar']//a[normalize-space()='%d']",nextDay))).click();
            driver.findElement(By.xpath("//input[@value='Apply Filters']")).click();
            this.waitForLoading();
        }

        DevTools devTools = driver.getDevTools();
        AtomicReference<String> encodedResponse = new AtomicReference<>();

        try {
            devTools.createSessionIfThereIsNotOne();
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            Semaphore sem = new Semaphore(1);
            sem.acquire();

            devTools.addListener(Network.responseReceived(), responseReceived -> {
                Response response = responseReceived.getResponse();
                if(response.getUrl().contains("downloadreport")) {
                    log.info("Intercepted Response URL: " + response.getUrl());

                    // Get the response body using the requestId
                    encodedResponse.set(devTools.send(Network.getResponseBody(responseReceived.getRequestId())).getBody());
                    log.info("Intercepted Response: " + encodedResponse.get().length());
                    sem.release();
//                    devTools.clearListeners();
                }
            });

            // Get transaction
            By accountElementLocator = By.xpath("//i[@class='csv-download-icon ']");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(accountElementLocator));
            driver.findElement(accountElementLocator).click();

            if(!sem.tryAcquire(timeoutConfig.getWaitForIntercept(), TimeUnit.SECONDS)) {
                throw new WaitForDownloadFileException("timeout while waiting for transactions");
            }
        } catch (Exception e) {
            devTools.clearListeners();
            throw new RuntimeException(e);
        }
        devTools.clearListeners();
        String csv = encodedResponse.get();

        if(csv.isEmpty()) {
            throw new RuntimeException("No response received for transactions");
        }

        return CSVProcessor.processTransactionToCSV(csv, account, this.getUser().getUsername());
    }

    private void retryIfStale(Runnable task){
        try {
            task.run();
        }catch (StaleElementReferenceException e){
            task.run();
        }
    }
}
