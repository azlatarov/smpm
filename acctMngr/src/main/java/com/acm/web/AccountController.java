package com.acm.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acm.data.Account;
import com.acm.data.repo.AccountsRepo;

import java.util.Date;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Resource
	private AccountsRepo accountsRepo;
	
	// Restful API method
	@RequestMapping(method=RequestMethod.PUT)
	@ResponseBody
	public String createAccount(@RequestParam String fName,
								@RequestParam String lName,
								@RequestParam String email,
								@RequestParam Date birth) {
		
		accountsRepo.save(new Account(fName, lName, email, birth));
		
		return "successfully added account";
	}
	
	// Restful API method
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public String updateAccount(@RequestParam Long id,
								@RequestParam String fName,
								@RequestParam String lName,
								@RequestParam String email,
								@RequestParam Date birth) {
		
		if(!accountsRepo.existsById(id))
			return "no such account";
		
		Account a = accountsRepo.findById(id).get();
		a.setfName(fName);
		a.setlName(lName);
		a.setEmail(email);
		a.setBirth(birth);
		
		accountsRepo.save(a);
		
		return "successfully updated account";
	}
	
	// Restful API method
	@RequestMapping(method=RequestMethod.DELETE)
	@ResponseBody
	public String deleteAccount(@RequestParam Long id) {
		if(id==null)
			return "mandatory parameter is missing";
		else if(!accountsRepo.existsById(id))
			return "no such account";
		
		accountsRepo.deleteById(id);
		
		return "successfully deleted account";
	}
	
	//Show form to create new account
	@GetMapping(path="create")
	public String showCreateForm() {
		return "createForm";
	}
	
	//Process creation form
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String processAccountCreation(@Valid Account account, Errors errors) {
		if (errors.hasErrors())
			return "createForm";
		
		accountsRepo.save(account);
		
		return "redirect:/account/" + account.getId();
	}
	
	//Show user details
	@GetMapping(path="/{id}")
	public String showAccountDetails(@PathVariable Long id, Model model) {
		Account account = accountsRepo.findById(id).get();
		model.addAttribute(account);
		return "accountDetails";
	}
	
	//Show form to update account
	@GetMapping(path="update/{id}")
	public String showUpdateForm(@PathVariable Long id, Model model) {
		Account account = accountsRepo.findById(id).get();
		model.addAttribute("account", account);
		return "updateForm";
	}
	
	//Process update form
	@RequestMapping(value="/update/{id}", method=RequestMethod.POST)
	public String processAccountUpdate(@Valid Account account, Errors errors) {
		if (errors.hasErrors())
			return "updateForm";
		
		accountsRepo.save(account);
		
		return "redirect:/account/" + account.getId();
	}
	
	//Show form to update account
	@GetMapping(path="delete/{id}")
	public String showDeleteConfirm(@PathVariable Long id, Model model) {
		Account account = accountsRepo.findById(id).get();
		model.addAttribute("account", account);
		return "deleteConfirmation";
	}
	
	//Show form to update account
	@RequestMapping(path="delete/{id}", method=RequestMethod.POST)
	public String processAccountDelete(@PathVariable Long id) {
		accountsRepo.deleteById(id);
		
		return "redirect:/accounts/list";
	}

}
