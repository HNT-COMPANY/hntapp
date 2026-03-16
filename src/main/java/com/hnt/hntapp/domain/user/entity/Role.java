package com.hnt.hntapp.domain.user.entity;

/**
 * 사용자 권한 열거형
 * - ADMIN: 본사 관리자 (모든 권한)
 * - FRANCHISEE: 가맹점주 (자기 가맹점만)
 * - STAFF: 가맹점 직원 (출퇴근/재고 조회)
 * - CUSTOMER: 고객 (나중에 활성화)
 */
public enum Role {
    ADMIN, // 본사 관리자
    FRANCHISEE, // 가맹점주
    STAFF, // 가맹점 직원
    CUSTOMER // 고객
}
