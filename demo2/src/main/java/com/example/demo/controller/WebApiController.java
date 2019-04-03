package com.example.demo.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.GoodsEntity;
import com.example.demo.exception.ExceptionConflictName;
import com.example.demo.exception.ExceptionNone;
import com.example.demo.repository.GoodsRepository;

@Controller
@RestController
public class WebApiController {
	@Autowired
	private GoodsRepository repository;
	
    @RequestMapping(path = "/show", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<GoodsEntity> show() {
        return repository.findAll();
    }
    
    @RequestMapping(path = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<GoodsEntity> add(Model model, @RequestBody GoodsEntity good) throws Exception {
    	List<GoodsEntity> record = repository.findByName(good.getName());
    	if(record.size() != 0) {
    		throw new ExceptionConflictName(good.getName(), good.getId());
    	}
        repository.save(good);
        return repository.findAll();
    }

    
    @RequestMapping(path="/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<GoodsEntity> search(Model model, @RequestBody GoodsEntity good) {
    	List<GoodsEntity> target = repository.findByName(good.getName());
    	if (target.size() == 0) {
    		throw new ExceptionNone(good);
    	}
    	return repository.findByName(good.getName());
    }
    
    @RequestMapping(value="/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<GoodsEntity> update(Model model, @RequestBody GoodsEntity good) {
    	List<GoodsEntity> target = repository.findByName(good.getName());
    	if (target.size() == 0) {
    		throw new ExceptionNone(good);
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
    
    /* Exception */
    
    @ExceptionHandler(SQLException.class)
    private void sqlExceptionHandler(Exception e) {
    	System.out.println("SQLException Handler");
    }
    @ExceptionHandler(RuntimeException.class)
    private void runtimeExceptionHandler(RuntimeException e) {
    	System.out.println("RuntimeException Handler");
    }
    
    @ExceptionHandler(ExceptionConflictName.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleException(ExceptionConflictName e) {
    	Map<String, Object> map = new HashMap<>();
        map.put("message", e.getType() + " is already resisted");
        return map;
    }
    
    @ExceptionHandler(ExceptionNone.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleException(ExceptionNone e) {
    	Map<String, Object> map = new HashMap<>();
        map.put("message", e.getGood().getName() + " is not in the store");
        return map;
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    protected Map<String, Object> exceptionHandler(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("override", "error");
        return map;
    }
}