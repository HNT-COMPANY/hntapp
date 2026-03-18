package com.hnt.hntapp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ApiResponse<T> {

    // 성공 여부 ( true: 성공 / false : 실패
    private boolean success;

    // 응답 메세지
    private String message;

    // 응답 데이터 ( 제네릭 타입 )
    private T data;

    /** 성공 응답 메세지 */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /** 실패 응답 생성 */
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }

}
