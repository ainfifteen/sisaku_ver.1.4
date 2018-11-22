package sisaku;

import java.io.IOException;

public class Main{
	public static void main(String args[]) throws IOException{

		double simTime = 0.1;//シミュレーション間隔 0.1
		double endTime = 31.2;//シミュレーション時間(招集しないなら406秒
		double lapseTime = 1;//経過時間

		//long sleepTime = (long) (simTime * 1000);

		int[][] field = new int[24][24];//データの格納
		for(int i = 0; i < 24; i++) {
			for(int j = 0; j < 24; j++) {
				field[i][j] = 0;
				field[0][6] = 1;
			}
		}


		int[][] area = new int[9][2];//エリア生成
		int x = 0, y = 0;
		for(int i = 0; i < 9; i++) {
			if(i % 3 == 0) {
				x = 0;
				y += 240;

			}
			area[i][0] = x;
			area[i][1] = y;
			x += 240;

		}
		//for(int i = 0; i < 9; i++) {//初期位置の確認
			//System.out.println("x："+area[i][0]+"   "+"y："+area[i][1]);
		//}


		Drone[] drone = new Drone[9];//ドローン9台生成


		for(int i = 0; i < 9; i++) {//ドローンに値を割り当て
			drone[i] = new Drone(i + 50001, area[i][0], area[i][1]);
		}

		EdgeServer edgeServer = new EdgeServer();//インスタンス生成

		while(lapseTime < endTime) {

			for(int i = 0; i < 9; i++) {
				drone[i].move(simTime);
				drone[i].dataGet(field);
				System.out.println("ドローン"+(i+1)+":状態"+ drone[i].state+" x:"+ drone[i].x+"  y:"+drone[i].y+
						" 方向:"+drone[i].direction + " "+ drone[i].battery+" "+drone[i].gatheringState);
				//System.out.println(" x:"+ drone[1].x + " " + " x:"+ drone[2].x);
				edgeServer.receiveData();
				System.out.println("");
			}

			lapseTime += simTime;
			System.out.println("経過時間："+lapseTime);

			/*try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}*/
		}


	}
}