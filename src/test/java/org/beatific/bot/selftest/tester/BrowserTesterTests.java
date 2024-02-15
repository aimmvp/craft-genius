package org.beatific.bot.selftest.tester;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.parser.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.google.common.collect.Iterables;

public class BrowserTesterTests {

    BrowserTester tester = new BrowserTester();

    @Test
    public void testNaverLogin() {
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.setHTMLParserListener(HTMLParserListener.LOG_REPORTER);
        client.setJavaScriptEngine(new JavaScriptEngine(client));
        client.getOptions().setJavaScriptEnabled(true);
        client.getCookieManager().setCookiesEnabled(true);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.getCache().setMaxSize(0);
        client.getOptions().setRedirectEnabled(true);

        String url = "https://nid.naver.com/nidlogin.login?mode=form&url=https://www.naver.com/";
        HtmlPage loginPage;
        try {
            loginPage = client.getPage(url);
        } catch (FailingHttpStatusCodeException | IOException e) {
            e.printStackTrace();
        }
        client.waitForBackgroundJavaScript(1000000);

        HtmlForm loginForm = loginPage.getFormByName("frmNIDLogin");
        HtmlTextInput inputId = loginForm.getInputByName("id");
        HtmlPasswordInp
        
        
    }
}
