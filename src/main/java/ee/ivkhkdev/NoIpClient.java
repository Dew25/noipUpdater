package ee.ivkhkdev;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

public class NoIpClient {
    private String username;
    private String mailPasswordField="";

    private WebDriver driver;

    public NoIpClient() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverPath;

        if (os.contains("win")) {
            driverPath = "chromedriver.exe"; // Путь для Windows
        } else if (os.contains("mac")) {
            driverPath = "/usr/local/bin/chromedriver"; // Путь для macOS
        } else if (os.contains("nix") || os.contains("nux")) {
            driverPath = "/usr/bin/chromedriver"; // Путь для Linux
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }

        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        // Устанавливаем неявное ожидание в 3 секунды
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    public boolean login(String username, String password) {
        driver.get("https://noip.com/login");
        WebElement usernameField = driver.findElement(By.xpath("//*[@id=\"username\"]"));
        this.username = usernameField.getText();
        WebElement passwordField = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
        String url = driver.getCurrentUrl();
        if(driver.getCurrentUrl().contains("verify")){
            verify(driver);
        }
        return driver.getCurrentUrl().contains("my"); // Проверка успешного входа
    }

    private void verify(WebDriver driver) {
        WebDriver driverGmail = new ChromeDriver();
        driverGmail.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebElement emailFieldLogin = driverGmail.findElement(By.xpath("//*[@id=\"identifierId\"]"));
        emailFieldLogin.sendKeys(username);
        WebElement nextButton = driverGmail.findElement(By.xpath("//*[@id=\"identifierNext\"]/div/button"));
        nextButton.click();
        WebElement emailFieldPassword = driverGmail.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input"));
        emailFieldPassword.sendKeys(mailPasswordField);
        emailFieldPassword.clear();
        WebElement emailsTable = driverGmail.findElement(By.xpath("//*[@id=\":1n\"]"));
        List<WebElement> noIpMail = emailsTable.findElements(By.tagName("tr"));
        noIpMail.get(0).click();
        WebElement verifyNumber = driverGmail.findElement(By.xpath("//*[@id=\":h2\"]/div[2]/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/p[2]/strong"));
        String verifyNumberStr = verifyNumber.getText();


    }

    public boolean confirmService() {
        try {
            WebElement dinamicDNS = driver.findElement(By.xpath("//*[@id=\"main-menu-inner\"]/ul/li[2]/a"));
            dinamicDNS.click();
            WebElement noIpHostnames = driver.findElement(By.xpath("//*[@id=\"main-menu-inner\"]/ul/li[2]/ul/li[1]/a"));
            noIpHostnames.click();
            WebElement confirm = driver.findElement(By.xpath("//*[@id=\"host-panel\"]/table/tbody/tr/td[6]/button[1]"));
            confirm.click();
            try{
                driver.findElement(By.xpath("//*[@id=\"host-panel\"]/table/tbody/tr/td[6]/button[1]"));
            }catch (Exception e){
                System.out.println("Продление доменного имени успешно произведено");
            }
            return true; // Успешное нажатие на кнопку
        } catch (Exception e) {
            return false; // Ошибка при нажатии
        }
    }

    public void close() {
        driver.quit();
    }

    public static void main(String[] args) {
        NoIpClient client = new NoIpClient();
        try {
            if (client.login("", "")) {
                System.out.println("Login successful.");
                if (client.confirmService()) {
                    System.out.println("Domain confirmed.");
                } else {
                    System.out.println("Domain confirmation failed.");
                }
            } else {
                System.out.println("Login failed.");
            }
        } finally {
            client.close();
        }
    }
}
