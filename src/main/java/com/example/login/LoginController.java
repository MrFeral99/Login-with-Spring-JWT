package com.example.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/login") // This means URL's start with /login (after Application path)
public class LoginController {
    @Autowired // This means to get the bean called userRepository
               // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    private Iterable<Users> users;


    @PostMapping(path="/accedi", consumes = "application/json", produces="application/json")
    public @ResponseBody Iterable<Users> login(@RequestBody Users u){

        users=userRepository.findByFilter(u.getEmail(), u.getPassword());
        Iterator<Users> iterator=users.iterator();
        if(iterator.hasNext()){
            return users;
        }else {
            return null;
        }

    }

    @PostMapping(path="/add",consumes = "application/json", produces = "application/json")
    public @ResponseBody ResponseRest addNewUser (@RequestBody Users u) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        ResponseRest rest= new ResponseRest("success");
        Users n = new Users();
        n.setName(u.getName());
        n.setSurname(u.getSurname());
        n.setEmail(u.getEmail());
        n.setPassword(u.getPassword());
        userRepository.save(n);
        return rest;
    }
}