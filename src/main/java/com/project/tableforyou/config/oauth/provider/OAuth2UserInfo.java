package com.project.tableforyou.config.oauth.provider;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getUsername();
    String getName();
    String getNickname();
    String getEmail();

}
