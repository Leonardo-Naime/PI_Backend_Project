package br.com.projeto.carsoncars.Services;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.projeto.carsoncars.Entities.User.User;
import br.com.projeto.carsoncars.Repository.Repositorio;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@SuppressWarnings("deprecation")
@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o encoder de senha
    
    @Autowired
    private Repositorio action;

    public ResponseEntity<?> authenticate(String email, String senha) {

        User user = action.findByEmail(email);

        if (user == null || !user.getSenha().equals(senha)) {
            return new ResponseEntity<>("Email ou senha invalidos", HttpStatus.UNAUTHORIZED);
        }

        // Criação de uma chave segura para HS512
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        String token = Jwts.builder()
               .setSubject(user.getId().toString())
               .signWith(key)
               .compact();

        user.setSenha(passwordEncoder.encode(user.getSenha()));
        user.setConfirmarSenha(passwordEncoder.encode(user.getConfirmarSenha()));
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user); // Adicione o usuário ao mapa
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
}
