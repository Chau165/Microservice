package service.CSFC.CSFC_auth_service.service;

import service.CSFC.CSFC_auth_service.model.dto.request.RolesRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.UpdateRolesRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RolesResponse;

import java.util.List;

public interface RolesService {
       RolesResponse createRole(RolesRequest request);
       List<RolesResponse> getAllRoles();
       void deleteRoleById(int id);
       RolesResponse updateRoleById(UpdateRolesRequest  request);
}
