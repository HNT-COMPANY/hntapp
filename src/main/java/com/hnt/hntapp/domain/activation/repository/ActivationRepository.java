package com.hnt.hntapp.domain.activation.repository;

import com.hnt.hntapp.domain.activation.entity.Activation;
import com.hnt.hntapp.domain.activation.entity.ActivationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivationRepository extends JpaRepository<Activation, UUID> {

    /** 가맹점 + 날짜 전표 목록 */
    List<Activation> findByFranchiseIdAndActivationDate(
            UUID franchiseId, LocalDate date);

    /** 가맹점 + 월별 전표 목록 */
    @Query("SELECT a FROM Activation a " +
            "WHERE a.franchise.id = :franchiseId " +
            "AND EXTRACT(YEAR FROM a.activationDate) = :year " +
            "AND EXTRACT(MONTH FROM a.activationDate) = :month " +
            "ORDER BY a.activationDate DESC, a.createdAt DESC")
    List<Activation> findByFranchiseAndMonth(
            @Param("franchiseId") UUID franchiseId,
            @Param("year") int year,
            @Param("month") int month);

    /** 상태별 전표 조회 (관리자 검토용) */
    List<Activation> findByStatusOrderByCreatedAtDesc(ActivationStatus status);

    /** 가맹점별 상태별 전표 조회 */
    List<Activation> findByFranchiseIdAndStatusOrderByActivationDateDesc(
            UUID franchiseId, ActivationStatus status);

    // ──────────────────────────────────────────
    // 집계 쿼리 (getDailySummary 최적화)
    // ──────────────────────────────────────────

    /** 일별 전표 건수 */
    int countByFranchiseIdAndActivationDate(UUID franchiseId, LocalDate date);

    /** 일별 상태별 전표 건수 */
    int countByFranchiseIdAndActivationDateAndStatus(
            UUID franchiseId, LocalDate date, ActivationStatus status);

    /** 일별 실마진 합계 */
    @Query("SELECT COALESCE(SUM(a.realMargin), 0) FROM Activation a " +
            "WHERE a.franchise.id = :franchiseId " +
            "AND a.activationDate = :date " +
            "AND a.status <> com.hnt.hntapp.domain.activation.entity.ActivationStatus.REJECTED")
    Long sumRealMarginByFranchiseAndDate(
            @Param("franchiseId") UUID franchiseId,
            @Param("date") LocalDate date);

    /** 월별 실마진 합계 (월 정산용) */
    @Query("SELECT COALESCE(SUM(a.realMargin), 0) FROM Activation a " +
            "WHERE a.franchise.id = :franchiseId " +
            "AND EXTRACT(YEAR FROM a.activationDate) = :year " +
            "AND EXTRACT(MONTH FROM a.activationDate) = :month " +
            "AND a.status = com.hnt.hntapp.domain.activation.entity.ActivationStatus.APPROVED")
    Long sumRealMarginByFranchiseAndMonth(
            @Param("franchiseId") UUID franchiseId,
            @Param("year") int year,
            @Param("month") int month);
}
