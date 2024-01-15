package org.mvasylchuk.pfcc.measurement;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mvasylchuk.pfcc.jooq.tables.Measurement.MEASUREMENT;

@Component
@RequiredArgsConstructor
public class MeasurementJooqRepository {
    private final DSLContext ctx;

    public List<MeasurementDto> getMeasurementList(Long foodId) {
        long count = ctx.selectFrom(MEASUREMENT)
                .where(MEASUREMENT.FOOD_ID.equal(foodId))
                .stream().count();
        if (count == 0) {
            throw new PfccException(ApiErrorCode.MEASUREMENTS_ARE_NOT_FOUND_WITH_THIS_FOOD_ID);
        }
        List<MeasurementDto> result = ctx.selectFrom(MEASUREMENT)
                .where(MEASUREMENT.FOOD_ID.equal(foodId))
                .fetch(dbMeasurement ->
                {
                    MeasurementDto measurement = new MeasurementDto();
                    measurement.setId(dbMeasurement.get(MEASUREMENT.ID));
                    measurement.setFoodId(dbMeasurement.get(MEASUREMENT.FOOD_ID));
                    measurement.setToGramMultiplier(dbMeasurement.get(MEASUREMENT.TO_GRAM_MULTIPLIER));
                    measurement.setName(dbMeasurement.get(MEASUREMENT.NAME));
                    measurement.setDefaultValue(dbMeasurement.get(MEASUREMENT.DEFAULT_VALUE));
                    return measurement;
                });

        return result;
    }
}
