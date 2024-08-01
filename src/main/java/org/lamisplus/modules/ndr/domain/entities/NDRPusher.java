package org.lamisplus.modules.ndr.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
@Entity
@Table(name = "ndr_pusher",  schema = "public")
@Data
@NoArgsConstructor
public class NDRPusher implements Serializable, Persistable<Long>
{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer code;
    private String batchNumber;
    private LocalDate pushDate;
    private String deMessage;
    private String message;
    private Long facilityId;
    private Boolean isAuthenticated;
    @Override
    public boolean isNew() {
        return false;
    }
}
