package service.CSFC.CSFC_auth_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.mapper.RolesMapper;
import service.CSFC.CSFC_auth_service.model.dto.request.RolesRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.UpdateRolesRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RolesResponse;
import service.CSFC.CSFC_auth_service.model.entity.Roles;
import service.CSFC.CSFC_auth_service.repository.RolesRepository;
import service.CSFC.CSFC_auth_service.service.RolesService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolesServiceImp implements RolesService {
    private final RolesRepository rolesRepository;
    private final RolesMapper rolesMapper;

    @Override
    public RolesResponse createRole(RolesRequest request) {
        String roleName = request.getRoleName().toUpperCase();
        if (rolesRepository.existsByName(roleName)) {
            throw new RuntimeException("Role đã tồn tại: " + roleName);
        }
        Roles role = rolesMapper.toEntity(roleName);
         Roles saveRole= rolesRepository.save(role);
        return rolesMapper.toResponse(saveRole);
    }

    @Override
    public List<RolesResponse> getAllRoles() {

        return rolesRepository.findAll()
                .stream()
                .map(rolesMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoleById(int id) {
        if (!rolesRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Role với ID: " + id);
        }
        if (rolesRepository.findById(id).get().getUsers() != null && !rolesRepository.findById(id).get().getUsers().isEmpty()) {
            throw new RuntimeException("Không thể xóa Role này vì có người dùng đang sử dụng!");
        }
        rolesRepository.deleteById(id);
    }

    @Override
    public RolesResponse updateRoleById(UpdateRolesRequest request) {
        // 1. Tìm Role cũ trong DB
        Roles existingRole = rolesRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role với ID: " + request.getId()));

        String newName = request.getRoleName().trim().toUpperCase();


        if (!existingRole.getName().equals(newName) && rolesRepository.existsByName(newName)) {
            throw new RuntimeException("Tên Role mới này đã tồn tại!");
        }

        existingRole.setName(newName);


        Roles updatedRole = rolesRepository.save(existingRole);
        return rolesMapper.toResponse(updatedRole);
    }
}
