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
    
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.user WHERE " +
            "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.phone LIKE %:keyword% OR c.user.username LIKE %:keyword% OR c.email LIKE %:keyword%) AND " +
            "(:status IS NULL OR c.status = :status) ORDER BY c.createdAt DESC")
    Page<Customer> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") CustomerStatus status,
            Pageable pageable
    );
}
