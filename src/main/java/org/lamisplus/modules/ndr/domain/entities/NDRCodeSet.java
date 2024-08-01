package org.lamisplus.modules.ndr.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ndr_code_set")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NDRCodeSet implements Persistable<String> {
    @Id
   private String code;
   private String codeSetNm;
   private String codeDescription;
   private String altDescription;
   private String sysDescription;

   @Override
   public String getId() {
      return  code;
   }

   @Override
   public boolean isNew() {
      return code == null;
   }
}
