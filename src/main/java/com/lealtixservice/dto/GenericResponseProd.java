package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponseProd {
    private int code;
    private String message;
    private Object object;
    private int totalRecords;
}

