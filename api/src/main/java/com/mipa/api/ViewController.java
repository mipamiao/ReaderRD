package com.mipa.api;

import com.mipa.auth.Security.UserSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/api/view")
public class ViewController {
	@GetMapping(path = "write-in-web")
	public String writeInWeb(
			//@AuthenticationPrincipal UserSecurity userSecurity,
			//@RequestParam(name = "chapterId") String chapterId
	){
		return "redirect:/writerPage.html";
	}
}
