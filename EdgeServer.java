package sisaku;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class EdgeServer {
	final static int ID = 60000;;//エッジサーバのID
	final static int mCastid = 4000;
	double[][] beforeConvocation = new double[9][4];
	double[] convenerCoordinate = new double[2];//招集元のXY座標
	double relativeDistance[][] = new double[9][2];

	String message;

	Udp udp;
	UdpSecond udps;
	mCastUdp mcastudp;

	EdgeServer() throws IOException{
		udp = new Udp(ID);//UDPインスタンスにID付与
		udp.makeMulticastSocket();//ソケット生成
		udp.startListener();//受信

		udps = new UdpSecond(ID);
		udps.makeMulticastSocket() ;//ソケット生成
		udps.startListener() ;

		mcastudp = new mCastUdp(mCastid);
		mcastudp.makeMulticastSocket() ;
		mcastudp.startListener();

	}

	void receiveData() throws IOException{//受信メソッド
		byte[] rcvData = udp.lisner.getData();//受信データ
		byte[] rcvDataS = udps.lisner1.getData();
		if(rcvDataS != null) {
			String str1 = new String(rcvDataS,0,101);
			String[] cData = str1.split(" ", 0);

			/*for (int i = 0 ; i < cData.length ; i++){
			      System.out.println(i + "番目の要素 = :" + cData[i]);
			}*/

			int dronePort = Integer.parseInt(cData[1]);
			double coordinateX = Double.parseDouble(cData[4]);//X座標
			double coordinateY = Double.parseDouble(cData[6]);//Y座標
			double droneBattery = Double.parseDouble(cData[8]);

			for(int i = 0;i < 9; i++) {
				if(dronePort == 50001 + i) {
					beforeConvocation[i][0] = dronePort;
					beforeConvocation[i][1] = coordinateX;
					beforeConvocation[i][2] = coordinateY;
					beforeConvocation[i][3] = droneBattery;
				}
			}


		}
		/*for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 4; j++) {
				System.out.println(allCoordinate[i][j]);
			}

		}*/

		if(rcvData != null) {
			String str = new String(rcvData,0,101);//byte型から文字に変換
			System.out.println("(エッジサーバ受信データ) "+str);
			String[] eachData = str.split(" ", 0);//受信データの分割
			convenerCoordinate[0] = Double.parseDouble(eachData[6]);//X座標
			convenerCoordinate[1] = Double.parseDouble(eachData[8]);//Y座標

			/*for (int i = 0 ; i < eachData.length ; i++){
			      System.out.println(i + "番目の要素 = :" + eachData[i]);
			}*/


			int dport = Integer.parseInt(eachData[1]);//ドローンのポート番号

			if((eachData[11].equals("Normal"))) {
				judgmentData(dport,eachData[3]);//eachData[3]=discovery
			}


			if(!(eachData[11].equals("Normal")) && !(eachData[11].equals("Decline"))) {//プロトコルへ

				/*if(eachData[11].equals("Yes")) {
					//System.out.println(eachData[9]);
					message = "ProvisionalRequest";
					udp.sendMsgsFromServer(dport,message);
					udp.lisner.resetData();
				}*/

				if(eachData[11].equals("ProvisionalReply") || eachData[11].equals("Accept")) {
					calcDistance();
					message = "MainRequest";
					udp.sendMsgsFromServer(dport,message);
					udp.lisner.resetData();
				}

				if(eachData[11].equals("MainReply")) {
					message = "";
					System.out.println("よろしくお願いします");
					udp.sendMsgsFromServer(dport,message);
					udp.lisner.resetData();
				}

				if(eachData[11].equals("start")) {//特に書かないでいい

				}

				if(eachData[11].equals("end")) {
					message = "DissolutionRequest";
				}

			}


			//System.out.println(eachData[9]);//メッセージ表示

			try {//ファイルへの書き込み
				FileWriter fw = new FileWriter("/Users/TKLab/Desktop/data.txt",true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(str);
				bw.newLine();//改行
				bw.flush();
				bw.close();//ファイル閉鎖
			}catch(IOException e) {
				System.out.println("エラー");
			}

			udp.lisner.resetData();//バッファの中のデータをリセット
		}

	}

	void  judgmentData(int dport,String peaple){
		int human = Integer.parseInt(peaple);

		if(human != 0) {
			message = "T";
			udp.sendMsgsFromServer(dport,message);
		}
		else {
			message = "F";
			udp.sendMsgsFromServer(dport,message);
		}
	}

	void calcDistance() {
		for(int i = 0; i < 9; i++) {
			//if(beforeConvocation[i][3]> 50.0) {//バッテリー50%以上
				relativeDistance[i][0] = beforeConvocation[i][0];//ポート番号
				relativeDistance[i][1]
				= Math.abs(convenerCoordinate[0]-beforeConvocation[i][1])+Math.abs(convenerCoordinate[1]-beforeConvocation[i][2]);

			//}

		}
		for(int i = 0; i < 9; i++) {//並び替え
			for(int j=0; j < 9-i-1; j++) {
				if(relativeDistance[j][1] >= relativeDistance[j + 1][1]) {
					double asc = relativeDistance[j][1];
					double port = relativeDistance[j][0];
					relativeDistance[j][1] = relativeDistance[j + 1][1];
					relativeDistance[j][0] = relativeDistance[j + 1][0];
					relativeDistance[j + 1][1] = asc;
					relativeDistance[j + 1][0] = port;
				}
			}
		}

		for(int i = 0; i < 9; i++) {
				System.out.println(relativeDistance[i][0] + " " + relativeDistance[i][1]);
			}


	}

}