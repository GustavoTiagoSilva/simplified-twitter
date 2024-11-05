package com.demo.simplified_twitter.dto;

import java.time.Instant;

public record HttpErrorResponseDto(Instant timestamp,
                                   Integer httpStatus,
                                   String error,
                                   String errorMessage,
                                   String path) {
}
