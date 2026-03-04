package service.CSFC.CSFC_auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.Roles;

import java.util.Optional;

@Repository
public interface RolesRepository  extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByName(String name);
    boolean existsByName(String name);
}
