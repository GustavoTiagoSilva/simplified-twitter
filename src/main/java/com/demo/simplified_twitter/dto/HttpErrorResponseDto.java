package com.demo.simplified_twitter.dto;

import java.time.Instant;
import java.util.Objects;

public record HttpErrorResponseDto(Instant timestamp,
                                   Integer httpStatus,
                                   String error,
                                   String errorMessage,
                                   String path) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpErrorResponseDto that = (HttpErrorResponseDto) o;
        return Objects.equals(path, that.path) && Objects.equals(error, that.error) && Objects.equals(httpStatus, that.httpStatus) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpStatus, error, errorMessage, path);
    }
}
