package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.ndr.domain.entities.NDRCodeSet;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.schema.CodedSimpleType;
import org.lamisplus.modules.ndr.schema.RegimenCodedSimpleType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NDRCodeSetResolverService {
    private final NDRCodeSetRepository ndrCodeSetRepository;



    public Optional<String> getNDRCodeSetCode(String codeSetNm, String sysDescription){
        Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetByCodeSetNmAndSysDescription (codeSetNm, sysDescription);
        return ndrCodeSet.map (NDRCodeSet::getCode);

    }
    public Optional<RegimenCodedSimpleType> getNDRCodeSet(String codeSetNm, String sysDescription){
        Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetByCodeSetNmAndSysDescription (codeSetNm, sysDescription);
        if(ndrCodeSet.isPresent ()){
            RegimenCodedSimpleType codedSimpleType = new RegimenCodedSimpleType();
            codedSimpleType.setCodeDescTxt (ndrCodeSet.get ().getCodeDescription ());
            codedSimpleType.setCode (ndrCodeSet.get ().getCode ());
            codedSimpleType.setNDRCode(ndrCodeSet.get().getNdrCode());
            return Optional.of (codedSimpleType);
        }
        return Optional.empty ();
    }

    public Optional<CodedSimpleType> getCodeSet(String codeSetNm, String sysDescription){
        Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetByCodeSetNmAndSysDescription (codeSetNm, sysDescription);
        if(ndrCodeSet.isPresent ()){
            CodedSimpleType codedSimpleType = new CodedSimpleType();
            codedSimpleType.setCodeDescTxt (ndrCodeSet.get ().getCodeDescription ());
            codedSimpleType.setCode (ndrCodeSet.get ().getCode ());
            return Optional.of (codedSimpleType);
        }
        return Optional.empty ();
    }

    public Optional<RegimenCodedSimpleType> getSimpleCodeSet(String sysDescription){
        Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetBySysDescription (sysDescription);
        return ndrCodeSet.map (this::getCodedSimpleType);
    }

    public Optional<RegimenCodedSimpleType> getRegimen(String display) {
        Optional<String> regimenResolver = ndrCodeSetRepository.getNDREquivalentRegimenUsingSystemRegimen (display);
        if (regimenResolver.isPresent ()) {
            Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetByCodeDescription (regimenResolver.get ());
            if (ndrCodeSet.isPresent ()) {
                log.info("ndr resolver service " + ndrCodeSet.get().getCode() + " " + ndrCodeSet.get ().getCodeDescription () + " " + ndrCodeSet.get().getNdrCode());
               log.info("NDR REGIMEN CODE: "+ ndrCodeSet.get().getCode());
                RegimenCodedSimpleType codedSimpleType = new RegimenCodedSimpleType();
                codedSimpleType.setCode (ndrCodeSet.get ().getCode ());
                codedSimpleType.setCodeDescTxt (ndrCodeSet.get ().getCodeDescription ());
                codedSimpleType.setNDRCode(ndrCodeSet.get().getNdrCode());
                return Optional.of (codedSimpleType);
            }
        }
        return Optional.empty ();
    }

    @NotNull
    private RegimenCodedSimpleType getCodedSimpleType(NDRCodeSet ndrCodeSet1) {
        RegimenCodedSimpleType codedSimpleType = new RegimenCodedSimpleType ();
        codedSimpleType.setCode (ndrCodeSet1.getCode ());
        codedSimpleType.setCodeDescTxt (ndrCodeSet1.getCodeDescription ());
        codedSimpleType.setNDRCode(ndrCodeSet1.getNdrCode());
        return codedSimpleType;
    }
}
