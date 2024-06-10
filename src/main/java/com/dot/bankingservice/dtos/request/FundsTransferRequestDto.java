package com.dot.bankingservice.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundsTransferRequestDto {

    @Pattern(regexp = "\\d{10}", message = "Source Account must be 10 digit")
    private String sourceAccountNumber;

    @Pattern(regexp = "\\d{10}", message = "Beneficiary Account must be 10 digit")
    private String beneficiaryAccountNumber;

    @DecimalMin(value = "100", message = "Amount must be at least 100")
    private BigDecimal amount;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 5, max = 20, message = "Description must be between 5 and 20 characters")
    private String description;
}
