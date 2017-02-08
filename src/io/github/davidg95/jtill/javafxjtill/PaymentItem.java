/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *
 * @author David
 */
public class PaymentItem implements Serializable, Cloneable {

    private final PaymentType type;
    private final BigDecimal value;

    public enum PaymentType {
        CASH, CARD, CHEQUE, ACCOUNT,
    }

    public PaymentItem(PaymentType type, BigDecimal value) {
        this.type = type;
        this.value = value;
    }

    public PaymentType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public PaymentItem clone() {
        try {
            final PaymentItem result = (PaymentItem) super.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        DecimalFormat df;
        if (value.compareTo(BigDecimal.ZERO) > 1) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("0.00");
        }
        return this.type.toString() + " - £" + df.format(value);
    }
}
