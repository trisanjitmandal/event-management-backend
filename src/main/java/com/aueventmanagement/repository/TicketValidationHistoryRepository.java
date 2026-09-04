package com.aueventmanagement.repository;

import com.aueventmanagement.entity.TicketValidationHistory;
import com.aueventmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketValidationHistoryRepository extends
        JpaRepository<TicketValidationHistory, UUID> {

    List<TicketValidationHistory> findByStaffOrderByValidationDateTimeDesc(User staff);


}
