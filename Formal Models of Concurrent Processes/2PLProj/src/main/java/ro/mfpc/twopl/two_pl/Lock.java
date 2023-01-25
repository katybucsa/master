package ro.mfpc.twopl.two_pl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Lock {
    static Integer id = 0;
    int lockId;
    LockType lockType;
    int recordId;
    String table;
    Transaction transaction;

    public Lock(LockType lockType, int recordId, String table, Transaction transaction) {

        synchronized (id) {
            id += 1;
            this.lockId = id;
        }
        this.lockType = lockType;
        this.recordId = recordId;
        this.table = table;
        this.transaction = transaction;
    }
}
