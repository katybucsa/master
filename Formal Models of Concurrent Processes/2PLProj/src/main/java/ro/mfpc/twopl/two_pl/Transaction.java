package ro.mfpc.twopl.two_pl;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Transaction {

    static Integer id = 0;
    int transId;
    LocalDateTime timestamp;
    TransactionStatus status;
    List<Operation> operations = new ArrayList<>();

    public Transaction() {

        synchronized (id) {
            id += 1;
            this.transId = id;
        }
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.ACTIVE;
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
    }
}
