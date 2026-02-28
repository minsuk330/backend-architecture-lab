package com.backend.lab.domain.details.service;

import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.entity.dto.req.DetailReq;
import com.backend.lab.domain.details.entity.dto.resp.DetailsResp;
import com.backend.lab.domain.details.repository.DetailsRepository;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailsService {

  private final DetailsRepository detailsRepository;

  @Transactional
  public void create(DetailReq req, Property property) {
    detailsRepository.save(
        Details.builder()
            .content(req.getContent())
            .property(property)
            .etc(req.getEtc())
            .propertyImages(req.getPropertyImages())
            .dataImages(req.getDataImages())
            .build()
    );
  }

  public Details getById(Long id) {
    return detailsRepository.findById(id).orElse(null);
  }

  public List<Details> getsByPropertyId(List<Long> propertyIds) {
    return detailsRepository.findAllByPropertyIdIn(propertyIds);
  }

  public Details getByPropertyId(Long propertyId) {
    return detailsRepository.findByProperty(propertyId);
  }

  @Transactional
  public void update(DetailReq req, Property property) {
    Details details = detailsRepository.findByProperty(property.getId());

    details.setContent(req.getContent() != null ? req.getContent() : "");
    details.setEtc(req.getEtc() != null ? req.getEtc() : "");
    
    if(req.getPropertyImages() != null) {
      if (details.getPropertyImages() != null) {
        details.getPropertyImages().clear();
      }
      details.setPropertyImages(req.getPropertyImages());
    }
    
    if(req.getDataImages() != null) {
      if (details.getDataImages() != null) {
        details.getDataImages().clear();
      }
      details.setDataImages(req.getDataImages());
    }
  }

  @Transactional
  public void delete(Long id) {
    Details byId = getById(id);
    detailsRepository.delete(byId);
  }

  public DetailsResp detailsResp(Details details, List<UploadFileResp> propertyImages,
      List<UploadFileResp> dataImages) {
    if (details == null) {
      return DetailsResp.builder().build();
    }
    return DetailsResp.builder()
        .content(details.getContent() != null ? details.getContent() : "")
        .etc(details.getEtc() != null ? details.getEtc() : "")
        .propertyImages(propertyImages != null ? propertyImages : new ArrayList<>())
        .dataImages(dataImages != null ? dataImages : new ArrayList<>())
        .build();
  }

}
