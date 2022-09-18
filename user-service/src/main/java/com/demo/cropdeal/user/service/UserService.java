package com.demo.cropdeal.user.service;

import com.demo.cropdeal.user.dto.UserDto;
import com.demo.cropdeal.user.model.Address;
import com.demo.cropdeal.user.model.Bank;
import com.demo.cropdeal.user.model.CropItem;
import com.demo.cropdeal.user.model.User;
import com.demo.cropdeal.user.repository.AddressRepository;
import com.demo.cropdeal.user.repository.BankRepository;
import com.demo.cropdeal.user.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService implements IUserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BankRepository bankRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Autowired
	EmailSenderService emailSenderService;
	
	@Autowired
	RestTemplate restTemplate;
	
	
	@Override
	public String addUser(UserDto userDto) {
		
		String s1 = " ";
		
		var user = userDto.getUserFromUserDto(userDto);
		
		boolean usernameAbsent = userRepository.getByUserName(user.getUserName()) == null;
		boolean MobileNoAbsent = userRepository.getByPhoneNumber(user.getPhoneNumber()) == null;
		boolean EmaiLIdAbsent = userRepository.getByEmail(user.getEmail()) == null;
		boolean AccountNoAbsent = bankRepository.getByAccountNo(user.getBank().getAccountNo()) == null;
		
		if (usernameAbsent && MobileNoAbsent && EmaiLIdAbsent && AccountNoAbsent) {
			
			String userRole = user.getRoles().split("_")[1];
			
			user.setRoles(userRole);
			
			//bank id 6324e2a86879610ec04cdc35  address id 6324e2a86879610ec04cdc36  user id:6324e2a86879610ec04cdc37
			
			User user1;
			Bank bank=bankRepository.save(user.getBank());
			
			Address address=addressRepository.save(user.getAddress());
			user1=userRepository.save(user);
			System.out.println("bank id "+bank.getId()+"address id "+address.getId()+"user id:"+user1.getId());
			
			
			
			emailSenderService.sendEmail(user.getEmail(),
				user.getFullName() + " you are registered successfully..as " + userRole, "Registration Status");
			return "user Added";
		} else {
			if (usernameAbsent == false) {
				s1 = s1 + "  *username already taken*";
			}
			
			if (MobileNoAbsent == false) {
				s1 = s1 + "  *mobile number already exists*";
			}
			
			if (EmaiLIdAbsent == false) {
				s1 = s1 + "  *email already taken*";
			}
			if (AccountNoAbsent == false) {
				s1 = s1 + "  *account number already exists*";
			}
			
			
		}
		return s1;
		
		
	}
	
	@Override
	public  String deleteUser(String userId) {
		
		/*
		 * fetch user from db
		 * check if user is not null
		 *     - if not null then delete the user
		 *           cross check by fetching same user from db and comparing with null
		 *           return msg "user deleted successfully"
		 *     - else return msg "invalid id user not present"
		 */
		var user = userRepository.getById(userId);
		if (user != null) {
			
			long accountNo=user.getBank().getAccountNo();
			String addressId=user.getAddress().getId();
			bankRepository.deleteByAccountNo(accountNo);
		    addressRepository.deleteById(addressId);
			userRepository.deleteById(userId);
			
			return "user deleted successfully";
			
		}
		else
		    return "invalid id user not present";
	}
	
	@Override
	public User getUser(String userId) {
		
		/*
		 * if user data is present in database then return user
		 *
		 * For this fetch user using the given id
		 * check if user is not null
		 *   -if not null then return user data
		 *
		 */
		
		var user = userRepository.getById(userId);
		if (user != null) {
			return user;
		}
		
		return null;
	}
	
	
	public User getUserByEmail(String email) {
		
		/*
		 * if user data is present in database then return user
		 *
		 * For this fetch user using the given email id
		 * check if user is not null
		 *   -if not null then return user data
		 *
		 */
		
		var user = userRepository.getByEmail(email);
		if (user != null) {
			return user;
		}
		
		return null;
	}
	
	
	public User getUserByUsername(String username) {
		
		
		var user = userRepository.getByUserName(username);
		if (user != null) {
			return user;
		}
		
		return null;
	}
	
	
	public List<User> getAllUsers() {
		List<User> users = userRepository.findAll();
		if (users != null) {
			System.out.println(users);
			return users;
			
		} else {
			return new ArrayList<>();
		}
		
	}
//	
//	[User [id=6322d5994960672fccd90a15, fullName=test, roles=ROLE_FARMER, userName=null, password=$2a$10$ZMoeafRyW0MfjQPZqdmDdexEEBOd4cf8jlvFbpH5Oi5Ey3AJDQC8W, phoneNumber=+918601297319, email=test, active=true, bank=null, address=null, cropIds=null],
//	 User [id=632404dc08432d6a938c5313, fullName=Anmol, roles=ROLE_FARMER, userName=anmol, password=dfjalkfa, phoneNumber=123413333, email=anmol@gamial.com, active=false, bank=Bank [accountNo=423423, accountHolderName=afasfa, bankName=as, bankBranch=afas, bankIFSC=FSdf], address=Address [Id=null, houseNo=adfs, streetName=fasdfa, localityName=fasdfa, pincode=23423, city=Adsfas, state=dsfasdf, country=dsafas], cropIds=null],
//	User [id=6324b0486abb006534c6edbb, fullName=test, roles=ROLE_FARMER, userName=farmer1, password=test, phoneNumber=94833678848, email=testtt@gmail.com, active=null, bank=Bank [accountNo=336699996633, accountHolderName=test, bankName=Bank of Maharashtra, bankBranch=Jalochi, bankIFSC=BOMH26155799], address=Address [Id=6324b0486abb006534c6edba, houseNo=3, streetName=xyz street, localityName=Amarsingh colony, pincode=413110, city=Baramati, state=Maharashtra, country=India], cropIds=null], 
//	User [id=6324b24a6abb006534c6edbe, fullName=Rutuja Bhoite, roles=ROLE_DEALER, userName=ritzzz, password=rsb006, phoneNumber=94833678877, email=rsb@gmail.com, active=null, bank=Bank [accountNo=10987654321, accountHolderName=Rutuja Bhoite, bankName=Bank of Maharashtra, bankBranch=Jalochi, bankIFSC=BOMH26155799], address=Address [Id=6324b24a6abb006534c6edbd, houseNo=2, streetName=ABCxyz street, localityName=abc colony, pincode=413110, city=Baramati, state=Maharashtra, country=India], cropIds=null], 
//	User [id=6324e1c36879610ec04cdc34, fullName=abc abc abc, roles=ROLE_FARMER, userName=rit123, password=rsb006, phoneNumber=9483328877, email=r@gmail.com, active=null, bank=Bank [accountNo=16987654321, accountHolderName=abc abc abc, bankName=Bank of Maharashtra, bankBranch=Jalochi, bankIFSC=BOMH26155799], address=Address [Id=6324e1c36879610ec04cdc33, houseNo=2, streetName=ABCxyz street, localityName=abc colony, pincode=413110, city=Baramati, state=Maharashtra, country=India], cropIds=null], 
	//User [id=6324e2a86879610ec04cdc37, fullName=hh, roles=DEALER, userName=xyz123, password=rsb006, phoneNumber=12345, email=hh@f.f, active=true, bank=Bank [accountNo=54, accountHolderName=hh, bankName=hh, bankBranch=hh, bankIFSC=454], address=Address [Id=6324e2a86879610ec04cdc36, houseNo=33, streetName=hh, localityName=hh, pincode=45, city=hh, state=hh, country=hh], cropIds=[6325c5eb008022651a7786f1, 6325c7b7008022651a7786f3, 6326143af46b5d0ea45a50d0]], //
	//User [id=6325712ab655a57d39b0ff32, fullName=Anmol, roles=ROLE_FARMER, userName=null, password=$2a$10$2Xcf5qNUffto9hj9kgTfXuBIl03tV7YMOD3RaotfgpqHvU3t1BS8S, phoneNumber=null, email=admin@gmail.com, active=null, bank=null, address=null, cropIds=null], User [id=6325b0da7fd041170c5b7046, fullName=Anmol, roles=ROLE_FARMER, userName=null, password=$2a$10$JtfLu24ciMLumkiJYOHjve.MloVY/bLEVGj5FgRmiBEDDOWYe5I6y, phoneNumber=null, email=anmolsh.1908.1@gmail.com, active=null, bank=null, address=null, cropIds=null],
	//User [id=6325b1547fd041170c5b7047, fullName=Anmol, roles=ROLE_FARMER, userName=null, password=$2a$10$aeaEJZ.Wk0QTkVXo/BScWu3gozfRe.TvL0lTuBgoGszRnrKnV49yq, phoneNumber=null, email=anmolsh@gmail.com, active=null, bank=null, address=null, cropIds=null], User [id=6325cc6a008022651a7786f6, fullName=sss abc, roles=FARMER, userName=xyzsss123, password=rsb006, phoneNumber=9466328877, email=xyz@gmail.com, active=null, bank=Bank [accountNo=222654321, accountHolderName=xy abc abc, bankName=Bank of Maharashtra, bankBranch=Jalochi, bankIFSC=BOMH26155799], address=Address [Id=6325cc6a008022651a7786f5, houseNo=2, streetName=ABCxyz street, localityName=abc colony, pincode=413110, city=Baramati, state=Maharashtra, country=India], cropIds=null], 
	//User [id=632610dcf46b5d0ea45a50cf, fullName=ss abc, roles=FARMER, userName=xysss123, password=rsb006, phoneNumber=943228877, email=xyz@mail.com, active=null, bank=Bank [accountNo=2288654321, accountHolderName=xy abc abc, bankName=Bank of Maharashtra, bankBranch=Jalochi, bankIFSC=BOMH26155799], address=Address [Id=632610dcf46b5d0ea45a50ce, houseNo=2, streetName=ABCxyz street, localityName=abc colony, pincode=413110, city=Baramati, state=Maharashtra, country=India], cropIds=null]]
//
//	
//	
	
	public String markUserStatus(String userId, boolean userStatus) {
		User user = userRepository.getById(userId);
		
		if (user != null) {
			user.setActive(userStatus);
			userRepository.save(user);
			if (userRepository.getById(userId).getActive().equals(userStatus)) {
				return "updated user status as " + (user.getActive() ? "active" : "in-active");
			} else {
				return "user not updated";
			}
		} else {
			return "invalid user id";
		}
		
		
	}
	
	public CropItem addCrops(String userId,CropItem cropItem){
		
		List<String> list=userRepository.getById(userId).getCropIds();
		String url="http://localhost:8083/api/v1/cropitems";
		System.out.println(cropItem);
		CropItem crop=restTemplate.postForObject(url,cropItem,CropItem.class);
		if(crop!=null) {
			list.add(crop.getId());
			System.out.println("crop id is "+crop.getId());
			System.out.println("crop is : "+crop);
			User user=userRepository.getById(userId);
			user.setCropIds(list);
			user=userRepository.save(user); 
			System.out.println(user);
			return crop;
			
		}
		else
			return new CropItem();
	}
	
//	CropItem [id=null, name=tomato, type=veg, qnt=25Kg, price=40]
//	crop id is 6325b244008022651a7786ec
//	crop is : CropItem [id=6325b244008022651a7786ec, name=tomato, type=veg, qnt=25Kg, price=40]
//	User [id=6324e2a86879610ec04cdc37, fullName=798ghjgjj, roles=FARMER, userName=xyz123, password=rsb006, phoneNumber=900090, email=hjj, active=null, bank=Bank [accountNo=22222654321, accountHolderName=ggggggg, bankName=Bank of Maharashtra, bankBranch=Jalochi, bankIFSC=BOMH26155799], address=Address [Id=6324e2a86879610ec04cdc36, houseNo=2, streetName=ABCxyz street, localityName=abc colony, pincode=413110, city=Baramati, state=Maharashtra, country=India], cropIds=[6325b244008022651a7786ec]]
//
//	
	
	public List<String> getUserCropIdList(String userId){
		User user=userRepository.getById(userId);
		if(user.getCropIds()!=null) {
			return user.getCropIds();
		}
		else 
			return new ArrayList<>();
	}
	
	@Override
	public User updateUser(String userId, User user) {
		
		//fetch user from database using given id
		var user1 = userRepository.getById(userId);
		
        /*
         
          check if requested  user data is not null
          check all the fields with null value and update that user fetched from database 
          with given details that are not null
           
       */
		
		if (user != null) {
			
			
			if (user.getRoles() != null && user.getRoles().isBlank() == false) {
				
				String userRole = user.getRoles().split("_")[1];
				user1.setRoles(userRole);
			}
			
			if (user.getEmail() != null && user.getEmail().isBlank() == false) {
				user1.setEmail(user.getEmail());
			}
			if (user.getPhoneNumber()  != null && user.getPhoneNumber().isBlank() == false) {
				user1.setPhoneNumber(user.getPhoneNumber());
			}
			if (user.getPassword() != null) {
				user1.setPassword(user.getPassword());
			}
			if (user.getFullName().length() != 0) {
				user1.setFullName(user.getFullName());
			}
			user1.setActive(user.getActive());
			
		/*
		 * 
		 if(user.getUserStatus()!=null ) {
			user1.setUserStatus(user.getUserStatus());
		}
		
		*/
			
			var bank1 = updateBank(user, user1);
			
			var address1 = updateAddress(user, user1);
			
			bankRepository.save(bank1);
			addressRepository.save(address1);
			
			user1.setBank(bank1);
			user1.setAddress(address1);
			
			userRepository.save(user1);
			return user1;
		}
		
		
		return user1;
		
	}
	
	public Bank updateBank(User user, User user1) {
		var bank1 = user1.getBank();
		if (user.getBank() != null) {
			
			if (user.getBank().getAccountHolderName() != null && user.getBank().getAccountHolderName().isBlank() == false) {
				bank1.setAccountHolderName(user.getBank().getAccountHolderName());
			}
			
			if (user.getBank().getAccountNo() != null && user.getBank().getAccountNo() != 0) {
				bank1.setAccountNo(user.getBank().getAccountNo());
			}
			
			if (user.getBank().getBankBranch() != null && user.getBank().getBankBranch().isBlank() == false) {
				bank1.setBankBranch(user.getBank().getBankBranch());
			}
			
			
			if (user.getBank().getBankIFSC() != null && user.getBank().getBankIFSC().isBlank() == false) {
				bank1.setBankIFSC(user.getBank().getBankIFSC());
			}
			
			if (user.getBank().getBankName() != null && user.getBank().getBankName().isBlank() == false) {
				bank1.setBankName(user.getBank().getBankName());
			}
			
			
		} else {
			bank1 = user1.getBank();
		}
		
		return bank1;
	}
	
	public Address updateAddress(User user, User user1) {
		
		var address1 = user1.getAddress();
		if (user.getAddress() != null) {
			
			if (user.getAddress().getCity() != null && user.getAddress().getCity().isBlank() == false) {
				address1.setCity(user.getAddress().getCity());
			}
			
			if (user.getAddress().getCountry() != null && user.getAddress().getCountry().isBlank() == false) {
				address1.setCountry(user.getAddress().getCountry());
			}
			
			
			if (user.getAddress().getHouseNo() != null && user.getAddress().getHouseNo().isBlank() == false) {
				address1.setHouseNo(user.getAddress().getHouseNo());
			}
			
			
			if (user.getAddress().getLocalityName() != null && user.getAddress().getLocalityName().isBlank() == false) {
				address1.setLocalityName(user.getAddress().getLocalityName());
			}
			
			if (user.getAddress().getPincode() != 0) {
				address1.setPincode(user.getAddress().getPincode());
			}
			
			
			if (user.getAddress().getState() != null && user.getAddress().getState().isBlank() == false) {
				address1.setState(user.getAddress().getState());
				
			}
			
			if (user.getAddress().getStreetName() != null && user.getAddress().getStreetName().isBlank() == false) {
				address1.setStreetName(user.getAddress().getStreetName());
			}
			
		} else {
			address1 = user1.getAddress();
		}
		return address1;
		
	}


	
	
}
