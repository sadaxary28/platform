package com.infomaximum.subsystems.querypool;

import com.infomaximum.database.domainobject.DomainObjectSource;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.subsystems.exception.SubsystemException;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс не потокобезопасен
 */
public class QueryTransaction implements AutoCloseable {

    @FunctionalInterface
    public interface CommitListener {
        void onCommitted();
    }

    @FunctionalInterface
    public interface RollbackListener {
        void onRollbacked(SubsystemException cause);
    }

    private final Transaction transaction;

    private List<CommitListener> commitListeners;
    private List<RollbackListener> rollbackListeners;

    QueryTransaction(DomainObjectSource domainObjectSource) {
        transaction = domainObjectSource.buildTransaction();
    }

    public Transaction getDBTransaction() {
        return transaction;
    }

    public void addCommitListener(CommitListener listener) {
        if (closed()) {
            throw new RuntimeException("Нельзя добавлять слушателя после закрытия транзакции");
        }
        if (commitListeners == null){
            commitListeners = new ArrayList<>();
        }
        commitListeners.add(listener);
    }

    public void addRollbackListener(RollbackListener listener) {
		if (closed()) {
			throw new RuntimeException("Нельзя добавлять слушателя после закрытия транзакции");
		}
        if (rollbackListeners == null) {
            rollbackListeners = new ArrayList<>();
        }
        rollbackListeners.add(listener);
    }

    /**
     * Discard all changes. Does not close the current internal transaction.
     */
    public void rollback() throws SubsystemException {
        try {
            transaction.getDBTransaction().rollback();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    public boolean closed() {
        return transaction.isClosed();
    }

    /**
     * If the method failed, the transaction automatically rolls back
     */
    void commit() throws SubsystemException {
        try {
            transaction.commit();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    void fireCommitListeners() {
        if (commitListeners != null) {
            //Переписывать сразу на итератор не стоит, так как пробежка по индексу решает проблему когда один из подписчиков
            //в момент комита выполняет код который в конечном итоге также подписывается на коммит(своеобразная рекурсия)
            //По этой же причине не реализован метод removeCommitListener
            for (CommitListener listener : commitListeners) {
                listener.onCommitted();
            }
        }
    }

    void fireRollbackListeners(SubsystemException cause) {
        if (rollbackListeners != null) {
            for (QueryTransaction.RollbackListener listener : rollbackListeners) {
                listener.onRollbacked(cause);
            }
        }
    }

    @Override
    public void close() {
        try {
            transaction.close();
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
