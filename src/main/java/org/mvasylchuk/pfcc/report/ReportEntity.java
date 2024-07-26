package org.mvasylchuk.pfcc.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.report.dto.ReportStatus;
import org.mvasylchuk.pfcc.report.dto.ReportType;
import org.mvasylchuk.pfcc.user.UserEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reports")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class ReportEntity {
    private static final String ID_GENERATOR_NAME = "report_id_gen";
    private static final String ID_SEQ_NAME = "report_id_seq";

    @Id
    @GeneratedValue(generator = ID_GENERATOR_NAME)
    @SequenceGenerator(name = ID_GENERATOR_NAME, sequenceName = ID_SEQ_NAME, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path")
    @Getter(AccessLevel.PROTECTED)
    private String filePath;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    public static ReportEntity periodReport(UserEntity user, LocalDate from, LocalDate to) {
        return new ReportEntity(
                null,
                user,
                "Period report %s - %tF-%tF".formatted(user.getName(), from, to),
                null,
                ReportStatus.INITIALIZED,
                ReportType.PERIOD,
                LocalDateTime.now()
        );
    }

    public void setPath(Path path) {
        if (path != null) {
            this.filePath = path.toAbsolutePath().toString();
        } else {
            this.filePath = null;
        }
    }

    public Path getPath() {
        if (this.filePath == null) {
            return null;
        }

        return Paths.get(this.filePath);
    }
}
