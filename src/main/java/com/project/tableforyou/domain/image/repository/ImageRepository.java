package com.project.tableforyou.domain.image.entity.repository;

import com.project.tableforyou.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
