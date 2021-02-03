package com.infomaximum.platform.sdk.dbprovider;

import com.infomaximum.cluster.struct.Component;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.provider.*;
import com.infomaximum.platform.sdk.dbprovider.remote.RControllerDBProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RemoteDBProvider implements DBProvider {

    private final static Logger log = LoggerFactory.getLogger(RemoteDBProvider.class);

    private final Component component;

    RemoteDBProvider(Component component) {
        this.component = component;
    }

    @Override
    public DBIterator createIterator(String columnFamily) throws DatabaseException {
        return new Iterator(getRemoteProvider().createIterator(columnFamily));
    }

    @Override
    public DBTransaction beginTransaction() throws DatabaseException {
        return new Transaction(getRemoteProvider().beginTransaction());
    }

    @Override
    public byte[] getValue(String columnFamily, byte[] key) throws DatabaseException {
        return getRemoteProvider().getValue(columnFamily, key);
    }

    @Override
    public boolean containsColumnFamily(String name) throws DatabaseException {
        return getRemoteProvider().containsColumnFamily(name);
    }

    @Override
    public String[] getColumnFamilies() throws DatabaseException {
        return getRemoteProvider().getColumnFamilies();
    }

    @Override
    public void createColumnFamily(String name) throws DatabaseException {
        getRemoteProvider().createColumnFamily(name);
    }

    @Override
    public void dropColumnFamily(String name) throws DatabaseException {
        getRemoteProvider().dropColumnFamily(name);
    }

    @Override
    public void compactRange() throws DatabaseException {
        getRemoteProvider().compactRange();
    }

    @Override
    public boolean containsSequence(String name) throws DatabaseException {
        return getRemoteProvider().containsSequence(name);
    }

    @Override
    public void createSequence(String name) throws DatabaseException {
        getRemoteProvider().createSequence(name);
    }

    @Override
    public void dropSequence(String name) throws DatabaseException {
        getRemoteProvider().dropSequence(name);
    }

    private RControllerDBProvider getRemoteProvider() {
        throw new RuntimeException("Not implemented");
        //TODO not implemented
//        return component.getRemotes().getFromCKey(shardKey, RControllerDBProvider.class);
    }

    private class Iterator implements DBIterator {

        private final long iteratorId;

        Iterator(long iteratorId) {
            this.iteratorId = iteratorId;
        }

        @Override
        public KeyValue seek(KeyPattern pattern) throws DatabaseException {
            return getRemoteProvider().seekIterator(pattern, iteratorId);
        }

        @Override
        public KeyValue next() throws DatabaseException {
            return getRemoteProvider().nextIterator(iteratorId);
        }

        @Override
        public KeyValue step(StepDirection direction) throws DatabaseException {
            return getRemoteProvider().stepIterator(direction, iteratorId);
        }

        @Override
        public void close() throws DatabaseException {
            getRemoteProvider().closeIterator(iteratorId);
        }
    }

    private class Transaction implements DBTransaction {

        private final long transactionId;

        Transaction(long transactionId) {
            this.transactionId = transactionId;
        }

        @Override
        public DBIterator createIterator(String columnFamily) throws DatabaseException {
            return new Iterator(getRemoteProvider().createIteratorTransaction(columnFamily, transactionId));
        }

        @Override
        public long nextId(String sequenceName) throws DatabaseException {
            return getRemoteProvider().nextIdTransaction(sequenceName, transactionId);
        }

        @Override
        public byte[] getValue(String columnFamily, byte[] key) throws DatabaseException {
            return getRemoteProvider().getValueTransaction(columnFamily, key, transactionId);
        }

        @Override
        public void put(String columnFamily, byte[] key, byte[] value) throws DatabaseException {
            getRemoteProvider().putTransaction(columnFamily, key, value, transactionId);
        }

        @Override
        public void delete(String columnFamily, byte[] key) throws DatabaseException {
            getRemoteProvider().deleteTransaction(columnFamily, key, transactionId);
        }

        @Override
        public void deleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException {
            getRemoteProvider().deleteRangeTransaction(columnFamily, beginKey, endKey, transactionId);
        }

        @Override
        public void singleDelete(String columnFamily, byte[] key) throws DatabaseException {
            getRemoteProvider().singleDeleteTransaction(columnFamily, key, transactionId);
        }

        @Override
        public void singleDeleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException {
            getRemoteProvider().singleDeleteRangeTransaction(columnFamily, beginKey, endKey, transactionId);
        }

        @Override
        public void singleDeleteRange(String columnFamily, KeyPattern keyPattern) throws DatabaseException {
            getRemoteProvider().singleDeleteRangeTransaction(columnFamily, keyPattern, transactionId);
        }

        @Override
        public void commit() throws DatabaseException {
            getRemoteProvider().commitTransaction(transactionId);
        }

        @Override
        public void rollback() throws DatabaseException {
            getRemoteProvider().rollbackTransaction(transactionId);
        }

        @Override
        public void compactRange() throws DatabaseException {
            getRemoteProvider().compactRange();
        }

        @Override
        public void close() throws DatabaseException {
            getRemoteProvider().closeTransaction(transactionId);
        }
    }

    @Override
    public void close() {
        log.error("Not implemented!");
        //TODO not implemented
    }
}
