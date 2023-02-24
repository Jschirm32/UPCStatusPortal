package com.astralbrands.upc.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailData {

    private String subject;
    private String emailBody;
}
