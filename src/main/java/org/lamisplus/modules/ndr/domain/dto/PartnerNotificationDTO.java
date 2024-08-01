package org.lamisplus.modules.ndr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerNotificationDTO implements Serializable {

    String partnername;
    String phoneNumber;
    String indexRelation;
    String partnerGender;
    String descriptiveAddress;


}
