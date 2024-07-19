package com.joinzad.interviewcase.repository;

import com.joinzad.interviewcase.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserModel, Long> {
}
