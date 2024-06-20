create table if not exists PUBLIC.ACCOUNT_DETAILS
(
    ID              BIGINT auto_increment
        primary key,
    ACCOUNT_BALANCE NUMERIC(38, 2),
    ACCOUNT_NUMBER  CHARACTER VARYING(255)
        constraint UK_LI8FUYC7AW8ASGU38LRE87RV5
            unique,
    FIRST_NAME      CHARACTER VARYING(255),
    LAST_NAME       CHARACTER VARYING(255)
);



create table if not exists PUBLIC.TRANSACTION_SUMMARY
(
    ID               BIGINT auto_increment
        primary key,
    CREATED_AT       TIMESTAMP,
    TOTAL_COMMISSION NUMERIC(38, 2),
    TOTAL_CREDIT     NUMERIC(38, 2),
    TOTAL_DEBIT      NUMERIC(38, 2),
    TOTAL_FEES       NUMERIC(38, 2),
    UPDATED_AT       TIMESTAMP
);

create table if not exists PUBLIC.TRANSACTIONS
(
    ID                         BIGINT auto_increment
        primary key,
    ACCOUNT_NUMBER             CHARACTER VARYING(255),
    AMOUNT                     NUMERIC(38, 2),
    BALANCE_AFTER_TRANSACTION  NUMERIC(38, 2),
    BALANCE_BEFORE_TRANSACTION NUMERIC(38, 2),
    BILLED_AMOUNT              NUMERIC(38, 2),
    COMMISSION                 NUMERIC(38, 2),
    COMMISSION_WORTHY          BOOLEAN not null,
    CREATED_AT                 TIMESTAMP,
    DESCRIPTION                CHARACTER VARYING(255),
    RECEIVER                   CHARACTER VARYING(255),
    SENDER                     CHARACTER VARYING(255),
    STATUS_MESSAGE             CHARACTER VARYING(255),
    TRANSACTION_FEE            NUMERIC(38, 2),
    TRANSACTION_REFERENCE      CHARACTER VARYING(255),
    TRANSACTION_STATUS         CHARACTER VARYING(255),
    TRANSACTION_TYPE           CHARACTER VARYING(255),
    UPDATED_AT                 TIMESTAMP,
    check ("TRANSACTION_STATUS" IN ('SUCCESSFUL', 'INSUFFICIENT_FUNDS', 'FAILED', 'PROCESSING')),
    check ("TRANSACTION_TYPE" IN ('DR', 'CR'))
);




