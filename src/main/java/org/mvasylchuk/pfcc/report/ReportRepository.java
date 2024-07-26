package org.mvasylchuk.pfcc.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    @Query("FROM ReportEntity r WHERE r.createdAt < :bound")
    Collection<ReportEntity> findOlderThan(LocalDateTime bound);
}
