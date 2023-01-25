package ro.mfpc.twopl.two_pl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
public class WaitFor {

    private LockType type;
    private int recordId;
    private String table;
    int transHasLock;
    int transWaitsLock;

    public WaitFor(LockType lockType, int recordId, String table, int transHasLock, int transWaitsLock) {

        this.type = lockType;
        this.table = table;
        this.recordId = recordId;
        this.transHasLock = transHasLock;
        this.transWaitsLock = transWaitsLock;
    }
}
