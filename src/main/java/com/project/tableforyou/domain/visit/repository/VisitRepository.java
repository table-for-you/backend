package com.project.tableforyou.domain.visit.repository;

import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByVisitor(User visitor);
}
