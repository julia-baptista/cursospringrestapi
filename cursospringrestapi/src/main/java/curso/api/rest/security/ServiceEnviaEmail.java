package curso.api.rest.security;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {
	
	private String userName = "juliabaptistadev@gmail.com";
	private String senha = "emjf huvw hkrs jold";
	
	
	public void enviarEmail (String assunto, String emailDestino, String mensagem) throws Exception {
		
		Properties properties = new Properties();
		properties.put("mail.smtp.ssl.trust", "*");
		properties.put("mail.smtp.auth", "true"); /*Autorização*/
		properties.put("mail.smtp.starttls", "true"); /*Autenticação*/
		properties.put("mail.smtp.host", "smtp.gmail.com"); /*Servidor Google*/
		properties.put("mail.smtp.port", "465"); /*Porta do servidor*/
		properties.put("mail.smtp.socketFactory.port", "465"); /*Especifica porta socket*/
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); /*Classe de conexão socket*/
		
		Session sesssion = Session.getInstance(properties, new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication(userName, senha);
				
			}
		});
		
		Address[] toUser = InternetAddress.parse(emailDestino);
		
		Message message = new MimeMessage(sesssion);
		message.setFrom(new InternetAddress(userName)); /*Quem está enviando, no caso nós do projeto*/
		message.setRecipients(Message.RecipientType.TO, toUser); /*Para quem vai o e-mail*/
		message.setSubject(assunto);
		message.setText(mensagem);
		
		Transport.send(message);
	}

}
