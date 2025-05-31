package org.edu.fpm.transportation.service;

import org.edu.fpm.transportation.entity.Role;
import org.edu.fpm.transportation.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Integer id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + id));
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
            .orElseThrow(() -> new NoSuchElementException("Role not found with name: " + roleName));
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Integer id, Role updatedRole) {
        Role existingRole = getRoleById(id);
        existingRole.setRoleName(updatedRole.getRoleName());
        existingRole.setEmail(updatedRole.getEmail());
        return roleRepository.save(existingRole);
    }

    public void deleteRole(Integer id) {
        roleRepository.deleteById(id);
    }
    
    public Role assignEmailToRole(Integer roleId, String email) {
        Role role = getRoleById(roleId);
        role.setEmail(email);
        return roleRepository.save(role);
    }
}
