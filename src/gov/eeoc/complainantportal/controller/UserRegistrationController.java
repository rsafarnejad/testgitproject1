package gov.eeoc.complainantportal.controller;


import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.eeoc.complainantportal.service.ComplainantDataService;
import gov.eeoc.complainantportal.service.WebCacheManager;
import gov.eeoc.complainantportal.util.Const;
import gov.eeoc.complainantportal.util.WebUtil;

@ManagedBean(name = "userRegistrationController")
@SessionScoped
public class UserRegistrationController implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private ComplainantDataService complainantDataService;
	@Inject
	private WebCacheManager webCacheManager;
	private String securityQuestion;
	private String securityAnswer;
	private String userAnswer;
	private String documentSubmitterEmail;
	private Map<String, String> questionAnswer = new HashMap<String, String>();
	
	static final Logger log = LoggerFactory.getLogger(UserRegistrationController.class);
	
	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}
	public String getDocumentSubmitterEmail() {
		return documentSubmitterEmail;
	}

	public void setDocumentSubmitterEmail(String documentSubmitterEmail) {
		this.documentSubmitterEmail = documentSubmitterEmail;
	}
	public Map<String, String> getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(Map<String, String> questionAnswer) {
		this.questionAnswer = questionAnswer;
	}
	
	@PostConstruct
	public void init() {
		questionAnswer = complainantDataService.generateChallengeQuestionAnswer();
		if (questionAnswer.size() == 1) {
			for (Map.Entry<String, String> entry : questionAnswer.entrySet()) {
				log.debug("Key : " + entry.getKey() + " Value : "
						+ entry.getValue());
				securityQuestion = entry.getKey();
				securityAnswer = entry.getValue();
			}
		}
	}

	/**
	 * Email Submission Process
	 * 
	 */
	
	public void sendEmail(){
		log.debug("sendEmail()..start");
		boolean isValidAnswer = false;
		
		isValidAnswer = getSecurityAnswer().equalsIgnoreCase(getUserAnswer());
		if (isValidAnswer == true) {
			log.debug("Challenge question answered correctly");
		String token = complainantDataService.generateToken();
		log.debug("Token :" + token);		
		webCacheManager.addToken(getDocumentSubmitterEmail().toLowerCase(), token);
		try {
			
			StringBuffer buf = new StringBuffer();
			buf.append("Thank you for using the Complainant Portal.");
			buf.append("<br/><br/>");
			buf.append("Your passcode is ");
			buf.append(token);
			buf.append("<br/>");
			buf.append("Please click on the link below to upload documents in the Complainant Portal. ");
			buf.append("<br/></br>");
			buf.append("<a href='");
			buf.append(WebUtil.getServerContext(FacesContext
					.getCurrentInstance()));
			buf.append("/public/complainantportal.jsf");
			buf.append("'>ComplainantPortal Document Upload</a>");
			buf.append("<br/></br>");
			buf.append("If you are unable to upload documents with the link above then please copy and paste the following link into web browser:");
			buf.append("<br/></br>");
			buf.append(WebUtil.getServerContext(FacesContext
					.getCurrentInstance()));
			buf.append("/public/complainantportal.jsf");
			buf.append("<br/></br>");
			buf.append("If you did not make this request, please contact EEOC Help Desk immediately at (202) 663-4767.");
			buf.append("<br/><br/>");
			buf.append("Office Of Information Technology");
			buf.append("<br/>");
			buf.append("U.S. Equal Employment Opportunity Commission");
			buf.append("<br/><br/>");
			buf.append("<i>This is an autogenerated email. Please do not reply to this email.</i>");
			
			sendEmail(this.documentSubmitterEmail,Const.FROM_EMAIL,"Your Passcode for Complainant Portal",buf.toString());
				
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest request = (HttpServletRequest)ec.getRequest();
			HttpServletResponse response = (HttpServletResponse)ec.getResponse();
			FacesContext.getCurrentInstance().getExternalContext().redirect("emailconfirmation.jsf");
			request.getSession().invalidate();
			
					
		} catch (Exception e) {
			
			log.debug("Error while sending email: ");
			log.debug(documentSubmitterEmail);
			e.printStackTrace();

		}
		
		}else {
			init();
			setUserAnswer("");
			FacesMessage msg = new FacesMessage("Please answer the question correctly ");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
		
	}
	

	private void sendEmail(String email, String from, String subject,String content) throws Exception {

		String host = Const.SMTP_SERVER;

		Properties properties = System.getProperties();

		properties.setProperty("mail.smtp.host", host);

		Session session = Session.getDefaultInstance(properties);
		try {

			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

			message.setSubject(subject);

			message.setContent(content, "text/html");

			Transport.send(message);
			
			log.debug("Security Token email send successfully:"+ email);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}


	private ServletRequest getSession() {
		// TODO Auto-generated method stub
		return null;
	}
}
