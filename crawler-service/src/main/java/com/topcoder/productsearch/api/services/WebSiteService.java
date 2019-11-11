package com.topcoder.productsearch.api.services;


import com.topcoder.productsearch.api.exceptions.BadRequestException;
import com.topcoder.productsearch.api.exceptions.NotFoundException;
import com.topcoder.productsearch.api.models.OffsetLimitPageable;
import com.topcoder.productsearch.api.models.WebSiteSearchRequest;
import com.topcoder.productsearch.common.entity.WebSite;
import com.topcoder.productsearch.common.repository.WebSiteRepository;
import com.topcoder.productsearch.common.util.Common;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

/**
 * the website service
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WebSiteService {

  /**
   * the website repository
   */
  private final WebSiteRepository webSiteRepository;

  /**
   * get website by id
   *
   * @param id the website id
   * @return the website
   */
  public WebSite get(Integer id) {

    if (id == null) {
      throw new BadRequestException("WebSite id cannot be null.");
    }
    WebSite site = webSiteRepository.findByDeletedAndId(Boolean.FALSE, id);
    if (site == null) {
      throw new NotFoundException("No WebSite exists.");
    }
    return site;
  }

  /**
   * create new website
   *
   * @param webSite the request website
   * @return the new website
   */
  public WebSite create(WebSite webSite) {
    webSite.setCreatedAt(Date.from(Instant.now()));
    webSiteRepository.save(webSite);
    return webSite;
  }

  /**
   * update website , support partial update
   *
   * @param id     the website
   * @param entity the request website
   * @return the updated website
   */
  public WebSite update(Integer id, WebSite entity) {
    WebSite webSite = get(id);
    BeanUtils.copyProperties(entity, webSite, Common.getNullPropertyNames(entity));
    webSiteRepository.save(webSite);
    return webSite;
  }

  /**
   * remove website by id
   *
   * @param id the website id
   */
  public void remove(Integer id) {
    WebSite webSite = get(id);
    webSite.setLastModifiedAt(Date.from(Instant.now()));
    webSite.setDeleted(Boolean.TRUE);
    webSiteRepository.save(webSite);
  }

  /**
   * search website by query text
   *
   * @param request the search request
   * @return the page of request
   */
  public Page<WebSite> search(WebSiteSearchRequest request) {
    Pageable pageable = new OffsetLimitPageable(request.getStart(), request.getRows());
    if (request.getQuery() == null) {
      return webSiteRepository.findByDeleted(Boolean.FALSE, pageable);
    }
    return webSiteRepository.findWebSitesWithQuery(Boolean.FALSE, request.getQuery(), pageable);
  }
}