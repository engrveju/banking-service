package com.dot.bankingservice.models;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String accountNumber;

    private BigDecimal accountBalance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDetails that = (AccountDetails) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(accountBalance, that.accountBalance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, accountNumber, accountBalance);
    }
}
