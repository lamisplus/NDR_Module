package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.ndr.domain.entities.NDRCodeSet;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.schema.CodedSimpleType;
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
    public Optional<CodedSimpleType> getNDRCodeSet(String codeSetNm, String sysDescription){
        Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetByCodeSetNmAndSysDescription (codeSetNm, sysDescription);
        if(ndrCodeSet.isPresent ()){
            CodedSimpleType codedSimpleType = new CodedSimpleType();
            codedSimpleType.setCodeDescTxt (ndrCodeSet.get ().getCodeDescription ());
            codedSimpleType.setCode (ndrCodeSet.get ().getCode ());
            return Optional.of (codedSimpleType);
        }
        return Optional.empty ();
    }

    public Optional<CodedSimpleType> getSimpleCodeSet(String sysDescription){
        Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetBySysDescription (sysDescription);
        return ndrCodeSet.map (this::getCodedSimpleType);
    }

    public Optional<CodedSimpleType> getRegimen(String display) {
        Optional<String> regimenResolver = ndrCodeSetRepository.getNDREquivalentRegimenUsingSystemRegimen (display);
        if (regimenResolver.isPresent ()) {
            Optional<NDRCodeSet> ndrCodeSet = ndrCodeSetRepository.getNDRCodeSetByCodeDescription (regimenResolver.get ());
            if (ndrCodeSet.isPresent ()) {
               log.info("NDR REGIMEN CODE: "+ ndrCodeSet.get().getCode());
                CodedSimpleType codedSimpleType = new CodedSimpleType ();
                codedSimpleType.setCode (ndrCodeSet.get ().getCode ());
                codedSimpleType.setCodeDescTxt (ndrCodeSet.get ().getCodeDescription ());
                return Optional.of (codedSimpleType);
            }
        }
        return Optional.empty ();
    }

    @NotNull
    private CodedSimpleType getCodedSimpleType(NDRCodeSet ndrCodeSet1) {
        CodedSimpleType codedSimpleType = new CodedSimpleType ();
        codedSimpleType.setCode (ndrCodeSet1.getCode ());
        codedSimpleType.setCodeDescTxt (ndrCodeSet1.getCodeDescription ());
        return codedSimpleType;
    }
}
