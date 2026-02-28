package com.backend.lab.common.oauth.service;

import com.backend.lab.common.oauth.dto.OauthUserInfo;
import java.net.URI;

public interface OauthService {

  URI getLoginURI(String redirectUri);
  OauthUserInfo getProviderId(String payload, String state);
}
