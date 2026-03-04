package service.CSFC.CSFC_auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.Permission;

import java.security.Permissions;
import java.util.Optional;

@Repository
public interface PermissionsRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByName(String name);
}