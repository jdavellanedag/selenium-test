package com.unir.plataformas.petclinic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.Duration;
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
        final String firstname = PODAM.manufacturePojo(String.class); // TODO: Generate valid random name
        final String lastName = PODAM.manufacturePojo(String.class); // TODO: Generate valid random lastName
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
    public void createVet_invalidData_test() {
        driver.get(TestConstants.VET_CREATE_PATH);
        final String firstname = PODAM.manufacturePojo(String.class);
        final String lastName = PODAM.manufacturePojo(String.class);
        final TestConstants.Specialties specialty = PODAM.manufacturePojo(TestConstants.Specialties.class);

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("New Veterinarian");

        // New vet form
        driver.findElement(By.id("firstName")).sendKeys(firstname);
        driver.findElement(By.id("lastName")).sendKeys(lastName);
        final Select selectedSpeciality = new Select(driver.findElement(By.id("specialties")));
        selectedSpeciality.selectByVisibleText(specialty.name().toLowerCase());

        final WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        // TODO: Validate invalid data
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
        driver.get(String.format(TestConstants.VET_EDIT_PATH, "1"));

        final WebElement pageTitle = getPageTitle();
        assertThat(pageTitle.getText()).isEqualTo("Edit Veterinarian");
        // TODO: Everything :)
    }

    private WebElement getPageTitle() {
        return driver.findElement(By.tagName("h2"));
    }

}
