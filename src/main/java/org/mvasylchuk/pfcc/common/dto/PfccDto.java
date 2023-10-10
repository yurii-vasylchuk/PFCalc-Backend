package org.mvasylchuk.pfcc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PfccDto {

    private BigDecimal protein;

    private BigDecimal fat;

    private BigDecimal carbohydrates;

    private BigDecimal calories;

    public Pfcc toPfcc(){
        return new Pfcc(this.protein, this.fat,this.carbohydrates,this.calories);
    }
    public static PfccDto fromPfcc(Pfcc pfcc){
        return new PfccDto(pfcc.getProtein(),pfcc.getFat(),pfcc.getCarbohydrates(),pfcc.getCalories());
    }

}
