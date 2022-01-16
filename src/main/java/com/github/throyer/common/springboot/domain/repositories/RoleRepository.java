package com.github.throyer.common.springboot.domain.repositories;

import java.util.Optional;

import com.github.throyer.common.springboot.domain.models.entity.Role;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RoleRepository extends SoftDeleteRepository<Role> {

    @Override
    @Modifying
    @Transactional
    @Query("""
        UPDATE 
            #{#entityName}
        SET
            deleted_name = (
                SELECT name FROM #{#entityName} WHERE id = ?1
            ),
            name = NULL,
            deleted_initials = (
                SELECT name FROM #{#entityName} WHERE id = ?1
            ),
            initials = NULL,
            deleted_at = CURRENT_TIMESTAMP,
            active = false,
            deleted_by = ?#{principal?.id}
        WHERE id = ?1
    """)
    void deleteById(Long id);

    @Override
    @Transactional
    default void delete(Role role) {
        deleteById(role.getId());
    }

    @Override
    @Transactional
    default void deleteAll(Iterable<? extends Role> entities) {
        entities.forEach(entity -> deleteById(entity.getId()));
    }

    Optional<Role> findOptionalByInitials(String initials);

    Optional<Role> findOptionalByName(String name);

    Boolean existsByInitials(String initials);

    Boolean existsByName(String name);
}