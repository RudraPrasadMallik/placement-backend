package com.example.demo.service;

import com.example.demo.dto.ContactMessageDTO;
import com.example.demo.model.ContactMessage;
import com.example.demo.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    public ContactMessage saveMessage(ContactMessageDTO dto) {
        ContactMessage message = new ContactMessage();
        message.setName(dto.getName());
        message.setEmail(dto.getEmail());
        message.setPhone(dto.getPhone());
        message.setSubject(dto.getSubject());
        message.setMessage(dto.getMessage());

        return contactMessageRepository.save(message);
    }
}