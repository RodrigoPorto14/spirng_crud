package com.rodri.crud.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodri.crud.dto.ClientDTO;
import com.rodri.crud.entities.Client;
import com.rodri.crud.repositories.ClientRepository;
import com.rodri.crud.services.exceptions.DataBaseException;
import com.rodri.crud.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	private ClientRepository repository;
	
	@Transactional(readOnly=true)
	public Page<ClientDTO> findPage(PageRequest pageRequest)
	{
		return repository.findAll(pageRequest).map(x -> new ClientDTO(x));
		//return repository.findAll(pageRequest).stream().map(x -> new ClientDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public ClientDTO findById(Long id) 
	{
		Client entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ClientDTO(entity);
	}
	
	@Transactional
	public ClientDTO insert(ClientDTO dto) {
		return saveEntity(dto,new Client());		
	}

	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try
		{
			Client entity = repository.getReferenceById(id); // ou getReferenceById
			return saveEntity(dto,entity);
		}
		catch(EntityNotFoundException e)
		{
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}

	public void delete(Long id) {
		try{repository.deleteById(id);}
		catch(EmptyResultDataAccessException e){throw new ResourceNotFoundException("Id not found " +id);}
		catch(DataIntegrityViolationException e) {throw new DataBaseException("Integrity violation");}
	}
	
	private ClientDTO saveEntity(ClientDTO dto, Client entity)
	{
		entity.setName(dto.getName());
		entity.setCpf(dto.getCpf());
		entity.setIncome(dto.getIncome());
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());
		return new ClientDTO(repository.save(entity));
	}
	
}
