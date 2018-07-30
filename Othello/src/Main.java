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
	
	private static final Scanner scan = new Scanner(System.in);
	private static int plateSize = 6; //판의 길이. 짝수로만 가능
	private static Pos[][] plate = new Pos[plateSize][plateSize];
	private static int totalCount = plateSize*plateSize;
	private static int currentCount = 0;
	private static int countOfWhiteStone = 0, countOfBlackStone = 0;
	private static int blackOutCount = 0, whiteOutCount = 0;//일정 횟수 선택을 못하면 아웃
	private static boolean myTurn = false;
	private static String myColor = "black";
	private static String enemyColor = "white";
	private static List<Pos> choices;
	private static Stack<Pos> stack;
	
	public static void main(String[] args) {
		init(plateSize);
		displayCurrentSituation();
		play();
	}
	
	//게임 초기화
	public static void init(int plateSize){
		System.out.println();
		System.out.println("******** Othello ********");
		System.out.println("-----게임을 초기화 합니다.-----");
		initStone(plateSize);
	}
	
	//현재 게임상황 display
	public static void displayCurrentSituation(){
		System.out.println();
		StringBuffer gameDisplayer = new StringBuffer();
		for(int i=0; i<plateSize; i++){
			gameDisplayer.append("　　　　");
			for(int j=0; j<plateSize; j++){
				showStoneStatusAndFindPointOfPossiblePoint(gameDisplayer, i, j);
			}
			gameDisplayer.append("\n");
		}
		System.out.println(gameDisplayer.toString());
	}

	// 0,2 를 선택했을때 위에 돌이 안바뀌는 오류가있음 public과 private의 구분이 심상치 않다
	public static void play(){
		systemWaitForReality();
		System.out.println("-----오델로 게임을 시작합니다.-----");
		System.out.println();
		
		while(!isFinish()){
			myTurn = !myTurn;//내 턴으로 시작(true)
			myColor = (myTurn ? "black" : "white");
			enemyColor = (myTurn ? "white" : "black");
			if(myTurn){
				System.out.println("---------나의턴---------");
				System.out.printf("선택 가능 좌표 :  ");
				
			}else {
				System.out.println("---------상대턴---------");
			}
			
			findChoices();
			playTurn();
		}
		
		System.out.println("----------게임 종료!!!----------");
		System.out.println("현재카운트 : " + currentCount);
		System.out.println("검은 돌 : " + countOfBlackStone);
		System.out.println("하얀 돌 : " + countOfWhiteStone);
		System.out.println("승리 : " + (countOfBlackStone > countOfWhiteStone ? "player" : "상대편"));
		
	}

	private static void playTurn() {
		if(isChoosable()){
			displayChoosablePoints();
			displayCurrentSituation();
			
			if(myTurn){
				System.out.printf("돌을 놓을 좌표를 선택하세요. (입력형식:x,y) :");
				String[] userSelectedXandY = scan.nextLine().split(",");
				if(isUserSelectedUnCorrectly(userSelectedXandY)){ // 목록에 있는 좌표만 선택되도록 개선
					System.out.println();
					System.out.println("------입력 에러!!!------");
					System.out.println("좌표를 정확히 입력해 주세요ex) 3,1");
					System.out.println();
					myTurn = !myTurn;//다시 내턴이 오기위해
					return;
				}
				String userSelectedY = userSelectedXandY[1];
				String userSelectedX = userSelectedXandY[0];
				plate[Integer.parseInt(userSelectedY)][Integer.parseInt(userSelectedX)].setColor(myColor);
				countOfBlackStone = countOfBlackStone+1;
				System.out.println(userSelectedX+ ","+userSelectedY+" 을 선택하셨습니다.");
				modifyStatus(Integer.parseInt(userSelectedX), Integer.parseInt(userSelectedY));
				
				blackOutCount = 0;
			}else {
				Random ran = new Random();
				int randInt = ran.nextInt(choices.size()); 
				int enemySelectedY = choices.get(randInt).getY();
				int enemySelectedX = choices.get(randInt).getX();
				plate[enemySelectedY][enemySelectedX].setColor(myColor);
				countOfWhiteStone = countOfWhiteStone+1;
				System.out.println("상대가 " + enemySelectedX+ ","+enemySelectedY+" 을 선택하였습니다.");
				modifyStatus(enemySelectedX, enemySelectedY);
				
				whiteOutCount = 0;
			}
			currentCount++;
			displayCurrentSituation();
		} else{
			System.out.println("선택할 수 있는 돌이 없어 상대 턴으로 넘어갑니다.");
			
			if(myTurn) blackOutCount++;
			else whiteOutCount++;
		}
	}
	
	//현재 영역에서 선택할 수 있는 선택지를 찾아 제공한다.
	public static void findChoices(){
		validate();//선택할 수 있는곳을 검사한다.
		choices = new ArrayList<>();
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				if(plate[i][j].getChoosable()){
					choices.add(plate[i][j]);
				}
			}
		}
	}
	
	//해당 돌을 선택할 수 있는지 여부를 저장한다.
	public static void validate(){
		clearPlate();
		searchDirectionForChoices();
	}

	private static void clearPlate() {
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				plate[i][j].setChoosable(false);
			}
		}
	}
	
	//선택한 돌의 좌표로 부터 4방향의 배열에 변경되야 할 돌을 찾아 바꾼다.
	private static void modifyStatus(int x, int y){

		stack = new Stack<>();

		if(enemyExistsInNorthWest(x, y)){//왼쪽 대각선 위
			for(int q = y-1, r= x-1; q >= 0 && r >= 0; q--,r--){
				checkAndFlip(plate[q][r]);
			}
			stack.clear();
		}
		if(enemyExistsInNorth(x, y)){//위
			for(int q= y-1; q >= 0; q--){
				checkAndFlip(plate[q][x]);
			}
			stack.clear();
		} 
		if(enemyExistsInNorthEast(x, y)){//오른쪽 대각선 위
			for(int q = y-1, r= x+1; q >= 0 && r < plateSize; q--,r++){
				checkAndFlip(plate[q][r]);
			}
			stack.clear();
		}
		if(enemyExistsInWest(x, y)){//왼쪽
			for(int r = x-1; r >= 0; r--){
				checkAndFlip(plate[y][r]);
			}
			stack.clear();
		}
		if(enemyExistsInEast(x, y)){//오른쪽
			for(int r = x+1; r < plateSize; r++){
				checkAndFlip(plate[y][r]);
			}
			stack.clear();
		}
		if(enemyExistsInSouthWest(x, y)){//왼쪽 대각선 아래
			for(int q = y+1, r = x-1; q < plateSize && r >= 0; q++,r--){
				checkAndFlip(plate[q][r]);
			}
			stack.clear();
		}
		if(enemyExistsInSouth(x, y)){//아래
			for(int q = y+1; q < plateSize; q++){
				checkAndFlip(plate[q][x]);
			}
			stack.clear();
		}
		if(enemyExistsInSouthEast(x, y)){//오른 대각 아래
			for(int q = y+1, r= x+1; q < plateSize && r < plateSize; q++, r++){
				checkAndFlip(plate[q][r]);
			}
			stack.clear();
		}
	}

	private static void checkAndFlip(Pos discoveredStone) {
		if(enemyColor.equalsIgnoreCase(discoveredStone.getColor())){
			stack.push(discoveredStone);
		}else if(myColor.equalsIgnoreCase(discoveredStone.getColor())){
			flipStone();
			return;
		}else if("none".equalsIgnoreCase(discoveredStone.getColor())){
			return;
		}
	}
	
	//상대 돌 뒤집기
	private static void flipStone(){
		while(!stack.isEmpty()){
			stack.pop().setColor(myColor);
			if(myColor == "black"){
				countOfBlackStone = countOfBlackStone+1;
				countOfWhiteStone = countOfWhiteStone-1;
			} else{
				countOfWhiteStone = countOfWhiteStone+1;
				countOfBlackStone = countOfBlackStone-1;
			}
		}
	}
	
	private static void initStone(int plateSize) {
		systemWaitForReality();
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				if(isBlackStartingPlace(plateSize, i, j)){
					plate[i][j] = new Pos("black", j, i, plateSize);
					countOfBlackStone++;
					currentCount++;
				}else if(isWhiteStartingPlace(plateSize, i, j)){
					plate[i][j] = new Pos("white", j, i, plateSize);
					countOfWhiteStone++;
					currentCount++;
				}else{
					plate[i][j] = new Pos("none", j, i, plateSize);
				}
			}
		}
	}

	private static void showStoneStatusAndFindPointOfPossiblePoint(StringBuffer displayer, int i, int j) {
		if(plate[i][j].getColor().equals("black")){
			displayer.append("●  ");
		}else if(plate[i][j].getColor().equals("white")){
			displayer.append("○  ");
		}else if(plate[i][j].getChoosable() == true){
			displayer.append("¤  ");
		}else {
			displayer.append("□  ");
		}
	}
	
	//선택할 수 있는 지점을 표시해주는 함수.. 내용은 지저분하다
	private static void searchDirectionForChoices() {
		for(int i=0; i<plateSize; i++){
			for(int j=0; j<plateSize; j++){
				if(plate[i][j].getColor() == enemyColor){//적의 돌에서 주변 8방향 수색
					if(isEmptyNorthWest(i, j)){//왼쪽 대각선 위가 비었으면
						for(int q = i+1, r= j+1; q < plateSize && r < plateSize; q++,r++){//반대방향에 나의 돌이 하나라도 있다면 있다면 해당 지역을 선택할 수 있다.
							if(plate[q][r].getColor() == myColor){
								plate[i-1][j-1].setChoosable(true);
								break;
							}else if(plate[q][r].getColor() == "none"){
								break;
							}
						}
					}
					if(isEmptyNorth(i, j)){//위
						for(int q= i+1; q < plateSize; q++){
							if(plate[q][j].getColor() == myColor){
								plate[i-1][j].setChoosable(true);
								break;
							}else if(plate[q][j].getColor() == "none"){
								break;
							}
						}
					} 
					if(isEmptyNorthEast(i, j)){//오른쪽 대각선 위
						for(int q = i+1, r= j-1; q < plateSize && r >= 0; q++,r--){
							if(plate[q][r].getColor() == myColor){
								plate[i-1][j+1].setChoosable(true);
								break;
							}else if(plate[q][r].getColor()== "none"){
								break;
							}
						}
					}
					if(isEmptyWest(i, j)){//왼쪽
						for(int r = j+1; r < plateSize; r++){
							if(plate[i][r].getColor() == myColor){
								plate[i][j-1].setChoosable(true);
								break;
							}else if(plate[i][r].getColor() == "none"){
								break;
							}
						}
					}
					if(isEmptyEast(i, j)){//오른쪽
						for(int r = j-1; r >= 0; r--){
							if(plate[i][r].getColor() == myColor){
								plate[i][j+1].setChoosable(true);
								break;
							}else if(plate[i][r].getColor() == "none"){
								break;
							}
						}
					}
					if(isEmptySouthWest(i, j)){//왼쪽 대각선 아래
						for(int q = i-1, r= j+1; q >= 0 && r < plateSize; q--, r++){
							if(plate[q][r].getColor()==myColor){
								plate[i+1][j-1].setChoosable(true);
								break;
							}else if(plate[q][r].getColor() == "none"){
								break;
							}
						}
					}
					if(isEmptySouth(i, j)){//아래
						for(int q= i-1; q >= 0; q--){
							if(plate[q][j].getColor() == myColor){
								plate[i+1][j].setChoosable(true);
								break;
							}else if(plate[q][j].getColor() == "none"){
								break;
							}
						}
					}
					if(isEmptySouthEast(i, j)){//오른 대각 아래
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
	
	private static void displayChoosablePoints() {
		for(int i=0, size = choices.size(); i < size; i++){
			System.out.printf("[" + choices.get(i).getX()+"," + choices.get(i).getY() + "] ");
		}
	}
	
	//if문 조건 추출을 위해 만든 private 메소드들.. 볼것 없는 부분
	//현재 돌의 갯수를 세어 더 이상 돌을 놓을 수 없으면 true를 리턴한다. 
	private static boolean isFinish(){
		if(totalCount - currentCount == 0 || countOfWhiteStone == 0 || countOfBlackStone == 0 ||
				whiteOutCount == 5 || blackOutCount == 5){
			System.out.println(totalCount);
			System.out.println(currentCount);
			System.out.println(countOfWhiteStone);
			System.out.println(countOfBlackStone);
			System.out.println(whiteOutCount);
			System.out.println(blackOutCount);
			return true;
		}
		return false;
	}
	
	private static boolean isWhiteStartingPlace(int plateSize, int i, int j) {
		return (i == (plateSize/2)-1 && j == plateSize/2) || (i == plateSize/2 && j == (plateSize/2)-1);
	}

	private static boolean isBlackStartingPlace(int plateSize, int i, int j) {
		return (i == (plateSize/2)-1 && j == (plateSize/2)-1) || (i == plateSize/2 && j == plateSize/2);
	}
	
	private static boolean isUserSelectedUnCorrectly(String[] userSelectedXandY) {
		return (userSelectedXandY.length != 2) || !Character.isDigit(userSelectedXandY[0].charAt(0))
				|| !Character.isDigit(userSelectedXandY[1].charAt(0)) || Integer.parseInt(userSelectedXandY[0]) > 5 
				|| Integer.parseInt(userSelectedXandY[0]) < 0 || Integer.parseInt(userSelectedXandY[1]) > 5
				|| Integer.parseInt(userSelectedXandY[1]) < 0;
	}

	private static boolean isChoosable() {
		return choices.size()>0;
	}
	
	private static boolean enemyExistsInSouthEast(int x, int y) {
		return y < plateSize-2 && x < plateSize-2 && plate[y+1][x+1].getColor() == enemyColor;
	}

	private static boolean enemyExistsInSouth(int x, int y) {
		return y < plateSize-2 && plate[y+1][x].getColor() == enemyColor;
	}

	private static boolean enemyExistsInSouthWest(int x, int y) {
		return y < plateSize-2 && x > 2 && plate[y+1][x-1].getColor() == enemyColor;
	}

	private static boolean enemyExistsInEast(int x, int y) {
		return x < plateSize-2 && plate[y][x+1].getColor() == enemyColor;
	}

	private static boolean enemyExistsInWest(int x, int y) {
		return x > 2 && plate[y][x-1].getColor() == enemyColor;
	}

	private static boolean enemyExistsInNorthEast(int x, int y) {
		return y > 2 && x < plateSize-2 && plate[y-1][x+1].getColor() == enemyColor;
	}

	private static boolean enemyExistsInNorth(int x, int y) {
		return y > 2 && plate[y-1][x].getColor() == enemyColor;
	}

	private static boolean enemyExistsInNorthWest(int x, int y) {
		return y > 2 && x > 2 && plate[y-1][x-1].getColor() == enemyColor;
	}
	
	private static boolean isEmptySouthEast(int i, int j) {
		return i < plateSize-1 && j < plateSize-1 && plate[i+1][j+1].getColor() == "none";
	}

	private static boolean isEmptySouth(int i, int j) {
		return i < plateSize-1 && plate[i+1][j].getColor() == "none";
	}

	private static boolean isEmptySouthWest(int i, int j) {
		return i < plateSize-1 && j > 0 && plate[i+1][j-1].getColor() == "none";
	}

	private static boolean isEmptyEast(int i, int j) {
		return j < plateSize-1 && plate[i][j+1].getColor().equals("none");
	}

	private static boolean isEmptyWest(int i, int j) {
		return j > 0 && plate[i][j-1].getColor() == "none";
	}

	private static boolean isEmptyNorthEast(int i, int j) {
		return i > 0 && j < plateSize-1 && plate[i-1][j+1].getColor() == "none";
	}

	private static boolean isEmptyNorth(int i, int j) {
		return i > 0 && plate[i-1][j].getColor() == "none";
	}

	private static boolean isEmptyNorthWest(int i, int j) {
		return i > 0 && j > 0 && plate[i-1][j-1].getColor() == "none";
	}
	
	private static void systemWaitForReality() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
