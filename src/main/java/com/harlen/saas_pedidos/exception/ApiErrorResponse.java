package com.harlen.saas_pedidos.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    List<String> messages
) {
}
