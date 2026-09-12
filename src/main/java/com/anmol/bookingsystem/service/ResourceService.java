package com.anmol.bookingsystem.service;

import com.anmol.bookingsystem.dto.ResourceDTO;
import com.anmol.bookingsystem.entity.Resource;
import com.anmol.bookingsystem.exception.ResourceNotFoundException;
import com.anmol.bookingsystem.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<ResourceDTO> getAllResources() {
        return resourceRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ResourceDTO getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        return toDTO(resource);
    }

    public ResourceDTO createResource(ResourceDTO dto) {
        Resource resource = new Resource();
        resource.setName(dto.getName());
        resource.setType(dto.getType());
        resource.setDescription(dto.getDescription());
        resource.setAvailable(dto.isAvailable());

        Resource saved = resourceRepository.save(resource);
        return toDTO(saved);
    }

    public ResourceDTO updateResource(Long id, ResourceDTO dto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        resource.setName(dto.getName());
        resource.setType(dto.getType());
        resource.setDescription(dto.getDescription());
        resource.setAvailable(dto.isAvailable());

        Resource updated = resourceRepository.save(resource);
        return toDTO(updated);
    }

    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
        resourceRepository.deleteById(id);
    }

    private ResourceDTO toDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setType(resource.getType());
        dto.setDescription(resource.getDescription());
        dto.setAvailable(resource.isAvailable());
        return dto;
    }
}