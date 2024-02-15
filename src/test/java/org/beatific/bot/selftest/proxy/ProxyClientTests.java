package org.beatific.bot.selftest.proxy;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyClientTests {
    
    private ProxyClient client;

    @Test
    public void get() {

        log.error("start proxyclient test");
        // ProxyClient proxy = new ProxyClient("34.160.111.145", "http", 80, "http://ifconfig.me", 1000, 10000);
        // PlainClient proxy = new PlainClient("34.160.111.145", "http", 80, "http://ifconfig.me", 1000, 10000);
        ProxyClient proxy = new ProxyClient("34.160.111.145", "https", 443, "https://ifconfig.me", 1000, 10000);
        // ProxyClient proxy = new ProxyClient("223.130.195.95", "https", 443, "https://www.naver.com", 1000, 10000);
        // ProxyClient proxy = new ProxyClient("ifconfig.me", "https", 443, "https://ifconfig.me", 1000, 10000);
        // System.setProperty("http.proxyHost", "proxy.11st.com");
        // System.setProperty("http.proxyPort", "1111");
        proxy.get("/", str -> log.error("result : {} ", str));
    }vscode-file://vscode-app/Applications/Visual%20Studio%20Code.app/Contents/Resources/app/out/vs/code/electron-sandbox/workbench/workbench.html
}
