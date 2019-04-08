package com.example.demo.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.QuerySpeficiations;
import com.example.demo.entity.GoodsEntity;
import com.example.demo.entity.SearchQuery;
import com.example.demo.exception.ExceptionCommon;
import com.example.demo.repository.GoodsRepository;

@SuppressWarnings("deprecation")
@Controller
@RestController
public class WebApiController {
	@Autowired
	private GoodsRepository repository;

	//for Debuig showiung all goods
    @RequestMapping(path = "/show", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<GoodsEntity> show() {
        return repository.findAll();
    }
    
    //add goods
    @RequestMapping(path = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<GoodsEntity> add(Model model, @RequestBody GoodsEntity good) throws Exception {
    	if(good.getName() == null || good.getDescription() == null || good.getPrice() == 0){
    		throw new ExceptionCommon("Your request is not enouth.");
    	}
    	
    	List<GoodsEntity> record = repository.findByName(good.getName());
    	
    	//check which is user response is new.
    	if(record.size() != 0) {
    		throw new ExceptionCommon(good.getName() + " is already added");
    	}
        repository.save(good);
        return repository.findAll();
    }

    //search goods
    @RequestMapping(path="/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<GoodsEntity> search(Model model, @RequestBody SearchQuery query) {

    	List<GoodsEntity> result = repository.findAll(Specifications
    			.where(QuerySpeficiations.nameContains(query.getName()))
    			.and(QuerySpeficiations.descriptionContains(query.getDescription()))
    			.and(QuerySpeficiations.priceGreaterThanEqual(query.getMinPrice()))
    			.and(QuerySpeficiations.priceLessThanEqual(query.getMaxPrice())));
    	if (result.size() == 0) {
    		throw new ExceptionCommon("Nothing in the store");
    	}
    	
    	return result;
    }

    //update goods data
	@RequestMapping(value="/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<GoodsEntity> update(Model model, @RequestBody GoodsEntity good) {
		if(good.getName() == null || good.getDescription() == null || good.getPrice() == 0){
    		throw new ExceptionCommon("Your request is not enouth.");
    	}
    	List<GoodsEntity> target = repository.findByName(good.getName());
    	if (target.size() == 0) {
    		throw new ExceptionCommon(good.getName() + " is nothing in the store");
    	}
    	for(GoodsEntity tar : target) {
    		tar.setPrice(good.getPrice());
    		tar.setDescription(good.getDescription());
    		repository.save(tar);
    	}
    	return repository.findAll();
    }

    @RequestMapping(value="/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<GoodsEntity> delete(Model model, @RequestBody GoodsEntity good) {
    	List<GoodsEntity> target = repository.findByName(good.getName());
    	for(GoodsEntity tar : target) {
    		repository.deleteById(tar.getId());
    	}
    	return repository.findAll();
    }
}