package com.dezonnov1.SpringGuideBotMinecraft.repository;

import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

}