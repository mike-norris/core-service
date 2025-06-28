package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TicketBillingDetails {
    @JsonProperty("output_transaction")
    String outputTransaction;
    @JsonProperty("output_reference_id")
    String outputReferenceId;
    BigDecimal amount;

    public String getOutputTransaction() {
        return outputTransaction;
    }

    public void setOutputTransaction(String outputTransaction) {
        this.outputTransaction = outputTransaction;
    }

    public String getOutputReferenceId() {
        return outputReferenceId;
    }

    public void setOutputReferenceId(String outputReferenceId) {
        this.outputReferenceId = outputReferenceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
