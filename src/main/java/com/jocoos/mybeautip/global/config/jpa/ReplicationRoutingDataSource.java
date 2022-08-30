package com.jocoos.mybeautip.global.config.jpa;

import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Log4j2
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() { // (1)
        return (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) ? "slave" : "master"; //(2)
    }
}
