package com.jocoos.mybeautip.domain.operation.persistence.domain;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.operation.code.OperationTargetType;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OperationLog extends CreatedDateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationTargetType targetType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Column(nullable = false)
    private String targetId;

    @Column
    private String description;

    @Column
    private long createdBy;
}
