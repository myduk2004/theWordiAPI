package theWordI.backend.domain.readingPlan.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlanStatus {
    WAITING,
    PROCEEDING,
    COMPLETED,
    ABANDONED;
}
