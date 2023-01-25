package ro.mfpc.twopl.two_pl;

import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Operation {

    LockType lockType;
    OperationType operationType;
    String table;
    Object before;
    Object after;
    JpaRepository managObj;
}
