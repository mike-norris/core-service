package com.openrangelabs.services.signing.services;

import com.openrangelabs.services.signing.SNClient;

public abstract class ApiService {
    protected SNClient client;

    protected ApiService(SNClient client) {
        this.client = client;
    }
}
