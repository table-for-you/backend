package com.project.tableforyou.domain.visit.repository;

import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByVisitor(User visitor);

    @Transactional
    @Modifying
    @Query("delete from Visit v where v.id in :ids")
    void deleteAllVisitByIdInQuery(@Param("ids") List<Long> ids);
}
