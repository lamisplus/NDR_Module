package org.lamisplus.modules.ndr.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ndr_pusher_config",  schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NDRPusherConfig implements Serializable, Persistable<Long>
{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long facilityId;
    private String username;
    private String password;
    private String baseUrl;

    @Override
    public boolean isNew() {
        return false;
    }
}
