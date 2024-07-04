package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.AppplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;

/*Gera o token e também valida o token enviado*/
@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/*Tempo de validade do Token de 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;
	
	/*Uma senha unica para compor a autenticacao*/
	private static final String SECRET = "*SenhaExtremamenteSecreta";
	
	/*Prefixo padrão de Token*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	/*Gerando token de autenticação e adicionando no cabeçalho e resposta Http*/
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		
		
		/*Montage do Token*/
		String JWT = Jwts.builder() /*Chama o gerador de Token*/
				.setSubject(username) /*Adiciona o usuario*/
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*Tempo de expiração*/
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Algoritmo de geração de senha e compactação*/
		
		/*Junta o token com o prefixo*/
		String token = TOKEN_PREFIX + " " + JWT; /* Bearer 7986454ds89g98fg89sf9g84546g4s4g89s*/
		
		/*Adiciona no cabeçalho http*/
		response.addHeader(HEADER_STRING, token); /*Authorization: Bearer 7986454ds89g98fg89sf9g84546g4s4g89s*/
			
		/*Escreve token como resposta no corpo http*/
		response.getWriter().write("{\"Authorization\": \"" +token+" \"}");
		
	}
	
	/*Retorna o usuário validado com token ou caso não seja valido retorna null*/
	public Authentication getAuthentication(HttpServletRequest request) {
		
		/*Pega o token enviado no cabeçalho http*/	
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			
			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
			
			/*Faz a validação do token do usuário na requisição*/
			String user = Jwts.parser().setSigningKey(SECRET)
					.parseClaimsJws(tokenLimpo)
					.getBody().getSubject(); /*João Silva*/
					
			if (user != null) {
				
				Usuario usuario = AppplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).findUserByLogin(user);
				
				if (usuario != null) {
					
					if(tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
						
						return new UsernamePasswordAuthenticationToken(
								usuario.getLogin(),
								usuario.getSenha(),
								usuario.getAuthorities());
					}
					
					
				}
				
			}
			
		} 
		
		return null; /*Não autorizado*/
		
	}
	
	
}














