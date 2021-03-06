package com.example.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

@Controller    // This means that this class is a Controller
@RequestMapping(path = "/login") // This means URL's start with /login (after Application path)
public class LoginController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    private Iterable<Users> users;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @CrossOrigin
    @PostMapping(path = "/add", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    ResponseRest addNewUser(@RequestBody Users u) {
        // @ResponseBody means the returned String is the response, not a view name
        ResponseRest rest = new ResponseRest("success");
        String enc_pwd= bCryptPasswordEncoder.encode(u.getPassword());
        Users n = new Users();
        n.setName(u.getName());
        n.setSurname(u.getSurname());
        n.setEmail(u.getEmail());
        n.setPassword(enc_pwd);
        userRepository.save(n);
        return rest;
    }

/*    @PostMapping(path="/token", consumes = "application/json", produces = "application/json")
    public @ResponseBody String createToken(){
        String token="";
        long seconds=10000;//millisecs 10 seconds

        Calendar date = Calendar.getInstance();
        long t= date.getTimeInMillis();
        Date afterAddingOneMin=new Date(t + (seconds));
        try {
            Algorithm algorithm = Algorithm.HMAC256("qwertyuioplkjhgfdsazxcvbnm1234567890");
            token = JWT.create()
                    .withIssuer("auth0")
                    .withExpiresAt(afterAddingOneMin)
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.

        }
        return token;

    }*/

    @CrossOrigin
    @PostMapping(path = "/validate", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    ResponseValidation validateToken(@RequestBody String token) throws JSONException {
        JSONObject d = new JSONObject(token);
        String pp = d.getString("token");
        ResponseValidation status = new ResponseValidation();
        try {
            Algorithm algorithm = Algorithm.HMAC256("qwertyuioplkjhgfdsazxcvbnm1234567890");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    //.acceptExpiresAt(30)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(pp);
            status.setStatus("valid");
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            status.setStatus("invalid");
        }

        return status;

    }


    @CrossOrigin
    @PostMapping(path = "/accedi", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    ResponseToken accedi(@RequestBody Users u) {

        long seconds = 3600000;//millisecs 1 hour  divide by 1000 and you'll get the time in seconds

        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date exp = new Date(t + (seconds));

        users = userRepository.findByEmail(u.getEmail());
        Iterator<Users> iterator = users.iterator();
        ResponseToken token = new ResponseToken();
        if (iterator.hasNext()) {
            Users user = iterator.next();
            if (bCryptPasswordEncoder.matches(u.getPassword(), user.getPassword())) {
                try {
                    Algorithm algorithm = Algorithm.HMAC256("qwertyuioplkjhgfdsazxcvbnm1234567890");
                    token.setToken(JWT.create()
                            .withIssuer("auth0")
                            .withExpiresAt(exp)
                            .sign(algorithm));

                } catch (JWTCreationException exception) {
                    //Invalid Signing configuration / Couldn't convert Claims.

                }
            } else {
                token.setFailed("Password errata!");
            }
        } else {
            token.setFailed("Utente non registrato");
        }
        return token;
    }

}