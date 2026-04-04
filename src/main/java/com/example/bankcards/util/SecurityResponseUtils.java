package com.example.bankcards.util;

import com.example.bankcards.constants.SecurityConstants;
import com.example.bankcards.dto.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.io.IOException;

@UtilityClass
public class SecurityResponseUtils {

    public static void sendError(HttpServletResponse httpResponse,
                                 ObjectMapper objectMapper,
                                 HttpStatus httpStatus,
                                 String message) throws IOException {

        httpResponse.setStatus(httpStatus.value());
        httpResponse.setContentType(SecurityConstants.CONTENT_TYPE_JSON);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, message);
        objectMapper.writeValue(httpResponse.getWriter(), CommonResponse.error(problemDetail));

    }

}
