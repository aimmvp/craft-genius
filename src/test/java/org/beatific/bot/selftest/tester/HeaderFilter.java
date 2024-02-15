package org.beatific.bot.selftest.tester;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HeaderFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

                String ngmNum = HeaderUtils.getHeader("ngmNum");
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
	}

    public class HeaderUtils {

        public static String getHeader(String key) {
            return "testNumber";
        }
    }
    
}
