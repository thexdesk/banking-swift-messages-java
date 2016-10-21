package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftDecimalFormatter;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.subfield.DebitCreditMark;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.List;

/**
 * <b>Forward Available Balance</b>
 * <p>
 * <b>Field Tag</b> :65:
 * <p>
 * <b>Format</b> 1!a6!n3!a15d
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 1!a - Debit/Credit Mark - 'D' = Debit, 'C' Credit
 * 2: 6!n - Entry date - Format 'YYMMDD'
 * 3: 3!a - Currency - Three Digit Code
 * 4: 15d - Amount
 * </pre>
 */
public class ForwardAvailableBalance implements SwiftField {

    public static final String FIELD_TAG_65 = "65";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("1!a6!n3!a15d");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyMMdd");

    private final DebitCreditMark debitCreditMark;

    private final LocalDate entryDate;

    private final BigMoney amount;


    public ForwardAvailableBalance( LocalDate entryDate, DebitCreditMark debitCreditMark, BigMoney amount) {

        Preconditions.checkArgument(debitCreditMark != null, "debitCreditMark can't be null");
        Preconditions.checkArgument(entryDate != null, "entryDate can't be null");
        Preconditions.checkArgument(amount != null, "amount can't be null");
        Preconditions.checkArgument(amount.isPositiveOrZero(), "amount can't be negative");

        this.debitCreditMark = debitCreditMark;
        this.entryDate = entryDate;
        this.amount = amount;
    }

    public static ForwardAvailableBalance of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_65), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        DebitCreditMark debitCreditMark = DebitCreditMark.ofFieldValue(subFields.get(0));
        LocalDate entryDate = LocalDate.parse(subFields.get(1), DATE_FORMATTER);
        CurrencyUnit amountCurrency = CurrencyUnit.of(subFields.get(2));
        BigDecimal amountValue = SwiftDecimalFormatter.parse(subFields.get(3));

        BigMoney amount = BigMoney.of(amountCurrency, amountValue);

        return new ForwardAvailableBalance(entryDate, debitCreditMark, amount);
    }

    public DebitCreditMark getDebitCreditMark() {
        return debitCreditMark;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public BigMoney getSignedAmount() {
        if(getDebitCreditMark().sign() < 0) {
            return amount.negated();
        }
        return amount;
    }

    @Override
    public String getTag() {
        return FIELD_TAG_65;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(

                    debitCreditMark.toFieldValue(),
                    DATE_FORMATTER.print(entryDate),
                    amount.getCurrencyUnit().getCode(),
                    SwiftDecimalFormatter.format(amount.getAmount())
            ));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }
}
