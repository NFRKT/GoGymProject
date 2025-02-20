package com.GoGym.service;

import com.GoGym.module.ClientDetails;
import com.GoGym.module.User;
import com.GoGym.repository.ClientDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClientDetailsService {

    private final ClientDetailsRepository clientDetailsRepository;

    public ClientDetailsService(ClientDetailsRepository clientDetailsRepository) {
        this.clientDetailsRepository = clientDetailsRepository;
    }

    public ClientDetails getClientDetails(User user) {
        return clientDetailsRepository.findByUser(user);
    }

    public void saveClientDetails(ClientDetails clientDetails) {
        clientDetails.setUpdateDate(new Date());
        clientDetailsRepository.save(clientDetails);
    }
}
