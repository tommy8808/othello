import java.util.List;

public class MessageService {
	private static int plateSize;

	public MessageService(int plateSize) {
		MessageService.plateSize = plateSize;
	}
	
	public void printInitMsg() {
		System.out.println();
		System.out.println("******** Othello ********");
		System.out.println("-----게임을 초기화 합니다.-----");
	}
	
	public void printStartMsg() {
		systemWaitForReality();
		System.out.println("-----오델로 게임을 시작합니다.-----");
		System.out.println();
	}

	public void printFinishMsg(int currentCount, int countOfBlackStone, int countOfWhiteStone) {
		System.out.println("----------게임 종료!!!----------");
		System.out.println("현재카운트 : " + currentCount);
		System.out.println("검은 돌 : " + countOfBlackStone);
		System.out.println("하얀 돌 : " + countOfWhiteStone);
		System.out.println("승리 : " + (countOfBlackStone > countOfWhiteStone ? "player" : "상대편"));
	}
	
	private static void systemWaitForReality() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//현재 게임상황 display
	public void printGameStatus(Pos[][] plate){
		System.out.println();
		StringBuffer gameDisplayer = new StringBuffer();
		for(int i=0; i<plateSize; i++){
			gameDisplayer.append("　　　　");
			for(int j=0; j<plateSize; j++){
				if(plate[i][j].getColor().equals("black")){
					gameDisplayer.append("●  ");
				}else if(plate[i][j].getColor().equals("white")){
					gameDisplayer.append("○  ");
				}else if(plate[i][j].getChoosable() == true){
					gameDisplayer.append("¤  ");
				}else {
					gameDisplayer.append("□  ");
				}
			}
			gameDisplayer.append("\n");
		}
		System.out.println(gameDisplayer.toString());
	}
	
	public void printWhoIsTurn(Boolean myTurn) {
		if(myTurn){
			System.out.println("---------나의턴---------");
			System.out.printf("선택 가능 좌표 :  ");
			
		}else {
			System.out.println("---------상대턴---------");
		}
	}
	
	public void printChoosablePoints(List<Pos> choices) {
		for(int i=0, size = choices.size(); i < size; i++){
			System.out.printf("[" + choices.get(i).getX()+"," + choices.get(i).getY() + "] ");
		}
	}
	
	public void printInputErrorMsg() {
		System.out.println();
		System.out.println("------입력 에러!!!------");
		System.out.println("선택 가능 목록의 좌표를 정확히 입력해 주세요. ex) 3,1");
		System.out.println();
	}
	
}
