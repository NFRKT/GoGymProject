package com.GoGym.service;

import com.GoGym.module.ClientDetails;
import com.GoGym.module.User;
import com.GoGym.repository.ClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClientDetailsService {

    @Autowired
    private ClientDetailsRepository clientDetailsRepository;

    public ClientDetails getClientDetails(User user) {
        return clientDetailsRepository.findByUser(user);
    }

    public void saveClientDetails(ClientDetails clientDetails) {
        clientDetails.setUpdateDate(new Date());
        clientDetailsRepository.save(clientDetails);
    }

}
