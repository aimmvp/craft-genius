package org.beatific.bot.selftest.tester;

import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.parser.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;

public class NaverLogin {

    @Test
    public void login() {
        try {
            loginToNaver("beatificx", "Ro");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginToNaver(String username, String password) throws Exception {
        try (final WebClient webClient = new WebClient()) {
            // 네이버 로그인 페이지로 이동
            webClient.setHTMLParserListener(HTMLParserListener.LOG_REPORTER);
            webClient.setJavaScriptEngine(new JavaScriptEngine(webClient));
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getCache().setMaxSize(0);
            webClient.getOptions().setRedirectEnabled(true);
            HtmlPage page = webClient.getPage("https://nid.naver.com/nidlogin.login");

            
            // ID와 Password 입력
            HtmlForm form= page.getFormByName("frmNIDLogin");
            HtmlTextInput idInput = form.getInputByName("id");
            HtmlPasswordInput pwInput = (HtmlPasswordInput)form.getInputByName("pw");
            idInput.type(username);
            pwInput.type(password);

            // 로그인 버튼 클릭
            HtmlButton submitButton =  form.getFirstByXPath("//button[@type='submit']");
            submitButton.click();
            HtmlPage nextPage = (HtmlPage)submitButton.click();

            // 로그인 후 페이지의 내용을 출력
            System.out.println(nextPage.asNormalizedText());

            // 로그인 후 필요한 작업을 수행하면 됩니다.
        }
    }
}