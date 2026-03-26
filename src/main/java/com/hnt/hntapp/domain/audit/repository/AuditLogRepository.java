package com.hnt.hntapp.domain.audit.repository;

import com.hnt.hntapp.domain.audit.entity.AuditLog;
import com.hnt.hntapp.domain.audit.entity.AuditType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /** 가맹점 + 검수 유형별 로그 */
    List<AuditLog> findByFranchiseIdAndAuditTypeOrderByCreatedAtDesc(
            UUID franchiseId, AuditType auditType);

    /** 가맹점 + 정산월 2차 검수 로그 */
    List<AuditLog> findByFranchiseIdAndSettlementMonthOrderByCreatedAtDesc(
            UUID franchiseId, String settlementMonth);

    /** 일련번호 검수 이력 */
    List<AuditLog> findBySerialNumberOrderByCreatedAtDesc(String serialNumber);

    /** 미처리 불일치 건수 (본사 대시보드) */
    long countByPassedFalseAndResolutionIsNull();

    /** 전표 ID 연관 검수 로그 */
    List<AuditLog> findByActivationId(UUID activationId);
}
