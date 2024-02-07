package com.unir.plataformas.petclinic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.Select;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VetsPageTest {

    private static final PodamFactory PODAM = new PodamFactoryImpl();
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        this.driver = new ChromeDriver();
    }

    @AfterEach
    void cleanUp() {
        this.driver.close();
    }


    @Test
    public void openVetPage_test() {
        driver.get(TestConstants.VET_PATH);

        final String title = driver.getTitle();

        assertThat(title).isEqualTo("SpringPetclinicAngular");
    }

    @Test
    public void listVets_test() {
        driver.get(TestConstants.VET_PATH);

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("Veterinarians");

        final List<WebElement> tableRows = driver.findElements(By.xpath("//*[@id=\"vets\"]/thead/tr/th"));
        final List<WebElement> tableColumns = driver.findElements(By.xpath("//*[@id=\"vets\"]/tbody/tr"));


        assertThat(tableRows).hasSize(4);
        assertThat(tableColumns).isNotEmpty();
    }

    @Test
    public void createVet_test() {
        driver.get(TestConstants.VET_CREATE_PATH);
        final String firstname = "Firstname";
        final String lastName = "Lastname";
        final TestConstants.Specialties specialty = PODAM.manufacturePojo(TestConstants.Specialties.class);

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("New Veterinarian");

        // New vet form
        driver.findElement(By.id("firstName")).sendKeys(firstname);
        driver.findElement(By.id("lastName")).sendKeys(lastName);
        final Select selectedSpeciality = new Select(driver.findElement(By.id("specialties")));
        selectedSpeciality.selectByVisibleText(specialty.name().toLowerCase());

        final WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        driver.get(TestConstants.VET_PATH);
        final WebElement fullName = driver.findElement(By.xpath("//*[@id=\"vets\"]/tbody/tr[last()]/td[1]"));
        final WebElement vetSpeciality = driver.findElement(By.xpath("//*[@id=\"vets\"]/tbody/tr[last()]/td[2]"));


        assertThat(fullName.getText()).isEqualTo(String.format("%s %s", firstname, lastName));
        assertThat(vetSpeciality.getText()).isEqualTo(specialty.name().toLowerCase());
    }

    @Test
    public void createVet_requiredFieldsMissing_test() {
        driver.get(TestConstants.VET_CREATE_PATH);

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("New Veterinarian");

        final WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        final WebElement firstNameRequired = driver.findElement(By.cssSelector("#vet > div:nth-child(2) > div > span.help-block"));
        final WebElement lastNameRequired = driver.findElement(By.cssSelector("#vet > div:nth-child(3) > div > span.help-block"));

        assertThat(firstNameRequired.getText()).isEqualTo("First name is required");
        assertThat(lastNameRequired.getText()).isEqualTo("Last name is required");
    }

    @Test
    public void createVet_invalidFieldLength_test() {
        driver.get(TestConstants.VET_CREATE_PATH);

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("New Veterinarian");

        // New vet form
        driver.findElement(By.id("firstName")).sendKeys("t");
        driver.findElement(By.id("lastName")).sendKeys("t");

        final WebElement firstNameLength = driver.findElement(By.cssSelector("#vet > div:nth-child(2) > div > span.help-block"));
        final WebElement lastNameLength = driver.findElement(By.cssSelector("#vet > div:nth-child(3) > div > span.help-block"));

        assertThat(firstNameLength.getText()).isEqualTo("First name must be at least 2 characters long");
        assertThat(lastNameLength.getText()).isEqualTo("Last name must be at least 2 characters long");
    }

    @Test
    public void createVet_invalidData_test() {
        driver.get(TestConstants.VET_CREATE_PATH);
        final String firstname = "Not valid_";
        final String lastName = "Not valida %";
        final TestConstants.Specialties specialty = PODAM.manufacturePojo(TestConstants.Specialties.class);

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("New Veterinarian");

        // New vet form
        driver.findElement(By.id("firstName")).sendKeys(firstname);
        driver.findElement(By.id("lastName")).sendKeys(lastName);
        final Select selectedSpeciality = new Select(driver.findElement(By.id("specialties")));
        selectedSpeciality.selectByVisibleText(specialty.name().toLowerCase());

        final WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Delay
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        List<LogEntry> logs = driver.manage().logs().get(LogType.BROWSER).getAll();

        assertThat(logs).extracting(LogEntry::getMessage).anyMatch(message -> message.contains("addVet failed: must match"));
        assertThat(driver.getCurrentUrl()).isEqualTo(TestConstants.VET_CREATE_PATH);
    }

    @Test
    public void deleteVet_test() {
        driver.get(TestConstants.VET_PATH);

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("Veterinarians");

        final String fullName = driver.findElement(By.xpath("//*[@id=\"vets\"]/tbody/tr[last()]/td[1]")).getText();
        final int initialNumOfVets = driver.findElements(By.xpath("//*[@id=\"vets\"]/tbody/tr")).size();

        final WebElement deleteButton = driver.findElement(By.xpath("//*[@id=\"vets\"]/tbody/tr[last()]/td[3]/button[2]"));
        deleteButton.click();

        // Delay until action is completed
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        final int newNumOfVets = driver.findElements(By.xpath("//*[@id=\"vets\"]/tbody/tr")).size();
        final String lastVetFullName = driver.findElement(By.xpath("//*[@id=\"vets\"]/tbody/tr[last()]/td[1]")).getText();

        // We deleted the last vet, after deletion last vet shouldn´t be the same
        assertThat(fullName).isNotEqualTo(lastVetFullName);
        assertThat(newNumOfVets).isEqualTo(initialNumOfVets - 1);
    }

    @Test
    public void updateVet_test() {

        final String randomName = PODAM.manufacturePojo(String.class).replaceAll("[^a-zA-Z]+", "");
        driver.get(TestConstants.VET_PATH);

        final String originalName = Arrays.stream(driver
                .findElement(By.cssSelector("#vets > tbody > tr:nth-child(1) > td:nth-child(1)"))
                .getText().split(" ")).toList().get(0);

        final WebElement editButton = driver.findElement(By.cssSelector("#vets > tbody > tr:nth-child(1) > td:nth-child(3) > button:nth-child(1)"));
        editButton.click();

        assertThat(driver.getCurrentUrl()).isEqualTo(String.format(TestConstants.VET_EDIT_PATH, "1"));

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("Edit Veterinarian");

        driver.findElement(By.id("firstName")).clear();
        driver.findElement(By.id("firstName")).sendKeys(randomName);

        final WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Delay until action is completed
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        final String newName = Arrays.stream(driver
                .findElement(By.cssSelector("#vets > tbody > tr:nth-child(1) > td:nth-child(1)"))
                .getText().split(" ")).toList().get(0);

        assertThat(originalName).isNotEqualTo(newName);
        assertThat(newName).isEqualTo(randomName);
    }

    @Test
    public void updateVet_missingRequiredField_test() {

        driver.get(String.format(TestConstants.VET_EDIT_PATH, "1"));

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("Edit Veterinarian");

        driver.findElement(By.id("firstName")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));

        final String errorMessage = driver.findElement(By.cssSelector("#vet_form > div.form-group.has-feedback.has-error > div > span.help-block.ng-star-inserted")).getText();
        final WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        final String buttonStatus = sendButton.getAttribute("disabled");

        assertThat(errorMessage).isEqualTo("First name is required");
        assertThat(buttonStatus).isEqualTo("true");
    }

    private WebElement getPageTitle() {
        return driver.findElement(By.tagName("h2"));
    }

}
