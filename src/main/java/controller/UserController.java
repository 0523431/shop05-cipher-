package controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.Item;
import logic.Sale;
import logic.SaleItem;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private ShopService service;
	
	@GetMapping("*")
	public String form(Model model) {
		model.addAttribute(new User());
		return null;
	}
	
	/* 	비밀번호 : 해쉬화 db에 저장
		이메일 : 키는 id의 해쉬값에서 결정 / 암호화 db에 저장  */
	@PostMapping("userEntry")
	public ModelAndView userEntry(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		
		// 유효성 검증
		// @Valid : 이 가능하려면 User 객체에 어노테이션이 되어있어야함
		if(bresult.hasErrors()) {
			bresult.reject("error.input.user"); // (최상단)입력정보에 문제가 있습니다
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		
		try {
			// 암호화 부분 추가
			try {
				String cipherPass = cipher_util.CipherUnit.makehash(user.getPassword());
				String key = cipher_util.CipherUnit.makehash(user.getUserid());
				String cipherEmail = cipher_util.CipherUnit.encrypt(user.getemail(), key.substring(0,16));
				user.setPassword(cipherPass);
				user.setemail(cipherEmail);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			// useraccount테이블에 내용 등록
			service.userInsert(user);
			// login.jsp로 이동
			// 이동방법1 (아이디값 넘겨주기)
			mav.setViewName("user/login");
			mav.addObject("user", user); // login.jsp로 넘어가는 값 modelAttribute="user"
			
			// 이동방법2 (넘겨주지않음)
			// mav.addObject("user", new User()); // 값이 나타나지않음
			
			// 이동방법3 (넘겨주지않음)
			// mav.setViewName("redirect:user/login.shop"); // 값이 나타나지 않음
		} catch(DataIntegrityViolationException e) {
			e.printStackTrace();
			bresult.reject("error.duplicate.user");
		}
		return mav;
	}
	
	@PostMapping("login")
	public ModelAndView login(@Valid User user, BindingResult bresult, HttpSession session) throws Exception {
		
		ModelAndView model = new ModelAndView(); // view
		
		// validation
		if(bresult.hasErrors()) {
			bresult.reject("error.input.user");
			model.getModel().putAll(bresult.getModel());
			return model;
		}
		
		try {
			User dbUser = service.getUser(user.getUserid());
			
//			String inputpass = user.getPassword();
//			// 내가 입력한 비밀번호를 암호화하는 과정
//			MessageDigest md = MessageDigest.getInstance("SHA-256");
//			String hashpass = "";
//			byte[] plain = inputpass.getBytes();
//			byte[] hash = md.digest(plain);
//			for (byte h : hash) {
//				hashpass += String.format("%02X", h);
//			}

			String inputpass = user.getPassword();
			String hashpass = cipher_util.CipherUnit.makehash(inputpass);
			System.out.println("db : " + dbUser.getPassword());
			System.out.println(hashpass);
			
			if(!dbUser.getPassword().equals(hashpass)) {
				bresult.reject("error.login.password");
				return model;
			} else {
				try {
					String key = cipher_util.CipherUnit.makehash(user.getUserid());
					String restoreEmail = cipher_util.CipherUnit.decrypt(dbUser.getemail(), key.substring(0,16));
					dbUser.setemail(restoreEmail);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				session.setAttribute("loginUser", dbUser);
				model.setViewName("redirect:main.shop");
			}
		} catch(LoginException e) {
			// mybatis에서 없는 아이디로 로그인시 나오는 오류 해결
			// 원래 있던 EmptyResultDataAccessException은 spring jdbc에서만 발생하는 예외
			// 내가 만든 예외로 바꿔주기
			e.printStackTrace();
			bresult.reject("error.login.id");
		}
		return model;
	}
	
//	@RequestMapping("logout")
//	public ModelAndView logout(HttpSession session) {
//		ModelAndView mav = new ModelAndView();
//		session.invalidate();
//		mav.setViewName("redirect:login.shop");
//		return mav;
//	}
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login.shop";
	}
	
	/* check로 시작하고, session로 끝나면 ==> 
	 * checkmain : 핵심로직이야 (받아서 user/main.shop으로 보내는거)
	 * 
	 * session은 aop를 실행하게 만드는 조건 때문에 넣어줌
	 * 
	 * UserLoginAspect 클래스에 해당하는 핵심로직
	 */
	@RequestMapping("main")
	public String checkmain(HttpSession session) {
		return "user/main";
	}
	
	/*  AOP에 걸리는 핵심로직
		1. 로그인 확인
		2. session등록 아이디와 로그인 아이디 확인
		3. 관리자인 경우 확인
	*/
//	@RequestMapping("mypage")
	@GetMapping("mypage")
	public ModelAndView checkmypage(String id, HttpSession session) throws Exception {
		ModelAndView mav = new ModelAndView();
		// heamaster인 경우 파라미터에 해당하는 id조회
		// (관리자때문에 userLogin은 사용할 수 없음)
		User user = service.getUser(id);
		
		// 암호화된 이메일 복호화해서 출력하기
		String email = user.getemail();
		// userid의 해쉬값중 16자리를 key값으로 쓸거야
		String key = cipher_util.CipherUnit.makehash(user.getUserid()).substring(0,16);
		String restoreEmail = cipher_util.CipherUnit.decrypt(email, key);
		user.setemail(restoreEmail);
		
		// 사용자가 주문한 모든 주문내역을 조회
		// 관리자일 경우, 모든 주문 내역을 조회
		List<Sale> salelist = service.salelist(id);
		for(Sale sale : salelist) {
			// 주문번호에 해당하는 주문 상품 내역 조회(아이템 리스트)
			List<SaleItem> saleitemlist = service.saleItemList(sale.getSaleid());
			for(SaleItem itemlist :saleitemlist) { // 주문내역 1개 saleitemlist
				// 주문내역 1개에 해당하는 Item 조회
				// 주문내역이 있을 때, 아이템 삭제가 불가능함 (db 외래키 설정)
				Item item = service.itemInfo(itemlist.getItemid());
				itemlist.setItem(item); // SaleItem에 저장
			}
			sale.setItemList(saleitemlist);
			
			// Sale에 있는 User에 사용자정보(값) 넣어주기
			try {
				User username = service.getUser(sale.getUserid());
				sale.setUser(username); // sale은 salelist에 저장되게 됨
			} catch(LoginException e) {
				// 탈퇴한 회원이 있을 경우	
			}
		}
		
		mav.addObject("user", user);
		mav.addObject("salelist", salelist); // 결국 모든 정보는 다 salelist가 가지고 있게 됨
		return mav;
	}
	
	/* AOP 대상
	 * update, delete 두개의 요청을 하나의 method로 실행할 수 있음
	 */
	@GetMapping(value= {"update", "delete"})
	public ModelAndView checkview(String id, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		User user = service.getUser(id);
		try {
			// 암호화된 이메일
			String key = cipher_util.CipherUnit.makehash(user.getUserid());
			String restoreEmail = cipher_util.CipherUnit.decrypt(user.getemail(), key.substring(0,16));
			user.setemail(restoreEmail);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		mav.addObject("user", user);
		return mav;
	}
	
//	@PostMapping("update")
	@RequestMapping("update")
	public ModelAndView checkupdate(@Valid User user, BindingResult bresult, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			bresult.reject("error.input.user");
			return mav;
		}
		// 비밀번호 검증 : 입력된 비밀번호와 db에 저장된 비밀번호를 비교
		// 일치 : update
		// 불일치 : 메시지를 유효성출력으로 화면에 출력
		// ↓ 이건 관리자일때 곤란해짐
//		User dbUser = service.getUser(user.getUserid());
//		if(!dbUser.getPassword().equals(user.getPassword())) {
//			bresult.reject("error.login.password");
//			return mav;
//		}
		// ↓ 이렇게 하면, 관리자일때 다른사람의 정보를 수정하고자 할 때 관리자의 비밀번호를 입력하면 수정이 가능해짐
		User loginUser = (User)session.getAttribute("loginUser");
		String hashpass = null;
		try {
			String inputpass = user.getPassword();
			hashpass = cipher_util.CipherUnit.makehash(inputpass);
			user.setPassword(hashpass);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(!user.getPassword().equals(loginUser.getPassword())) {
			System.out.println("db 비밀번호와 비교 : " + loginUser.getPassword());
			bresult.reject("error.login.password");
			return mav;
		}
		
		// 이제 진짜 업데이트 기능
		try {
			// 이메일 암호화해서 insert
			String key = cipher_util.CipherUnit.makehash(user.getUserid());
			String cipherEmail = cipher_util.CipherUnit.encrypt(user.getemail(), key.substring(0,16));
			user.setemail(cipherEmail);
		
			service.userUpdate(user);
			mav.setViewName("redirect:mypage.shop?id="+user.getUserid());
			// 관리자가 아닌경우, session에 있는 로그인정보 역시 수정해줘야함
			// 업데이트가 성공하면 수정된 로그인 정보를 바꿔줌
			if(!loginUser.getUserid().equals("headmaster")) {
				session.setAttribute("loginUser", user);
			}
		} catch(Exception e) {
			e.printStackTrace();
			bresult.reject("error.user.update");
		}
		return mav;
	}
	
	// parameter가 2개 필요해 ==> 아이디, 비밀번호
	// User user 객체로 받아도 되고 (아이디와 비밀번호만 있음 == form에서 그것만 넘겨줌)
	// String userid(히든), String password 이렇게 따로 받아도 됨
	@PostMapping("delete")
	public ModelAndView checkdelete(User user, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		
		// 웹단에서 유효성 검증을 하지 않음 ==> 예외처리 할거야
		// session에서 loginUser를 가져옴
		User loginUser = (User)session.getAttribute("loginUser");
		String hashpass = null;
		try {
			String inputpass = user.getPassword();
			hashpass = cipher_util.CipherUnit.makehash(inputpass);
			System.out.println(hashpass);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(!loginUser.getPassword().equals(hashpass)) {
			throw new LoginException("회원탈퇴시 비밀번호가 틀립니다", "delete.shop?id="+user.getUserid());
		}	
		try {
			service.userDelete(user.getUserid());
			if(loginUser.getUserid().equals("headmaster")) {
				mav.setViewName("redirect:/admin/list.shop");
			} else {
				// 로그아웃
				mav.addObject("msg", loginUser.getUsername() + " 회원님, 탈퇴! GOODBYE" + user.getUsername() + "getUsername()으로 불러움");
				session.invalidate();
				mav.addObject("url", "login.shop");
				mav.setViewName("alert");
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new LoginException("회원 탈퇴 중 오류가 발생했습니다 ==> 전산부 연락 요망", "delete.shop?id="+user.getUserid());
		}
		return mav;
	}
	
	// @ResponseBody : return 데이터 자체를 view의 내용으로 전달
	// 		↑가 실행될 때, 파싱방법을 미리 준비함
	//		html이 적용되고 한글을 가져올 수 있도록 설정해줌 : produces="text/html; charset=UTF-8"
	@PostMapping(value="changePass", produces="text/html; charset=UTF-8")
	@ResponseBody
	public String checkchangePass(@RequestParam HashMap<String,String> param, HttpSession session) {
		// @RequestParam HashMap<String,String> param
		// parameter를 hashmap객체로 받는거야
		// 그래서 param.get("파라미터 이름")을 쓸 수 있는거야
		
		
		// 웹단에서 유효성 검증을 하지 않음 ==> 예외처리 할거야
		User loginUser = (User) session.getAttribute("loginUser");
		String hashpass =null;
		try {
			String inputpass = param.get("password");
			hashpass = cipher_util.CipherUnit.makehash(inputpass);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!loginUser.getPassword().equals(hashpass)) {
			throw new LoginException("입력한 기존비밀번호가 다릅니다", "changePass.shop");
		}
		String newpass =null;
		try {
			newpass = cipher_util.CipherUnit.makehash(param.get("newpass"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		service.userPasswordUpdate(loginUser.getUserid(), newpass);
		// 로그인 정보의 비밀번호를 바뀐 비밀번호로 수정해줌
		loginUser.setPassword(newpass);
		
		// @ResponseBody : return 데이터 자체를 view의 내용으로 전달해줌 (이름이 아닌 내용으로 전달)
		// 스크립트 자체가 화면으로 가서 실행되는거야
		// 굳이 view단을 새로만드는게 아니고
		// ajax을 통해서 json파일을 전달할 때 많이 쓰이는 방법
		return "<script>"
				+ " alert('비밀번호가 변경되었습니다')\n "
				+ " self.close()\n "
				+ " </script> ";
	}
}
