package com.lealtixservice.service.impl;

import com.lealtixservice.dto.CampaignTemplateDTO;
import com.lealtixservice.entity.CampaignTemplate;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.mapper.CampaignMapper;
import com.lealtixservice.repository.CampaignTemplateRepository;
import com.lealtixservice.service.CampaignTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignTemplateServiceImpl implements CampaignTemplateService {

    private final CampaignTemplateRepository templateRepository;

    @Override
    public List<CampaignTemplateDTO> findAll() {
        return templateRepository.findAll().stream()
                .map(CampaignMapper::toTemplateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CampaignTemplateDTO findById(Long id) {
        CampaignTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampaignTemplate no encontrado id=" + id));
        return CampaignMapper.toTemplateDTO(template);
    }

    @Override
    @Transactional
    public CampaignTemplateDTO create(CampaignTemplateDTO dto) {
        CampaignTemplate entity = CampaignMapper.toTemplateEntity(dto);
        if (entity.getIsActive() == null) entity.setIsActive(true);
        CampaignTemplate saved = templateRepository.save(entity);
        return CampaignMapper.toTemplateDTO(saved);
    }

    @Override
    @Transactional
    public CampaignTemplateDTO update(Long id, CampaignTemplateDTO dto) {
        CampaignTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampaignTemplate no encontrado id=" + id));
        CampaignMapper.updateTemplateEntity(dto, template);
        CampaignTemplate saved = templateRepository.save(template);
        return CampaignMapper.toTemplateDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CampaignTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampaignTemplate no encontrado id=" + id));
        templateRepository.delete(template);
    }
}
