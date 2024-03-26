package com.example.demo.mypage.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.hall.dto.HallDTO;
import com.example.demo.mypage.service.MypageService;
import com.example.demo.user.dto.UserDTO;

@Controller
@RequestMapping("mypage")
public class MypageController {

	@Autowired
	MypageService mypageService;
	
	// 마이페이지 기본
	@GetMapping("")
	public String mypage(Model model) {
		model.addAttribute("my_info", new UserDTO());
		return "html/mypage";
	}
	
	// 로그인 회원 마이페이지
	@GetMapping("/{id}")
	public String mpage(Model model, @PathVariable("id") Integer id) {
		UserDTO myInfo = mypageService.findByID(id);
		model.addAttribute("my_info", myInfo);
		model.addAttribute("id", id);
		return "html/mypage";
	}
	
	// 공연자 정보
	@GetMapping("/performer")
	public String performer() {
		return "html/performer_info";
	}
	
	// 내 즐겨찾기
	@GetMapping("/favorite")
	public String favorite() {
		return "html/my_favorites";
	}
	
	// 예약 내역
	@GetMapping("/reserve")
	public String reserve() {
		return "html/reservation_list";
	}
	
	// 이용 내역
	@GetMapping("/uselist")
	public String uselist() {
		return "html/use_list";
	}
	
	// 리뷰 작성 및 작성 내역
	@GetMapping("/review")
	public String review() {
		return "html/my_review";
	}
	
	// 닉네임 수정
	@PostMapping("update/nickname/{id}")
	public String updateNickname(@ModelAttribute UserDTO dto, @PathVariable("id") Integer id) {	
		mypageService.updateNickname(dto, id);
		return "redirect:/mypage/" + id;
	}
	
	// 패스워드 수정
	@PostMapping("update/pw/{id}")
	public String updatePw(@ModelAttribute UserDTO dto, @PathVariable("id") Integer id) {
		mypageService.updatePw(dto, id);
		return "redirect:/mypage/" + id;
	}
	
}