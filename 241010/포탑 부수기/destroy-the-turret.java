import java.util.*;
import java.io.*;
public class Main {
	static int N, M, K;
	static Tower[][] map;
	static List<Tower> towers = new ArrayList<>();
	//우/하/좌/상
	static int[] DR = {0, 1, 0, -1};
	static int[] DC = {1, 0, -1, 0};
	static int[] NR = {-1, -1, -1, 0, 0 ,1, 1, 1};
	static int[] NC = {-1,0, 1, -1, 1, -1, 0, 1};
	
	public static void main(String[] args) {
		input();
		solve();
		cal();
	}
	
	static void cal() {
		int res = 0;
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				res = Math.max(res, map[i][j].dmg);
			}
		}
		System.out.println(res);
	}
	
	static void solve() {
		for(int i = 0; i < K; i++) {
//			log(i + 1 + "round start");
			setTowers();
			if(towers.size() < 2) return; //부서지지 않은 포탑이 1개가 된다면 그 즉시 중지
			
			//공격자 선정
			Tower atkTower = Collections.min(towers);
			atkTower.attacked = true;
			atkTower.recent = i + 1;
			Tower defTower = Collections.max(towers);
			defTower.attacked = true;
			//핸디캡
			atkTower.dmg += (N + M); 
			//공격
			List<int[]> laserPath = tryLaserAtk(atkTower, defTower); //레이져 공격 시도
			if(laserPath.isEmpty()) tryBomb(atkTower, defTower);
			else checkLaserDamage(atkTower, defTower, laserPath);
			
			//포탑 부서짐 및 정비
			brokenAndRepair();
			
//			log(i + 1 + "round end");
		}
	}
	
	static void brokenAndRepair() {
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				Tower tower = map[i][j];
				if(tower.dmg == 0) continue;
				if(tower.attacked) tower.attacked = false;
				else tower.dmg += 1;
			}
		}
	}
	
 	static void tryBomb(Tower atkTower, Tower defTower) {
 		int damage = atkTower.dmg;
		int r = defTower.r;
		int c = defTower.c;
		//공격 대상
		defTower.dmg = Math.max(0, defTower.dmg - damage);
		
		//8방
		for(int i = 0; i < 8; i++) {
			int nr = checkOutOfMap(r + NR[i], N);
			int nc = checkOutOfMap(c + NC[i], M);
			if(atkTower.r == nr && atkTower.c == nc) continue;
			
			Tower tower = map[nr][nc];
			tower.attacked = true;
			tower.dmg = Math.max(0, tower.dmg - (damage / 2));
		}
	}
	
	static void checkLaserDamage(Tower atkTower, Tower defTower, List<int[]> laserPath) {
		int damage = atkTower.dmg;
		//처음, 마지막은 제외(시작과 끝)
		for(int i = 1; i < laserPath.size() - 1; i++) {
			int[] now = laserPath.get(i);
			int r = now[0];
			int c = now[1];
			
			Tower tower = map[r][c];
			tower.attacked = true;
			tower.dmg = Math.max(0, tower.dmg - (damage / 2));
		}
		
		//마지막
		defTower.dmg = Math.max(0, defTower.dmg - damage);
	}
	
	static List<int[]> tryLaserAtk(Tower atkTower, Tower defTower) {
		boolean[][] check = new boolean[N][M];
		Queue<Trace> bfs = new ArrayDeque<>();
		bfs.add(new Trace(atkTower.r, atkTower.c));
		
		//목표
		int resR = defTower.r;
		int resC = defTower.c;
		List<int[]> traceLine = new ArrayList<>(); //레이져 경로
		
		while(!bfs.isEmpty()) {
			Trace trace = bfs.poll();
			int r = trace.r;
			int c = trace.c;
			List<int[]> line = trace.line;
			if(check[r][c] || map[r][c].dmg <= 0) continue;
			check[r][c] = true;
			line.add(new int[] {r, c});
			
			if(r == resR && c == resC) {
				traceLine = line;
				break;
			}
			
			for(int i = 0; i < 4; i++) {
				int nr = checkOutOfMap(r + DR[i], N);
				int nc = checkOutOfMap(c + DC[i], M);
				if(check[nr][nc]) continue;
				Trace next = new Trace(nr, nc);
				for(int j = 0; j < line.size(); j++) {
					int[] copy = line.get(j);
					next.line.add(new int[] {copy[0], copy[1]});
				}
				
				bfs.add(next);
			}
		}
		
		return traceLine;
	}
	
	static void setTowers() {
		towers.clear();
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				if(map[i][j].dmg == 0) continue;
				towers.add(map[i][j]);
			}
		}
	}
	
	
	static int checkOutOfMap(int num, int len) {
		num %= len;
		if(num < 0) num += len;
		return num;
	}
	
	static boolean cantGo(int r, int c) {
		return r < 0 || c < 0 || r >= N || c >= M;
	}
	
	static void input() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			StringTokenizer st = new StringTokenizer(br.readLine());
			N = Integer.parseInt(st.nextToken());
			M = Integer.parseInt(st.nextToken());
			K = Integer.parseInt(st.nextToken());
			map = new Tower[N][M];
			for(int i = 0; i < N; i++) {
				st = new StringTokenizer(br.readLine());
				for(int j = 0; j < M; j++) {
					int dmg = Integer.parseInt(st.nextToken());
					map[i][j] = new Tower(i, j, dmg, 0);
				}
			}
			
		} catch(IOException e) {
			
		}
	}
}

class Tower implements Comparable<Tower>{
	Integer r, c, dmg, recent;
	boolean attacked = false;

	@Override
	public int compareTo(Tower t) {
	
	    if (!this.dmg.equals(t.dmg)) {
	        return Integer.compare(this.dmg, t.dmg);
	    }
	    
	    if (!this.recent.equals(t.recent)) {
	        return Integer.compare(t.recent, this.recent);
	    }
	    
	    int thisSum = this.r + this.c;
	    int tSum = t.r + t.c;
	    if (thisSum != tSum) {
	        return Integer.compare(tSum, thisSum);
	    }
	    
	    return Integer.compare(t.c, this.c);
	}
	
	public Tower(int r, int c, int dmg, int recent) {
		this.r = r;
		this.c = c;
		this.dmg = dmg;
		this.recent = recent;
	}

}

class Trace {
	int r, c;
	List<int[]> line = new ArrayList<>();
	public Trace(int r, int c) {
		this.r = r;
		this.c = c;
	}
}