import java.io.*;
import java.util.*;
public class Main {
	static int N, M, K, result, map[][];
	static Queue<Pos> players = new ArrayDeque<>();
	static Queue<Pos> targetRotates = new ArrayDeque<>();
	static Pos exit;
	static int[] DR = {-1, 1, 0, 0};
	static int[] DC = {0, 0, -1, 1};
	static int[] sArr = new int[3];
	
	public static void solve() {
		for(int i = 0; i < K; i++) {
			move();
			square();
			if(players.isEmpty()) break;
			rotate();
			if(players.isEmpty()) break;
		}
		while(!players.isEmpty()) result += players.poll().sum;
		System.out.println(result);
		System.out.println((exit.r + 1) + " " + (exit.c + 1));
	}
	
	static void rotate() {
		//출구 및 사람 판단
		int size = players.size();
		while(size-- > 0) { //사람
			Pos p = players.poll();
			rotateEach(p);
			players.add(p);
		}
		rotateEach(exit); //출구 
		
		//벽 회전
		int tr = sArr[0]; //Square r
		int tc = sArr[1]; //.. c
		int n = sArr[2];  //.. size
		int temp[][] = new int[n + 1][n + 1];
		for(int i = 0; i <= n; i++) {
			for(int j = 0; j <= n; j++) {
				temp[j][n - i] = map[tr + i][tc + j];
				if(temp[j][n - i] != 0) temp[j][n - i]--; //1 감소
			}
		}
		for(int i = 0; i <= n; i++) {
			for(int j = 0; j <= n; j++) {
				map[tr + i][tc + j] = temp[i][j]; 
			}
		}
	}
	
	static void rotateEach(Pos p) {
		int tr = sArr[0]; //Square r
		int tc = sArr[1]; //.. c
		int n = sArr[2];  //.. size
		if(isInner(p, tr, tr + n, tc, tc + n)) {
			//[i, j] 90 right rotate -> [j, N - 1 - i] .. 0,0 기준. tr, tc 기준점에서는 상대 좌표 필요
			int i = p.r - tr; //상대좌표
			int j = p.c - tc;
			//90 회전
			int ni = j;
			int nj = n - i;
			//상대좌표 보정
			int r = tr + ni;
			int c = tc + nj;
			p.r = r;
			p.c = c;
		}
	}
 	static void square() {
 		sArr = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}; //r, c, size
		int size = players.size();
		while(size-- > 0) {
			Pos p = players.poll();
			checkCanSmallSquare(p);
			players.add(p);
		}
	}
	static void checkCanSmallSquare(Pos p) {
		for(int n = 1; n < N; n++) {
			for(int i = 0; i < N - n; i++) { //i, j는 정사각형의 좌상단 기준점임.
				for(int j = 0; j < N - n; j++) {
					int lr = i + n; //우하단 기준점임
					int lc = j + n;
					if(!isInner(p, i, lr, j, lc)) continue; //조건 불가
					if(!isInner(exit, i, lr, j, lc)) continue;
					setSquareArr(i, j, n); //최저 사각형 판별
					return; //find
				}
			}
		}
	}
	static void setSquareArr(int r, int c, int n) {
		if(sArr[2] < n) return;
		if(sArr[2] == n ) {
			if(sArr[0] < r) return;
			if(sArr[0] == r && sArr[1] < c) return;
		}
		sArr[0] = r;
		sArr[1] = c;
		sArr[2] = n;
	}
	static boolean isInner(Pos p, int tr, int lr, int tc, int lc) {
		if(tr <= p.r && p.r <= lr && tc <= p.c && p.c <= lc) return true;
		return false;
	}
	
	static void move() {
		int size = players.size();
		while(size-- > 0) {
			Pos p = players.poll();
			for(int i = 0; i < 4; i++) {
				int r = p.r + DR[i];
				int c = p.c + DC[i];
				if(cantGo(r, c) || map[r][c] != 0) continue;
				int nowShort = getShortest(p.r, p.c, exit.r, exit.c);
				int nextShort = getShortest(r, c, exit.r, exit.c);
				if(nowShort <= nextShort) continue;
				p.r = r;
				p.c = c;
				p.sum++;
				break;
			}
			if(p.r == exit.r && p.c == exit.c) {
				result += p.sum; 
			}
			else players.add(p);
		}
	}
	static boolean cantGo(int r, int c) {
		return r < 0 || c < 0 || r >= N || c >= N;
	}
	static int getShortest(int r1, int c1, int r2, int c2) {
		return Math.abs(r1 - r2) + Math.abs(c1 - c2);
	}
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new int[N][N];
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		for(int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			players.add(new Pos(Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1));
		}
		st = new StringTokenizer(br.readLine());
		exit = new Pos(Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1);
		solve();
	}
	
} class Pos {
	int r, c, sum = 0;
	public Pos(int r, int c) {this.r = r; this.c = c;}
}