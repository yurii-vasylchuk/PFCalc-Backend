package org.mvasylchuk.pfcc.measurement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<MeasurementEntity, Long> {
}
