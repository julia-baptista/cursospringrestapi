package curso.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.ObjetoErro;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.security.ServiceEnviaEmail;

@RestController
@RequestMapping(value="/recuperar")
public class RecuperaController {
	
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;
	
	@ResponseBody
	@PostMapping(value="/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) throws Exception {
		
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		
		if (user == null ) {
			objetoErro.setCode("404"); /*Não encontrado*/
			objetoErro.setError("Usuario não encontrado");		
		} else {
			/*Rotina de envio de email*/
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
			
			usuarioRepository.updateSenha(senhaCriptografada, user.getId());
			
			String assunto = "Recuperação de senha";
			
			String emailDestino = user.getLogin();
			
			String mensagem = "Sua nova senha é : " + senhaNova + ".";
			
			serviceEnviaEmail.enviarEmail(assunto, emailDestino, mensagem);
			
			/* ------------ */
			
			
			objetoErro.setCode("200"); /*Encontrado*/
			objetoErro.setError("Acesso enviado para seu e-mail");
		}
		
		return new ResponseEntity<ObjetoErro>(objetoErro, HttpStatus.OK);
		
	}

}
