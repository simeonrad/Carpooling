package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Make;
import com.telerikacademy.web.carpooling.repositories.contracts.MakeRepository;
import com.telerikacademy.web.carpooling.services.contracts.MakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MakeServiceImpl implements MakeService {

    private final MakeRepository makeRepository;

    @Autowired
    public MakeServiceImpl(MakeRepository makeRepository) {
        this.makeRepository = makeRepository;
    }

    @Override
    public Make create(String value) {
        try{
            return getByValue(value);
        } catch (EntityNotFoundException e){
            Make make = new Make();
            make.setName(value);
            makeRepository.create(make);
            return getByValue(value);
        }
    }
    @Override
    public void delete(Make make) {
       makeRepository.delete(make);
    }

    @Override
    public List<Make> getAll(){
        return makeRepository.getAll();
    }

    @Override
    public Make getByValue(String value){
        return makeRepository.getByValue(value);
    }
}
