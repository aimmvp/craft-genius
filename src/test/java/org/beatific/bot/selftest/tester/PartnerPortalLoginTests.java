package org.beatific.bot.selftest.tester;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.parser.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PartnerPortalLoginTests {
    
    @Test
    public void login() {
        try(WebClient webClient = new WebClient()) {
            webClient.setHTMLParserListener(HTMLParserListener.LOG_REPORTER);
            webClient.setJavaScriptEngine(new JavaScriptEngine(webClient));
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getCache().setMaxSize(0);
            webClient.getOptions().setRedirectEnabled(true);

            try {
                HtmlPage page = webClient.getPage("https://partnersso.sktelecom.com/swing/skt/login.html");
                
                List<FrameWindow> frames = page.getFrames();
                HtmlPage formpage = null;
                for (FrameWindow frame : frames) {
                    log.error(frame.getFrameElement().getId());
                    if ("loginFormFrame".equals(frame.getFrameElement().getId())) {
                        formpage = (HtmlPage) frame.getEnclosedPage();
                    }
                }

                // ID와 Password 입력
                HtmlForm form= formpage.getFormByName("sso");
                HtmlTextInput idInput = form.getInputByName("USER");
                HtmlPasswordInput pwInput = (HtmlPasswordInput)form.getInputByName("PASSWORD");
                idInput.type("UK573");
                pwInput.type("Q!w2e3r4t5y6");

                HtmlAnchor submitButton =  form.getFirstByXPath("//a[@class='submit']");
                // submitButton.click();
                HtmlPage nextPage = (HtmlPage)submitButton.click();
                webClient.waitForBackgroundJavaScript(1000000);
                webClient.getCookies(new URL("https://partnersso.sktelecom.com")).forEach(cookie -> log.error("{}, {}}", cookie.getName(), cookie.getValue()));
            
                HtmlPage page2 = webClient.getPage(new URL("https://swgresearch.sktelecom.com/swp/ms.do?qstnId="));

                log.error(page2.asXml());
            } catch (FailingHttpStatusCodeException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
