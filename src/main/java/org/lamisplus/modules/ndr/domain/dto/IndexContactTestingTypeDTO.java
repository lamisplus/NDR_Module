package org.lamisplus.modules.ndr.domain.dto;

import java.util.List;

public interface IndexContactTestingTypeDTO {
    String  getArtClinic();
    String  getIndexClientIDType();
    String  getIndexClientID();
    String  getIndexClientLGA();
    String  getIndexClientState();
    String  getClientCategory();
    String  getOfferedIndexTestingServices();
    String  getAcceptedIndexTestingServices();
    List<IndexContactTypeDTO> indexContact();
}
