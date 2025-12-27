package org.mvasylchuk.pfcc.report;

import jakarta.persistence.*;
import lombok.*;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Path getPath() {
        if (this.filePath == null) {
            return null;
        }

        return Paths.get(this.filePath);
    }

    public void setPath(Path path) {
        if (path != null) {
            this.filePath = path.toAbsolutePath().toString();
        } else {
            this.filePath = null;
        }
    }
}
