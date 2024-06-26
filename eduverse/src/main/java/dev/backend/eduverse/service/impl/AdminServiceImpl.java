package dev.backend.eduverse.service.impl;

import dev.backend.eduverse.dto.AdminDto;
import dev.backend.eduverse.exception.ResourceNotFoundException;
import dev.backend.eduverse.model.Admin;
import dev.backend.eduverse.model.AdminRole;
import dev.backend.eduverse.repository.AdminRepository;
import dev.backend.eduverse.repository.AdminRoleRepository;
import dev.backend.eduverse.service.AdminService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    private final AdminRoleRepository adminRoleRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    //private final BCrypyPasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, AdminRoleRepository adminRoleRepository) {
        this.adminRepository = adminRepository;
        this.adminRoleRepository = adminRoleRepository;
    }

    @Override
    public List<AdminDto> findAll() {
        List<Admin> admins = adminRepository.findAll();
        return admins
                .stream()
                .map(admin -> modelMapper.map(admin, AdminDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AdminDto findById(Long id) {
        Admin admin = adminRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "Id", id));
        return modelMapper.map(admin, AdminDto.class);
    }

    @Override
    public AdminDto createAdmin(AdminDto adminDto) {
        AdminRole adminRole = adminRoleRepository
                .findById(adminDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin Role", "Id", adminDto.getRoleId()));

        Admin admin = modelMapper.map(adminDto, Admin.class);

        admin.setStatus(true);
        admin.setAdminRole(adminRole);

        Admin savedAdmin = adminRepository.save(admin);

        return modelMapper.map(savedAdmin, AdminDto.class);
    }

    @Override
    public AdminDto updateAdmin(AdminDto adminDto, Long id) {
        Admin admin = adminRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "Id", id));

        AdminRole adminRole = adminRoleRepository
                .findById(adminDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin Role", "Id", adminDto.getRoleId()));

        admin.setUsername(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        admin.setEmail(adminDto.getEmail());
        admin.setPhoneNumber(adminDto.getPhoneNumber());
        admin.setAdminRole(adminRole);

        Admin updatedAdmin = adminRepository.save(admin);

        return modelMapper.map(updatedAdmin, AdminDto.class);
    }

    @Override
    public void deleteById(Long id) {
        logger.info("ID to delete : " + id.toString());
        Admin admin = adminRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "Id", id));
        logger.info("Object to delete : " + admin);
        adminRepository.deleteById(id);
    }
}
