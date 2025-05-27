package org.example.grainoilsystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.grainoilsystem.entity.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InterceptorResponseUtil {
    public static void writeJson(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
} 