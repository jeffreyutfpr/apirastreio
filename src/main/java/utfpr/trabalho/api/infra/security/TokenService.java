package utfpr.trabalho.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import utfpr.trabalho.api.model.user.UsersModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UsersModel user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret); //secret para criptografia vem da variavel de ambiente
            String token = JWT.create()
                    .withIssuer("auth-api") // quem criou, nome que identifique a aplicaçao
                    .withSubject(user.getLogin()) // usuario que vai receber o token e vai salvar o login do usuario no token
                    .withExpiresAt(generateExpirationDate()) //
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException exception){
                throw new RuntimeException("Error while generating token ",exception);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
        // tempo de agora + 2 horas e colocou na fuso horario de brasilia
    }
}
