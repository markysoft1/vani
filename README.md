# Vani
*Vani* should help to write and maintain UI tests based on [Selenium](http://seleniumhq.org/). To reach this aim, it provides an extensions for [Spring-Framework](https://spring.io/), so you can write your tests in spring-style.

*Vani* is written in Java and can only used in conjunction with the java version of Selenium.

**The framework is still under development**

## Features
- auto instantiating and injecting of *WebElement* and *PageObject* of annotated fields ([Details](https://github.com/markysoft1/vani/wiki/Element-Locating)
	- supports all *FindBy*-annotations ([Details](https://github.com/markysoft1/vani/wiki/Element-Locating#selenium-selectors))
	- you can also use the power of *[JQuery](https://jquery.com/)*'s selectors by annotating with *@FindByJQuery* ([Details](https://github.com/markysoft1/vani/wiki/Element-Locating#jquery-selectors))
	- injected instances are proxied (except page and fragment objects). So locating of annotated elements will be executing during a call on it.
- introduce *FragmentObject*, so you can declare reusable parts or reduce complexity of *PageObject*s by extracting code in fragments ([Details](https://github.com/markysoft1/vani/wiki/Page-Object))
- all annotations supports spring placeholders (also all Selenium's *@FindBy*)
- declaring a startpage which is automatically opened before your test method is called ([Details](https://github.com/markysoft1/vani/wiki/Page-Object#startpage))
- explicit waits for completing jQuery's ajax requests by *@Xhr*
- conditional selection of fragment implementations by *@ContentCondition* ([Details](https://github.com/markysoft1/vani/wiki/Page-Object#conditional-fragments))
- provides a page crawling mechanism which opens specific links on testing pages ([Details](https://github.com/markysoft1/vani/wiki/Page-Crawling-Mechanism))
- convenience interface for injecting and executing javascript source code into testing pages

## Requirements
- [Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Spring-Framework](https://spring.io/) (current tested version: 4.2.4)
- [Selenium Java](http://seleniumhq.org/) (current tested version 2.52.0)

## Other Dependencies
- [ByteBuddy](http://bytebuddy.net/)
- [Reflections](https://github.com/ronmamo/reflections)


## Usage
Example for declaring a test class:
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/config/test-context.xml" })
public class LoginTest{
	@Page
	@Startpage
	private HomePage homePage;
	
	@Test
	public void login() {
		homePage.login("hello","world");
	}
}
```

After declaring a normal Junit test class with spring, it defines a page object as startpage. So *Vani* will instantiate and inject an instance of `HomePage` and opens it in default `WebDriver` instance.

The test method will only call a method of the injected page object.

In your context-configuration xml (`test-context.xml` in the above example) you only have to import the spring default configuration xml of *Vani*:
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd">

	<import resource="classpath:org/vani/spring/vani-context.xml" />
</beans>
```
This will setup the default configuration of *Vani*. So you don't have to declare all required beans manual.

At the end, we declare the page object:
```java
import org.vani.core.locating.PageObject;
import org.vani.core.locating.locator.FindByJQuery;

@PageUrl("${config.blogUrl}")
public class HomePage extends PageObject{
	@FindByJQuery("${content.jq.login.username}")
	private JQueryElement inputUsername;
	@FindByJQuery("${content.jq.login.password}")
	private JQueryElement inputPassword;
	@FindByJQuery("${content.jq.loginLink}")
	private JQueryElement loginLink;
	
	public void login(String username,String password) {
		inputUsername.sendKeys(username);
		inputPassword.sendKeys(password);
		loginLink.click();
		
		//this will mark current page object as invalid, so all cached elements will be relocated during next access
		this.invalidate();
	}
}
```
The `@PageUrl` annotation will be used to declare the url of the page object. **It's important that you extends your `PageObject`-classes from the *Vani*'s `PageObject` and not from the *Selenium*'s one.** 
At the next lines, we declare the required fields by jquery selectors. 

The method will set the specified values of located input fields and click the login link. The locating of each `JQueryElement` will be executed during access on its wrapped html element. This provide you the possibility, to get an instance of your page object although its desired html elements are still unavailable. Finally, the current instance will be marked as invalid, so all cached elements are relocated at the next access. This is useful if you use `@ContentCondition` for selecting specific fragment implemenation.

