package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;


/*Mapeia URL, endereços, autoriza ou bloqueia acesso a URL*/
/*Mapeia toda a parte de segurança e registra as classes de Token*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	/*Configura as solicitações de acesso por Http*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/*Ativando a proteção contra usuários que não estão validados por token*/		
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		/*Ativando a permissão para acesso a pagina inicial do sistema*/
			.disable()
		    .authorizeRequests()
		        .antMatchers("/").permitAll()
		        .antMatchers("/index").permitAll()
		    /*Qualquer outra requisição deve ser autenticada.*/
		    .anyRequest().authenticated()
		    /*Configura a URL de logout (/logout) e redireciona para /index após o logout e invalida o usuário*/
		    .and().logout().logoutSuccessUrl("/index")
		    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		    .and()
		    /*Filtra as requisições de login para autenticação*/
		    .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), 
		                      UsernamePasswordAuthenticationFilter.class)
		    /*Filtra as demais requisições para verificar a presença do TOKEN JWT*/
		    .addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}

	
	/*Passando o provedor de autenticação*/
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		/*Service que irá consultar o usuário no banco de dados*/
		//auth.userDetailsService(implementacaoUserDetailsService)
		
		/*Padrão de codificação de senha*/
		//.passwordEncoder(new BCryptPasswordEncoder());
		
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
	

}
