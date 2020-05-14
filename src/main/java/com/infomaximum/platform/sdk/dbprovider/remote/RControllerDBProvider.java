package com.infomaximum.platform.sdk.dbprovider.remote;

import com.infomaximum.cluster.core.remote.struct.RController;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.provider.DBIterator;
import com.infomaximum.database.provider.KeyPattern;
import com.infomaximum.database.provider.KeyValue;

public interface RControllerDBProvider extends RController {

    long createIterator(String columnFamily) throws DatabaseException;
    long beginTransaction() throws DatabaseException;
    byte[] getValue(String columnFamily, byte[] key) throws DatabaseException;
    boolean containsColumnFamily(String name) throws DatabaseException;
    String[] getColumnFamilies() throws DatabaseException;
    void createColumnFamily(String name) throws DatabaseException;
    void dropColumnFamily(String name) throws DatabaseException;
    boolean containsSequence(String name) throws DatabaseException;
    void createSequence(String name) throws DatabaseException;
    void dropSequence(String name) throws DatabaseException;

    KeyValue seekIterator(KeyPattern pattern, long iteratorId) throws DatabaseException;
    KeyValue nextIterator(long iteratorId) throws DatabaseException;
    KeyValue stepIterator(DBIterator.StepDirection direction, long iteratorId) throws DatabaseException;
    void closeIterator(long iteratorId) throws DatabaseException;

    long createIteratorTransaction(String columnFamily, long transactionId) throws DatabaseException;
    long nextIdTransaction(String sequenceName, long transactionId) throws DatabaseException;
    byte[] getValueTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException;
    void putTransaction(String columnFamily, byte[] key, byte[] value, long transactionId) throws DatabaseException;

    void deleteTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException;
    void deleteRangeTransaction(String columnFamily, byte[] beginKey, byte[] endKey, long transactionId) throws DatabaseException;
    void singleDeleteTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException;
    void singleDeleteRangeTransaction(String columnFamily, byte[] beginKey, byte[] endKey, long transactionId) throws DatabaseException;
    void singleDeleteRangeTransaction(String columnFamily, KeyPattern keyPattern, long transactionId) throws DatabaseException;

    void commitTransaction(long transactionId) throws DatabaseException;
    void rollbackTransaction(long transactionId) throws DatabaseException;
    void closeTransaction(long transactionId) throws DatabaseException;
}
