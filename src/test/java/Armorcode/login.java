package Armorcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import baseclass.baseclass;



public class login extends baseclass
{
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD) //on method level
	public @interface TestRails {
	String id() default "none";
	}
	
	@TestRails(id = "1")
	@Test(priority = 1)
	public void Title_Check() 
	{
		String testcaseid="1";
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		WebElement text = driver.findElement(By.xpath("//div[@class='login-box']/h3"));
		WebElement text2 = driver.findElement(By.xpath("//div[@class='login-box']/div[.='Welcome! Please sign in to continue']"));
		String logintext = text.getText();
		Assert.assertEquals(logintext, "Login");
		String logintext1 = text2.getText();
		Assert.assertEquals(logintext1, "Welcome! Please sign in to continue");
	}
}


