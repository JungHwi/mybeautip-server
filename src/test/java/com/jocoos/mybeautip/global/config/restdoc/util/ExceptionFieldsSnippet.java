package com.jocoos.mybeautip.global.config.restdoc.util;

import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

public class ExceptionFieldsSnippet extends AbstractFieldsSnippet {

    public ExceptionFieldsSnippet(String name,
                                  FieldDescriptor descriptor) {

        super("exception_" + name, "exception", Collections.singletonList(descriptor), null, true);
    }

    @Override
    protected MediaType getContentType(Operation operation) {
        return operation.getResponse().getHeaders().getContentType();
    }

    @Override
    protected byte[] getContent(Operation operation) throws IOException {
        return operation.getResponse().getContent();
    }

    public static FieldDescriptor exceptionConvertFieldDescriptor(ResultActions result) throws UnsupportedEncodingException {
        return exceptionConvertFieldDescriptor(result, "");
    }

    public static FieldDescriptor exceptionConvertFieldDescriptor(ResultActions result, String description) throws UnsupportedEncodingException {
        Map<String, Object> map = StringConvertUtil.convertJsonToMapObject(result.andReturn().getResponse().getContentAsString());

        if (map == null) {
            return null;
        }
        ErrorCode errorCode = ErrorCode.of(map.get("error").toString());

        return fieldWithPath("error").attributes(
                key("errorCode").value(errorCode.getKey()),
                key("errorDescription").value(errorCode.getDescription()),
                key("description").value(description));
    }
}
