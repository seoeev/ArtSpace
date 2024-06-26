package com.example.demo.company.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.SessionUtil;
import com.example.demo.company.dto.CompanyDTO;
import com.example.demo.company.service.CompanyService;
import com.example.demo.file.service.FileService;
import com.example.demo.hall.dto.HallDTO;
import com.example.demo.hall.dto.ReviewDTO;
import com.example.demo.hall.service.HallService;
import com.example.demo.mypage.service.MypageService;
import com.example.demo.reservation.dto.ReservationDTO;
import com.example.demo.reservation.dto.ReservationEquipmentDTO;
import com.example.demo.reservation.dto.ReserveDateDTO;
import com.example.demo.user.dto.UserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")
public class CompanyController {

	SessionUtil user_session = new SessionUtil();

	@Autowired
	MypageService mypageService;

	@Autowired
	CompanyService companyService;

	@Autowired
	FileService fileService;

	@Autowired
	HttpSession session;

	@Autowired
	HallService hallService;

	// 공통부분 정리
	private void myInfo(Model model) {
		user_session.setSessionValue(session);
		UserDTO myInfo = mypageService.findByID(user_session.getUser_id());
		model.addAttribute("my_info", myInfo);
		model.addAttribute("user_id", user_session.getUser_id());
		model.addAttribute("nickname", user_session.getNickname());
	}

	// 법인 마이페이지 기본
	@GetMapping("")
	public String company(Model model) {
		myInfo(model);

		String authority = user_session.getAuthority();
		if (user_session.getUser_id() != null) {
			if (authority.equals("SCN") || authority.equals("SCY")) {
				return "html/company/company_page";
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/login";
		}

	}

	// 닉네임 수정
	@PostMapping("/update/nickname")
	public String updateNickname(@ModelAttribute UserDTO dto) {

		user_session.setSessionValue(session);
		if (user_session.getUser_id() == null) {
			return "redirect:/login";
		}

		dto.setUser_id(user_session.getUser_id());
		mypageService.updateNickname(dto);
		session.setAttribute("nickname", dto.getNickname());
		return "redirect:/mypage";
	}

	// 패스워드 수정
	@PostMapping("/update/pw")
	public String updatePw(@ModelAttribute UserDTO dto) {

		user_session.setSessionValue(session);
		if (user_session.getUser_id() == null) {
			return "redirect:/login";
		}

		dto.setUser_id(user_session.getUser_id());
		mypageService.updatePw(dto);
		return "redirect:/mypage";
	}

	// 핸드폰번호 수정
	@PostMapping("/update/phone")
	public String updatePhone(@ModelAttribute UserDTO dto) {

		user_session.setSessionValue(session);
		if (user_session.getUser_id() == null) {
			return "redirect:/login";
		}

		dto.setUser_id(user_session.getUser_id());
		mypageService.updatePhone(dto);
		return "redirect:/mypage";
	}

	// 사업자정보
	@GetMapping("/info")
	public String companyInfo(Model model) {
		myInfo(model);
		String authority = user_session.getAuthority();
		if (user_session.getUser_id() != null) {
			if (authority.equals("SCN") || authority.equals("SCY")) {

				CompanyDTO comDTO = companyService.findByID(user_session.getUser_id());

				model.addAttribute("com_info", comDTO);
				int company_id = comDTO.getCompany_id();
				// 파일 제출 여부를 알기 위한 제출 file count
				int fileCount = companyService.fileCount(company_id);
				model.addAttribute("fileCount", fileCount);

				return "html/company/company_info";
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/login";
		}
	}

	// 사업자정보 등록
	@PostMapping("/info")
	public String companyInfoUpdate(@ModelAttribute CompanyDTO dto,
			@RequestParam(value = "file", required = false) MultipartFile[] files,
			@RequestParam("company_id") Integer company_id) {
		user_session.setSessionValue(session);
		if (user_session.getUser_id() == null) {
			return "redirect:/login";
		}

		dto.setUser_id(user_session.getUser_id());
		companyService.updateInfo(dto);

		if (files != null) {
			fileService.uploadObject(files, company_id);
		}

		return "redirect:/company/info";
	}

	// 등록한 공연장 목록
	@GetMapping("/hall")
	public String companyHall(Model model) {
		myInfo(model);

		String authority = user_session.getAuthority();
		if (user_session.getUser_id() != null) {
			if (authority.equals("SCN") || authority.equals("SCY")) {
				List<HallDTO> myHall = companyService.getHall(user_session.getUser_id());
				model.addAttribute("my_hall", myHall);

				return "html/company/company_hall";
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/login";
		}
	}

	// 공연장 정보 수정
	@PostMapping("form/{id}")
	public String hallUpdate(Model model, @PathVariable("id") Integer id) {
		user_session.setSessionValue(session);
		if (user_session.getUser_id() == null) {
			return "redirect:/login";
		}

		HallDTO hallInfo = hallService.findById(id, user_session.getUser_id());
		model.addAttribute("hall_info", hallInfo);

		return "redirect:/hall/form" + id;
	}

	// 공연장 정보 삭제
	@PostMapping("/hall/delete")
	public String hallDelete(@RequestParam("hall_id") Integer hall_id) {
		user_session.setSessionValue(session);
		if (user_session.getUser_id() == null) {
			return "redirect:/login";
		}

		companyService.hallDelete(hall_id);

		return "redirect:/company/hall";
	}

	// 해당 법인이 등록한 공연장에 대한 예약 정보
	@GetMapping("/reserve")
	public String companyReserve(Model model) {
		myInfo(model);
		String authority = user_session.getAuthority();
		if (user_session.getUser_id() != null) {
			if (authority.equals("SCN") || authority.equals("SCY")) {

				List<ReservationDTO> reserveList = companyService.getReserve(user_session.getUser_id());
				model.addAttribute("reserve_list", reserveList);
				
				// 예약 날짜 중 가장 빠른 날짜
				Map<Integer, LocalDateTime> earliestReserveDates = mypageService.getEarliestReserveDates(reserveList);
				model.addAttribute("earliest_reserve_date", earliestReserveDates);

				model.addAttribute("current_date", LocalDateTime.now()); // 현재 날짜 추가
				
				return "html/company/company_reserve";
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/login";
		}
	}

	// 공연장 예약 상세 정보
	// 예약 상세
	@PostMapping("/reserve")
	@ResponseBody
	public Map<String, Object> reserveDetail(@RequestParam("reserve_id") Integer reserve_id) {
		Map<String, Object> response = new HashMap<>();
		try {
			ReservationDTO reservationDetail = mypageService.reserveDetail(reserve_id);
			List<ReserveDateDTO> reserveDate = mypageService.reserveDate(reserve_id);
			List<ReservationEquipmentDTO> reservationEquipments = mypageService.getAllReservationEquip(reserve_id);
			response.put("reservationDetail", reservationDetail);
			response.put("reserveDate", reserveDate);
			response.put("reservationEquipments", reservationEquipments);
			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}

	// 해당 공연장에 대한 예약 삭제
	@PostMapping("/reserve/delete")
	public String reserveDelete(Model model, @RequestParam("reserve_id") Integer reserve_id) {
		user_session.setSessionValue(session);
		if (user_session.getUser_id() == null) {
			return "redirect:/login";
		}

		companyService.reserveDelete(reserve_id);
		return "redirect:/company/reserve";
	}

}
