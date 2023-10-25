package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PfccMappingService {
    @Transactional(rollbackOn = Exception.class)
    public Pfcc toPfcc(PfccDto pfccDto){
        return new Pfcc(pfccDto.getProtein(),
                pfccDto.getFat(),
                pfccDto.getCarbohydrates(),
                pfccDto.getCalories());
    }

    @Transactional(rollbackOn = Exception.class)
    public PfccDto toPfccDto(Pfcc pfcc){
        return new PfccDto(pfcc.getProtein(),
                pfcc.getFat(),
                pfcc.getCarbohydrates(),
                pfcc.getCalories());
    }
}
