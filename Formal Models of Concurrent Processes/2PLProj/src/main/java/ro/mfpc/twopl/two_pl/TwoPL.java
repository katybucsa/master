package ro.mfpc.twopl.two_pl;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public class TwoPL {

    final Map<Integer, Lock> lockMap = new HashMap<>();
    final Map<Integer, Transaction> transactionMap = new HashMap<>();
    //    final Set<WaitFor> waitForGraph = new HashSet<>(); // start a thread in the application in this class that checks if there are cycles
    final Graph graph = new Graph();

    {
        new Thread(() -> {

            while (true) {
                synchronized (graph) {
                    Vertex cycleVertx = graph.hasCycle();
                    if (cycleVertx != null) {
                        graph.removeVertex(cycleVertx);
                        abort(Objects.requireNonNull(cycleVertx).getTransaction());
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean lock(LockType lockType, int recordId, String table, Transaction transaction, OperationType operationType, Object before, Object after, JpaRepository managObj) {

        boolean canLock = checkIfCanLock(lockType, recordId, table, transaction.transId);
        if (canLock) {
            Lock lock = new Lock(lockType, recordId, table, transaction);
            synchronized (lockMap) {
                lockMap.put(lock.getLockId(), lock);
            }
        } else {
            Transaction transHasLock;
            synchronized (lockMap) {
                transHasLock = lockMap.values().stream().filter(l -> Arrays.asList(recordId, -1).contains(l.recordId) && l.table.equals(table)).findFirst().get().transaction;
            }
            WaitFor waitFor = new WaitFor(lockType, recordId, table, transHasLock.transId, transaction.transId);
//            synchronized (waitForGraph) {
//                waitForGraph.add(waitFor);
//            }
            Vertex v1 = graph.getVertex(transHasLock);
            Vertex v2 = graph.getVertex(transaction);
            synchronized (graph) {
                graph.addEdge(v1, v2);
            }
        }

        while (transaction.status.equals(TransactionStatus.ACTIVE) && !canLock) {
            canLock = checkIfCanLock(lockType, recordId, table, transaction.transId);
            if (canLock) {
                Lock lock = new Lock(lockType, recordId, table, transaction);
                synchronized (lockMap) {
                    lockMap.put(lock.getLockId(), lock);
                }
                break;
            }
        }

        if (transaction.status.equals(TransactionStatus.ABORT)) {
            releaseLocks(transaction);
            rollback(transaction);
            return false;
        }

        Operation operation = Operation.builder()
                .lockType(lockType)
                .operationType(operationType)
                .table(table)
                .before(before)
                .after(after)
                .managObj(managObj).build();

        transaction.addOperation(operation);
        return true;
    }

    public void commit(Transaction transaction) {

        transaction.status = TransactionStatus.COMMIT;

        synchronized (transactionMap) {
            transactionMap.put(transaction.transId, transaction);
        }
    }

    private boolean checkIfCanLock(LockType lockType, int recordId, String table, int transId) {

        synchronized (lockMap) {
            if (lockType.equals(LockType.WRITE))
                return lockMap.values().stream().noneMatch(l -> Arrays.asList(recordId, -1).contains(l.recordId) && l.table.equals(table) && l.transaction.getTransId() != transId);
            return lockMap.values().stream().noneMatch(l -> l.lockType.equals(LockType.WRITE) && Arrays.asList(recordId, -1).contains(l.recordId) && l.table.equals(table) && l.transaction.getTransId() != transId);
        }
    }

    public Transaction initTransaction() {

        Transaction t = new Transaction();

        synchronized (transactionMap) {
            transactionMap.put(t.getTransId(), t);
        }

        Vertex v = new Vertex(t);
        synchronized (graph) {
            graph.addVertex(v);
        }

        return t;
    }

    public void releaseLocks(Transaction transaction) {

        synchronized (lockMap) {
            lockMap.values().removeIf(l -> l.transaction.transId == transaction.transId);
        }
//        synchronized (waitForGraph) {
//            waitForGraph.removeIf(wfg -> wfg.transWaitsLock == transaction.transId || wfg.transHasLock == transaction.transId);
//        }
        synchronized (graph) {
            graph.removeVertex(graph.getVertex(transaction));
        }
    }

    public void abort(Transaction transaction) {

        transaction.setStatus(TransactionStatus.ABORT);
        synchronized (transactionMap) {
            transactionMap.put(transaction.getTransId(), transaction);
        }
    }

    private void rollback(Transaction transaction) {

        transaction.operations.forEach(o -> {
            if (o.lockType.equals(LockType.WRITE)) {
                JpaRepository repo = o.managObj;
                switch (o.operationType) {
                    case INSERT -> repo.delete(o.after);
                    case UPDATE -> repo.save(o.before);
                    case DELETE -> repo.save(o.before);
                    default -> {
                    }
                }
            }
        });
    }
}
