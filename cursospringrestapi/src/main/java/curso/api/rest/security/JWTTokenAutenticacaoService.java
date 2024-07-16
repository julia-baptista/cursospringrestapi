package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	
	/*Tem de validade do Token 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;
	
	/*Uma senha unica para compor a autenticacao e ajudar na segurança*/
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	/*Prefixo padrão de Token*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	/*Nome do header*/
	private static final String HEADER_STRING = "Authorization";
	
	/*Gerando token de autenticado e adiconando ao cabeçalho e resposta Http*/
	public void addAuthentication(HttpServletResponse response , String username) throws IOException {
		
		/*Montagem do Token*/
		String JWT = Jwts.builder() /*Chama o gerador de Token*/
				        .setSubject(username) /*Adicona o usuario*/
				        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*Tempo de expiração*/
				        .signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Compactação e algoritmos de geração de senha*/
		
		/*Junta token com o prefixo*/
		String token = TOKEN_PREFIX + " " + JWT; /*Bearer 87878we8we787w8e78w78e78w7e87w*/
		
		/*Adiciona no cabeçalho http*/
		response.addHeader(HEADER_STRING, token); /*Authorization: Bearer 87878we8we787w8e78w78e78w7e87w*/
		
		/*Salva o token no banco de dados*/
//	    Usuario usuario = ApplicationContextLoad.getApplicationContext()
//	                        .getBean(UsuarioRepository.class).findUserByLogin(username);
//	    
//	    if (usuario != null) {
//	        usuario.setToken(JWT); // Salva o token sem o prefixo "Bearer"
//	        ApplicationContextLoad.getApplicationContext()
//	            .getBean(UsuarioRepository.class).save(usuario);
//	    }
		
		ApplicationContextLoad.getApplicationContext()
		.getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
		
		
		
		/*Liberando resposta para portas diferentes que usam a API ou caso clientes web*/
		 liberacaoCors(response);
		
		/*Escreve token como responsta no corpo http*/
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
		
	}
	
	
	/*Retorna o usuário validado com token ou caso não sejá valido retorna null*/
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		
		/*Pega o token enviado no cabeçalho http*/
		
		String token = request.getHeader(HEADER_STRING);
		
		try{
			
			if (token != null) {
				
				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
				
				/*Faz a validação do token do usuário na requisição*/
				String user = Jwts.parser().setSigningKey(SECRET) /*Bearer 87878we8we787w8e78w78e78w7e87w*/
						.parseClaimsJws(tokenLimpo) /*87878we8we787w8e78w78e78w7e87w*/
						.getBody().getSubject(); /*João Silva*/
				if (user != null) {
					
					Usuario usuario = ApplicationContextLoad.getApplicationContext()
							.getBean(UsuarioRepository.class).findUserByLogin(user);
					
					if (usuario != null) {
						
						if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
							
							return new UsernamePasswordAuthenticationToken(
									usuario.getLogin(), 
									usuario.getSenha(),
									usuario.getAuthorities());
						}
					}
				}
				
			}
			
		} catch(io.jsonwebtoken.ExpiredJwtException e) {
			try {
				response.getOutputStream().println("Seu TOKEN está expirado. Faça login ou informe um novo TOKEN para autenticação.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
			
		liberacaoCors(response);
		return null; /*Não autorizado*/
				
		//Resumo:
//		O token é extraído do cabeçalho HTTP.
//		O prefixo "Bearer" é removido.
//		O token é validado e o usuário é extraído.
//		O usuário é buscado no repositório e comparado com o token.
		
	}
	
	private void liberacaoCors(HttpServletResponse response) {

		/*Permite que recursos sejam solicitados a partir de qualquer origem, ou seja, qualquer domínio pode acessar a API.*/
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		/*Especifica os cabeçalhos que podem ser usados durante a solicitação real. Aqui, estamos permitindo todos os cabeçalhos (*)*/
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		/*Permite que o cliente envie qualquer cabeçalho em suas requisições.*/
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		/* Especifica os métodos HTTP permitidos ao acessar o recurso. Aqui, todos os métodos são permitidos (*), como GET, POST, PUT, DELETE, etc.*/
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
	

}
		
		
		
		
		
		
		
		
		
		
		
		