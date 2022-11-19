package com.company.demo.util;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E, I> extends JpaRepository<E, I>, JpaSpecificationExecutor<E> {

    @Override
    @Query("select e from #{#entityName} e where e.deleted is false")
    List<E> findAll();

    @Query("select e from #{#entityName} e where e.id = ?1 and e.deleted is false")
    Optional<E> findById(I id);

    @Query("select e from #{#entityName} e where e.deleted is true")
    List<E> findAllDeleted();

    @Query("select e from #{#entityName} e where e.id = ?1 and e.deleted is true")
    Optional<E> findDeletedById(I id);

    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    @Transactional
    @Modifying
    void softDelete(I id);

    @Query("update #{#entityName} e set e.deleted=false where e.id = ?1")
    @Transactional
    @Modifying
    void restoreDeleted(I id);
}
