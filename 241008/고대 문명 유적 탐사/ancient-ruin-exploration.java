import java.util.*;
import java.io.*;
public class Main {
    static final int LN = 5; // 고대 문명 전체 격자 크기
    static final int SN = 3; // 회전시킬 격자의 크기
    static final int[][] targetGrid = new int[SN][SN];
    static final int[][] targetGridResult = new int[SN][SN];
    static int K, M;
    static int[][] nowMap, testMap;
    static Queue<Integer> relics = new ArrayDeque<>();
    static Queue<Pos> pq = new PriorityQueue<>();
    static Queue<int[]> bfs = new ArrayDeque<>();
    static int[] DR = {-1, 0, 1, 0};
    static int[] DC = {0, 1, 0, -1};
    static int[][] pos = { //열이 가장 작은 구간, 행이 작은 구간 순서로 정렬
			{1, 1}, {2, 1}, {3, 1}, 
			{1, 2}, {2, 2}, {3, 2}, 
			{1, 3}, {2, 3}, {3, 3}
	};
    
    static boolean solve() {
    	int[][] maxTestMap = new int[LN][LN]; //최대 회전 맵
    	int[] res = new int[] {0, 0}; //최대 유물 수, 최대 유물일 때 회전각
    	
		//9개, 각 회전 3회의 경우의 수 전부 비교 후 최대값 갱신
		for(int j = 0; j < pos.length; j++) {
			for(int k = 1; k <= 3; k++) {
				checkEach(j, k, res, maxTestMap);
			}
		}
		
		if(res[0] == 0) return false; //유물 획득 실패 : 종료
		
		//조각 집어넣고 재귀 탐색하기
		int lastScore = 0;
		while(true) {
			fillRelic(maxTestMap);
			int nowScore = checkRelicAndGetScore(maxTestMap);
			res[0] += nowScore; //가산
			if(lastScore == 0 && nowScore == 0) break; //기저조건
			lastScore = nowScore; //이전 점수 넘겨주기
		}
	
		//맵 갱신하기
    	for(int i = 0; i < LN; i++) {
    		for(int j = 0; j < LN; j++) {
    			nowMap[i][j] = maxTestMap[i][j];
    		}
    	}
		
		System.out.print(res[0] + " "); //아니라면 출력 후 이어가기
    	return true;
    }
    
    static void fillRelic(int[][] map) {
    	if(relics.isEmpty()) return; //렐릭 없음
    	
    	pq.clear();
    	for(int i = 0; i < LN; i++) {
    		for(int j = 0; j < LN; j++) {
    			if(map[i][j] == 0) pq.add(new Pos(i, j));
    		}
    	}
    	
    	while(!pq.isEmpty() && !relics.isEmpty()) {
    		Pos p = pq.poll();
    		map[p.r][p.c] = relics.poll();
    	}
    }
    
    static void checkEach(int idx, int rotate, int[] res, int[][] maxTestMap) {
    	int nowRelicCount = 0; //현재 점수
    	//1. 초기화
    	resetTestMap();
    	//2. 회전 진행
    	for(int i = 0; i < rotate; i++) rotateRight(pos[idx][0], pos[idx][1]);
    	//3. 유물 획득
    	nowRelicCount += checkRelicAndGetScore(testMap); 
    	
    	//System.out.println("검증 : " + pos[idx][0] + ", " + pos[idx][1] + " : rotate(" + rotate +")" + " : " + nowRelicCount);
    	
    	//지금 카운트가 더 크거나, 같을 때 회전각이 더 작은 경우 최댓값 갱신
    	if(res[0] < nowRelicCount || (res[0] == nowRelicCount && res[1] > rotate)) { 
    		res[0] = nowRelicCount; //값 갱신
    		res[1] = rotate;
    		
    		//최대 회전 배열 복사
    		for(int i = 0; i < LN; i++) {
    			for(int j = 0; j < LN; j++) {
    				maxTestMap[i][j] = testMap[i][j]; 
    			}
    		}
    	}
    }
    
    static int checkRelicAndGetScore(int[][] map) {
    	int totalScore = 0;
    	
    	for(int i = 0; i < LN; i++) {
    		for(int j = 0; j < LN; j++) {
    			int no = map[i][j]; //현재 렐릭 번호
    			if(no == 0) continue; //0 = 없음
    			
    			boolean check[][] = new boolean[LN][LN];
    			int nowScore = 0;
    			bfs.clear();
    			bfs.add(new int[] {i, j});
    			while(!bfs.isEmpty()) {
    				int[] now = bfs.poll();
    				int r = now[0];
    				int c = now[1];
    				if(check[r][c]) continue;
    				check[r][c] = true;
    				nowScore++;
    				
    				for(int k = 0; k < 4; k++) {
    					int nr = r + DR[k];
    					int nc = c + DC[k];
    					if(cantGo(nr, nc) || check[nr][nc] || map[nr][nc] != no) continue;
    					bfs.add(new int[] {nr, nc});
    				}
    			}
    			//조각 3 이상일 경우
    			if(nowScore >= 3) {
    				totalScore += nowScore;
    				for(int l = 0; l < LN; l++) {
    					for(int m = 0; m < LN; m++) {
    						if(check[l][m]) map[l][m] = 0; //초기화 
    					}
    				}
    			}
    		}
    	}
    	
    	return totalScore;
    }
    
    
    static void resetTestMap() {
    	for(int i = 0; i < LN; i++) {
    		for(int j = 0; j < LN; j++) {
    			testMap[i][j] = nowMap[i][j];
    		}
    	}
    }
    
    //90도 회전 로직
    static void rotateRight(int r, int c) {
    	int diff = SN / 2;
    	int r1 = 0;
    	int c1 = 0;
    	//targetGrid 넣기
    	for(int i = r - diff; i <= r + diff; i++) {
    		for(int j = c - diff; j <= c + diff; j++) {
    			targetGrid[r1][c1++] = testMap[i][j];
    		}
    		r1++;
    		c1 = 0;
    	}
    	//90도 회전
    	for(int i = 0; i < SN; i++) {
    		for(int j = 0; j < SN; j++) {
    			targetGridResult[j][SN - 1 - i] = targetGrid[i][j];
    		}
    	}
    	//결과 넣어주기
    	r1 = 0;
    	c1 = 0;
    	//targetGrid 넣기
    	for(int i = r - diff; i <= r + diff; i++) {
    		for(int j = c - diff; j <= c + diff; j++) {
    			testMap[i][j] = targetGridResult[r1][c1++];
    		}
    		r1++;
    		c1 = 0;
    	}
    }
    
    public static boolean cantGo(int r, int c) {
    	return r < 0 || c < 0 || r >= LN || c >= LN;
    }
    
    public static void main(String[] args) throws Exception {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	StringTokenizer st = new StringTokenizer(br.readLine());
    	K = Integer.parseInt(st.nextToken());
    	M = Integer.parseInt(st.nextToken());
    	
    	//맵
    	nowMap = new int[LN][LN];
    	testMap = new int[LN][LN];
    	for(int i = 0; i < LN; i++) {
    		st = new StringTokenizer(br.readLine());
    		for(int j = 0; j < LN; j++) {
    			nowMap[i][j] = Integer.parseInt(st.nextToken());
    		}
    	}
    	
    	//큐
    	st = new StringTokenizer(br.readLine());
    	while(st.hasMoreElements()) relics.add(Integer.parseInt(st.nextToken()));
    	
    	//메인 로직
    	for(int i = 0; i < K; i++) {
    		if(!solve()) break;
    	}
    }
}
class Pos implements Comparable<Pos> {
	int r, c;
	
	@Override
	public int compareTo(Pos p) {
		if(this.c == p.c) return Integer.compare(p.r, this.r);
		return Integer.compare(this.c, p.c);
	}
	
	public Pos(int r, int c) {
		this.r = r;
		this.c = c;
	}
}