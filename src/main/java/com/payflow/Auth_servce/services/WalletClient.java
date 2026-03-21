package com.payflow.Auth_servce.services;

import com.payflow.Auth_servce.util.WalletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WalletClient {
    private final RestClient restClient;

    public WalletClient(@Value("${wallet.service.base.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void createWallet(WalletRequest  walletRequest) {
        restClient.post().uri("/api/internal/wallets").body(walletRequest).retrieve().toBodilessEntity();
    }
}
