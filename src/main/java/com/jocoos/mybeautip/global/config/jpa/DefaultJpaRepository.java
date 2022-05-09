package com.jocoos.mybeautip.global.config.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface DefaultJpaRepository<E, I extends Serializable> extends JpaRepository<E, I>, JpaSpecificationExecutor<E> {
}