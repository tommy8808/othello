import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Main {

	/***********************************************************/
	/***********************오델로 게임 조건.******************/
	/*약식 오델로 게임으로 플레이어는 무조건 검은돌로, 선 공격한다*/
	/*모든 화면의 돌이 꽉 차거나, 선택할 영역이 없으면 돌의 수를 */
	/*비교하여 검은돌이 더 많으면 승리한다.*/
	/*선택할 수 있는 영역은 프로그램이 계산하여 목록으로 제공한다.*/
	/*화면에 *모양으로 표시 된 곳 중 한 곳을 선택한다.*/
	
	static final Scanner scan = new Scanner(System.in);
	static int plateSize = 6; //판의 길이. 짝수로만 가능
	static Pos[][] plate = new Pos[plateSize][plateSize];
	static List<Pos> posList = new ArrayList<>();
	static int totalCount = plateSize*plateSize;
	static int currentCount = 0;
	static int whiteStone = 0, blackStone = 0;
	static int blackOutCount = 0, whiteOutCount = 0;//일정 횟수 선택을 못하면 아웃
	static boolean myTurn = true;
	public static void main(String[] args) {

		System.out.println();
		System.out.println("******** Othello ********");
		
		//System.out.println("수를 입력하세요.");
		//int n = Integer.parseInt(scan.nextLine());
		//String[] arItems = scan.nextLine().split(" ");
		//System.out.println(arItems[0]);
		
		init(plateSize);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		play();
	}
	
	//게임 초기화
	public static void init(int plateSize){
		System.out.println("-----게임을 초기화 합니다.-----");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				if((i == plateSize/2-1 && j == plateSize/2-1) || (i == plateSize/2 && j == plateSize/2)){
					plate[i][j] = new Pos("black", j, i, plateSize);
				}else if((i == plateSize/2-1 && j == plateSize/2) || (i == plateSize/2 && j == plateSize/2-1)){
					plate[i][j] = new Pos("white", j, i, plateSize);
				}else{
					plate[i][j] = new Pos("none", j, i, plateSize);
				}
			}
		}
		whiteStone = 2;
		blackStone = 2;
		display();
	}
	//현재 게임상황 display
	public static void display(){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<plateSize; i++){
			sb.append("　　　　");
			for(int j=0; j<plateSize; j++){
				
				if(plate[i][j].getColor().equals("black")){
					sb.append("●  ");
				}else if(plate[i][j].getColor().equals("white")){
					sb.append("○  ");
				}else if(plate[i][j].getChoosable() == true){
					sb.append("¤  ");
				}else {
					sb.append("□  ");
				}
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
		
		
	}

	public static void play(){
		System.out.println("-----오델로 게임을 시작합니다.-----");
		System.out.println();
		
		while(!isFinish()){
			System.out.println("---------나의턴---------");
			myTurn = true;
			System.out.printf("선택 가능 좌표 :  ");
			
			List<Pos> possibleChoices = findChoices(myTurn);
			if(possibleChoices.size()>0){
				for(int i=0; i<possibleChoices.size(); i++){
					System.out.printf("[" + possibleChoices.get(i).getX()+"," + possibleChoices.get(i).getY() + "] ");
				}
				System.out.println();
				display();
				
				System.out.printf("돌을 놓을 좌표를 선택하세요. (입력형식:x,y) :");
				String[] arItems = scan.nextLine().split(",");
				if(arItems.length != 2){
					System.out.println();
					System.out.println("------입력 에러!!!------");
					System.out.println("좌표를 정확히 입력해 주세요ex) 3,1");
					System.out.println();
					continue;
				}
				plate[Integer.parseInt(arItems[1])][Integer.parseInt(arItems[0])].setColor("black");
				blackStone++;
				System.out.println(arItems[0]+ ","+arItems[1]+" 을 선택하셨습니다.");
				changeStone(Integer.parseInt(arItems[0]), Integer.parseInt(arItems[1]), (myTurn == true ? "black":"white"));
				display();
				currentCount++;
				blackOutCount = 0;
			} else{
				System.out.println("선택할 수 있는 돌이 없어 상대 턴으로 넘어갑니다.");
				blackOutCount++;
			}
			
			System.out.println("---------상대턴---------");
			myTurn = false;
			List<Pos> enemyChoices = findChoices(myTurn);//white 도 찾을 수 있도록 파라미터 변경
			if(enemyChoices.size() > 0){
				Random ran = new Random();
				
				int randInt = ran.nextInt(enemyChoices.size()); 
				
				plate[enemyChoices.get(randInt).getY()][enemyChoices.get(randInt).getX()].setColor("white");
				whiteStone++;
				System.out.println("상대가 " + enemyChoices.get(randInt).getX()+ ","+enemyChoices.get(randInt).getY()+" 을 선택하였습니다.");
				changeStone(enemyChoices.get(randInt).getX(), enemyChoices.get(randInt).getY(), (myTurn == true ? "black":"white"));
				display();
				currentCount++;
				whiteOutCount = 0;
			} else{
				System.out.println("선택할 수 있는 돌이 없어 상대 턴으로 넘어갑니다.");
				whiteOutCount++;
			}
		}
		
		System.out.println("----------게임 종료!!!----------");
		System.out.println("검은 돌 : " + blackStone);
		System.out.println("하얀 돌 : " + whiteStone);
		System.out.println("승리 : " + (blackStone > whiteStone ? "player" : "상대편"));
		
	}
	
	//선택한 돌의 좌표로 부터 4방향의 배열에 변경되야 할 돌을 찾아 바꾼다.
	public static void changeStone(int x, int y, String color){

		Stack<Pos> stack = new Stack<>();
		String myColor = color;
		String enemyColor = (color == "black" ? "white" : "black");
		if(y > 2 && x > 2 && plate[y-1][x-1].getColor() == enemyColor){//왼쪽 대각선 위
			for(int q = y-1, r= x-1; q >= 0 && r >= 0; q--,r--){
				stack.push(plate[q][r]);
				if(plate[q][r].getColor() == myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[q][r].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		}
		if(y > 2 && plate[y-1][x].getColor() == enemyColor){//위
			for(int q= y-1; q >= 0; q--){
				stack.push(plate[q][x]);
				if(plate[q][x].getColor()==myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[q][x].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		} 
		if(y > 2 && x < plateSize-2 && plate[y-1][x+1].getColor() == enemyColor){//오른쪽 대각선 위
			for(int q = y-1, r= x+1; q >= 0 && r < plateSize; q--,r++){
				stack.push(plate[q][r]);
				if(plate[q][r].getColor()==myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[q][r].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		}
		if(x > 2 && plate[y][x-1].getColor() == enemyColor){//왼쪽
			for(int r = x-1; r >= 0; r--){
				stack.push(plate[y][r]);
				if(plate[y][r].getColor()==myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[y][r].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		}
		if(x < plateSize-2 && plate[y][x+1].getColor() == enemyColor){//오른쪽
			for(int r = x+1; r < plateSize; r++){
				stack.push(plate[y][r]);
				if(plate[y][r].getColor()==myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[y][r].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		}
		if(y < plateSize-2 && x > 2 && plate[y+1][x-1].getColor() == enemyColor){//왼쪽 대각선 아래
			for(int q = y+1, r = x-1; q < plateSize && r >= 0; q++,r--){
				stack.push(plate[q][r]);
				if(plate[q][r].getColor()==myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[q][r].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		}
		if(y < plateSize-2 && plate[y+1][x].getColor() == enemyColor){//아래
			for(int q = y+1; q < plateSize; q++){
				stack.push(plate[q][x]);
				if(plate[q][x].getColor()==myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[q][x].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		}
		if(y < plateSize-2 && x < plateSize-2 && plate[y+1][x+1].getColor() == enemyColor){//오른 대각 아래
			for(int q = y+1, r= x+1; q < plateSize && r < plateSize; q++, r++){
				stack.push(plate[q][r]);
				if(plate[q][r].getColor()==myColor){
					flipStone(stack, myColor);
					break;
				}else if(plate[q][r].getColor()== "none"){
					break;
				}
			}
			stack.clear();
		}
	}
	
	//상대 돌 뒤집기
	public static void flipStone(Stack<Pos> stack, String myColor){
		while(!stack.isEmpty()){
			stack.pop().setColor(myColor);
			if(myColor == "black"){
				blackStone++;
				whiteStone--;
			} else{
				whiteStone++;
				blackStone--;
				System.out.println("흰돌 증가: "+whiteStone);
			}
		}
	}
	
	//현재 영역에서 선택할 수 있는 선택지를 찾아 제공한다.
	public static List<Pos> findChoices(boolean myTurn){
		List<Pos> list = new ArrayList<Pos>();
		String myColor = "black";
		if(!myTurn) myColor = "white";
		validate(myColor);
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				if(plate[i][j].getChoosable()){
					list.add(plate[i][j]);
				}
			}
		}
		
		return list;
	}
	
	//현재 돌의 갯수를 세어 더 이상 돌을 놓을 수 없으면 true를 리턴한다. 
	public static boolean isFinish(){
		if(totalCount - currentCount == 0 || whiteStone == 0 || blackStone == 0 ||
				whiteOutCount == 5 || blackOutCount == 5){
			return true;
		}
		return false;
	}
	
	//해당 돌을 선택할 수 있는지 여부를 저장한다.
	public static void validate(String color){
		//선택 초기화
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				plate[i][j].setChoosable(false);
			}
		}
		String myColor = color;
		String enemyColor = (color == "black" ? "white" : "black");
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				if(plate[i][j].getColor() == enemyColor){//주변 8방향 수색
					if(i > 0 && j > 0 && plate[i-1][j-1].getColor() == "none"){//왼쪽 대각선 위
						for(int q = i+1, r= j+1; q < plateSize && r < plateSize; q++,r++){
							if(plate[q][r].getColor() == myColor){
								plate[i-1][j-1].setChoosable(true);
								break;
							}else if(plate[q][r].getColor() == "none"){
								break;
							}
						}
					}
					if(i > 0 && plate[i-1][j].getColor() == "none"){//위
						for(int q= i+1; q < plateSize; q++){
							if(plate[q][j].getColor() == myColor){
								plate[i-1][j].setChoosable(true);
								break;
							}else if(plate[q][j].getColor() == "none"){
								break;
							}
						}
					} 
					if(i > 0 && j < plateSize-1 && plate[i-1][j+1].getColor() == "none"){//오른쪽 대각선 위
						for(int q = i+1, r= j-1; q < plateSize && r >= 0; q++,r--){
							if(plate[q][r].getColor() == myColor){
								plate[i-1][j+1].setChoosable(true);
								break;
							}else if(plate[q][r].getColor()== "none"){
								break;
							}
						}
					}
					if(j > 0 && plate[i][j-1].getColor() == "none"){//왼쪽
						for(int r = j+1; r < plateSize; r++){
							if(plate[i][r].getColor() == myColor){
								plate[i][j-1].setChoosable(true);
								break;
							}else if(plate[i][r].getColor() == "none"){
								break;
							}
						}
					}
					if(j < plateSize-1 && plate[i][j+1].getColor().equals("none")){//오른쪽
						for(int r = j-1; r >= 0; r--){
							if(plate[i][r].getColor() == myColor){
								plate[i][j+1].setChoosable(true);
								break;
							}else if(plate[i][r].getColor() == "none"){
								break;
							}
						}
					}
					if(i < plateSize-1 && j > 0 && plate[i+1][j-1].getColor() == "none"){//왼쪽 대각선 아래
						for(int q = i-1, r= j+1; q >= 0 && r < plateSize; q--, r++){
							if(plate[q][r].getColor()==myColor){
								plate[i+1][j-1].setChoosable(true);
								break;
							}else if(plate[q][r].getColor() == "none"){
								break;
							}
						}
					}
					if(i < plateSize-1 && plate[i+1][j].getColor() == "none"){//아래
						for(int q= i-1; q >= 0; q--){
							if(plate[q][j].getColor() == myColor){
								plate[i+1][j].setChoosable(true);
								break;
							}else if(plate[q][j].getColor() == "none"){
								break;
							}
						}
					}
					if(i < plateSize-1 && j < plateSize-1 && plate[i+1][j+1].getColor() == "none"){//오른 대각 아래
						for(int q = i-1, r= j-1; q >= 0 && r >= 0; q--,r--){
							if(plate[q][r].getColor() == myColor){
								plate[i+1][j+1].setChoosable(true);
								break;
							}else if(plate[q][r].getColor() == "none"){
								break;
							}
						}
					}
				}
			}
		}
	}
}
