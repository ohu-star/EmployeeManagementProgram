
import java.util.Calendar;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

//수정사항 - 56, 64, 205, 221, 235, 274, 343, 358, 361, 362, 368, 502, 516, 636, 729, 753, 775
//본사, 지점 상속->공통?? or 따로 들어가게
//인적사항 -> 급여, 점수
//Employee를 상속받은 인턴, 임원(직급을 가진 직원이 상속받음), private, protected 신경쓰기 
//퇴사자가 Employee를 상속받음??

//급여, 고과점수 -> employee
//employee -> 임원
//employee -> 인턴


//급여 클래스
class Salary {
	private float basePay = 2200000;
	private float allowance = 0;
	
	float getBasePay() { return basePay; }
	float getAllowance() { return allowance; }
	float getSalary() { return basePay + allowance; }
	void setBasePay(float basePay) { this.basePay = basePay; }
	void setAllowance(float allowance) { this.allowance += allowance; }
	void setAllowance() { this.allowance = 0; }
	float getActualSalary() { 
		float actualSalary = (float)(basePay - (basePay*0.045 + basePay*0.03545 + basePay*0.03545*0.1281 + basePay*0.009)) + allowance; 
		return actualSalary;
	}
}

//고과점수 클래스
class Score {
	private float performanceScore = 0; //인사고과 점수
	private float extraScore = 0; //가산점
	
	float getPerformanceScore() { return performanceScore; }
	float getExtraScore() { return extraScore; }
	float getScore() { return performanceScore + extraScore; }
	void setPerformanceScore(float performanceScore) { this.performanceScore = performanceScore; }
	void setExtraScore(float extraScore) { this.extraScore += extraScore; }
	void setExtraScore() { this.extraScore = 0; }
}

//사원 클래스
class Employee implements Cloneable { //정보 - 사번, 이름, 생일, 주소, 입사일, 부서, 급여, 고과점수
	//퇴사일, 직급 추가!!!!!!!!!!!!!!!!!! -> 상속으로 해결??
	private String id;
	private String name;
	private Calendar birth = Calendar.getInstance();
	private String address;
	private Calendar startDate;
	private String department; //부서를 번호로 입력받아서 부서명으로 저장할 수 있게하기
	Salary salary = new Salary();
	Score score = new Score();
	
	Employee(String id, String name, int year, int month, int date, String address, String department) {
		this.id = id; this.name = name; this.birth.set(year, month, date); 
		this.address = address; this.department = department;
	}
	
	//사원정보 리턴
	public String getId() { return id; }
	public String getName() { return name; }
	public Calendar getBirth() { return birth; }
	public String getAddress() { return address; }
	public Calendar getStartDate() { return startDate; }
	public String getDepartment() { return department; }
	
	//사원정보 설정
	public void setId(String id) { this.id = id; }
	public void setName(String name) { this.name = name; }
	public void setBirth(int year, int month, int date) {
		birth = Calendar.getInstance();
		birth.set(year, month, date);
	}
	public void setAddress(String address) { this.address = address; }
	public void setStartDate(int year, int month, int date) { 
		startDate = Calendar.getInstance();
		startDate.set(year, month, date);
	}
	public void setDepartment(String department) { this.department = department; }
	
	//기본정보출력
	@Override
	public String toString() {
		return "사번: " + id + "\n이름: " + name + "\n생일: " + birth.get(Calendar.YEAR) + "년 " + birth.get(Calendar.MONTH) + "월 " + birth.get(Calendar.DATE) +
				"일" + "\n주소: " + address + "\n부서: " + department + "\n";
	}
	
	//급여정보출력
	public String salaryString() {
		return "사번: " + id + "\n이름: " + name + "\n부서: " + department + "\n실급여: " + salary.getActualSalary() + "\n";
	}
	
	//고과점수정보출력
	public String scoreString() {
		return "사번: " + id + "\n이름: " + name + "\n부서: " + department + "\n고과점수: " + score.getScore() + "\n";
	}

	//복사하기 위한..
	@Override
	protected Employee clone() throws CloneNotSupportedException {
		return (Employee) super.clone();
	}
}


class ProgramMenu {
	ArrayList<Employee> employees; //사원 원본 
	ArrayList<Employee> formerEmployees; //퇴사자 원본
	ArrayList<Employee> copyEmployees; //사원 복사본
	
	Scanner scan = new Scanner(System.in);
	
	Random rand = new Random();
	String randNum;
	
	//ProgramMenu의 생성자 -> 파일을 읽어들여서 사원정보, 퇴사자정보 저장
	public ProgramMenu(ArrayList<Employee> employees, ArrayList<Employee> formerEmployees, ArrayList<Employee> copyEmployees) {
		this.employees = employees;
		this.formerEmployees = formerEmployees;
		this.copyEmployees = copyEmployees;
		
		//사원데이터
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream("EmployeesData.dat"));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		
		while(true) {
			Employee tempE = new Employee("", "", 0, 0, 0, "", "");
			try {
				tempE.setId(dis.readUTF());
				tempE.setName(dis.readUTF());
				tempE.setBirth(dis.readInt(), dis.readInt(), dis.readInt());
				tempE.setAddress(dis.readUTF());
				tempE.setDepartment(dis.readUTF());
				tempE.salary.setBasePay(dis.readFloat());
				tempE.salary.setAllowance(dis.readFloat());
				tempE.score.setPerformanceScore(dis.readFloat());
				tempE.score.setExtraScore(dis.readFloat());
				
				this.employees.add(tempE);
			} catch (EOFException e) {
				System.out.println("사원 읽어들이기 완료");
				break;
			} catch(IOException e1) {
				System.out.println("오류 발생");
			}
		}

		//퇴사자데이터
		try {
			dis = new DataInputStream(new FileInputStream("FormerEmployeesData.dat"));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		
		while(true) {
			Employee tempE = new Employee("", "", 0, 0, 0, "", "");
			try {
				tempE.setId(dis.readUTF());
				tempE.setName(dis.readUTF());
				tempE.setBirth(dis.readInt(), dis.readInt(), dis.readInt());
				tempE.setAddress(dis.readUTF());
				tempE.setDepartment(dis.readUTF());
				tempE.salary.setBasePay(dis.readFloat());
				tempE.salary.setAllowance(dis.readFloat());
				tempE.score.setPerformanceScore(dis.readFloat());
				tempE.score.setExtraScore(dis.readFloat());
				
				this.formerEmployees.add(tempE);
			} catch(EOFException e) {
				System.out.println("퇴사자 읽어들이기 완료");
				break;
			} catch(IOException e1) {
				System.out.println("오류 발생");
			}
		}
	}
	
	//사원 관리 프로그램 시작
	void startMenu() {
		int taskNum = 0;
		do {
			System.out.println("----------사원 관리 프로그램----------");
			System.out.println("1. 사원조회");
			System.out.println("2. 입사/퇴사"); //생각해보기
			System.out.println("3. 급여관리");
			System.out.println("4. 인사고과관리");
			System.out.println("5. 종료");
			System.out.println("어떤 작업을 하시겠습니까?");
			try {
				taskNum = scan.nextInt();
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
				scan.nextLine();
			}
			
			switch(taskNum) {
			case 1:
				EmployeeInquiry(0); break; //사원조회
			case 2:
				ChangeEmployee(); break; //메소드 이름 생각해보기 //입사/퇴사
			case 3:
				SalaryManagement(); break; //급여관리
			case 4:
				ScoreManagement(); break; //인사고과관리
			case 5:
				EndMenu(); break;//종료
			default:
				System.out.println("작업번호가 잘못입력되었습니다. 다시 입력해주세요.");
			}
		} while(taskNum != 5);
	}
	
	//사원조회----------------------------------------------------------------------------------
	void EmployeeInquiry(int flag) { //flag 말고 좀 더 활용성 있게?는 안될까 --> 1. 사원조회 3. 급여조회 4. 고과점수관리에서 1,3,4 의 taskNum을 따로 저장(같이 쓸 수 있는 곳에)해서 그 변수에 따라서 결정.
		int taskNum = 0;
		
		do {
			if(flag == 0) 
				System.out.println("----------사원조회----------");
			else if(flag == 1)
				System.out.println("----------급여조회----------");
			else if(flag == 2)
				System.out.println("----------고과점수조회----------");
			System.out.println("1. 전체조회");
			System.out.println("2. 부서별조회");
			System.out.println("3. 사번조회");
			System.out.println("4. 이전메뉴");
			
			System.out.println("어떤 작업을 하시겠습니까?");

			try {
				taskNum = scan.nextInt();
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
			}

			switch(taskNum) {
			case 1:
				FullInquiry(flag); break; //전체조회
			case 2:
				DepartmentInquiry(flag); break; //부서별조회
			case 3:
				idInquiry(flag); break; //사번조회
			case 4:
				return; //이전메뉴
			default:
				System.out.println("작업번호가 잘못입력되었습니다. 다시 입력해주세요.");
			}
		} while(taskNum != 4);
	}
	
	//전체조회
	//사번으로 정렬해보기
	void FullInquiry(int flag) {
		int numOfEmployees = 0;
		float totalOfSalary = 0;
		//float exceptionalScore[] = {-1, -1, -1};
		//int index[] = new int[3];
		
		System.out.println("----------전체조회----------");
		
		//1. 사원조회에서 접근
		if(flag == 0) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				System.out.println(e);
				
				numOfEmployees++;
			}
		}
		//3. 급여관리에서 접근
		else if(flag == 1) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				System.out.println(e.salaryString());
				
				totalOfSalary += e.salary.getActualSalary();
				numOfEmployees++;
			}
		}
		//4. 고과점수관리에서 접근
		else if(flag == 2) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				System.out.println(e.scoreString());
				
				numOfEmployees++;
			}
		}
		
		/*
		if(e.score.getScore() > maxScore) {
			maxScore = e.score.getScore();
			highestScoreIndex = i;
		}
		numOfEmployees++;
		*/
		
		/*
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			for(int j=0; j<index.length; j++) {
				//if((i==0 && e.score.getScore() > index[j]))
			}
		}
		*/
		
		//복사본에 원본을 복사하여 저장
		copyEmployees.clear(); //저장되어있는 복사본 지우기
		for(Employee e : employees) {
			try {
				copyEmployees.add(e.clone());
			} catch (CloneNotSupportedException e3) {
				e3.printStackTrace();
			}
		}
		
		//복사된 객체 배열을 점수 기준으로 정렬
		Collections.sort(copyEmployees, new Comparator<Employee>() {
			@Override
			public int compare(Employee e1, Employee e2) {
				return (int) (e2.score.getScore() - e1.score.getScore()); //int로 형변환해도 문제없나?
			}
		});
		
		//검색된 총 인원 출력
		System.out.println("-------------검색된 인원: " + numOfEmployees + "-------------");
		System.out.println();
		
		//3. 급여관리에서 접근했다면 전체급여 출력
		if(flag == 1) {
			System.out.println("-------------전체 급여:" + totalOfSalary + "-------------");
			
		}
				
		//4. 고과점수관리에서 접근했다면 우수사원 출력
		//따로 빼거나 부서별에서도 나올 수 있게 하거나
		if(flag == 2) {
			for(int i=0; i<3;) { 
				Employee e = copyEmployees.get(i); //employees가 3명보다 작으면??
				System.out.println("-------------우수사원 " + ++i + ": " + e.getId() + " - " + e.getName() + "(" + e.score.getScore() + "점)"); //점수 같은 사원은 어떻게 처리?
			}
		}
		
	}
	
	//부서별 검색 -> 부서를 arraylist나 다른걸로 저장?
	void DepartmentInquiry(int flag) {
		int departNum = 0;
		int numOfEmployees = 0;
		int totalOfSalary = 0;
		
		System.out.println("----------부서별조회----------");
		System.out.println("1 - 영업부\n2 - 마케팅부\n3 - 개발부\n4 - 인사부");
		System.out.print("검색할 부서를 입력하세요: ");
		
		departNum = scan.nextInt();
		
		//1. 사원조회에서 접근
		if(flag == 0) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getDepartment().equals(Integer.toString(departNum))) {
					System.out.println(e);
					numOfEmployees++;
				}
			}
		}
		//3. 급여관리에서 접근
		else if(flag == 1) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getDepartment().equals(Integer.toString(departNum))) {
					System.out.println(e.salaryString());
					
					totalOfSalary += e.salary.getActualSalary();
					numOfEmployees++;
				}
			}
		}
		//4. 고과점수관리에서 접근
		else if(flag == 2) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getDepartment().equals(Integer.toString(departNum))) {
					System.out.println(e.scoreString());
					
					numOfEmployees++;
				}
			}
		}
		System.out.println("-------------검색된 인원: " + numOfEmployees + "-------------");
		System.out.println();
		
		//3. 급여관리에서 접근했다면 전체 급여 출력
		if(flag == 1) {
			System.out.println("-------------전체 급여:" + totalOfSalary + "-------------");
		}
	}
	
	//사번검색
	void idInquiry(int flag) {
		int inputId = 0;
		int numOfEmployees = 0;
		
		System.out.println("----------사번조회----------");
		System.out.print("검색할 사번을 입력하세요: ");
		
		inputId = scan.nextInt();
		System.out.println();
		
		//1. 사원조회에서 접근
		if(flag == 0) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(Integer.toString(inputId))) {
					System.out.println(e);
					numOfEmployees++;
				}
			}
		}
		//3. 급여관리에서 접근
		else if(flag == 1) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(Integer.toString(inputId))) {
					System.out.println(e.salaryString());
					numOfEmployees++;
				}
			}
		}
		//4. 고과점수관리에서 접근
		else if(flag == 2) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(Integer.toString(inputId))) {
					System.out.println(e.scoreString());
					numOfEmployees++;
				}
			}
		}
		//만약 검색된 사원이 없다면
		if(numOfEmployees == 0)
			System.out.println("검색된 사원이 없습니다.");
	}
	
	//사원조회 끝----------------------------------------------------------------------------
	
	//사원입퇴사-----------------------------------------------------------------------------
	void ChangeEmployee() {
		int taskNum = 0;
		
		do {
			System.out.println("----------사원 입사/퇴사----------");
			System.out.println("1. 입사관리");
			System.out.println("2. 퇴사관리");
			System.out.println("3. 퇴사조회");
			System.out.println("4. 이전메뉴");
			System.out.println("어떤 작업을 하시겠습니까?");
			taskNum = scan.nextInt();
		
			switch(taskNum) {
			case 1:
				joinCompany(); break; //입사관리
			case 2:
				leaveCompany(); break; //퇴사관리
			case 3:
				formerEmployeesInquiry(); break; //퇴사자조회
			case 4:
				return; //이전메뉴
			}
		} while(taskNum != 4);
		
	}
	
	//입사관리
	void joinCompany() {
		String name; int year; int month; int date; String address; String department;
		//String id, String name, int year, int month, int date, String address, String department
		//급여 등은 입력받고 입력이 없으면 0으로 설정
		//id - 부서별로 id 앞자리가 같도록????????????
		
		//입사 시, 랜덤한 고유의 사원번호 지정
		while(true) {
			randNum = Integer.toString(rand.nextInt(10000) + 10000);
			for(int i=0; i< employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(randNum)) { //만약 id가 같은 사원이 있다면 다시 랜덤번호 부여받음
					break;
				}
			}
			break; 
		} //while문을 빠져나왔다는건 다른 사원들과 겹치지 않는 사원번호를 부여받았다는 뜻
		while(true) {
			System.out.print("입사자 성명을 입력해주십시오: "); //성, 이름을 따로 입력받기?
			try {
				name = scan.next();
				break;
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
			}
		}
		
		while(true) {
			System.out.print("입사자 생년월일을 띄어쓰기로 구분하여 입력해주십시오: ");
			try {
				year = scan.nextInt();
				month = scan.nextInt();
				date = scan.nextInt();
				break;
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
			}
		}
		
		while(true) {
			System.out.print("입사자 주소를 입력해주십시오: ");
			try {
				//버퍼 비우기
				scan.next();
				address = scan.nextLine();
				break;
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
			}
		}
		
		while(true) {
			System.out.print("입사자 부서(영업부: 1, 마케팅부: 2, 개발부: 3, 인사부: 4)를 입력해주십시오: ");
			try {
				department = scan.next();
				break;
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
			}
		}
		
		employees.add(new Employee(randNum, name, year, month, date, address, department));
	}
	
	//퇴사관리
	void leaveCompany() {
		String inputId;
		Employee temp = null;
		System.out.print("퇴사자의 사원번호를 입력해주세요: ");
		while(true) {
			try {
				inputId = scan.next();
				break;
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
			}
		}
		
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			if(e.getId().equals(inputId)) {
				temp = e;
				employees.remove(i); //사원들을 저장해놓은 arraylist에서 삭제
				System.out.println(temp.getId() + "-" + temp.getName() + "의 퇴사처리가 완료되었습니다.");
			}
		}
		//퇴사명단에 추가
		formerEmployees.add(temp);
	}
	
	//퇴사조회
	void formerEmployeesInquiry() {
		System.out.println("----------퇴사자조회----------");
		for(int i=0; i<formerEmployees.size(); i++) {
			Employee e = formerEmployees.get(i);
			System.out.println(e);
		}
	}
	//사원입퇴사 끝-------------------------------------------------------------
	
	//사원급여관리-------------------------------------------------------------
	void SalaryManagement() {
		int taskNum = 0;
		do {
			System.out.println("----------사원급여관리----------");
			System.out.println("1. 급여조회");
			System.out.println("2. 기본급변경");
			System.out.println("3. 추가수당");
			System.out.println("4. 수당초기화");
			System.out.println("5. 이전메뉴");
			
			System.out.println("어떤 작업을 하시겠습니까?");
			taskNum = scan.nextInt();
			
			switch(taskNum) {
			case 1:
				EmployeeInquiry(1); break; //급여조회
			case 2:
				ChangeBasePay(); break; //기본급변경
			case 3:
				ChangeAllowance(); break; //추가수당
			case 4:
				resetAllowance(); break; //수당초기화
			case 5:
				return; //이전메뉴
			}
		} while(taskNum != 5);
		
	}
	
	//기본급변경
	void ChangeBasePay() {
		String inputId;
		float inputBasePay;
		
		System.out.println("----------기본급변경----------");
		
		System.out.println("급여를 변경할 사원번호를 입력해주세요.");
		inputId = scan.next(); //try-catch 추가 
		System.out.println("변경된 기본급을 입력해주세요.");
		inputBasePay = scan.nextFloat();
		
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			if(e.getId().equals(inputId)) {
				e.salary.setBasePay(inputBasePay);
			}
		}
	}
	
	//추가수당
	void ChangeAllowance() {
		String inputId;
		float inputAllowance;
		
		System.out.println("----------추가수당----------");
		
		System.out.println("급여를 변경할 사원번호를 입력해주세요.");
		inputId = scan.next();
		System.out.println("추가할 수당을 입력해주세요.");
		inputAllowance = scan.nextFloat();
		
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			if(e.getId().equals(inputId)) {
				e.salary.setAllowance(inputAllowance); //매개변수를 보내면 수당을 추가하는 함수
			}
		}
	}
	
	//수당초기화(매달 1일 실시)
	void resetAllowance() {
		String ans;
		System.out.println("----------수당 초기화----------");
		
		System.out.println("수당을 초기화하시겠습니까? -매달 1일에 초기화합니다.(예: Y, 아니오: N)");
		ans = scan.next();
		ans = ans.trim();
		
		if(ans.equals("Y") || ans.equals("y")) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				e.salary.setAllowance(); //수당을 0으로 초기화
			}
		}
	}
//사원급여관리 끝----------------------------------------------------
	
//고과점수관리------------------------------------------------------
	void ScoreManagement() {
		int taskNum = 0;
		
		do {
			System.out.println("----------고과점수관리----------");
			System.out.println("1. 인사고과조회");
			System.out.println("2. 고과점수입력");
			System.out.println("3. 가산점입력");
			System.out.println("4. 가산점초기화");
			System.out.println("5. 이전메뉴");
			
			System.out.println("어떤 작업을 하시겠습니까?");
			try {
				taskNum = scan.nextInt();
			} catch(InputMismatchException e) {
				System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
			}
			
			switch(taskNum) {
			case 1:
				EmployeeInquiry(2); break; //인사고과조회
			case 2:
				ChangePerformanceScore(); break; //고과점수입력
			case 3:
				ChangeExtraScore(); break; //가산점입력
			case 4:
				resetExtraScore(); break; //가산점초기화
			case 5:
				return; //이전메뉴
			}
		} while(taskNum != 5);
	}
	
	//고과점수입력
	void ChangePerformanceScore() {
		int flag = 0;
		String inputId;
		float inputPerformanceScore;
		
		System.out.println("----------고과점수입력----------");
		
		System.out.println("고과점수를  변경할 사원번호를 입력해주세요.");
		inputId = scan.next(); //try-catch 작성
		System.out.println("고과점수를 입력해주세요.");
		inputPerformanceScore = scan.nextFloat();
		
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			if(e.getId().equals(inputId)) {
				flag++;
				e.score.setPerformanceScore(inputPerformanceScore);
				System.out.println("고과점수가 입력되었습니다.");
			}
		}
		if(flag == 0) System.out.println("사원을 찾을 수 없습니다.");
	}
	
	//가산점입력
	void ChangeExtraScore() {
		int flag = 0;
		String inputId;
		float inputExtraScore;
		
		System.out.println("----------가산점입력----------");
		
		System.out.println("가산점을 입력할 사원번호를 입력해주세요.");
		inputId = scan.next(); //try-catch 추가
		System.out.println("가산점을 입력해주세요.");
		inputExtraScore = scan.nextFloat(); //try-catch 추가
		
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			if(e.getId().equals(inputId)) {
				flag++;
				e.score.setExtraScore(inputExtraScore); //매개변수를 보내면 점수를 더하는 함수
				System.out.println("고과점수가 입력되었습니다.");
			}
		}
		if(flag == 0) System.out.println("사원을 찾을 수 없습니다.");
	}
	
	//가산점 초기화
	void resetExtraScore() {
		String ans;
		
		System.out.println("----------가산점 초기화----------");

		System.out.println("가산점을 초기화하시겠습니까? (예: Y, 아니오: N)");
		ans = scan.next(); //try-catch 추가
		ans = ans.trim();
		
		if(ans.equals("Y") || ans.equals("y")) {
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				e.score.setExtraScore(); //매개변수를 보내지 않으면 가산점을 0으로 초기화
			}
		}
	}
	
	//메뉴종료 및 저장
	void EndMenu() {
		//사원데이터 저장
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("EmployeesData.dat"));
			for(int i=0; i<employees.size(); i++) {
				Employee e = employees.get(i);
				
				dos.writeUTF(e.getId());
				dos.writeUTF(e.getName());
				dos.writeInt(e.getBirth().get(Calendar.YEAR));
				dos.writeInt(e.getBirth().get(Calendar.MONTH));
				dos.writeInt(e.getBirth().get(Calendar.DATE));
				dos.writeUTF(e.getAddress());
				dos.writeUTF(e.getDepartment());
				dos.writeFloat(e.salary.getBasePay());
				dos.writeFloat(e.salary.getAllowance());
				dos.writeFloat(e.score.getPerformanceScore());
				dos.writeFloat(e.score.getExtraScore());
			}
			
			dos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//퇴사자 데이터 저장
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("FormerEmployeesData.dat"));
			for(int i=0; i<formerEmployees.size(); i++) {
				Employee e = formerEmployees.get(i);
				
				dos.writeUTF(e.getId());
				dos.writeUTF(e.getName());
				dos.writeInt(e.getBirth().get(Calendar.YEAR));
				dos.writeInt(e.getBirth().get(Calendar.MONTH));
				dos.writeInt(e.getBirth().get(Calendar.DATE));
				dos.writeUTF(e.getAddress());
				dos.writeUTF(e.getDepartment());
				dos.writeFloat(e.salary.getBasePay());
				dos.writeFloat(e.salary.getAllowance());
				dos.writeFloat(e.score.getPerformanceScore());
				dos.writeFloat(e.score.getExtraScore());
			}
			
			dos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

} 
//end of menu


public class EmployeeManagementProgram {
	static ArrayList<Employee> employees = new ArrayList<Employee>();
	static ArrayList<Employee> formerEmployees = new ArrayList<Employee>();
	static ArrayList<Employee> copyEmployees = new ArrayList<Employee>();
	
	
	public static void main(String[] args) {
		//클론 생성 원래 여기였음
		ProgramMenu myMenu = new ProgramMenu(employees, formerEmployees, copyEmployees);
		
		myMenu.startMenu();
		
		/*
		while(true) {
			randNum = Integer.toString(rand.nextInt(10000) + 10000);
			for(int i=0; i< employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(randNum)) {
					break;
				}
			}
			employees.add(new Employee("10101", "홍길동", 1995, 5, 9, "경기도 수원시", "1"));
			break;
		}
		
		while(true) {
			randNum = Integer.toString(rand.nextInt(10000) + 10000);
			for(int i=0; i< employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(randNum)) {
					break;
				}
			}
			employees.add(new Employee("10102", "이영아", 1997, 3, 14, "서울특별시", "2"));
			break;
		}
		
		while(true) {
			randNum = Integer.toString(rand.nextInt(10000) + 10000);
			for(int i=0; i< employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(randNum)) {
					break;
				}
			}
			employees.add(new Employee("10103", "김서준", 1999, 12, 7, "경기도 용인시", "2"));
			break;
		}
		
		while(true) {
			randNum = Integer.toString(rand.nextInt(10000) + 10000);
			for(int i=0; i< employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(randNum)) {
					break;
				}
			}
			employees.add(new Employee("10104", "정서진", 1992, 9, 19, "서울특별시", "1"));
			break;
		}
		
		while(true) {
			randNum = Integer.toString(rand.nextInt(10000) + 10000);
			for(int i=0; i< employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(randNum)) {
					break;
				}
			}
			employees.add(new Employee("10105", "배진아", 1990, 7, 15, "경기도 화성시", "3"));
			break;
		}
		
		while(true) {
			randNum = Integer.toString(rand.nextInt(10000) + 10000);
			for(int i=0; i< employees.size(); i++) {
				Employee e = employees.get(i);
				if(e.getId().equals(randNum)) {
					break;
				}
			}
			employees.add(new Employee("10106", "임우진", 1899, 10, 11, "서울특별시", "3"));
			break;
		}
		*/
	}	
}
