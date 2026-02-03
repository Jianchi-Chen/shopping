package io.cjc.backend.repository;

import io.cjc.backend.entity.Customer;
import io.cjc.backend.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    
    @Query("SELECT c FROM Customer c WHERE " +
            "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.phone LIKE %:keyword%) AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Customer> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") CustomerStatus status,
            Pageable pageable
    );
}
