package com.qoomon.banking.swift.submessage.field;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;


public class TransactionGroup {

    /**
     * @see StatementLine#FIELD_TAG_61
     */
    private final StatementLine statementLine;

    /**
     * @see InformationToAccountOwner#FIELD_TAG_86
     */
    private final Optional<InformationToAccountOwner> informationToAccountOwner;


    public TransactionGroup(StatementLine statementLine, InformationToAccountOwner informationToAccountOwner) {

        Preconditions.checkArgument(statementLine != null, "statementLine can't be null");

        this.statementLine = statementLine;
        this.informationToAccountOwner = Optional.fromNullable(informationToAccountOwner);
    }

    public StatementLine getStatementLine() {
        return statementLine;
    }

    public Optional<InformationToAccountOwner> getInformationToAccountOwner() {
        return informationToAccountOwner;
    }

}
