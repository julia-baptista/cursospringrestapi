package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;


@CrossOrigin(origins = "*")
@RestController /*Arquitetura REST*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
//	@GetMapping(value ="/")
//	public ResponseEntity init(@RequestParam (value = "nome", required = true, defaultValue = "joão nome default") String nome) {
//		return new ResponseEntity("Olá usuário " + nome + " REST String Boot", HttpStatus.OK);
//	}
	
//	@GetMapping(value ="/{id}/relatoriopdf", produces = "application/json")
//	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id) {
//		
//		Optional<Usuario> usuario = usuarioRepository.findById(id);
//		
//		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
//	}
	
	
	@GetMapping(value ="/{id}", produces = "application/json")
	public ResponseEntity<Usuario> usuario(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping(value ="/", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarios() {
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/", produces = "application/json")
		public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
		
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
	
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) {
			
		usuarioRepository.deleteById(id);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
	}
	
	
	
	
	

}
