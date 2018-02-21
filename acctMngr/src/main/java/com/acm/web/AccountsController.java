package com.acm.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acm.data.repo.AccountsRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@Controller
@RequestMapping("/accounts")
public class AccountsController {
	private static final String defaultSortBy = "fName";
	
	@Autowired
	private AccountsRepo accountsRepo;

	@GetMapping(path="/list")
	public String listAccountsSorted(
			//@RequestParam(value="account_id", defaultValue="0") long accountId,
			@RequestParam(value="sort_by", defaultValue=defaultSortBy) String sortBy,
			Model model) {
		
		//possible upgrade to prevent errors if illegal sort option is chosen
		//String sortToUse = (EnumUtils.getEnum(SortOptions.class, sortBy) != null) ? sortBy : defaultSortBy;
		
		model.addAttribute("accountsList", accountsRepo.findAll(Sort.by(Order.asc(sortBy))));
		
		return "accounts";
	}

}