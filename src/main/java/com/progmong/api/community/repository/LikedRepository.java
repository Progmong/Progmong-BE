package com.progmong.api.community.repository;

import com.progmong.api.community.entity.Liked;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedRepository extends JpaRepository<Liked,Long> {
}
