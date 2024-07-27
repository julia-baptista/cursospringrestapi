package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import com.google.gson.Gson;

import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;


@CrossOrigin(origins = "*")
@RestController /*Arquitetura REST*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
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
	
	
	@GetMapping(value ="/v1/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> usuarioV1(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		System.out.println("Executando versão 1");
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	@GetMapping(value ="/v2/{id}", produces = "application/json")
	//@GetMapping(value ="/{id}", produces = "application/json" headers = "X-API_Version=v2")
	public ResponseEntity<Usuario> usuarioV2(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		System.out.println("Executando versão 2");
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value ="/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException {
		
		PageRequest pageRequest = null;
		Page<Usuario> list = null;
		
		if (nome == null || nome.trim().isEmpty() || nome.equalsIgnoreCase("undefined")) {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		
		// List<Usuario> list = usuarioRepository.findUserByNome("%" + nome + "%");
		
		System.out.println("list: " + list);
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	
	
	/*Vamos supor que o carregamento de usuários seja um processo lento
	 * e queremos controlar ele com cache para agilizar o processo*/
	@GetMapping(value ="/", produces = "application/json")
	//@Cacheable("cacheusuarios")
	@CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarios() throws InterruptedException {
		
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);
		
		
		// Thread.sleep(6000);
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value ="/page/{pagina}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarioPagina(@PathVariable("pagina") int pagina) throws InterruptedException {
		
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);
		
		
		// Thread.sleep(6000);
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/", produces = "application/json")
		public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		//** Consumindo API publica externa
		/*
		 * URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
		 * URLConnection connection = url.openConnection(); InputStream is =
		 * connection.getInputStream(); BufferedReader br = new BufferedReader(new
		 * InputStreamReader(is, "UTF-8"));
		 * 
		 * String cep = ""; StringBuilder jsonCep = new StringBuilder();
		 * 
		 * while((cep = br.readLine()) != null) { jsonCep.append(cep); }
		 * 
		 * Usuario userAuxiliar = new Gson().fromJson(jsonCep.toString(),
		 * Usuario.class); usuario.setCep(userAuxiliar.getCep());
		 * usuario.setLogradouro(userAuxiliar.getLogradouro());
		 * usuario.setComplemento(userAuxiliar.getComplemento());
		 * usuario.setBairro(userAuxiliar.getBairro());
		 * usuario.setLocalidade(userAuxiliar.getLocalidade());
		 * usuario.setUf(userAuxiliar.getUf());
		 */
		
		
		
		//** Consumindo API publica externa
		
		
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
		
		
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
		
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		//Optional<Usuario> usuarioBanco = usuarioRepository.findById(usuario.getId());
		
		Optional<Usuario> userTemporario = usuarioRepository.findById(usuario.getId());
		
//		if(usuarioBanco.isPresent() && !usuarioBanco.get().getSenha().equals(usuario.getSenha())) {
//			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
//			usuario.setSenha(senhacriptografada);
//		}
		
		if(!userTemporario.get().getSenha().equals(usuario.getSenha())) {
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}
	
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) {
			
		usuarioRepository.deleteById(id);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
	}
	
	
	@DeleteMapping(value = "/removerTelefone/{id}")
	public ResponseEntity<String>  deleteTelefone(@PathVariable(value="id") Long id) {
		
		telefoneRepository.deleteById(id);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("ok");
	} 
	

}
