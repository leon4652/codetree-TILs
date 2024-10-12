import java.util.*;
import java.io.*;
public class Main {
	static int[] DR = {-1, 0, 1, 0}; //상 좌 하 우
	static int[] DC = {0, 1, 0, -1};
	static final int T = 4;
	static int N;
	static Field[][] map;
	static boolean[][] check;
	static Queue<Pos> bfs = new ArrayDeque<>();
	static Map<Integer, int[]> groupCountMap = new HashMap(); //gNo : {count, val}
	static Set<Integer> checkedGroupNoSet = new HashSet<>();
	static Map<Integer, Integer> temp = new HashMap<>(); //임시 사용 용도
	static int res;
	static void log(String msg, boolean isValue) {
		System.out.println(msg);
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(!isValue) System.out.print(map[i][j].groupNo + " ");
				else System.out.print(map[i][j].value + " ");
				
			}
			System.out.println();
		}
	}
	static void solve() {
		for(int i = 0; i < T; i++) {
			grouping();
			getScore();
			rotate();
			
//			log("test", true);
		}
		System.out.println(res);
	}
	
	static void rotate() {
		int C = N/2; //가운데 및 변 길이
		//십자 회전
		rotateCross(C);
		//사각형 회전
		rotateSquare(0, 0, C);
		rotateSquare(C + 1, 0, C);
		rotateSquare(0, C + 1, C);
		rotateSquare(C + 1, C + 1, C);
	}
	
	static void rotateSquare(int tr, int tc, int s) {
		int[][] arr = new int[s][s];
		
		for(int i = 0; i < s; i++) {
			for(int j = 0; j < s; j++) {
				arr[j][s - 1 - i] = map[tr + i][tc + j].value;
			}
		}
		
		int r = 0;
		int c = 0;
		for(int i = tr; i < tr + s; i++) {
			for(int j = tc; j < tc + s; j++) {
				map[i][j].value = arr[r][c];
				c++;
			}
			r++;
			c = 0;
		}
	}
	
	static void rotateCross(int c) {
		int[] values = new int[4];
		for(int i = 1; i <= c; i++) { //뻗어나가는 길이
			for(int j = 0; j < 4; j++) {
				values[j] = map[c + (DR[j] * i)][c + (DC[j] * i)].value;
			}
			
			for(int j = 0; j < 4; j++) {
				int k = (j - 1); //한칸 왼쪽
				if(k == -1) k = 3;
				map[c + (DR[k] * i)][c + (DC[k] * i)].value = values[j];
			}
		}
		
	}
	
	static void getScore() {
		int result = 0;
		checkedGroupNoSet.clear();
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				int gNo = map[i][j].groupNo;
				if(gNo == 0) throw new RuntimeException("getScore:: gNO 매핑 안 됨");
				if(checkedGroupNoSet.contains(gNo)) continue;
				result += getNowScore(i, j, gNo);//인근 조화로움 점수 찾기
				checkedGroupNoSet.add(gNo);
			}
		}
		
		result /= 2; //중복 제거
//		System.out.println("현재 예술 점수(출력만 함) " + result);
		res += result;
	}
	
	static int getNowScore(int r, int c, int gNo) {
		int result = 0;
		temp.clear();
		check = new boolean[N][N];
		bfs.clear();
		bfs.add(new Pos(r, c));
		while(!bfs.isEmpty()) {
			Pos p = bfs.poll();
			if(check[p.r][p.c]) continue;
			check[p.r][p.c] = true;
			//사방 탐색해서 인접한 다른 그룹 변 확인 
			for(int i = 0; i < 4; i++) {
				int nr = p.r + DR[i];
				int nc = p.c + DC[i];
				if(cantGo(nr, nc) || map[nr][nc].groupNo == gNo) continue;
				int nextGroupNo = map[nr][nc].groupNo; //인접한 다른 gNo 탐색
				if(!temp.containsKey(nextGroupNo)) temp.put(nextGroupNo, 1);
				else temp.put(nextGroupNo, temp.get(nextGroupNo) + 1);
			}
			
			//이동
			for(int i = 0; i < 4; i++) {
				int nr = p.r + DR[i];
				int nc = p.c + DC[i];
				if(cantGo(nr, nc) || check[nr][nc] || map[nr][nc].groupNo != gNo) continue;
				bfs.add(new Pos(nr, nc));
			}
		}
		
		int nowCount = groupCountMap.get(gNo)[0];
		int nowValue = groupCountMap.get(gNo)[1];
		
		for(Integer anotherGroupNo : temp.keySet()) {
			int near = temp.get(anotherGroupNo); //맞닿은 변의 수
			int anotherCount = groupCountMap.get(anotherGroupNo)[0];
			int anotherValue = groupCountMap.get(anotherGroupNo)[1];
			
			result += (nowCount + anotherCount) * nowValue * anotherValue * near;
		}
		
		return result;
	}
	
	static void grouping() {
		groupCountMap.clear();
		int gNo = 1;
		//초기화
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				map[i][j].clearGroup();
			}
		}
		
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(map[i][j].groupNo == 0) groupingStart(i, j, gNo++);
			}
		}
	}
	
	static void groupingStart(int r, int c, int gNo) {
		groupCountMap.put(gNo, new int[] {0, map[r][c].value});
		check = new boolean[N][N];
		int val = map[r][c].value;
		bfs.add(new Pos(r, c));
		while(!bfs.isEmpty()) {
			Pos p = bfs.poll();
			if(check[p.r][p.c]) continue;
			check[p.r][p.c] = true;
			map[p.r][p.c].groupNo = gNo;
			//점수 끼워넣기
			int[] info = groupCountMap.get(gNo);
			info[0]++; //count++
			groupCountMap.put(gNo, info);
			
			for(int i = 0; i < 4; i++) {
				int nr = p.r + DR[i];
				int nc = p.c + DC[i];
				if(cantGo(nr, nc) || check[nr][nc] || map[nr][nc].groupNo != 0 || map[nr][nc].value != val) continue;
				bfs.add(new Pos(nr, nc));
			}
		}
	}
	
	static boolean cantGo(int r, int c) {
		return r < 0 || c < 0|| r >= N || c >= N;
	}
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		
		map = new Field[N][N];
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				map[i][j] = new Field(new Pos(i, j), Integer.parseInt(st.nextToken()));
			}
		}
		
		solve();
	}
}


class Pos {
	int r, c;
	
	public Pos(int r,int c) {
		this.r = r;
		this.c = c;
	}
}
class Field {
	Pos p;
	int groupNo;
	int value;
	
	public Field(Pos p, int value) {
		this.p = p;
		this.value = value;
	}
	
	public void clearGroup() {
		this.groupNo = 0;
	}
}