import java.io.*;
import java.util.*;
public class Main {
	static int N, M, map[][];
	static boolean[][] check;
	static Queue<Pos> clouds = new ArrayDeque<>();
	static Queue<Pos> tempQueue = new ArrayDeque<>();
	static Queue<int[]> dAndP = new ArrayDeque<>();
	static int[] DR = {0, -1, -1, -1, 0, 1, 1, 1};
	static int[] DC = {1, 1, 0, -1, -1, -1, 0, 1};
	static int[] CR = {1, 1, -1, -1};
	static int[] CC = {1, -1, -1, 1};

	
	static void solve() {
		while(!dAndP.isEmpty()) {
			int[] dp = dAndP.poll();
			int d = dp[0];
			int p = dp[1];
			moveClouds(d, p);
			useCloud();
			addClouds();
		}
		result();
	}
	
	static void result() {
		int res = 0;
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				res += map[i][j];
			}
		}
		System.out.println(res);
	}
	
 	static void addClouds() {
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(check[i][j] || map[i][j] < 2) continue;
				map[i][j] -=2;
				clouds.add(new Pos(i, j));
			}
		}
	}
	
	static void useCloud() {
		check = new boolean[N][N];
		
		int size = clouds.size();
		while(size-- > 0) { //++1
			Pos now = clouds.poll();
			map[now.r][now.c]++;
			clouds.add(now);
		}
		
		//대각선 체크
		size = clouds.size();
		while(size-- > 0) {
			Pos now = clouds.poll();
			for(int i = 0; i < 4; i++) {
				int nr = now.r + CR[i];
				int nc = now.c + CC[i];
				if(cantGo(nr, nc) || map[nr][nc] == 0) continue;
				now.value++;
			}
			clouds.add(now);
		}
		
		while(!clouds.isEmpty()) {
			Pos now = clouds.poll();
			check[now.r][now.c] = true; //마킹
			map[now.r][now.c] += now.value; //성장

		}
	}
	
	static void moveClouds(int d, int p) {
		int size = clouds.size();
		while(size-- > 0) {
			Pos cloud = clouds.poll();
			int nr = checkPos(cloud.r + (DR[d] * p));
			int nc = checkPos(cloud.c + (DC[d] * p));
			cloud.r = nr;
			cloud.c = nc;
			clouds.add(cloud);
		}
	}
	
	static int checkPos(int n) {
		n = n % N;
		if(n < 0) return N + n;
		return n;
	}
	static boolean cantGo(int r, int c) {
		return r < 0 || c < 0 || r >= N || c >=N;
	}
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		map = new int[N][N];
		
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		clouds.add(new Pos(N - 2, 0));
		clouds.add(new Pos(N - 1, 0));
		clouds.add(new Pos(N - 2, 1));
		clouds.add(new Pos(N - 1, 1));
		
		for(int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			int d = Integer.parseInt(st.nextToken()) - 1;
			int p = Integer.parseInt(st.nextToken());
			dAndP.add(new int[] {d, p});
		}
		
		solve();
	}
} class Pos {
	int r; 
	int c;
	int value = 0;
	public Pos(int r, int c) {
		this.r = r;
		this.c = c;
	}
}