package com.kala.military.adapters.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Covers the defensive path of the handler that the HTTP slice cannot reach: a constraint violation
 * carrying no default message.
 */
final class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldFallBackToTheFieldNameWhenTheViolationHasNoMessage() throws NoSuchMethodException {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "trainUnitRequest");
        bindingResult.addError(new FieldError("trainUnitRequest", "unitType", (String) null));
        var parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("target", String.class), 0);

        var response = handler.handleValidationException(new MethodArgumentNotValidException(parameter, bindingResult));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("unitType", response.getBody().get("message"));
    }

    @Test
    void shouldJoinEveryViolationMessage() throws NoSuchMethodException {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "transformUnitRequest");
        bindingResult.addError(new FieldError("transformUnitRequest", "sourceType", "origen obligatorio"));
        bindingResult.addError(new FieldError("transformUnitRequest", "targetType", "destino obligatorio"));
        var parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("target", String.class), 0);

        var response = handler.handleValidationException(new MethodArgumentNotValidException(parameter, bindingResult));

        assertNotNull(response.getBody());
        assertEquals("origen obligatorio; destino obligatorio", response.getBody().get("message"));
    }

    private void target(String unitType) {
    }
}
