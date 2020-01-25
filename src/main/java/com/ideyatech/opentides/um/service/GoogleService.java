package com.ideyatech.opentides.um.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gamify.elearning.entity.ELearningUser;
import com.ideyatech.opentides.um.entity.BaseUser;

import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.request.GooglelogRequest;

@Service
public class GoogleService {

	@Autowired
    private UserRepository userRepository;
	
	 
	
//	public ELearningUser loginbyGoogle(GooglelogRequest googleuser) {
		public BaseUser loginbyGoogle(GooglelogRequest googleuser) {
		//ELearningUser user = new ELearningUser();
		BaseUser user = new BaseUser();
		user.setEmailAddress(googleuser.getEmail());
		user.setFirstName(googleuser.getFirstname());
		user.setGoogleUserId(googleuser.getGoogleUserid());
		user.setLastName(googleuser.getLastname());
		user.setProfilePhotoUrl(googleuser.getImage());
		user.setGoogleidToken(googleuser.getGooglidToken());
		user.setProvider(googleuser.getProvider());
		
		user.setArchived(true);
		userRepository.save(user);
		return user;
	}



	

		public Optional<BaseUser> userdetaile(String email) {
			
			return userRepository.findbyEmail(email);
				    
		}





		public String updatedtail(GooglelogRequest googleuser) {
			// TODO Auto-generated method stub
			BaseUser user = new BaseUser();
		//	user.setEmailAddress(googleuser.getEmail());
			user.setFirstName(googleuser.getFirstname());
			user.setGoogleUserId(googleuser.getGoogleUserid());
			user.setLastName(googleuser.getLastname());
			user.setProfilePhotoUrl(googleuser.getImage());
			user.setGoogleidToken(googleuser.getGooglidToken());
			user.setProvider(googleuser.getProvider());
			user.setArchived(true);
			userRepository.save(user);
			return "update successfully";
		}

	
}
